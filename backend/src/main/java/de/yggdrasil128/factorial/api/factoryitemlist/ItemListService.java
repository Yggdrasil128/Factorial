package de.yggdrasil128.factorial.api.factoryitemlist;

import de.yggdrasil128.factorial.engine.FactoryItemList;
import de.yggdrasil128.factorial.model.factory.Factory;
import org.springframework.stereotype.Service;

@Service
public class ItemListService {

    public FactoryItemList getFullList(Factory factory) {
        return FactoryItemList.of(factory);
    }

}
