package de.yggdrasil128.factorial.api.factoryitemlist;

import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.changelist.Changelist;
import de.yggdrasil128.factorial.model.factory.Factory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ItemListService {

    public ItemList getFullList(Factory factory) {
        List<Changelist> changelists = factory.getSave().getChangelists();
        Changelist primary = changelists.stream().filter(Changelist::isPrimary).findAny()
                .orElseThrow(ItemListService::reportChangelistNotAvailable);
        List<Changelist> active = changelists.stream().filter(Changelist::isActive).toList();
        return new ItemList(factory, primary, active);
    }

    public Changelist getPrimaryChangelist(Factory factory) {
        return factory.getSave().getChangelists().stream().filter(Changelist::isPrimary).findAny()
                .orElseThrow(ItemListService::reportChangelistNotAvailable);
    }

    private static ResponseStatusException reportChangelistNotAvailable() {
        return ModelService.report(HttpStatus.INTERNAL_SERVER_ERROR,
                "assertion failed: no primary changelist available");
    }

}
