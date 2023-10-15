package de.yggdrasil128.factorial.model.machine;

import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.gameversion.GameVersion;
import de.yggdrasil128.factorial.model.icon.Icon;
import de.yggdrasil128.factorial.model.icon.IconService;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifier;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifierService;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Collections.emptyList;

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
        Icon icon = 0 == input.getIconId() ? null : icons.get(input.getIconId());
        List<RecipeModifier> machineModifiers = null == input.getMachineModifierIds() ? null
                : recipeModifiers.get(input.getMachineModifierIds());
        List<String> category = null == input.getCategory() ? emptyList() : input.getCategory();
        return repository.save(new Machine(gameVersion, input.getName(), icon, machineModifiers, category));
    }

    public Machine update(int id, MachineInput input) {
        Machine machine = get(id);
        if (null != input.getName()) {
            machine.setName(input.getName());
        }
        if (0 != input.getIconId()) {
            machine.setIcon(icons.get(input.getIconId()));
        }
        if (null != input.getMachineModifierIds()) {
            machine.setMachineModifiers(recipeModifiers.get(input.getMachineModifierIds()));
        }
        if (null != input.getCategory()) {
            machine.setCategory(input.getCategory());
        }
        return repository.save(machine);
    }

}
