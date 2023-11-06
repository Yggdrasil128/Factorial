package de.yggdrasil128.factorial.api.transportlineusages;

import de.yggdrasil128.factorial.engine.TransportLineUsage;
import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.item.Item;
import de.yggdrasil128.factorial.model.transportline.TransportLine;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UsageService {

    public Map<Item, Map<Factory, TransportLineUsage>> getUsage(TransportLine transportLine) {
        return TransportLineUsage.of(transportLine);
    }

}
