package de.yggdrasil128.factorial.repository;

import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.factory.FactoryRepository;
import de.yggdrasil128.factorial.model.game.Game;
import de.yggdrasil128.factorial.model.game.GameRepository;
import de.yggdrasil128.factorial.model.gameversion.GameVersion;
import de.yggdrasil128.factorial.model.gameversion.GameVersionRepository;
import de.yggdrasil128.factorial.model.icon.Icon;
import de.yggdrasil128.factorial.model.icon.IconRepository;
import de.yggdrasil128.factorial.model.item.Item;
import de.yggdrasil128.factorial.model.item.ItemRepository;
import de.yggdrasil128.factorial.model.machine.Machine;
import de.yggdrasil128.factorial.model.machine.MachineRepository;
import de.yggdrasil128.factorial.model.productionstep.ProductionStep;
import de.yggdrasil128.factorial.model.productionstep.ProductionStepRepository;
import de.yggdrasil128.factorial.model.recipe.Recipe;
import de.yggdrasil128.factorial.model.recipe.RecipeRepository;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifierRepository;
import de.yggdrasil128.factorial.model.resource.Resource;
import de.yggdrasil128.factorial.model.save.Save;
import de.yggdrasil128.factorial.model.save.SaveRepository;
import de.yggdrasil128.factorial.model.transportlink.TransportLinkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Disabled for two reasons:
 */
@SpringBootTest// (properties = {"spring.datasource.url=jdbc:h2:mem:"})
public class RepositoriesTest {
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private GameVersionRepository gameVersionRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private RecipeModifierRepository recipeModifierRepository;
    @Autowired
    private MachineRepository machineRepository;
    @Autowired
    private SaveRepository saveRepository;
    @Autowired
    private FactoryRepository factoryRepository;
    @Autowired
    private ProductionStepRepository productionStepRepository;
    @Autowired
    private TransportLinkRepository transportLinkRepository;
    @Autowired
    private IconRepository iconRepository;

    @BeforeEach
    void setup() {
        saveRepository.deleteAll();
        factoryRepository.deleteAll();
        productionStepRepository.deleteAll();
        transportLinkRepository.deleteAll();
        gameRepository.deleteAll();
        gameVersionRepository.deleteAll();
        itemRepository.deleteAll();
        recipeRepository.deleteAll();
        recipeModifierRepository.deleteAll();
        machineRepository.deleteAll();
        iconRepository.deleteAll();
    }

    @Test
    void testIconData() {
        byte[] data = new byte[10];
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) i;
        }
        Icon icon = new Icon();
        icon.setImageData(data);
        icon.setMimeType("dummyMimeType");

        int iconId = iconRepository.save(icon).getId();

        icon = iconRepository.findById(iconId).orElseThrow();
        for (int i = 0; i < data.length; i++) {
            assertEquals(data[i], icon.getImageData()[i]);
        }
    }

    /**
     * Fails for the same reason that general deletion currently does not work: The JPA layer creates and maintains a
     * relation table between {@link GameVersion} and {@link Item}, but it does not delete from it when an item is
     * deleted. Instead, it directly deletes from the {@link Item} table, which violates the corresponding foreign key.
     */
    @Disabled("fails on item delete")
    @Test
    void testItemWithIcon() {
        Icon icon = new Icon();
        byte[] data = {0, 1, 2, 3};
        icon.setImageData(data);
        icon.setMimeType("dummyMimeType");
        icon = iconRepository.save(icon);
        assertTrue(iconRepository.existsById(icon.getId()));

        Game game = new Game();
        game.setName("Game");

        GameVersion gameVersion = new GameVersion();
        gameVersion.setGame(game);
        gameVersion.setName("Version 0");
        game.setVersions(List.of(gameVersion));

        Item item = new Item();
        item.setGameVersion(gameVersion);
        item.setName("Item");
        item.setIcon(icon);
        gameVersion.setItems(List.of(item));

        game = gameRepository.save(game);
        item = game.getVersions().get(0).getItems().get(0);

        assertTrue(itemRepository.existsById(item.getId()));

        itemRepository.delete(item);
        assertFalse(itemRepository.existsById(item.getId()));
        assertTrue(iconRepository.existsById(icon.getId()));
    }

    @Test
    void test() {
        Game game = new Game();
        game.setName("Game");

        GameVersion gameVersion = new GameVersion();
        gameVersion.setGame(game);
        gameVersion.setName("Version 0");

        game.setVersions(List.of(gameVersion));

        Item ironOre = new Item();
        ironOre.setGameVersion(gameVersion);
        ironOre.setName("Iron Ore");

        Item ironIngot = new Item();
        ironIngot.setGameVersion(gameVersion);
        ironIngot.setName("Iron Ingot");

        Item ironGear = new Item();
        ironGear.setGameVersion(gameVersion);
        ironGear.setName("Iron Gear");

        gameVersion.setItems(List.of(ironOre, ironIngot, ironGear));

        Machine smelter = new Machine();
        smelter.setGameVersion(gameVersion);
        smelter.setName("Smelter");

        Machine assembler = new Machine();
        assembler.setGameVersion(gameVersion);
        assembler.setName("Assembler");

        gameVersion.setMachines(List.of(smelter, assembler));

        Recipe oreToIngotRecipe = new Recipe();
        oreToIngotRecipe.setGameVersion(gameVersion);
        oreToIngotRecipe.setName("Ore to Ingot");
        oreToIngotRecipe.setInput(List.of(new Resource(ironOre, Fraction.of(1))));
        oreToIngotRecipe.setOutput(List.of(new Resource(ironIngot, Fraction.of(1))));
        oreToIngotRecipe.setApplicableMachines(List.of(smelter));
        oreToIngotRecipe.setDuration(Fraction.of(1));

        Recipe ingotToGearRecipe = new Recipe();
        ingotToGearRecipe.setGameVersion(gameVersion);
        ingotToGearRecipe.setName("Ingot to Gear");
        ingotToGearRecipe.setInput(List.of(new Resource(ironIngot, Fraction.of(1))));
        ingotToGearRecipe.setOutput(List.of(new Resource(ironGear, Fraction.of(1))));
        ingotToGearRecipe.setApplicableMachines(List.of(smelter));
        ingotToGearRecipe.setDuration(Fraction.of(1));

        gameVersion.setRecipies(List.of(oreToIngotRecipe, ingotToGearRecipe));

        gameRepository.save(game);

        Save save = new Save();
        save.setGameVersion(gameVersion);
        save.setName("Example");

        Factory factory = new Factory();
        factory.setSave(save);
        factory.setName("Factory");

        save.setFactories(List.of(factory));

        ProductionStep oreToIngotStep = new ProductionStep();
        oreToIngotStep.setFactory(factory);
        oreToIngotStep.setMachine(smelter);
        oreToIngotStep.setRecipe(oreToIngotRecipe);
        oreToIngotStep.setMachineCount(Fraction.of(1));

        ProductionStep ingotToGearStep = new ProductionStep();
        ingotToGearStep.setFactory(factory);
        ingotToGearStep.setMachine(assembler);
        ingotToGearStep.setRecipe(ingotToGearRecipe);
        ingotToGearStep.setMachineCount(Fraction.of(1));

        factory.setProductionSteps(List.of(oreToIngotStep, ingotToGearStep));

        saveRepository.save(save);
    }
}
