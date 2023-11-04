package de.yggdrasil128.factorial.api.simple;

import de.yggdrasil128.factorial.model.save.Save;
import de.yggdrasil128.factorial.model.save.SaveService;
import de.yggdrasil128.factorial.model.transportline.TransportLine;
import de.yggdrasil128.factorial.model.transportline.TransportLineInput;
import de.yggdrasil128.factorial.model.transportline.TransportLineOutput;
import de.yggdrasil128.factorial.model.transportline.TransportLineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TransportLineController {

    private final SaveService saveService;
    private final TransportLineService transportLineService;

    @Autowired
    public TransportLineController(SaveService saveService, TransportLineService transportLineService) {
        this.saveService = saveService;
        this.transportLineService = transportLineService;
    }

    @PostMapping("/save/transportLines")
    public TransportLineOutput create(int saveId, @RequestBody TransportLineInput input) {
        Save save = saveService.get(saveId);
        TransportLine transportLine = transportLineService.create(save, input);
        saveService.addAttachedTransportLine(save, transportLine);
        return new TransportLineOutput(transportLine);
    }

    @GetMapping("/save/transportLines")
    public List<TransportLineOutput> retrieveAll(int saveId) {
        return saveService.get(saveId).getTransportLines().stream().map(TransportLineOutput::new).toList();
    }

    @GetMapping("/transportLine")
    public TransportLineOutput retrieve(int transportLineId) {
        return new TransportLineOutput(transportLineService.get(transportLineId));
    }

    @PatchMapping("/transportLine")
    public TransportLineOutput update(int transportLineId, @RequestBody TransportLineInput input) {
        return new TransportLineOutput(transportLineService.update(transportLineId, input));
    }

    @DeleteMapping("/transportLine")
    public void delete(int transportLineId) {
        transportLineService.delete(transportLineId);
    }

}
