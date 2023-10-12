package de.yggdrasil128.factorial.model.changelist;

import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.icon.Icon;
import de.yggdrasil128.factorial.model.icon.IconService;
import de.yggdrasil128.factorial.model.save.Save;
import org.springframework.stereotype.Service;

import static java.util.Collections.emptyList;

@Service
public class ChangelistService extends ModelService<Changelist, ChangelistRepository> {

    private final IconService icons;

    public ChangelistService(ChangelistRepository repository, IconService icons) {
        super(repository);
        this.icons = icons;
    }

    public Changelist create(Save save, ChangeListStandalone input) {
        Icon icon = 0 == input.getIconId() ? null : icons.get(input.getIconId());
        return repository.save(input.with(save, icon, emptyList()));
    }

}
