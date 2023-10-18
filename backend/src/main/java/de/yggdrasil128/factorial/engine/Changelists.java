package de.yggdrasil128.factorial.engine;

import de.yggdrasil128.factorial.model.changelist.Changelist;
import de.yggdrasil128.factorial.model.save.Save;

import java.util.List;
import java.util.stream.Stream;

public class Changelists {

    public static Changelist getPrimary(Save save) {
        return save.getChangelists().stream().filter(Changelist::isPrimary).findAny()
                .orElseThrow(() -> new IllegalStateException("assertion failed: no primary changelist available"));
    }

    public static Stream<Changelist> getActive(Save save) {
        return save.getChangelists().stream().filter(Changelist::isActive);
    }

    private final Changelist primary;
    private final List<Changelist> active;

    public static Changelists of(Save save) {
        return new Changelists(getPrimary(save), getActive(save).toList());
    }

    private Changelists(Changelist primary, List<Changelist> active) {
        this.primary = primary;
        this.active = active;
    }

    public Changelist getPrimary() {
        return primary;
    }

    public List<Changelist> getActive() {
        return active;
    }

}
