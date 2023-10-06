package de.yggdrasil128.factorial.model.icon;

import de.yggdrasil128.factorial.model.ModelService;
import org.springframework.stereotype.Service;

@Service
public class IconService extends ModelService<Icon, IconRepository> {

    public IconService(IconRepository repository) {
        super(repository);
    }

    public Icon create(Icon input) {
        return repository.save(input);
    }

}
