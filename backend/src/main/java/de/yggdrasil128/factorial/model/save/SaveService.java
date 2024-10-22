package de.yggdrasil128.factorial.model.save;

import de.yggdrasil128.factorial.engine.Changelists;
import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.changelist.Changelist;
import de.yggdrasil128.factorial.model.changelist.ChangelistRemoved;
import de.yggdrasil128.factorial.model.changelist.ChangelistUpdated;
import de.yggdrasil128.factorial.model.factory.Factory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SaveService extends ModelService<Save, SaveRepository> {

    private final Map<Integer, Changelists> cache = new HashMap<>();

    public SaveService(SaveRepository repository) {
        super(repository);
    }

    public Changelists computeChangelists(Save save) {
        return cache.computeIfAbsent(save.getId(), key -> Changelists.of(save));
    }

    public void addAttachedFactory(Save save, Factory factory) {
        save.getFactories().add(factory);
        repository.save(save);
    }

    public void addAttachedChangelist(Save save, Changelist changelist) {
        save.getChangelists().add(changelist);
        repository.save(save);
        invalidateChangelists(save.getId());
    }

    @Override
    public Save update(Save entity) {
        // no need to invalidate resources, since we don't change anything related
        return super.update(entity);
    }

    @Override
    public void delete(int id) {
        super.delete(id);
        invalidateChangelists(id);
    }

    public void invalidateChangelists(int id) {
        cache.remove(id);
    }

    @EventListener
    public void on(ChangelistUpdated event) {
        if (event.isPrimaryActiveChanged()) {
            invalidateChangelists(event.getChangelist().getSave().getId());
        }
    }

    @EventListener
    public void on(ChangelistRemoved event) {
        invalidateChangelists(event.getSaveId());
    }

}
