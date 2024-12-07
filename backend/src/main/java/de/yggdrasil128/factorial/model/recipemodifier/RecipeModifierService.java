package de.yggdrasil128.factorial.model.recipemodifier;

import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.OptionalInputField;
import de.yggdrasil128.factorial.model.ParentedModelService;
import de.yggdrasil128.factorial.model.game.Game;
import de.yggdrasil128.factorial.model.game.GameRepository;
import de.yggdrasil128.factorial.model.icon.IconService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class RecipeModifierService
        extends ParentedModelService<RecipeModifier, RecipeModifierStandalone, Game, RecipeModifierRepository> {

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

    @Override
    protected int getEntityId(RecipeModifier recipeModifier) {
        return recipeModifier.getId();
    }

    @Override
    protected int getStandaloneId(RecipeModifierStandalone standalone) {
        return standalone.id();
    }

    @Override
    protected Game getParentEntity(int parentId) {
        return gameRepository.findById(parentId).orElseThrow(ModelService::reportNotFound);
    }

    @Override
    protected RecipeModifier prepareCreate(Game game, RecipeModifierStandalone standalone) {
        if (null == standalone.name()) {
            throw report(HttpStatus.BAD_REQUEST, "'name' is required");
        }
        ensureUniqueName(game, standalone);
        RecipeModifier recipeModifier = new RecipeModifier(game, standalone);
        applyRelations(recipeModifier, standalone);
        return recipeModifier;
    }

    @Override
    protected void handleBulkCreate(Game game, Iterable<RecipeModifier> recipeModifiers) {
        for (RecipeModifier recipeModifier : recipeModifiers) {
            game.getRecipeModifiers().add(recipeModifier);
            events.publishEvent(new RecipeModifierUpdatedEvent(recipeModifier));
        }
        gameRepository.save(game);
    }

    @Override
    protected void prepareUpdate(RecipeModifier recipeModifier, RecipeModifierStandalone standalone) {
        if (!recipeModifier.getName().equals(standalone.name())) {
            ensureUniqueName(recipeModifier.getGame(), standalone);
        }
        recipeModifier.applyBasics(standalone);
        applyRelations(recipeModifier, standalone);
    }

    private void ensureUniqueName(Game game, RecipeModifierStandalone standalone) {
        if (repository.existsByGameIdAndName(game.getId(), standalone.name())) {
            throw report(HttpStatus.CONFLICT, "A Recipe Modifier with that name already exists");
        }
    }

    private void applyRelations(RecipeModifier recipeModifier, RecipeModifierStandalone standalone) {
        OptionalInputField.ofId(standalone.iconId(), iconService::get).apply(recipeModifier::setIcon);
    }

    @Override
    protected void handleUpdate(RecipeModifier recipeModifier) {
        events.publishEvent(new RecipeModifierUpdatedEvent(recipeModifier));
    }

    @Override
    protected Game findParentEntity(int id) {
        Game game = gameRepository.findByRecipeModifiersId(id);
        if (null == game) {
            throw report(HttpStatus.CONFLICT, "recipe modifier does not belong to a game");
        }
        game.getRecipeModifiers().remove(get(id));
        return game;
    }

    @Override
    protected void handleDelete(Game game, int id) {
        events.publishEvent(new RecipeModifierRemovedEvent(game.getId(), id));
    }

}
