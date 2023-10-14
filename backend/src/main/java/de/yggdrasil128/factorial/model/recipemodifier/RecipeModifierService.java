package de.yggdrasil128.factorial.model.recipemodifier;

import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.gameversion.GameVersion;
import de.yggdrasil128.factorial.model.gameversion.GameVersionService;
import de.yggdrasil128.factorial.model.icon.Icon;
import de.yggdrasil128.factorial.model.icon.IconService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecipeModifierService extends ModelService<RecipeModifier, RecipeModifierRepository> {

    private final IconService icons;

    public RecipeModifierService(RecipeModifierRepository repository, IconService icons) {
        super(repository);
        this.icons = icons;
    }

    public List<RecipeModifier> get(GameVersion gameVersion, List<String> names) {
        return repository.getAllByGameVersionIdAndNameIn(gameVersion.getId(), names);
    }

    public RecipeModifier create(GameVersion gameVersion, RecipeModifierStandalone input) {
        Icon icon = 0 == input.getIconId() ? null : icons.get(input.getIconId());
        return new RecipeModifier(gameVersion, input.getName(), input.getDescription(), icon,
                input.getDurationMultiplier(), input.getInputQuantityMultiplier(), input.getOutputQuantityMultiplier());
    }

    public RecipeModifier doImport(GameVersion gameVersion, String name, RecipeModifierMigration input) {
        Icon icon = null == input.getIconName() ? null
                : GameVersionService.getDetachedIcon(gameVersion, input.getIconName());
        return new RecipeModifier(gameVersion, name, input.getDescription(), icon, input.getDurationMultiplier(),
                input.getInputQuantityMultiplier(), input.getOutputQuantityMultiplier());
    }

}
