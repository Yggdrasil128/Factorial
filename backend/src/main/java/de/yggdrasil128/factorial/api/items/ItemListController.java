package de.yggdrasil128.factorial.api.items;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.yggdrasil128.factorial.model.changelist.Changelist;
import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.factory.FactoryService;

@RestController
@RequestMapping("/api/items")
public class ItemListController {

    private final FactoryService factories;

    @Autowired
    public ItemListController(FactoryService factories) {
        this.factories = factories;
    }

    @GetMapping
    public ItemList getFullList(int factoryId) {
        Factory factory = factories.get(factoryId);
        List<Changelist> changelists = factory.getSave().getChangelists();
        Changelist primary = changelists.stream().filter(Changelist::isPrimary).findAny()
            .orElseGet(ItemListController::makeDefaultPrimaryChangelist);
        List<Changelist> active = changelists.stream().filter(Changelist::isActive).toList();
        return new ItemList(factory.getProductionSteps(), primary, active);
    }

    private static Changelist makeDefaultPrimaryChangelist() {
        Changelist primary = new Changelist();
        primary.setPrimary(true);
        return primary;
    }

}
