package de.yggdrasil128.factorial.controller;

import de.yggdrasil128.factorial.model.AsyncHelper;
import de.yggdrasil128.factorial.model.EntityPosition;
import de.yggdrasil128.factorial.model.External;
import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.save.SaveService;
import de.yggdrasil128.factorial.model.save.SaveStandalone;
import de.yggdrasil128.factorial.model.save.SaveSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api")
public class SaveController {

    private final AsyncHelper asyncHelper;
    private final SaveService saveService;

    @Autowired
    public SaveController(AsyncHelper asyncHelper, SaveService saveService) {
        this.asyncHelper = asyncHelper;
        this.saveService = saveService;
    }

    @PostMapping("/saves")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CompletableFuture<Void> create(@RequestBody List<SaveStandalone> input) {
        return asyncHelper.submit(result -> saveService.create(input, result));
    }

    @GetMapping("/saves")
    public CompletableFuture<List<SaveStandalone>> retrieveAll() {
        return asyncHelper.submit(() -> saveService.getAll());
    }

    @GetMapping("/save")
    public SaveStandalone retrieve(int saveId) {
        return SaveStandalone.of(saveService.get(saveId));
    }

    @GetMapping("/save/summary")
    public CompletableFuture<SaveSummary> retrieveSummary(int saveId) {
        return asyncHelper.submit(() -> saveService.getSummary(saveId, External.FRONTEND));
    }

    @PatchMapping("/saves/order")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CompletableFuture<Void> order(@RequestBody List<EntityPosition> input) {
        return asyncHelper.submit(result -> saveService.reorder(input, result));
    }

    @PatchMapping("/saves")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CompletableFuture<Void> update(@RequestBody List<SaveStandalone> input) {
        for (SaveStandalone in : input) {
            if (null != in.gameId()) {
                throw ModelService.report(HttpStatus.NOT_IMPLEMENTED, "cannot update game of an existing save");
            }
        }
        return asyncHelper.submit(result -> saveService.update(input, result));
    }

    @DeleteMapping("/saves")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CompletableFuture<Void> delete(String saveIds) {
        List<Integer> saveIdsList = Arrays.stream(saveIds.split(",")).map(Integer::parseInt).toList();
        return asyncHelper.submit(result -> saveService.delete(saveIdsList, result));
    }

}
