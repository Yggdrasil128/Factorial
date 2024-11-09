package de.yggdrasil128.factorial.model.recipemodifier;

import de.yggdrasil128.factorial.model.AsyncHelper;
import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.OptionalInputField;
import de.yggdrasil128.factorial.model.game.Game;
import de.yggdrasil128.factorial.model.game.GameRepository;
import de.yggdrasil128.factorial.model.icon.IconService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

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

    @Transactional
    public void create(int gameId, RecipeModifierStandalone standalone, CompletableFuture<Void> result) {
        Game game = gameRepository.findById(gameId).orElseThrow(ModelService::reportNotFound);
        RecipeModifier recipeModifier = new RecipeModifier(game, standalone);
        applyRelations(recipeModifier, standalone);
        AsyncHelper.complete(result);
        recipeModifier = create(recipeModifier);
        game.getRecipeModifiers().add(recipeModifier);
        gameRepository.save(game);
        events.publishEvent(new RecipeModifierUpdatedEvent(recipeModifier));
    }

    @Transactional
    public void update(int id, RecipeModifierStandalone standalone, CompletableFuture<Void> result) {
        RecipeModifier recipeModifier = get(id);
        recipeModifier.applyBasics(standalone);
        applyRelations(recipeModifier, standalone);
        AsyncHelper.complete(result);
        recipeModifier = update(recipeModifier);
        events.publishEvent(new RecipeModifierUpdatedEvent(recipeModifier));
    }

    private void applyRelations(RecipeModifier recipeModifier, RecipeModifierStandalone standalone) {
        OptionalInputField.ofId(standalone.iconId(), iconService::get).apply(recipeModifier::setIcon);
    }

    @Transactional
    public void delete(int id, CompletableFuture<Void> result) {
        Game game = gameRepository.findByRecipeModifiersId(id);
        if (null == game) {
            throw report(HttpStatus.CONFLICT, "recipe modifier does not belong to a game");
        }
        AsyncHelper.complete(result);
        delete(id);
        events.publishEvent(new RecipeModifierRemovedEvent(game.getId(), id));
    }

}
