package de.yggdrasil128.factorial.model.save;

import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.OptionalInputField;
import de.yggdrasil128.factorial.model.changelist.Changelist;
import de.yggdrasil128.factorial.model.changelist.ChangelistService;
import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.factory.FactoryService;
import de.yggdrasil128.factorial.model.gameversion.GameVersion;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class SaveService extends ModelService<Save, SaveRepository> {

    public SaveService(SaveRepository repository) {
        super(repository);
    }

    public Save create(Save save) {
        return repository.save(save);
    }

    public Save create(GameVersion gameVersion, SaveInput input) {
        Save save = new Save(gameVersion, input.getName(), new ArrayList<>(1), new ArrayList<>(1));
        save.getFactories().add(FactoryService.createSentinel(save));
        save.getChangelists().add(ChangelistService.createSentinel(save));
        return repository.save(save);
    }

    public void addAttachedFactory(Save save, Factory factory) {
        save.getFactories().add(factory);
        repository.save(save);
    }

    public void addAttachedChangelist(Save save, Changelist changelist) {
        save.getChangelists().add(changelist);
        repository.save(save);
    }

    public Save update(int id, SaveInput input) {
        Save save = get(id);
        OptionalInputField.of(input.getName()).apply(save::setName);
        if (0 != input.getGameVersionId()) {
            throw report(HttpStatus.NOT_IMPLEMENTED, "cannot update game version");
        }
        return repository.save(save);
    }

}
