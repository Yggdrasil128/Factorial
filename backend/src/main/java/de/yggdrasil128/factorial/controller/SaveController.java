package de.yggdrasil128.factorial.controller;

import de.yggdrasil128.factorial.engine.ProductionLineResources;
import de.yggdrasil128.factorial.engine.ProductionStepChanges;
import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.OptionalInputField;
import de.yggdrasil128.factorial.model.changelist.ChangelistStandalone;
import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.factory.FactoryService;
import de.yggdrasil128.factorial.model.factory.FactoryStandalone;
import de.yggdrasil128.factorial.model.factory.FactorySummary;
import de.yggdrasil128.factorial.model.gameversion.GameVersion;
import de.yggdrasil128.factorial.model.gameversion.GameVersionService;
import de.yggdrasil128.factorial.model.productionstep.ProductionStepService;
import de.yggdrasil128.factorial.model.productionstep.ProductionStepStandalone;
import de.yggdrasil128.factorial.model.resource.ResourceStandalone;
import de.yggdrasil128.factorial.model.save.Save;
import de.yggdrasil128.factorial.model.save.SaveService;
import de.yggdrasil128.factorial.model.save.SaveStandalone;
import de.yggdrasil128.factorial.model.save.SaveSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.function.Supplier;

@RestController
@RequestMapping("/api")
public class SaveController {

    private final GameVersionService gameVersionService;
    private final SaveService saveService;
    private final FactoryService factoryService;
    private final ProductionStepService productionStepService;

    @Autowired
    public SaveController(GameVersionService gameVersionService, SaveService saveService, FactoryService factoryService,
                          ProductionStepService productionStepService) {
        this.gameVersionService = gameVersionService;
        this.saveService = saveService;
        this.factoryService = factoryService;
        this.productionStepService = productionStepService;
    }

    @PostMapping("/saves")
    public SaveStandalone create(@RequestBody SaveStandalone input) {
        GameVersion gameVersion = gameVersionService.get((int) input.getGameVersion());
        Save save = new Save(gameVersion, input);
        save.setGameVersion(gameVersion);
        return new SaveStandalone(saveService.create(save));
    }

    @GetMapping("/saves")
    public List<SaveStandalone> retrieveAll() {
        return saveService.stream().map(SaveStandalone::new).toList();
    }

    @GetMapping("/save")
    public SaveStandalone retrieve(int saveId) {
        return new SaveStandalone(saveService.get(saveId));
    }

    @GetMapping("/save/summary")
    public SaveSummary retrieveSummary(int saveId) {
        return exportSave(saveService.get(saveId));
    }

    private SaveSummary exportSave(Save save) {
        Supplier<? extends ProductionStepChanges> changes = () -> saveService.computeProductionStepChanges(save);
        SaveSummary summary = new SaveSummary();
        summary.setSave(new SaveStandalone(save));
        summary.setFactories(save.getFactories().stream().map(factory -> exportFactory(factory, changes)).toList());
        summary.setChangelists(save.getChangelists().stream().map(ChangelistStandalone::new).toList());
        return summary;
    }

    private FactorySummary exportFactory(Factory factory, Supplier<? extends ProductionStepChanges> changes) {
        FactorySummary summary = new FactorySummary();
        summary.setFactory(new FactoryStandalone(factory));
        summary.setProductionSteps(
                factory.getProductionSteps().stream().map(productionStep -> new ProductionStepStandalone(productionStep,
                        productionStepService.computeThroughputs(productionStep, changes))).toList());
        ProductionLineResources resources = factoryService.computeResources(factory, changes);
        summary.setResources(factory.getResources().stream()
                .map(resource -> new ResourceStandalone(resource, resources.getContributions(resource))).toList());
        return summary;
    }

    @PatchMapping("/save")
    public SaveStandalone update(int saveId, @RequestBody SaveStandalone input) {
        Save save = saveService.get(saveId);
        applyBasics(input, save);
        if (0 != (int) input.getGameVersion()) {
            throw ModelService.report(HttpStatus.NOT_IMPLEMENTED, "cannot update game version");
        }
        return new SaveStandalone(saveService.update(save));
    }

    private static void applyBasics(SaveStandalone input, Save save) {
        OptionalInputField.of(input.getName()).apply(save::setName);
    }

    @DeleteMapping("/save")
    public void delete(int saveId) {
        saveService.delete(saveId);
    }

}
