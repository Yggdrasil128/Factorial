package de.yggdrasil128.factorial.devtools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/devtools")
public class DevToolsController {
    private final DevToolsService devToolsService;

    @Autowired
    public DevToolsController(DevToolsService devToolsService) {
        this.devToolsService = devToolsService;
    }

    @GetMapping("/wipeDatabaseAndRestart")
    public void wipeDatabaseAndRestart() {
        devToolsService.wipeDatabaseAndRestart();
    }

    @GetMapping("/shutdown")
    public void shutdown() {
        devToolsService.shutdown();
    }
}
