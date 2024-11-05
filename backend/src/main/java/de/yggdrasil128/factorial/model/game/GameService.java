package de.yggdrasil128.factorial.model.game;

import de.yggdrasil128.factorial.model.EntityPosition;
import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.icon.Icon;
import de.yggdrasil128.factorial.model.item.Item;
import de.yggdrasil128.factorial.model.machine.Machine;
import de.yggdrasil128.factorial.model.recipe.Recipe;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GameService extends ModelService<Game, GameRepository> {

    private final ApplicationEventPublisher events;

    public GameService(GameRepository repository, ApplicationEventPublisher events) {
        super(repository);
        this.events = events;
    }

    public Optional<Game> get(String name) {
        return repository.findByName(name);
    }

    public void addAttachedIcon(Game game, Icon icon) {
        game.getIcons().add(icon);
        repository.save(game);
    }

    public void addAttachedItem(Game game, Item item) {
        game.getItems().add(item);
        repository.save(game);
    }

    public void addAttachedRecipe(Game game, Recipe recipe) {
        game.getRecipes().add(recipe);
    }

    public void addAttachedRecipeModifier(Game game, RecipeModifier recipeModifier) {
        game.getRecipeModifiers().add(recipeModifier);
        repository.save(game);
    }

    public void addAttachedMachine(Game game, Machine machine) {
        game.getMachines().add(machine);
        repository.save(game);
    }

    public void reorder(List<EntityPosition> input) {
        Map<Integer, Integer> order = input.stream()
                .collect(Collectors.toMap(EntityPosition::id, EntityPosition::ordinal));
        Collection<Game> games = new ArrayList<>();
        // we want to all games in memory so that we are immune to circles in the given order
        for (Game game : stream().toList()) {
            Integer ordinal = order.get(game.getId());
            if (null != ordinal) {
                game.setOrdinal(ordinal.intValue());
                games.add(game);
                repository.save(game);
            }
        }
        events.publishEvent(new GamesReorderedEvent(games));
    }

}
