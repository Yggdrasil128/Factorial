package de.yggdrasil128.factorial.model.recipe;

import de.yggdrasil128.factorial.model.AsyncHelper;
import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.OptionalInputField;
import de.yggdrasil128.factorial.model.game.Game;
import de.yggdrasil128.factorial.model.game.GameRepository;
import de.yggdrasil128.factorial.model.icon.IconService;
import de.yggdrasil128.factorial.model.item.ItemService;
import de.yggdrasil128.factorial.model.machine.MachineService;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifierService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

@Service
public class RecipeService extends ModelService<Recipe, RecipeRepository> {

    private final ApplicationEventPublisher events;
    private final GameRepository gameRepository;
    private final IconService iconService;
    private final ItemService itemService;
    private final RecipeModifierService recipeModifierService;
    private final MachineService machineService;

    public RecipeService(RecipeRepository repository, ApplicationEventPublisher events, GameRepository gameRepository,
                         IconService iconService, ItemService itemService, RecipeModifierService recipeModifierService,
                         MachineService machineService) {
        super(repository);
        this.events = events;
        this.gameRepository = gameRepository;
        this.iconService = iconService;
        this.itemService = itemService;
        this.recipeModifierService = recipeModifierService;
        this.machineService = machineService;
    }

    @Transactional
    public void create(int gameId, RecipeStandalone standalone, CompletableFuture<Void> result) {
        Game game = gameRepository.findById(gameId).orElseThrow(ModelService::reportNotFound);
        Recipe recipe = new Recipe(game, standalone);
        applyRelations(recipe, standalone);
        AsyncHelper.complete(result);
        recipe = create(recipe);
        game.getRecipes().add(recipe);
        gameRepository.save(game);
        events.publishEvent(new RecipeUpdatedEvent(recipe));
    }

    @Transactional
    public void update(int id, RecipeStandalone standalone, CompletableFuture<Void> result) {
        Recipe recipe = get(id);
        recipe.applyBasics(standalone);
        applyRelations(recipe, standalone);
        AsyncHelper.complete(result);
        recipe = update(recipe);
        events.publishEvent(new RecipeUpdatedEvent(recipe));
    }

    private void applyRelations(Recipe recipe, RecipeStandalone standalone) {
        OptionalInputField.of(standalone.ingredients()).map(this::createResoruce).apply(recipe::setIngredients);
        OptionalInputField.of(standalone.products()).map(this::createResoruce).apply(recipe::setProducts);
        OptionalInputField.ofId(standalone.iconId(), iconService::get).apply(recipe::setIcon);
        OptionalInputField.ofIds(standalone.applicableModifierIds(), recipeModifierService::get)
                .applyList(recipe::setApplicableModifiers);
        OptionalInputField.ofIds(standalone.applicableMachineIds(), machineService::get)
                .applyList(recipe::setApplicableMachines);
    }

    private ItemQuantity createResoruce(ItemQuantityStandalone input) {
        return new ItemQuantity(itemService.get((int) input.itemId()), input.quantity());
    }

    @Transactional
    public void delete(int id, CompletableFuture<Void> result) {
        Game game = gameRepository.findByRecipesId(id);
        if (null == game) {
            throw report(HttpStatus.CONFLICT, "recipe does not belong to a game");
        }
        AsyncHelper.complete(result);
        delete(id);
        events.publishEvent(new RecipeRemovedEvent(game.getId(), id));
    }

}
