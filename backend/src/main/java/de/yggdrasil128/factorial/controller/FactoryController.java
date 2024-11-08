package de.yggdrasil128.factorial.controller;

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

@RestController
@RequestMapping("/api")
public class FactoryController {

    private final SaveService saveService;
    private final FactoryService factoryService;

    @Autowired
    public FactoryController(SaveService saveService, FactoryService factoryService) {
        this.saveService = saveService;
        this.factoryService = factoryService;
    }

    @PostMapping("/save/factories")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void create(int saveId, @RequestBody FactoryStandalone input) {
        factoryService.create(saveId, input);
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
    public void order(int saveId, @RequestBody List<EntityPosition> input) {
        factoryService.reorder(saveId, input);
    }

    @PatchMapping("/factory")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void update(int factoryId, @RequestBody FactoryStandalone input) {
        factoryService.update(factoryId, input);
    }

    @DeleteMapping("/factory")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void delete(int factoryId) {
        factoryService.delete(factoryId);
    }

    private static FactoryStandalone toOutput(Factory factory) {
        return FactoryStandalone.of(factory, External.FRONTEND);
    }

}
