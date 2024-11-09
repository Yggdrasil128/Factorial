package de.yggdrasil128.factorial.controller;

import de.yggdrasil128.factorial.model.AsyncHelper;
import de.yggdrasil128.factorial.model.EntityPosition;
import de.yggdrasil128.factorial.model.External;
import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.factory.FactoryService;
import de.yggdrasil128.factorial.model.factory.FactoryStandalone;
import de.yggdrasil128.factorial.model.save.SaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api")
public class FactoryController {

    private final AsyncHelper asyncHelper;
    private final SaveService saveService;
    private final FactoryService factoryService;

    @Autowired
    public FactoryController(AsyncHelper asyncHelper, SaveService saveService, FactoryService factoryService) {
        this.asyncHelper = asyncHelper;
        this.saveService = saveService;
        this.factoryService = factoryService;
    }

    @PostMapping("/save/factories")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CompletableFuture<Void> create(int saveId, @RequestBody FactoryStandalone input) {
        return asyncHelper.submit(result -> factoryService.create(saveId, input, result));
    }

    @GetMapping("/save/factories")
    public List<FactoryStandalone> retrieveAll(int saveId) {
        return saveService.get(saveId).getFactories().stream().map(FactoryController::toOutput).toList();
    }

    @GetMapping("/factory")
    public FactoryStandalone retrieve(int factoryId) {
        return toOutput(factoryService.get(factoryId));
    }

    @PatchMapping("/save/factories/order")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CompletableFuture<Void> order(int saveId, @RequestBody List<EntityPosition> input) {
        return asyncHelper.submit(result -> factoryService.reorder(saveId, input, result));
    }

    @PatchMapping("/factory")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CompletableFuture<Void> update(int factoryId, @RequestBody FactoryStandalone input) {
        return asyncHelper.submit(result -> factoryService.update(factoryId, input, result));
    }

    @DeleteMapping("/factory")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CompletableFuture<Void> delete(int factoryId) {
        return asyncHelper.submit(result -> factoryService.delete(factoryId, result));
    }

    private static FactoryStandalone toOutput(Factory factory) {
        return FactoryStandalone.of(factory, External.FRONTEND);
    }

}
