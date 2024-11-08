package de.yggdrasil128.factorial.model.icon;

import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.game.Game;
import de.yggdrasil128.factorial.model.game.GameRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class IconService extends ModelService<Icon, IconRepository> {

    private final ApplicationEventPublisher events;
    private final GameRepository gameRepository;

    public IconService(IconRepository repository, ApplicationEventPublisher events, GameRepository gameRepository) {
        super(repository);
        this.events = events;
        this.gameRepository = gameRepository;
    }

    public void create(int gameId, IconStandalone standalone) {
        Game game = gameRepository.findById(gameId).orElseThrow(ModelService::reportNotFound);
        Icon icon = new Icon(game, standalone);
        icon = create(icon);
        game.getIcons().add(icon);
        gameRepository.save(game);
        events.publishEvent(new IconUpdatedEvent(icon));
    }

    public void update(int id, IconStandalone standalone) {
        Icon icon = get(id);
        icon.applyBasics(standalone);
        icon = update(icon);
        events.publishEvent(new IconUpdatedEvent(icon));
    }

    @Override
    public void delete(int id) {
        Game game = gameRepository.findByIconsId(id);
        if (null == game) {
            throw report(HttpStatus.CONFLICT, "icon does not belong to a game");
        }
        super.delete(id);
        events.publishEvent(new IconRemovedEvent(game.getId(), id));
    }

}
