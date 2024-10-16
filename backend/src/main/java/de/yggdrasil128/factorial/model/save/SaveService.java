package de.yggdrasil128.factorial.model.save;

import de.yggdrasil128.factorial.engine.Changelists;
import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.changelist.Changelist;
import de.yggdrasil128.factorial.model.factory.Factory;
import org.springframework.stereotype.Service;

@Service
public class SaveService extends ModelService<Save, SaveRepository> {

    public SaveService(SaveRepository repository) {
        super(repository);
    }

    public Changelists computeChangelists(Save save) {
        return Changelists.of(save);
    }

    public void addAttachedFactory(Save save, Factory factory) {
        save.getFactories().add(factory);
        repository.save(save);
    }

    public void addAttachedChangelist(Save save, Changelist changelist) {
        save.getChangelists().add(changelist);
        repository.save(save);
    }

}
