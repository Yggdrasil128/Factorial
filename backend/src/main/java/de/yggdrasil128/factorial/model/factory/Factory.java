package de.yggdrasil128.factorial.model.factory;

import com.fasterxml.jackson.annotation.JsonBackReference;
import de.yggdrasil128.factorial.model.Balance;
import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.icon.Icon;
import de.yggdrasil128.factorial.model.productionstep.ProductionStep;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifier;
import de.yggdrasil128.factorial.model.resource.Resource;
import de.yggdrasil128.factorial.model.save.Save;
import jakarta.persistence.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;

@Entity
public class Factory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @ManyToOne(optional = false)
    @JsonBackReference
    private Save save;
    @Column(unique = true, nullable = false)
    private String name;
    private String description;
    @ManyToOne
    private Icon icon;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<ProductionStep> productionSteps = emptyList();

    public Factory() {
    }

    public Factory(Save save, String name, String description, Icon icon, List<ProductionStep> productionSteps) {
        this.save = save;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.productionSteps = productionSteps;
    }

    public int getId() {
        return id;
    }

    public Save getSave() {
        return save;
    }

    public void setSave(Save save) {
        this.save = save;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    public List<ProductionStep> getProductionSteps() {
        return productionSteps;
    }

    public void setProductionSteps(List<ProductionStep> productionSteps) {
        this.productionSteps = productionSteps;
    }

    public Map<String, Balance> getBalances() {
        Map<String, Balance> balances = new HashMap<>();
        for (ProductionStep productionStep : productionSteps) {
            RecipeModifier effectiveModifier = productionStep.getEffectiveModifier();
            for (Resource input : productionStep.getRecipe().getInput()) {
                Balance balance = balances.computeIfAbsent(input.getItem().getName(), key -> new Balance());
                balance.setConsumption(
                        balance.getConsumption().add(input.getQuantity().divide(productionStep.getRecipe().getDuration())
                                .multiply(effectiveModifier.getInputSpeedMultiplier()).multiply(Fraction.of(60))));
            }
            for (Resource output : productionStep.getRecipe().getOutput()) {
                Balance balance = balances.computeIfAbsent(output.getItem().getName(), key -> new Balance());
                balance.setProduction(
                        balance.getProduction().add(output.getQuantity().divide(productionStep.getRecipe().getDuration())
                                .multiply(effectiveModifier.getOutputSpeedMultiplier()).multiply(Fraction.of(60))));
            }
        }
        return balances;
    }

}
