package de.yggdrasil128.factorial.model.recipemodifier;

import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.OptionalInputField;
import de.yggdrasil128.factorial.model.game.Game;
import de.yggdrasil128.factorial.model.game.GameRepository;
import de.yggdrasil128.factorial.model.icon.IconService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class RecipeModifierService extends ModelService<RecipeModifier, RecipeModifierRepository> {

    private final ApplicationEventPublisher events;
    private final GameRepository gameRepository;
    private final IconService iconService;

    public RecipeModifierService(RecipeModifierRepository repository, ApplicationEventPublisher events,
                                 GameRepository gameRepository, IconService iconService) {
        super(repository);
        this.events = events;
        this.gameRepository = gameRepository;
        this.iconService = iconService;
    }

    public void create(int gameId, RecipeModifierStandalone standalone) {
        Game game = gameRepository.findById(gameId).orElseThrow(ModelService::reportNotFound);
        RecipeModifier recipeModifier = new RecipeModifier(game, standalone);
        applyRelations(recipeModifier, standalone);
        recipeModifier = create(recipeModifier);
        game.getRecipeModifiers().add(recipeModifier);
        gameRepository.save(game);
        events.publishEvent(new RecipeModifierUpdatedEvent(recipeModifier));
    }

    public void update(int id, RecipeModifierStandalone standalone) {
        RecipeModifier recipeModifier = get(id);
        recipeModifier.applyBasics(standalone);
        applyRelations(recipeModifier, standalone);
        recipeModifier = update(recipeModifier);
        events.publishEvent(new RecipeModifierUpdatedEvent(recipeModifier));
    }

    private void applyRelations(RecipeModifier recipeModifier, RecipeModifierStandalone standalone) {
        OptionalInputField.ofId(standalone.iconId(), iconService::get).apply(recipeModifier::setIcon);
    }

    @Override
    public void delete(int id) {
        Game game = gameRepository.findByRecipeModifiersId(id);
        if (null == game) {
            throw report(HttpStatus.CONFLICT, "recipe modifier does not belong to a game");
        }
        super.delete(id);
        events.publishEvent(new RecipeModifierRemovedEvent(game.getId(), id));
    }

}
