package de.yggdrasil128.factorial.controller;

import de.yggdrasil128.factorial.engine.ResourceStandalone;
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
        return new FactoryStandalone(factory);
    }

    @GetMapping("/save/factories")
    public List<FactoryStandalone> retrieveAll(int saveId) {
        return saveService.get(saveId).getFactories().stream().map(FactoryStandalone::new)
                .sorted(Comparator.comparing(FactoryStandalone::getOrdinal)).toList();
    }

    @PatchMapping("/save/factories/order")
    public void reorder(int saveId, @RequestBody List<ReorderInputEntry> input) {
        factoryService.reorder(saveService.get(saveId), input);
    }

    @GetMapping("/factory")
    public FactoryStandalone retrieve(int factoryId) {
        Factory factory = factoryService.get(factoryId);
        FactoryStandalone standalone = new FactoryStandalone(factory);
        standalone.setResources(
                factoryService.computeResources(factory, saveService.computeChangelists(factory.getSave())).stream()
                        .map(ResourceStandalone::new).toList());
        return standalone;
    }

    @PatchMapping("/factory")
    public FactoryStandalone update(int factoryId, @RequestBody FactoryStandalone input) {
        Factory factory = factoryService.get(factoryId);
        applyBasics(input, factory);
        applyRelations(input, factory);
        return new FactoryStandalone(factoryService.update(factory));
    }

    private static void applyBasics(FactoryStandalone input, Factory factory) {
        OptionalInputField.of(input.getName()).apply(factory::setName);
        OptionalInputField.of(input.getDescription()).apply(factory::setDescription);
    }

    private void applyRelations(FactoryStandalone input, Factory factory) {
        OptionalInputField.ofId((int) input.getIcon(), iconService::get).apply(factory::setIcon);
    }

    @DeleteMapping("/factory")
    public void delete(int factoryId) {
        factoryService.delete(factoryId);
    }

}
