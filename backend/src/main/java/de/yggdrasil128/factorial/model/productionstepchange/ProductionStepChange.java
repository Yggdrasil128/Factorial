package de.yggdrasil128.factorial.model.productionstepchange;

import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.productionstep.ProductionStep;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.ManyToOne;

@Embeddable
public class ProductionStepChange {

    @ManyToOne(optional = false)
    private ProductionStep productionStep;
    @Column(nullable = false)
    private Fraction change;

    public ProductionStepChange() {
    }

    public ProductionStepChange(ProductionStep productionStep, Fraction change) {
        this.productionStep = productionStep;
        this.change = change;
    }

    public ProductionStep getProductionStep() {
        return productionStep;
    }

    public void setProductionStep(ProductionStep productionStep) {
        this.productionStep = productionStep;
    }

    public Fraction getChange() {
        return change;
    }

    public void setChange(Fraction change) {
        this.change = change;
    }

}
