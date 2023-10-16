package de.yggdrasil128.factorial.api.simple;

import de.yggdrasil128.factorial.model.ReorderInputEntry;
import de.yggdrasil128.factorial.model.changelist.Changelist;
import de.yggdrasil128.factorial.model.changelist.ChangelistInput;
import de.yggdrasil128.factorial.model.changelist.ChangelistOutput;
import de.yggdrasil128.factorial.model.changelist.ChangelistService;
import de.yggdrasil128.factorial.model.factory.FactoryOutput;
import de.yggdrasil128.factorial.model.save.Save;
import de.yggdrasil128.factorial.model.save.SaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
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
    public ChangelistOutput create(int saveId, @RequestBody ChangelistInput input) {
        Save save = saveService.get(saveId);
        Changelist changelist = changelistService.create(save, input);
        saveService.addAttachedChangelist(save, changelist);
        return new ChangelistOutput(changelist);
    }

    @GetMapping("/save/changelists")
    public List<ChangelistOutput> retrieveAll(int saveId) {
        return saveService.get(saveId).getChangelists().stream().map(ChangelistOutput::new)
                .sorted(Comparator.comparing(ChangelistOutput::getOrdinal)).toList();
    }

    @PatchMapping("save/changelists/order")
    public void reorder(int saveId, @RequestBody List<ReorderInputEntry> input) {
        changelistService.reorder(saveService.get(saveId), input);
    }

    @GetMapping("/changelist")
    public ChangelistOutput retrieve(int changelistId) {
        return new ChangelistOutput(changelistService.get(changelistId));
    }

    @PatchMapping("/changelist")
    public ChangelistOutput update(int changelistId, @RequestBody ChangelistInput input) {
        return new ChangelistOutput(changelistService.update(changelistId, input));
    }

    @DeleteMapping("/changelist")
    public void delete(int changelistId) {
        changelistService.delete(changelistId);
    }

    @PostMapping("/changelist/apply")
    public List<FactoryOutput> apply(int changelistId) {
        return changelistService.apply(changelistId).stream().map(FactoryOutput::new).toList();
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
