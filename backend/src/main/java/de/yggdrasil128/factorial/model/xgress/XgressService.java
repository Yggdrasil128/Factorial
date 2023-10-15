package de.yggdrasil128.factorial.model.xgress;

import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.resource.ResourceService;
import org.springframework.stereotype.Service;

@Service
public class XgressService extends ModelService<Xgress, XgressRepository> {

    private final ResourceService resources;

    public XgressService(XgressRepository repository, ResourceService resources) {
        super(repository);
        this.resources = resources;
    }

    public Xgress create(Factory factory, XgressInput input) {
        return new Xgress(factory, input.getName(), resources.get(input.getResources()));
    }

    public Xgress update(int id, XgressInput input) {
        Xgress xgress = get(id);
        if (null != input.getName()) {
            xgress.setName(input.getName());
        }
        if (null != input.getResources()) {
            xgress.setResources(resources.get(input.getResources()));
        }
        return repository.save(xgress);
    }

}
