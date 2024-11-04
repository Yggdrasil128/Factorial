package de.yggdrasil128.factorial.controller;

import de.yggdrasil128.factorial.engine.ProductionLine;
import de.yggdrasil128.factorial.engine.ProductionStepChanges;
import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.changelist.ChangelistStandalone;
import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.factory.FactoryService;
import de.yggdrasil128.factorial.model.factory.FactoryStandalone;
import de.yggdrasil128.factorial.model.factory.FactorySummary;
import de.yggdrasil128.factorial.model.game.Game;
import de.yggdrasil128.factorial.model.game.GameService;
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

    private final GameService gameService;
    private final SaveService saveService;
    private final FactoryService factoryService;
    private final ProductionStepService productionStepService;

    @Autowired
    public SaveController(GameService gameService, SaveService saveService, FactoryService factoryService,
                          ProductionStepService productionStepService) {
        this.gameService = gameService;
        this.saveService = saveService;
        this.factoryService = factoryService;
        this.productionStepService = productionStepService;
    }

    @PostMapping("/saves")
    public SaveStandalone create(@RequestBody SaveStandalone input) {
        Game game = gameService.get((int) input.gameId());
        Save save = new Save(game, input);
        return SaveStandalone.of(saveService.create(save));
    }

    @GetMapping("/saves")
    public List<SaveStandalone> retrieveAll() {
        return saveService.stream().map(SaveStandalone::of).toList();
    }

    @GetMapping("/save")
    public SaveStandalone retrieve(int saveId) {
        return SaveStandalone.of(saveService.get(saveId));
    }

    @GetMapping("/save/summary")
    public SaveSummary retrieveSummary(int saveId) {
        return exportSave(saveService.get(saveId));
    }

    private SaveSummary exportSave(Save save) {
        Supplier<? extends ProductionStepChanges> changes = () -> saveService.computeProductionStepChanges(save);
        SaveSummary summary = new SaveSummary();
        summary.setSave(SaveStandalone.of(save));
        summary.setFactories(save.getFactories().stream().map(factory -> exportFactory(factory, changes)).toList());
        summary.setChangelists(save.getChangelists().stream().map(ChangelistStandalone::of).toList());
        return summary;
    }

    private FactorySummary exportFactory(Factory factory, Supplier<? extends ProductionStepChanges> changes) {
        FactorySummary summary = new FactorySummary();
        summary.setProductionSteps(factory.getProductionSteps().stream().map(productionStep -> ProductionStepStandalone
                .of(productionStep, productionStepService.computeThroughputs(productionStep, changes))).toList());
        ProductionLine productionLine = factoryService.computeProductionLine(factory, changes);
        summary.setResources(factory.getResources().stream()
                .map(resource -> ResourceStandalone.of(resource, productionLine.getContributions(resource))).toList());
        summary.setFactory(FactoryStandalone.of(factory, productionLine));
        return summary;
    }

    @PatchMapping("/save")
    public SaveStandalone update(int saveId, @RequestBody SaveStandalone input) {
        Save save = saveService.get(saveId);
        if (null != input.gameId()) {
            throw ModelService.report(HttpStatus.NOT_IMPLEMENTED, "cannot update game");
        }
        save.applyBasics(input);
        return SaveStandalone.of(saveService.update(save));
    }

    @DeleteMapping("/save")
    public void delete(int saveId) {
        saveService.delete(saveId);
    }

}
