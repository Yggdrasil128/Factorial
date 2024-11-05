package de.yggdrasil128.factorial.model.recipemodifier;

import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.game.Game;
import de.yggdrasil128.factorial.model.game.GameRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class RecipeModifierService extends ModelService<RecipeModifier, RecipeModifierRepository> {

    private final ApplicationEventPublisher events;
    private final GameRepository games;

    public RecipeModifierService(RecipeModifierRepository repository, ApplicationEventPublisher events,
                                 GameRepository games) {
        super(repository);
        this.events = events;
        this.games = games;
    }

    @Override
    public RecipeModifier create(RecipeModifier entity) {
        RecipeModifier recipeModifier = super.create(entity);
        events.publishEvent(new RecipeModifierUpdatedEvent(recipeModifier));
        return recipeModifier;
    }

    @Override
    public RecipeModifier update(RecipeModifier entity) {
        RecipeModifier recipeModifier = super.update(entity);
        events.publishEvent(new RecipeModifierUpdatedEvent(recipeModifier));
        return recipeModifier;
    }

    @Override
    public void delete(int id) {
        Game game = games.findByRecipeModifiersId(id);
        super.delete(id);
        if (null != game) {
            events.publishEvent(new RecipeModifierRemovedEvent(game.getId(), id));
        }
    }

}
