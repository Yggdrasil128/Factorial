package de.yggdrasil128.factorial.api.transportlineusages;

import de.yggdrasil128.factorial.model.transportline.TransportLine;
import de.yggdrasil128.factorial.model.transportline.TransportLineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transportLineUsages")
public class UsageController {

    private final UsageService service;
    private final TransportLineService transportLines;

    @Autowired
    public UsageController(UsageService service, TransportLineService transportLines) {
        this.service = service;
        this.transportLines = transportLines;
    }

    @GetMapping
    public UsagesOutput getUsages(int transportLineId) {
        TransportLine transportLine = transportLines.get(transportLineId);
        return new UsagesOutput(service.getUsage(transportLine));
    }

}
