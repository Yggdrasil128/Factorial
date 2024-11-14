package de.yggdrasil128.factorial.model.recipe;

import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.OptionalInputField;
import de.yggdrasil128.factorial.model.ParentedModelService;
import de.yggdrasil128.factorial.model.game.Game;
import de.yggdrasil128.factorial.model.game.GameRepository;
import de.yggdrasil128.factorial.model.icon.IconService;
import de.yggdrasil128.factorial.model.item.ItemService;
import de.yggdrasil128.factorial.model.machine.MachineService;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifierService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class RecipeService extends ParentedModelService<Recipe, RecipeStandalone, Game, RecipeRepository> {

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

    @Override
    protected int getEntityId(Recipe recipe) {
        return recipe.getId();
    }

    @Override
    protected int getStandaloneId(RecipeStandalone standalone) {
        return standalone.id();
    }

    @Override
    protected Game getParentEntity(int parentId) {
        return gameRepository.findById(parentId).orElseThrow(ModelService::reportNotFound);
    }

    @Override
    protected Recipe prepareCreate(Game game, RecipeStandalone standalone) {
        Recipe recipe = new Recipe(game, standalone);
        applyRelations(recipe, standalone);
        return recipe;
    }

    @Override
    protected void handleBulkCreate(Game game, Iterable<Recipe> recipes) {
        for (Recipe recipe : recipes) {
            game.getRecipes().add(recipe);
            events.publishEvent(new RecipeUpdatedEvent(recipe));
        }
        gameRepository.save(game);
    }

    @Override
    protected void prepareUpdate(Recipe recipe, RecipeStandalone standalone) {
        recipe.applyBasics(standalone);
        applyRelations(recipe, standalone);
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

    @Override
    protected void handleUpdate(Recipe recipe) {
        events.publishEvent(new RecipeUpdatedEvent(recipe));
    }

    @Override
    protected Game findParentEntity(int id) {
        Game game = gameRepository.findByRecipesId(id);
        if (null == game) {
            throw report(HttpStatus.CONFLICT, "recipe does not belong to a game");
        }
        game.getRecipes().remove(get(id));
        return game;
    }

    @Override
    protected void handleDelete(Game game, int id) {
        events.publishEvent(new RecipeRemovedEvent(game.getId(), id));
    }

}
