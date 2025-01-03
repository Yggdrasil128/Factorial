package de.yggdrasil128.factorial.model.icon;

import de.yggdrasil128.factorial.model.External;
import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.ParentedModelService;
import de.yggdrasil128.factorial.model.game.Game;
import de.yggdrasil128.factorial.model.game.GameRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IconService extends ParentedModelService<Icon, IconStandalone, Game, IconRepository> {

    private final ApplicationEventPublisher events;
    private final GameRepository gameRepository;
    private final IconDownloader iconDownloader;

    public IconService(IconRepository repository, ApplicationEventPublisher events, GameRepository gameRepository,
                       IconDownloader iconDownloader) {
        super(repository);
        this.events = events;
        this.gameRepository = gameRepository;
        this.iconDownloader = iconDownloader;
    }

    @Override
    protected int getEntityId(Icon icon) {
        return icon.getId();
    }

    @Override
    protected int getStandaloneId(IconStandalone standalone) {
        return standalone.id();
    }

    @Override
    protected Game getParentEntity(int parentId) {
        return gameRepository.findById(parentId).orElseThrow(ModelService::reportNotFound);
    }

    @Override
    protected Icon prepareCreate(Game game, IconStandalone standalone) {
        if (null == standalone.name()) {
            throw report(HttpStatus.BAD_REQUEST, "'name' is required");
        }
        ensureUniqueName(game, standalone);
        Icon icon = new Icon(game, standalone);
        iconDownloader.downloadIcon(icon, standalone);
        return icon;
    }

    @Override
    protected void handleBulkCreate(Game game, Iterable<Icon> icons) {
        for (Icon icon : icons) {
            game.getIcons().add(icon);
            events.publishEvent(new IconUpdatedEvent(icon));
        }
        gameRepository.save(game);
    }

    @Override
    protected void prepareUpdate(Icon icon, IconStandalone standalone) {
        if (!icon.getName().equals(standalone.name())) {
            ensureUniqueName(icon.getGame(), standalone);
        }
        icon.applyBasics(standalone);
    }

    private void ensureUniqueName(Game game, IconStandalone standalone) {
        if (repository.existsByGameIdAndName(game.getId(), standalone.name())) {
            throw report(HttpStatus.CONFLICT, "An Icon with that name already exists");
        }
    }

    @Override
    protected void handleUpdate(Icon icon) {
        events.publishEvent(new IconUpdatedEvent(icon));
    }

    @Override
    protected Game findParentEntity(int id) {
        Game game = gameRepository.findByIconsId(id);
        if (null == game) {
            throw report(HttpStatus.CONFLICT, "icon does not belong to a game");
        }
        game.getIcons().remove(get(id));
        return game;
    }

    @Override
    protected void handleDelete(Game game, int id) {
        events.publishEvent(new IconRemovedEvent(game.getId(), id));
    }

    @Transactional
    public IconStandalone getStandalone(int id) {
        return IconStandalone.of(get(id), External.SAVE_FILE);
    }

}
