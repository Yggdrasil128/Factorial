package de.yggdrasil128.factorial.model.icon;

import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.game.Game;
import de.yggdrasil128.factorial.model.game.GameRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class IconService extends ModelService<Icon, IconRepository> {

    private final ApplicationEventPublisher events;
    private final GameRepository games;

    public IconService(IconRepository repository, ApplicationEventPublisher events, GameRepository games) {
        super(repository);
        this.events = events;
        this.games = games;
    }

    @Override
    public Icon create(Icon entity) {
        Icon icon = super.create(entity);
        events.publishEvent(new IconUpdatedEvent(icon));
        return icon;
    }

    @Override
    public Icon update(Icon entity) {
        Icon icon = super.update(entity);
        events.publishEvent(new IconUpdatedEvent(icon));
        return icon;
    }

    @Override
    public void delete(int id) {
        Game game = games.findByIconsId(id);
        super.delete(id);
        if (null != game) {
            events.publishEvent(new IconRemovedEvent(game.getId(), id));
        }
    }

}
