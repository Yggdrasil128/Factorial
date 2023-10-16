package de.yggdrasil128.factorial.api.simple;

import de.yggdrasil128.factorial.model.ReorderInputEntry;
import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.factory.FactoryInput;
import de.yggdrasil128.factorial.model.factory.FactoryOutput;
import de.yggdrasil128.factorial.model.factory.FactoryService;
import de.yggdrasil128.factorial.model.save.Save;
import de.yggdrasil128.factorial.model.save.SaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
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
    public FactoryOutput create(int saveId, @RequestBody FactoryInput input) {
        Save save = saveService.get(saveId);
        Factory factory = factoryService.create(save, input);
        saveService.addAttachedFactory(save, factory);
        return new FactoryOutput(factory);
    }

    @GetMapping("/save/factories")
    public List<FactoryOutput> retrieveAll(int saveId) {
        return saveService.get(saveId).getFactories().stream().map(FactoryOutput::new)
                .sorted(Comparator.comparing(FactoryOutput::getOrdinal)).toList();
    }

    @PatchMapping("/save/factories/order")
    public void reorder(int saveId, @RequestBody List<ReorderInputEntry> input) {
        factoryService.reorder(saveService.get(saveId), input);
    }

    @GetMapping("/factory")
    public FactoryOutput retrieve(int factoryId) {
        return new FactoryOutput(factoryService.get(factoryId));
    }

    @PatchMapping("/factory")
    public FactoryOutput update(int factoryId, @RequestBody FactoryInput input) {
        return new FactoryOutput(factoryService.update(factoryId, input));
    }

    @DeleteMapping("/factory")
    public void delete(int factoryId) {
        factoryService.delete(factoryId);
    }

    // this does not fit into the item controller, since items do not carry an ordinal themselves
    @PatchMapping("/factory/items/order")
    public void reorderItems(int factoryId, @RequestBody List<ReorderInputEntry> input) {
        factoryService.reorderItems(factoryId, input);
    }

}
