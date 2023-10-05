package de.yggdrasil128.factorial.repository;

import de.yggdrasil128.factorial.model.*;
import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.factory.FactoryRepository;
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
import de.yggdrasil128.factorial.model.transportlink.TransportLinkRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest//(properties = {"spring.datasource.url=jdbc:h2:mem:"})
public class RepositoriesTest {
    @Autowired
    private FactoryRepository factoryRepository;
    @Autowired
    private IconRepository iconRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private MachineRepository machineRepository;
    @Autowired
    private ProductionStepRepository productionStepRepository;
    @Autowired
    private RecipeModifierRepository recipeModifierRepository;
    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private TransportLinkRepository transportLinkRepository;

    @BeforeEach
    void setup() {
        transportLinkRepository.deleteAll();
        factoryRepository.deleteAll();
        productionStepRepository.deleteAll();
        recipeRepository.deleteAll();
        machineRepository.deleteAll();
        recipeModifierRepository.deleteAll();
        itemRepository.deleteAll();
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

    @Test
    void testItemWithIcon() {
        Icon icon = new Icon();
        byte[] data = {0, 1, 2, 3};
        icon.setImageData(data);
        icon.setMimeType("dummyMimeType");
        icon = iconRepository.save(icon);
        assertTrue(iconRepository.existsById(icon.getId()));

        Item item = new Item();
        item.setName("Item");
        item.setIcon(icon);
        item = itemRepository.save(item);
        assertTrue(itemRepository.existsById(item.getId()));

        itemRepository.delete(item);
        assertFalse(itemRepository.existsById(item.getId()));
        assertTrue(iconRepository.existsById(icon.getId()));
    }

    @Test
    void test() {
        Item ironOre = new Item();
        ironOre.setName("Iron Ore");
        ironOre = itemRepository.save(ironOre);

        Item ironIngot = new Item();
        ironIngot.setName("Iron Ingot");
        ironIngot = itemRepository.save(ironIngot);

        Item ironGear = new Item();
        ironGear.setName("Iron Gear");
        ironGear = itemRepository.save(ironGear);

        Machine smelter = new Machine();
        smelter.setName("Smelter");
        smelter = machineRepository.save(smelter);

        Machine assembler = new Machine();
        assembler.setName("Assembler");
        assembler = machineRepository.save(assembler);

        Recipe oreToIngotRecipe = new Recipe();
        oreToIngotRecipe.setInput(List.of(new Resource(ironOre, Fraction.of(1))));
        oreToIngotRecipe.setOutput(List.of(new Resource(ironIngot, Fraction.of(1))));
        oreToIngotRecipe.setApplicableMachines(List.of(smelter));
        oreToIngotRecipe.setDuration(Fraction.of(1));
        oreToIngotRecipe = recipeRepository.save(oreToIngotRecipe);

        Recipe ingotToGearRecipe = new Recipe();
        ingotToGearRecipe.setInput(List.of(new Resource(ironIngot, Fraction.of(1))));
        ingotToGearRecipe.setOutput(List.of(new Resource(ironGear, Fraction.of(1))));
        ingotToGearRecipe.setApplicableMachines(List.of(smelter));
        ingotToGearRecipe.setDuration(Fraction.of(1));
        ingotToGearRecipe = recipeRepository.save(ingotToGearRecipe);

        ProductionStep oreToIngotStep = new ProductionStep();
        oreToIngotStep.setMachine(smelter);
        oreToIngotStep.setRecipe(oreToIngotRecipe);
        oreToIngotStep.setMachineCount(Fraction.of(1));
        oreToIngotStep = productionStepRepository.save(oreToIngotStep);

        ProductionStep ingotToGearStep = new ProductionStep();
        ingotToGearStep.setMachine(assembler);
        ingotToGearStep.setRecipe(ingotToGearRecipe);
        ingotToGearStep.setMachineCount(Fraction.of(1));
        ingotToGearStep = productionStepRepository.save(ingotToGearStep);

        Factory factory = new Factory();
        factory.setName("Factory");
        factory.setProductionSteps(List.of(oreToIngotStep, ingotToGearStep));
        factoryRepository.save(factory);
    }
}
