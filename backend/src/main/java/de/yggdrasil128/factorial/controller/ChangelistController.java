package de.yggdrasil128.factorial.controller;

import de.yggdrasil128.factorial.model.AsyncHelper;
import de.yggdrasil128.factorial.model.EntityPosition;
import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.changelist.Changelist;
import de.yggdrasil128.factorial.model.changelist.ChangelistService;
import de.yggdrasil128.factorial.model.changelist.ChangelistStandalone;
import de.yggdrasil128.factorial.model.productionstep.ProductionStep;
import de.yggdrasil128.factorial.model.save.SaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api")
public class ChangelistController {

    private final AsyncHelper asyncHelper;
    private final SaveService saveService;
    private final ChangelistService changelistService;

    @Autowired
    public ChangelistController(AsyncHelper asyncHelper, SaveService saveService, ChangelistService changelistService) {
        this.asyncHelper = asyncHelper;
        this.saveService = saveService;
        this.changelistService = changelistService;
    }

    @PostMapping("/save/changelists")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CompletableFuture<Void> create(int saveId, @RequestBody List<ChangelistStandalone> input) {
        for (ChangelistStandalone in : input) {
            if (in.primary() && !in.active()) {
                throw primaryInactive();
            }
        }
        return asyncHelper.submit(result -> changelistService.create(saveId, input, result));
    }

    @GetMapping("/save/changelists")
    public List<ChangelistStandalone> retrieveAll(int saveId) {
        return saveService.get(saveId).getChangelists().stream().map(ChangelistStandalone::of).toList();
    }

    @GetMapping("/changelist")
    public ChangelistStandalone retrieve(int changelistId) {
        return ChangelistStandalone.of(changelistService.get(changelistId));
    }

    @PatchMapping("save/changelists/order")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CompletableFuture<Void> order(int saveId, @RequestBody List<EntityPosition> input) {
        return asyncHelper.submit(result -> changelistService.reorder(saveId, input, result));
    }

    @PatchMapping("/changelists")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CompletableFuture<Void> update(@RequestBody List<ChangelistStandalone> input) {
        for (ChangelistStandalone in : input) {
            if (null != in.primary() && null != in.active() && in.primary().booleanValue()
                    && !in.active().booleanValue()) {
                throw primaryInactive();
            }
        }
        return asyncHelper.submit(result -> changelistService.update(input, result));
    }

    private static ResponseStatusException primaryInactive() {
        return ModelService.report(HttpStatus.CONFLICT, "cannot set primary changelist to inactive");
    }

    @DeleteMapping("/changelists")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CompletableFuture<Void> delete(List<Integer> changelistIds) {
        return asyncHelper.submit(result -> changelistService.delete(changelistIds, result));
    }

    /**
     * Applies the target {@link Changelist}.
     * <p>
     * This will apply the {@link Changelist#getProductionStepChanges() changes} to the individual {@link ProductionStep
     * ProductionSteps}, which means that the production step's {@link ProductionStep#getMachineCount() machine count}
     * will be changed by the respective amount. Additionally, the changelist's changes will be cleared.
     * 
     * @param changelistId the {@link Changelist#getId() id} of the target {@link Changelist}
     */
    @PostMapping("/changelist/apply")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void apply(int changelistId) {
        changelistService.apply(changelistId);
    }

}
