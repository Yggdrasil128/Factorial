package de.yggdrasil128.factorial.model.transportline;

import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.icon.Icon;
import de.yggdrasil128.factorial.model.resource.Resource;
import de.yggdrasil128.factorial.model.save.Save;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class TransportLine {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @ManyToOne(optional = false)
    private Save save;
    private String name;
    private String description;
    @ManyToOne
    private Icon icon;
    @ManyToOne(optional = false)
    private Factory sourceFactory;
    @ManyToOne(optional = false)
    private Factory targetFactory;
    @ElementCollection
    private List<Resource> resources;

    public TransportLine() {
    }

    public TransportLine(Save save, String name, String description, Icon icon, Factory sourceFactory,
                         Factory targetFactory, List<Resource> resources) {
        this.save = save;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.sourceFactory = sourceFactory;
        this.targetFactory = targetFactory;
        this.resources = resources;
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

    public Factory getSourceFactory() {
        return sourceFactory;
    }

    public void setSourceFactory(Factory sourceFactory) {
        this.sourceFactory = sourceFactory;
    }

    public Factory getTargetFactory() {
        return targetFactory;
    }

    public void setTargetFactory(Factory targetFactory) {
        this.targetFactory = targetFactory;
    }

    public List<Resource> getResources() {
        return resources;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) return true;
        if (that == null || getClass() != that.getClass()) return false;

        TransportLine transportLine = (TransportLine) that;

        return id == transportLine.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return name + " from " + sourceFactory + " to " + targetFactory;
    }

}
