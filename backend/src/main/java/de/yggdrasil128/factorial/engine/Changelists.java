package de.yggdrasil128.factorial.engine;

import de.yggdrasil128.factorial.model.changelist.Changelist;
import de.yggdrasil128.factorial.model.save.Save;

import java.util.List;
import java.util.Optional;

public class Changelists {

    public static Changelists of(Save save) {
        return new Changelists(save.getChangelists().stream().filter(Changelist::isPrimary).findAny().orElse(null),
                save.getChangelists().stream().filter(Changelist::isActive).toList());
    }

    private final Changelist primary;
    private final List<Changelist> active;

    private Changelists(Changelist primary, List<Changelist> active) {
        this.primary = primary;
        this.active = active;
    }

    public Changelist getPrimary() {
        if (null == primary) {
            throw new IllegalStateException("assertion failed: no primary changelist available");
        }
        return primary;
    }

    public Optional<Changelist> getOptionalPrimary() {
        return Optional.ofNullable(primary);
    }

    public List<Changelist> getActive() {
        return active;
    }

    @Override
    public String toString() {
        return "Changelists [primary=" + primary + ", active=" + active + "]";
    }

}
