package de.yggdrasil128.factorial.controller;

import de.yggdrasil128.factorial.model.OptionalInputField;
import de.yggdrasil128.factorial.model.ReorderInputEntry;
import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.factory.FactoryService;
import de.yggdrasil128.factorial.model.factory.FactoryStandalone;
import de.yggdrasil128.factorial.model.icon.IconService;
import de.yggdrasil128.factorial.model.save.Save;
import de.yggdrasil128.factorial.model.save.SaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api")
public class FactoryController {

    private final IconService iconService;
    private final SaveService saveService;
    private final FactoryService factoryService;

    @Autowired
    public FactoryController(IconService iconService, SaveService saveService, FactoryService factoryService) {
        this.iconService = iconService;
        this.saveService = saveService;
        this.factoryService = factoryService;
    }

    @PostMapping("/save/factories")
    public FactoryStandalone create(int saveId, @RequestBody FactoryStandalone input) {
        Save save = saveService.get(saveId);
        Factory factory = new Factory(save, input);
        applyRelations(input, factory);
        factory = factoryService.create(factory);
        saveService.addAttachedFactory(save, factory);
        return toOutput(factory);
    }

    @GetMapping("/save/factories")
    public List<FactoryStandalone> retrieveAll(int saveId) {
        return saveService.get(saveId).getFactories().stream().map(this::toOutput)
                .sorted(Comparator.comparing(FactoryStandalone::ordinal)).toList();
    }

    @PatchMapping("/save/factories/order")
    public void reorder(int saveId, @RequestBody List<ReorderInputEntry> input) {
        factoryService.reorder(saveService.get(saveId), input);
    }

    @GetMapping("/factory")
    public FactoryStandalone retrieve(int factoryId) {
        return toOutput(factoryService.get(factoryId));
    }

    @PatchMapping("/factory")
    public FactoryStandalone update(int factoryId, @RequestBody FactoryStandalone input) {
        Factory factory = factoryService.get(factoryId);
        factory.applyBasics(input);
        applyRelations(input, factory);
        return toOutput(factoryService.update(factory));
    }

    private void applyRelations(FactoryStandalone input, Factory factory) {
        OptionalInputField.ofId((int) input.iconId(), iconService::get).apply(factory::setIcon);
    }

    @DeleteMapping("/factory")
    public void delete(int factoryId) {
        factoryService.delete(factoryId);
    }

    private FactoryStandalone toOutput(Factory factory) {
        return FactoryStandalone.of(factory, factoryService.computeProductionLine(factory,
                () -> saveService.computeProductionStepChanges(factory.getSave())));
    }

}
