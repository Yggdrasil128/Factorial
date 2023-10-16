package de.yggdrasil128.factorial.model.machine;

import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.OptionalInputField;
import de.yggdrasil128.factorial.model.gameversion.GameVersion;
import de.yggdrasil128.factorial.model.icon.Icon;
import de.yggdrasil128.factorial.model.icon.IconService;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifier;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifierService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MachineService extends ModelService<Machine, MachineRepository> {

    private final IconService icons;
    private final RecipeModifierService recipeModifiers;

    public MachineService(MachineRepository repository, IconService icons, RecipeModifierService recipeModifiers) {
        super(repository);
        this.icons = icons;
        this.recipeModifiers = recipeModifiers;
    }

    public Machine create(GameVersion gameVersion, MachineInput input) {
        Icon icon = OptionalInputField.ofId(input.getIconId(), icons::get).get();
        List<RecipeModifier> machineModifiers = OptionalInputField
                .ofIds(input.getMachineModifierIds(), recipeModifiers::get).get();
        List<String> category = OptionalInputField.of(input.getCategory()).get();
        return repository.save(new Machine(gameVersion, input.getName(), icon, machineModifiers, category));
    }

    public Machine update(int id, MachineInput input) {
        Machine machine = get(id);
        OptionalInputField.of(input.getName()).apply(machine::setName);
        OptionalInputField.ofId(input.getIconId(), icons::get).apply(machine::setIcon);
        OptionalInputField.ofIds(input.getMachineModifierIds(), recipeModifiers::get)
                .apply(machine::setMachineModifiers);
        OptionalInputField.of(input.getCategory()).apply(machine::setCategory);
        return repository.save(machine);
    }

}
