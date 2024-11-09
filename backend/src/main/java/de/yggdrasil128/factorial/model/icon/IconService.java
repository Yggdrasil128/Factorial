package de.yggdrasil128.factorial.model.icon;

import de.yggdrasil128.factorial.model.AsyncHelper;
import de.yggdrasil128.factorial.model.External;
import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.game.Game;
import de.yggdrasil128.factorial.model.game.GameRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

@Service
public class IconService extends ModelService<Icon, IconRepository> {

    private final ApplicationEventPublisher events;
    private final GameRepository gameRepository;

    public IconService(IconRepository repository, ApplicationEventPublisher events, GameRepository gameRepository) {
        super(repository);
        this.events = events;
        this.gameRepository = gameRepository;
    }

    @Transactional
    public void create(int gameId, IconStandalone standalone, CompletableFuture<Void> result) {
        Game game = gameRepository.findById(gameId).orElseThrow(ModelService::reportNotFound);
        Icon icon = new Icon(game, standalone);
        AsyncHelper.complete(result);
        icon = create(icon);
        game.getIcons().add(icon);
        gameRepository.save(game);
        events.publishEvent(new IconUpdatedEvent(icon));
    }

    @Transactional
    public IconStandalone getStandalone(int id) {
        return IconStandalone.of(get(id), External.SAVE_FILE);
    }

    @Transactional
    public void update(int id, IconStandalone standalone, CompletableFuture<Void> result) {
        Icon icon = get(id);
        icon.applyBasics(standalone);
        AsyncHelper.complete(result);
        icon = update(icon);
        events.publishEvent(new IconUpdatedEvent(icon));
    }

    @Transactional
    public void delete(int id, CompletableFuture<Void> result) {
        Game game = gameRepository.findByIconsId(id);
        if (null == game) {
            throw report(HttpStatus.CONFLICT, "icon does not belong to a game");
        }
        AsyncHelper.complete(result);
        delete(id);
        events.publishEvent(new IconRemovedEvent(game.getId(), id));
    }

}
