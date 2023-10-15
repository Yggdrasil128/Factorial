package de.yggdrasil128.factorial.api.simple;

import de.yggdrasil128.factorial.model.gameversion.GameVersion;
import de.yggdrasil128.factorial.model.gameversion.GameVersionService;
import de.yggdrasil128.factorial.model.machine.Machine;
import de.yggdrasil128.factorial.model.machine.MachineInput;
import de.yggdrasil128.factorial.model.machine.MachineOutput;
import de.yggdrasil128.factorial.model.machine.MachineService;
import de.yggdrasil128.factorial.model.save.SaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MachineController {

    private final GameVersionService gameVersionService;
    private final MachineService machineService;
    private final SaveService saveService;

    @Autowired
    public MachineController(GameVersionService gameVersionService, MachineService machineService,
                             SaveService saveService) {
        this.gameVersionService = gameVersionService;
        this.machineService = machineService;
        this.saveService = saveService;
    }

    @PostMapping("/save/machines")
    public MachineOutput createFromSave(int saveId, @RequestBody MachineInput input) {
        return create(saveService.get(saveId).getGameVersion(), input);
    }

    @PostMapping("/gameVersion/machines")
    public MachineOutput create(int gameVersionId, @RequestBody MachineInput input) {
        return create(gameVersionService.get(gameVersionId), input);
    }

    private MachineOutput create(GameVersion gameVersion, MachineInput input) {
        Machine machine = machineService.create(gameVersion, input);
        gameVersionService.addAttachedMachine(gameVersion, machine);
        return new MachineOutput(machine);
    }

    @GetMapping("save/machines")
    public List<MachineOutput> retrieveAllFromSave(int saveId) {
        return retrieveAll(saveService.get(saveId).getGameVersion());
    }

    @GetMapping("gameVersion/machines")
    public List<MachineOutput> retrieveAll(int gameVersionId) {
        return retrieveAll(gameVersionService.get(gameVersionId));
    }

    private static List<MachineOutput> retrieveAll(GameVersion gameVersion) {
        return gameVersion.getMachines().stream().map(MachineOutput::new).toList();
    }

    @GetMapping("machine")
    public MachineOutput retrieve(int machineId) {
        return new MachineOutput(machineService.get(machineId));
    }

    @PatchMapping("/machine")
    public MachineOutput update(int machineId, @RequestBody MachineInput input) {
        return new MachineOutput(machineService.update(machineId, input));
    }

    @DeleteMapping("/machine")
    public void delete(int machineId) {
        machineService.delete(machineId);
    }

}
