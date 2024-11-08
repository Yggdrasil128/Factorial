package de.yggdrasil128.factorial.controller;

import de.yggdrasil128.factorial.model.EntityPosition;
import de.yggdrasil128.factorial.model.External;
import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.save.SaveService;
import de.yggdrasil128.factorial.model.save.SaveStandalone;
import de.yggdrasil128.factorial.model.save.SaveSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api")
public class SaveController {

    private final SaveService saveService;

    @Autowired
    public SaveController(SaveService saveService) {
        this.saveService = saveService;
    }

    @PostMapping("/saves")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void create(@RequestBody SaveStandalone input) {
        saveService.create(input);
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
    public CompletableFuture<SaveSummary> retrieveSummary(int saveId) {
        return saveService.getSummary(saveId, External.FRONTEND);
    }

    @PatchMapping("/saves/order")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void order(@RequestBody List<EntityPosition> input) {
        saveService.reorder(input);
    }

    @PatchMapping("/save")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void update(int saveId, @RequestBody SaveStandalone input) {
        if (null != input.gameId()) {
            throw ModelService.report(HttpStatus.NOT_IMPLEMENTED, "cannot update game of an existing save");
        }
        saveService.update(saveId, input);
    }

    @DeleteMapping("/save")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void delete(int saveId) {
        saveService.delete(saveId);
    }

}
