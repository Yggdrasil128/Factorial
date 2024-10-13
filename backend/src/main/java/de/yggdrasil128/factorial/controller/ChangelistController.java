package de.yggdrasil128.factorial.controller;

import de.yggdrasil128.factorial.model.OptionalInputField;
import de.yggdrasil128.factorial.model.ReorderInputEntry;
import de.yggdrasil128.factorial.model.changelist.Changelist;
import de.yggdrasil128.factorial.model.changelist.ChangelistService;
import de.yggdrasil128.factorial.model.changelist.ChangelistStandalone;
import de.yggdrasil128.factorial.model.icon.IconService;
import de.yggdrasil128.factorial.model.save.Save;
import de.yggdrasil128.factorial.model.save.SaveService;
import org.springframework.beans.factory.annotation.Autowired;
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
        OptionalInputField.ofId((int) input.getIcon(), iconService::get).apply(changelist::setIcon);
        changelist = changelistService.create(changelist);
        saveService.addAttachedChangelist(save, changelist);
        return new ChangelistStandalone(changelist);
    }

    @GetMapping("/save/changelists")
    public List<ChangelistStandalone> retrieveAll(int saveId) {
        return saveService.get(saveId).getChangelists().stream().map(ChangelistStandalone::new)
                .sorted(Comparator.comparing(ChangelistStandalone::getOrdinal)).toList();
    }

    @PatchMapping("save/changelists/order")
    public void reorder(int saveId, @RequestBody List<ReorderInputEntry> input) {
        changelistService.reorder(saveService.get(saveId), input);
    }

    @GetMapping("/changelist")
    public ChangelistStandalone retrieve(int changelistId) {
        return new ChangelistStandalone(changelistService.get(changelistId));
    }

    @PatchMapping("/changelist")
    public ChangelistStandalone update(int changelistId, @RequestBody ChangelistStandalone input) {
        Changelist changelist = changelistService.get(changelistId);
        OptionalInputField.of(input.getName()).apply(changelist::setName);
        OptionalInputField.ofId((int) input.getIcon(), iconService::get).apply(changelist::setIcon);
        return new ChangelistStandalone(changelistService.update(changelist));
    }

    @DeleteMapping("/changelist")
    public void delete(int changelistId) {
        changelistService.delete(changelistId);
    }

    @PostMapping("/changelist/apply")
    public void apply(int changelistId) {
        changelistService.apply(changelistId);
    }

    @PatchMapping("/changelist/primary")
    public void setPrimary(int changelistId) {
        changelistService.setPrimary(changelistId);
    }

    @PatchMapping("/changelist/active")
    public void setActive(int changelistId, boolean active) {
        changelistService.setActive(changelistId, active);
    }

}
