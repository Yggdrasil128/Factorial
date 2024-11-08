package de.yggdrasil128.factorial.controller;

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

@RestController
@RequestMapping("/api")
public class ChangelistController {

    private final SaveService saveService;
    private final ChangelistService changelistService;

    @Autowired
    public ChangelistController(SaveService saveService, ChangelistService changelistService) {
        this.saveService = saveService;
        this.changelistService = changelistService;
    }

    @PostMapping("/save/changelists")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void create(int saveId, @RequestBody ChangelistStandalone input) {
        if (input.primary() && !input.active()) {
            throw primaryInactive();
        }
        changelistService.create(saveId, input);
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
    public void order(int saveId, @RequestBody List<EntityPosition> input) {
        changelistService.reorder(saveId, input);
    }

    @PatchMapping("/changelist")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void update(int changelistId, @RequestBody ChangelistStandalone input) {
        if (null != input.primary() && null != input.active() && input.primary().booleanValue()
                && !input.active().booleanValue()) {
            throw primaryInactive();
        }
        changelistService.update(changelistId, input);
    }

    private static ResponseStatusException primaryInactive() {
        return ModelService.report(HttpStatus.CONFLICT, "cannot set primary changelist to inactive");
    }

    @DeleteMapping("/changelist")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void delete(int changelistId) {
        changelistService.delete(changelistId);
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
