package de.yggdrasil128.factorial.api.simple;

import de.yggdrasil128.factorial.model.save.Save;
import de.yggdrasil128.factorial.model.save.SaveService;
import de.yggdrasil128.factorial.model.transportlink.TransportLink;
import de.yggdrasil128.factorial.model.transportlink.TransportLinkInput;
import de.yggdrasil128.factorial.model.transportlink.TransportLinkOutput;
import de.yggdrasil128.factorial.model.transportlink.TransportLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TransportLinkController {

    private final SaveService saveService;
    private final TransportLinkService transportLinkService;

    @Autowired
    public TransportLinkController(SaveService saveService, TransportLinkService transportLinkService) {
        this.saveService = saveService;
        this.transportLinkService = transportLinkService;
    }

    @PostMapping("/save/transportLinks")
    public TransportLinkOutput create(int saveId, @RequestBody TransportLinkInput input) {
        Save save = saveService.get(saveId);
        TransportLink transportLink = transportLinkService.create(save, input);
        saveService.addAttachedTransportLink(save, transportLink);
        return new TransportLinkOutput(transportLink);
    }

    @GetMapping("/save/transportLinks")
    public List<TransportLinkOutput> retrieveAll(int saveId) {
        return saveService.get(saveId).getTransportLinks().stream().map(TransportLinkOutput::new).toList();
    }

    @GetMapping("/transportLink")
    public TransportLinkOutput retrieve(int transportLinkId) {
        return new TransportLinkOutput(transportLinkService.get(transportLinkId));
    }

    @PatchMapping("/transportLink")
    public TransportLinkOutput update(int transportLinkId, @RequestBody TransportLinkInput input) {
        return new TransportLinkOutput(transportLinkService.update(transportLinkId, input));
    }

    @DeleteMapping("/transportLink")
    public void delete(int transportLinkId) {
        transportLinkService.delete(transportLinkId);
    }

}
