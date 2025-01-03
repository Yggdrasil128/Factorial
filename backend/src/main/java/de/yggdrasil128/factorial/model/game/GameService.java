package de.yggdrasil128.factorial.model.game;

import de.yggdrasil128.factorial.model.*;
import de.yggdrasil128.factorial.model.icon.IconDownloader;
import de.yggdrasil128.factorial.model.icon.IconService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class GameService extends OrphanModelService<Game, GameStandalone, GameRepository> {

    private final ApplicationEventPublisher events;
    private final IconService iconService;
    private final IconDownloader iconDownloader;

    public GameService(GameRepository repository, ApplicationEventPublisher events, IconService iconService,
                       IconDownloader iconDownloader) {
        super(repository);
        this.events = events;
        this.iconService = iconService;
        this.iconDownloader = iconDownloader;
    }

    @Override
    protected int getEntityId(Game game) {
        return game.getId();
    }

    @Override
    protected int getStandaloneId(GameStandalone standalone) {
        return standalone.id();
    }

    @Override
    protected Game prepareCreate(GameStandalone standalone) {
        if (null == standalone.name()) {
            throw report(HttpStatus.BAD_REQUEST, "'name' is required");
        }
        ensureUniqueName(standalone);
        Game game = new Game(standalone);
        applyRelations(game, standalone);
        inferOrdinal(game);
        return game;
    }

    private void inferOrdinal(Game game) {
        if (0 >= game.getOrdinal()) {
            game.setOrdinal(stream().mapToInt(Game::getOrdinal).max().orElse(0) + 1);
        }
    }

    @Override
    protected void handleBulkCreate(Iterable<Game> games) {
        for (Game game : games) {
            events.publishEvent(new GameUpdatedEvent(game));
        }
    }

    @Override
    protected void prepareUpdate(Game game, GameStandalone standalone) {
        if (!game.getName().equals(standalone.name())) {
            ensureUniqueName(standalone);
        }
        game.applyBasics(standalone);
        applyRelations(game, standalone);
    }

    private void ensureUniqueName(GameStandalone standalone) {
        ensureUniqueName(standalone.name());
    }

    private void applyRelations(Game game, GameStandalone standalone) {
        OptionalInputField.ofId(standalone.iconId(), iconService::get).apply(game::setIcon);
    }

    @Override
    protected void handleUpdate(Game game) {
        events.publishEvent(new GameUpdatedEvent(game));
    }

    @Override
    protected void handleDelete(int id) {
        events.publishEvent(new GameRemovedEvent(id));
    }

    @Transactional
    public void doImport(GameSummary summary, CompletableFuture<Void> result) {
        ensureUniqueName(summary.getGame());
        Game game = Importer.importGame(summary, iconDownloader);
        game.setOrdinal(0);
        inferOrdinal(game);
        AsyncHelper.complete(result);
        repository.save(game);
        events.publishEvent(new GameUpdatedEvent(game));
    }

    @Transactional
    public void doClone(int id, String newName, CompletableFuture<Void> result) {
        ensureUniqueName(newName);
        Game game = get(id);
        GameSummary temp = Exporter.exportGame(game, External.SAVE_FILE);
        Game clone = Importer.importGame(temp, iconDownloader);
        clone.setName(newName);
        clone.setOrdinal(0);
        inferOrdinal(clone);
        AsyncHelper.complete(result);
        repository.save(clone);
        events.publishEvent(new GameUpdatedEvent(clone));
    }

    private void ensureUniqueName(String name) {
        if (repository.existsByName(name)) {
            throw report(HttpStatus.CONFLICT, "A Save with that name already exists");
        }
    }

    @Transactional
    public GameSummary getSummary(int id, External destination) {
        Game game = get(id);
        return Exporter.exportGame(game, destination);
    }

    public Optional<Game> get(String name) {
        return repository.findByName(name);
    }

    @Transactional
    public List<GameStandalone> getAll() {
        return stream().map(GameStandalone::of).toList();
    }

    @Transactional
    public void reorder(List<EntityPosition> input, CompletableFuture<Void> result) {
        Map<Integer, Integer> order = input.stream()
                .collect(Collectors.toMap(EntityPosition::id, EntityPosition::ordinal));
        AsyncHelper.complete(result);
        Collection<Game> games = new ArrayList<>();
        // we want to all games in memory so that we are immune to circles in the given order
        for (Game game : stream().toList()) {
            Integer ordinal = order.get(game.getId());
            if (null != ordinal) {
                game.setOrdinal(ordinal.intValue());
                games.add(game);
                repository.save(game);
            }
        }
        events.publishEvent(new GamesReorderedEvent(games));
    }

}
