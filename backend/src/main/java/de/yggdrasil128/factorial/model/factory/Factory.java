package de.yggdrasil128.factorial.model.factory;

import de.yggdrasil128.factorial.model.NamedModel;
import de.yggdrasil128.factorial.model.icon.Icon;
import de.yggdrasil128.factorial.model.productionstep.ProductionStep;
import de.yggdrasil128.factorial.model.resource.Resource;
import de.yggdrasil128.factorial.model.save.Save;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Factory implements NamedModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @ManyToOne(optional = false)
    private Save save;
    @Column(nullable = false)
    private int ordinal;
    @Column(unique = true, nullable = false)
    private String name;
    private String description;
    @ManyToOne
    private Icon icon;
    @JoinColumn
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<ProductionStep> productionSteps;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Resource> resources;

    public Factory() {
    }

    public Factory(Save save, FactoryStandalone standalone) {
        this.save = save;
        ordinal = standalone.getOrdinal();
        name = standalone.getName();
        description = standalone.getDescription();
        icon = null;
        productionSteps = new ArrayList<>();
        resources = new ArrayList<>();
    }

    @Override
    public int getId() {
        return id;
    }

    public Save getSave() {
        return save;
    }

    public void setSave(Save save) {
        this.save = save;
    }

    public int getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(int ordinal) {
        this.ordinal = ordinal;
    }

    @Override
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

    public List<Resource> getResources() {
        return resources;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Factory && id == ((Factory) obj).id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return name;
    }

}
