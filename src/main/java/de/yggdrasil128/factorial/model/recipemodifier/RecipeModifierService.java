package de.yggdrasil128.factorial.model.recipemodifier;

import java.util.List;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Service;

import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.gameversion.GameVersion;
import de.yggdrasil128.factorial.model.icon.Icon;
import de.yggdrasil128.factorial.model.icon.IconService;

@Service
public class RecipeModifierService extends ModelService<RecipeModifier, RecipeModifierRepository> {

    private final IconService icons;

    public RecipeModifierService(RecipeModifierRepository repository, IconService icons) {
        super(repository);
        this.icons = icons;
    }

    public List<RecipeModifier> get(GameVersion gameVersion, List<String> names) {
        return StreamSupport
            .stream(repository.getAllByGameVersionIdAndNameIn(gameVersion.getId(), names).spliterator(), false)
            .toList();
    }

    public RecipeModifier create(GameVersion gameVersion, RecipeModifierStandalone input) {
        Icon icon = 0 == input.getIconId() ? null : icons.get(input.getIconId());
        return new RecipeModifier(gameVersion, input.getName(), input.getDescription(), icon,
            input.getDurationMultiplier(), input.getInputQuantityMultiplier(), input.getOutputQuantityMultiplier());
    }

    public RecipeModifier doImport(GameVersion gameVersion, String name, RecipeModifierMigration input) {
        return new RecipeModifier(gameVersion, name, input.getDescription(), null, input.getDurationMultiplier(),
            input.getInputQuantityMultiplier(), input.getOutputQuantityMultiplier());
    }

}
