package de.yggdrasil128.factorial.model.machine;

import java.util.List;

import org.springframework.stereotype.Service;

import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.gameversion.GameVersion;
import de.yggdrasil128.factorial.model.icon.Icon;
import de.yggdrasil128.factorial.model.icon.IconService;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifier;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifierService;

@Service
public class MachineService extends ModelService<Machine, MachineRepository> {

    private final IconService icons;
    private final RecipeModifierService recipeModifiers;

    public MachineService(MachineRepository repository, IconService icons, RecipeModifierService recipeModifiers) {
        super(repository);
        this.icons = icons;
        this.recipeModifiers = recipeModifiers;
    }

    public Machine get(GameVersion gameVersion, String name) {
        return repository.findByGameVersionIdAndName(gameVersion.getId(), name)
            .orElseThrow(ModelService::reportNotFound);
    }

    public Machine create(GameVersion gameVersion, MachineStandalone input) {
        Icon icon = 0 == input.getIconId() ? null : icons.get(input.getIconId());
        List<RecipeModifier> machineModifiers = recipeModifiers.get(input.getMachineModifierIds());
        return repository.save(new Machine(gameVersion, input.getName(), icon, machineModifiers));
    }

    public Machine doImport(GameVersion gameVersion, String name, MachineMigration input) {
        List<RecipeModifier> machineModifiers = fromMigrations(gameVersion, input.getMachineModifierNames());
        return new Machine(gameVersion, name, null, machineModifiers);
    }

    private static List<RecipeModifier> fromMigrations(GameVersion gameVersion, List<String> modifierNames) {
        return modifierNames.stream().map(name -> getDetachedRecipeModifier(gameVersion, name)).toList();
    }

    private static RecipeModifier getDetachedRecipeModifier(GameVersion version, String recipeModifierName) {
        return version.getRecipeModifiers().stream()
            .filter(recipeModifier -> recipeModifier.getName().equals(recipeModifierName)).findAny()
            .orElseThrow(ModelService::reportNotFound);
    }

}
