package de.yggdrasil128.factorial.controller;

import de.yggdrasil128.factorial.engine.ProductionLine;
import de.yggdrasil128.factorial.model.AsyncHelper;
import de.yggdrasil128.factorial.model.EntityPosition;
import de.yggdrasil128.factorial.model.External;
import de.yggdrasil128.factorial.model.resource.global.GlobalResource;
import de.yggdrasil128.factorial.model.resource.global.GlobalResourceService;
import de.yggdrasil128.factorial.model.resource.global.GlobalResourceStandalone;
import de.yggdrasil128.factorial.model.save.SaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Manages {@link GlobalResource GlobalResources}.
 * <p>
 * In contrast to most other entities, resources cannot be created or deleted explicitly. Instead, they are spawned
 * and/or destroyed when computing a {@link ProductionLine}.
 */
@RestController
@RequestMapping("/api")
public class GlobalResourceController {

    private final AsyncHelper asyncHelper;
    private final SaveService saveService;
    private final GlobalResourceService resourceService;

    @Autowired
    public GlobalResourceController(AsyncHelper asyncHelper, SaveService saveService,
                                    GlobalResourceService resourceService) {
        this.asyncHelper = asyncHelper;
        this.saveService = saveService;
        this.resourceService = resourceService;
    }

    @GetMapping("/save/resources")
    public List<GlobalResourceStandalone> retrieveAll(int saveId) {
        return saveService.get(saveId).getResources().stream()
                .map(resource -> GlobalResourceStandalone.of(resource, External.FRONTEND)).toList();
    }

    @PatchMapping("/save/resources/order")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CompletableFuture<Void> order(int saveId, @RequestBody List<EntityPosition> input) {
        return asyncHelper.submit(result -> resourceService.reorder(saveId, input, result));
    }

}
