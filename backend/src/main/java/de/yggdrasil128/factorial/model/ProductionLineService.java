package de.yggdrasil128.factorial.model;

import de.yggdrasil128.factorial.engine.ProductionLine;
import de.yggdrasil128.factorial.engine.ResourceContributions;
import org.springframework.stereotype.Service;

/**
 * Defines the callbacks a {@link Service} must support in order to compute a {@link ProductionLine} for each of its
 * entities.
 */
public interface ProductionLineService {

    ResourceContributions spawnResource(int id, int itemId);

    void notifyResourceUpdate(int id, ResourceContributions contributions);

    void destroyResource(int id, int resourceId);

}
