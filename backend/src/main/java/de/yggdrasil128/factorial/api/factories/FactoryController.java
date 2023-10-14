package de.yggdrasil128.factorial.api.factories;

import de.yggdrasil128.factorial.model.factory.FactoryService;
import de.yggdrasil128.factorial.model.save.Save;
import de.yggdrasil128.factorial.model.save.SaveService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/factories")
public class FactoryController {

    private final SaveService saves;
    private final FactoryService factories;

    public FactoryController(SaveService saves, FactoryService factories) {
        this.saves = saves;
        this.factories = factories;
    }

    @GetMapping
    public List<ApiFactory> getFactories(int saveId) {
        Save save = saves.get(saveId);
        return save.getFactories().stream().map(ApiFactory::new).toList();
    }

}
