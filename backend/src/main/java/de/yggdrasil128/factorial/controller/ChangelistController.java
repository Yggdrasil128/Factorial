package de.yggdrasil128.factorial.controller;

import de.yggdrasil128.factorial.engine.ProductionStepChanges;
import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.OptionalInputField;
import de.yggdrasil128.factorial.model.EntityPosition;
import de.yggdrasil128.factorial.model.changelist.Changelist;
import de.yggdrasil128.factorial.model.changelist.ChangelistService;
import de.yggdrasil128.factorial.model.changelist.ChangelistStandalone;
import de.yggdrasil128.factorial.model.changelist.ProductionStepChangeStandalone;
import de.yggdrasil128.factorial.model.icon.IconService;
import de.yggdrasil128.factorial.model.productionstep.ProductionStep;
import de.yggdrasil128.factorial.model.productionstep.ProductionStepService;
import de.yggdrasil128.factorial.model.save.Save;
import de.yggdrasil128.factorial.model.save.SaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;

import static java.util.stream.Collectors.toMap;

@RestController
@RequestMapping("/api")
public class ChangelistController {

    private final IconService iconService;
    private final SaveService saveService;
    private final ChangelistService changelistService;
    private final ProductionStepService productionStepService;

    @Autowired
    public ChangelistController(IconService iconService, SaveService saveService, ChangelistService changelistService,
                                ProductionStepService productionStepService) {
        this.iconService = iconService;
        this.saveService = saveService;
        this.changelistService = changelistService;
        this.productionStepService = productionStepService;
    }

    @PostMapping("/save/changelists")
    public ChangelistStandalone create(int saveId, @RequestBody ChangelistStandalone input) {
        if (input.primary() && !input.active()) {
            throw primaryInactive();
        }
        Save save = saveService.get(saveId);
        Changelist changelist = new Changelist(save, input);
        if (changelist.isPrimary()) {
            disablePrimary(save);
        }
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
    public void order(int saveId, @RequestBody List<EntityPosition> input) {
        changelistService.reorder(saveService.get(saveId), input);
    }

    @GetMapping("/changelist")
    public ChangelistStandalone retrieve(int changelistId) {
        return ChangelistStandalone.of(changelistService.get(changelistId));
    }

    @PatchMapping("/changelist")
    public ChangelistStandalone update(int changelistId, @RequestBody ChangelistStandalone input) {
        if (null != input.primary() && null != input.active() && input.primary().booleanValue()
                && !input.active().booleanValue()) {
            throw primaryInactive();
        }
        Changelist changelist = changelistService.get(changelistId);
        if (changelist.isPrimary()) {
            if (null != input.primary() && !input.primary().booleanValue()) {
                throw ModelService.report(HttpStatus.CONFLICT,
                        "cannot set primary to false, set another changelist to primary instead");
            }
            if (null != input.active() && !input.active().booleanValue()) {
                throw primaryInactive();
            }
        }
        if (null != input.primary() && changelist.isPrimary() != input.primary().booleanValue()) {
            assert !changelist.isPrimary() && input.primary().booleanValue();
            disablePrimary(changelist.getSave());
        }
        changelist.applyBasics(input);
        applyRelations(input, changelist);
        return ChangelistStandalone.of(changelistService.update(changelist));
    }

    private static ResponseStatusException primaryInactive() {
        return ModelService.report(HttpStatus.CONFLICT, "cannot set primary changelist to inactive");
    }

    private void disablePrimary(Save save) {
        Changelist primary = changelistService
                .get(saveService.computeProductionStepChanges(save).getPrimaryChangelistId());
        primary.setPrimary(false);
        changelistService.update(primary);
    }

    private void applyRelations(ChangelistStandalone input, Changelist changelist) {
        OptionalInputField.ofId(input.iconId(), iconService::get).apply(changelist::setIcon);
        OptionalInputField.of(input.productionStepChanges())
                .apply(list -> changelist.setProductionStepChanges(list.stream()
                        .collect(toMap(change -> productionStepService.get((int) change.productionStepId()),
                                ProductionStepChangeStandalone::change))));
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

}
