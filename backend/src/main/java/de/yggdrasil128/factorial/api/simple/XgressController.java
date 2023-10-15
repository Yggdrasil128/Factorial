package de.yggdrasil128.factorial.api.simple;

import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.factory.FactoryService;
import de.yggdrasil128.factorial.model.xgress.Xgress;
import de.yggdrasil128.factorial.model.xgress.XgressInput;
import de.yggdrasil128.factorial.model.xgress.XgressOutput;
import de.yggdrasil128.factorial.model.xgress.XgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class XgressController {

    private final FactoryService factoryService;
    private final XgressService xgressService;

    @Autowired
    public XgressController(FactoryService factoryService, XgressService xgressService) {
        this.factoryService = factoryService;
        this.xgressService = xgressService;
    }

    @PostMapping("/factory/ingresses")
    public XgressOutput createIngress(int factoryId, @RequestBody XgressInput input) {
        Factory factory = factoryService.get(factoryId);
        Xgress ingress = xgressService.create(factory, input);
        factoryService.addAttachedIngress(factory, ingress);
        return new XgressOutput(ingress);
    }

    @PostMapping("/factory/egresses")
    public XgressOutput createEgress(int factoryId, @RequestBody XgressInput input) {
        Factory factory = factoryService.get(factoryId);
        Xgress ingress = xgressService.create(factory, input);
        factoryService.addAttachedEgress(factory, ingress);
        return new XgressOutput(ingress);
    }

    @GetMapping("/factory/ingresses")
    public List<XgressOutput> retrieveAllIngresses(int factoryId) {
        return factoryService.get(factoryId).getIngresses().stream().map(XgressOutput::new).toList();
    }

    @GetMapping("/factory/egresses")
    public List<XgressOutput> retrieveAllEgresses(int factoryId) {
        return factoryService.get(factoryId).getEgresses().stream().map(XgressOutput::new).toList();
    }

    @GetMapping("/xgress")
    public XgressOutput retrieve(int ingressId) {
        return new XgressOutput(xgressService.get(ingressId));
    }

    @PatchMapping("/xgress")
    public XgressOutput update(int itemId, @RequestBody XgressInput input) {
        return new XgressOutput(xgressService.update(itemId, input));
    }

    @DeleteMapping("/xgress")
    public void delete(int ingressId) {
        xgressService.delete(ingressId);
    }

}
