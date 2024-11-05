package de.yggdrasil128.factorial.model.recipe;

import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.game.Game;
import de.yggdrasil128.factorial.model.game.GameRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class RecipeService extends ModelService<Recipe, RecipeRepository> {

    private final ApplicationEventPublisher events;
    private final GameRepository games;

    public RecipeService(RecipeRepository repository, ApplicationEventPublisher events, GameRepository games) {
        super(repository);
        this.events = events;
        this.games = games;
    }

    @Override
    public Recipe create(Recipe entity) {
        Recipe recipe = super.create(entity);
        events.publishEvent(new RecipeUpdatedEvent(recipe));
        return recipe;
    }

    @Override
    public Recipe update(Recipe entity) {
        Recipe recipe = super.update(entity);
        events.publishEvent(new RecipeUpdatedEvent(recipe));
        return recipe;
    }

    @Override
    public void delete(int id) {
        Game game = games.findByRecipesId(id);
        super.delete(id);
        if (null != game) {
            events.publishEvent(new RecipeRemovedEvent(game.getId(), id));
        }
    }

}
