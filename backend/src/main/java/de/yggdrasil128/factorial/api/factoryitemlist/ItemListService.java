package de.yggdrasil128.factorial.api.factoryitemlist;

import de.yggdrasil128.factorial.model.changelist.Changelist;
import de.yggdrasil128.factorial.model.changelist.ChangelistService;
import de.yggdrasil128.factorial.model.factory.Factory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemListService {

    public ItemList getFullList(Factory factory) {
        List<Changelist> changelists = factory.getSave().getChangelists();
        Changelist primary = ChangelistService.getPrimaryChangelist(factory.getSave());
        List<Changelist> active = changelists.stream().filter(Changelist::isActive).toList();
        return new ItemList(factory, primary, active);
    }

}
