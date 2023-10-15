package de.yggdrasil128.factorial.model.recipemodifier;

import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.gameversion.GameVersion;
import de.yggdrasil128.factorial.model.icon.Icon;
import de.yggdrasil128.factorial.model.icon.IconService;
import org.springframework.stereotype.Service;

@Service
public class RecipeModifierService extends ModelService<RecipeModifier, RecipeModifierRepository> {

    private final IconService icons;

    public RecipeModifierService(RecipeModifierRepository repository, IconService icons) {
        super(repository);
        this.icons = icons;
    }

    public RecipeModifier create(GameVersion gameVersion, RecipeModifierInput input) {
        Icon icon = 0 == input.getIconId() ? null : icons.get(input.getIconId());
        Fraction durationMultiplier = null == input.getDurationMultiplier() ? Fraction.ONE
                : input.getDurationMultiplier();
        Fraction inputQuantityMultiplier = null == input.getInputQuantityMultiplier() ? Fraction.ONE
                : input.getInputQuantityMultiplier();
        Fraction outputQuantityMultiplier = null == input.getOutputQuantityMultiplier() ? Fraction.ONE
                : input.getOutputQuantityMultiplier();
        return new RecipeModifier(gameVersion, input.getName(), input.getDescription(), icon, durationMultiplier,
                inputQuantityMultiplier, outputQuantityMultiplier);
    }

    public RecipeModifier update(int id, RecipeModifierInput input) {
        RecipeModifier recipeModifier = get(id);
        if (null != input.getName()) {
            recipeModifier.setName(input.getName());
        }
        if (null != input.getDescription()) {
            recipeModifier.setDescription(input.getDescription());
        }
        if (0 != input.getIconId()) {
            recipeModifier.setIcon(icons.get(input.getIconId()));
        }
        if (null != input.getDurationMultiplier()) {
            recipeModifier.setDurationMultiplier(input.getDurationMultiplier());
        }
        if (null != input.getInputQuantityMultiplier()) {
            recipeModifier.setInputQuantityMultiplier(input.getInputQuantityMultiplier());
        }
        if (null != input.getOutputQuantityMultiplier()) {
            recipeModifier.setOutputQuantityMultiplier(input.getOutputQuantityMultiplier());
        }
        return repository.save(recipeModifier);
    }

}
