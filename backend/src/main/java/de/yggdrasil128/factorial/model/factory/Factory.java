package de.yggdrasil128.factorial.model.factory;

import com.fasterxml.jackson.annotation.JsonBackReference;
import de.yggdrasil128.factorial.model.icon.Icon;
import de.yggdrasil128.factorial.model.item.Item;
import de.yggdrasil128.factorial.model.productionstep.ProductionStep;
import de.yggdrasil128.factorial.model.save.Save;
import jakarta.persistence.*;

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
    @JoinColumn
    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
    private List<ProductionStep> productionSteps = emptyList();
    @ElementCollection
    private Map<Item, Integer> itemOrder;

    public Factory() {
    }

    public Factory(Save save, String name, String description, Icon icon, List<ProductionStep> productionSteps,
                   Map<Item, Integer> itemOrder) {
        this.save = save;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.productionSteps = productionSteps;
        this.itemOrder = itemOrder;
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

    public Map<Item, Integer> getItemOrder() {
        return itemOrder;
    }

    public void setItemOrder(Map<Item, Integer> itemOrder) {
        this.itemOrder = itemOrder;
    }

}
