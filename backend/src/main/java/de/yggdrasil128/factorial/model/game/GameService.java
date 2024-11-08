package de.yggdrasil128.factorial.model.game;

import de.yggdrasil128.factorial.model.EntityPosition;
import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.OptionalInputField;
import de.yggdrasil128.factorial.model.icon.IconService;
import de.yggdrasil128.factorial.model.icon.IconStandalone;
import de.yggdrasil128.factorial.model.item.ItemStandalone;
import de.yggdrasil128.factorial.model.machine.MachineStandalone;
import de.yggdrasil128.factorial.model.recipe.RecipeStandalone;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifierStandalone;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class GameService extends ModelService<Game, GameRepository> {

    private final ApplicationEventPublisher events;
    private final IconService iconService;

    public GameService(GameRepository repository, ApplicationEventPublisher events, IconService iconService) {
        super(repository);
        this.events = events;
        this.iconService = iconService;
    }

    public void create(GameStandalone standalone) {
        Game game = new Game(standalone);
        applyRelations(game, standalone);
        inferOrdinal(game);
        game = create(game);
        events.publishEvent(new GameUpdatedEvent(game));
    }

    private void inferOrdinal(Game game) {
        if (0 >= game.getOrdinal()) {
            game.setOrdinal(stream().mapToInt(Game::getOrdinal).max().orElse(0) + 1);
        }
    }

    public Optional<Game> get(String name) {
        return repository.findByName(name);
    }

    public CompletableFuture<GameSummary> getSummary(int id) {
        Game game = get(id);
        GameSummary summary = new GameSummary();
        summary.setGame(GameStandalone.of(game));
        summary.setIcons(game.getIcons().stream().map(icon -> IconStandalone.of(icon)).toList());
        summary.setItems(game.getItems().stream().map(item -> ItemStandalone.of(item)).toList());
        summary.setRecipes(game.getRecipes().stream().map(recipe -> RecipeStandalone.of(recipe)).toList());
        summary.setRecipeModifiers(game.getRecipeModifiers().stream()
                .map(recipeModifier -> RecipeModifierStandalone.of(recipeModifier)).toList());
        summary.setMachines(game.getMachines().stream().map(machine -> MachineStandalone.of(machine)).toList());
        return CompletableFuture.completedFuture(summary);
    }

    public void reorder(List<EntityPosition> input) {
        Map<Integer, Integer> order = input.stream()
                .collect(Collectors.toMap(EntityPosition::id, EntityPosition::ordinal));
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

    public void update(int id, GameStandalone standalone) {
        Game game = get(id);
        game.applyBasics(standalone);
        applyRelations(game, standalone);
        update(game);
        events.publishEvent(new GameUpdatedEvent(game));
    }

    private void applyRelations(Game game, GameStandalone standalone) {
        OptionalInputField.ofId(standalone.iconId(), iconService::get).apply(game::setIcon);
    }

    @Override
    public void delete(int id) {
        super.delete(id);
        events.publishEvent(new GameRemovedEvent(id));
    }

}
