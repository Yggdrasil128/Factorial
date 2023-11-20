package de.yggdrasil128.factorial.model.xgress;

import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.OptionalInputField;
import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.resource.Resource;
import de.yggdrasil128.factorial.model.resource.ResourceService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class XgressService extends ModelService<Xgress, XgressRepository> {

    private final ResourceService resources;

    public XgressService(XgressRepository repository, ResourceService resources) {
        super(repository);
        this.resources = resources;
    }

    public Xgress create(Factory factory, XgressInput input) {
        List<Resource> gressedResources = OptionalInputField.of(input.getResources()).map(resources::get).get();
        boolean unclogging = OptionalInputField.of(input.getUnclogging()).get().orElse(false);
        return new Xgress(factory, input.getName(), unclogging, gressedResources);
    }

    public Xgress update(int id, XgressInput input) {
        Xgress xgress = get(id);
        OptionalInputField.of(input.getName()).apply(xgress::setName);
        OptionalInputField.of(input.getUnclogging()).apply(xgress::setUnclogging);
        OptionalInputField.of(input.getResources()).map(resources::get).apply(xgress::setResources);
        return repository.save(xgress);
    }

}
