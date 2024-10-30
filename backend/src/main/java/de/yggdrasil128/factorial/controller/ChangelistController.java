package de.yggdrasil128.factorial.controller;

import de.yggdrasil128.factorial.engine.ProductionStepChanges;
import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.OptionalInputField;
import de.yggdrasil128.factorial.model.ReorderInputEntry;
import de.yggdrasil128.factorial.model.changelist.Changelist;
import de.yggdrasil128.factorial.model.changelist.ChangelistService;
import de.yggdrasil128.factorial.model.changelist.ChangelistStandalone;
import de.yggdrasil128.factorial.model.icon.IconService;
import de.yggdrasil128.factorial.model.productionstep.ProductionStep;
import de.yggdrasil128.factorial.model.save.Save;
import de.yggdrasil128.factorial.model.save.SaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ChangelistController {

    private final IconService iconService;
    private final SaveService saveService;
    private final ChangelistService changelistService;

    @Autowired
    public ChangelistController(IconService iconService, SaveService saveService, ChangelistService changelistService) {
        this.iconService = iconService;
        this.saveService = saveService;
        this.changelistService = changelistService;
    }

    @PostMapping("/save/changelists")
    public ChangelistStandalone create(int saveId, @RequestBody ChangelistStandalone input) {
        Save save = saveService.get(saveId);
        Changelist changelist = new Changelist(save, input);
        applyRelations(input, changelist);
        changelist = changelistService.create(changelist);
        saveService.addAttachedChangelist(save, changelist);
        return ChangelistStandalone.of(changelist);
    }

    @GetMapping("/save/changelists")
    public List<ChangelistStandalone> retrieveAll(int saveId) {
        return saveService.get(saveId).getChangelists().stream().map(ChangelistStandalone::of)
                .sorted(Comparator.comparing(ChangelistStandalone::ordinal)).toList();
    }

    @PatchMapping("save/changelists/order")
    public void reorder(int saveId, @RequestBody List<ReorderInputEntry> input) {
        changelistService.reorder(saveService.get(saveId), input);
    }

    @GetMapping("/changelist")
    public ChangelistStandalone retrieve(int changelistId) {
        return ChangelistStandalone.of(changelistService.get(changelistId));
    }

    /**
     * Updates the the target {@link Changelist} to represent the specified input.
     * <p>
     * This endpoint cannot be used to update the primary or update flag of the changelist. For that use
     * {@link #makePrimary(int) /changelist/primary} or {@link #setActive(int, boolean) /changelist/active} instead.
     * 
     * @param changelistId changelistId the {@link Changelist#getId() id} of the target {@link Changelist}
     * @param input the new values for the target {@link Changelist}
     * @return the target {@link Changelist} after the update was applied
     */
    @PatchMapping("/changelist")
    public ChangelistStandalone update(int changelistId, @RequestBody ChangelistStandalone input) {
        Changelist changelist = changelistService.get(changelistId);
        if (changelist.isPrimary() != input.primary()) {
            throw ModelService.report(HttpStatus.BAD_REQUEST,
                    "cannot change primary changelist via generic PATH, use '/changelist/primary' instead");
        }
        if (changelist.isActive() != input.active()) {
            throw ModelService.report(HttpStatus.BAD_REQUEST,
                    "cannot update changelist activity via generic PATCH, use '/changelist/active' instead");
        }
        applyBasics(input, changelist);
        applyRelations(input, changelist);
        return ChangelistStandalone.of(changelistService.update(changelist));
    }

    private static void applyBasics(ChangelistStandalone input, Changelist changelist) {
        OptionalInputField.of(input.name()).apply(changelist::setName);
        // we cannot update primary/active here, see error reports above
    }

    private void applyRelations(ChangelistStandalone input, Changelist changelist) {
        OptionalInputField.ofId((int) input.iconId(), iconService::get).apply(changelist::setIconId);
    }

    @DeleteMapping("/changelist")
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
    public void apply(int changelistId) {
        Changelist changelist = changelistService.get(changelistId);
        ProductionStepChanges changes = saveService.computeProductionStepChanges(changelist.getSave());
        changelistService.apply(changelist, changes);
    }

    /**
     * Makes the target {@link Changelist} that primary changelist.
     * <p>
     * This will change the {@link Changelist#isPrimary() primary} flag of the target {@link Changelist} to {@code true}
     * and the {@link Changelist#isPrimary() priamry} flag of the current primary changelist to {@code false}.
     * Additionally, the changes to affected {@link ProductionStep ProductionSteps} will be reflected as well.
     * 
     * @param changelistId the {@link Changelist#getId() id} of the target {@link Changelist}
     */
    @PatchMapping("/changelist/primary")
    public void makePrimary(int changelistId) {
        Changelist changelist = changelistService.get(changelistId);
        if (!changelist.isPrimary()) {
            Changelist oldPrimary = changelistService
                    .get(saveService.computeProductionStepChanges(changelist.getSave()).getPrimaryChangelistId());
            changelistService.setPrimaryActive(changelist, true, true);
            changelistService.setPrimaryActive(oldPrimary, false, oldPrimary.isActive());
        }
    }

    /**
     * Updates the {@link Changelist#isActive()} flag of the target {@link Changelist}.
     * <p>
     * This operation is valid only for changelists that are not the {@link Changelist#isPrimary() primary} changelist,
     * aside from setting the primary changelist to active, which is a no-op. Additionally, the changes to affected
     * {@link ProductionStep ProductionSteps} will be reflected as well.
     * 
     * @param changelistId the {@link Changelist#getId() id} of the target {@link Changelist}
     * @param active the new value for the active flag
     */
    @PatchMapping("/changelist/active")
    public void setActive(int changelistId, boolean active) {
        Changelist changelist = changelistService.get(changelistId);
        if (!changelist.isPrimary()) {
            changelistService.setPrimaryActive(changelist, false, active);
        } else if (!active) {
            throw ModelService.report(HttpStatus.CONFLICT, "cannot set primary changelist to inactive");
        }
    }

}
