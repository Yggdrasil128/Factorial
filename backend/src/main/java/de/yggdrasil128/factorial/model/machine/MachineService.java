package de.yggdrasil128.factorial.model.machine;

import de.yggdrasil128.factorial.model.ModelService;
import org.springframework.stereotype.Service;

@Service
public class MachineService extends ModelService<Machine, MachineRepository> {

    public MachineService(MachineRepository repository) {
        super(repository);
    }

}
