package de.yggdrasil128.factorial.model.icon;

import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.OptionalInputField;
import de.yggdrasil128.factorial.model.gameversion.GameVersion;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IconService extends ModelService<Icon, IconRepository> {

    public IconService(IconRepository repository) {
        super(repository);
    }

    public Icon create(GameVersion gameVersion, IconInput input) {
        List<String> category = OptionalInputField.of(input.getCategory()).get();
        return repository
                .save(new Icon(gameVersion, input.getName(), input.getImageData(), input.getMimeType(), category));
    }

    public Icon update(int id, IconInput input) {
        Icon icon = get(id);
        OptionalInputField.of(input.getName()).apply(icon::setName);
        if (null != input.getImageData()) {
            icon.setImageData(input.getImageData());
        }
        OptionalInputField.of(input.getMimeType()).apply(input::setMimeType);
        OptionalInputField.of(input.getCategory()).apply(input::setCategory);
        return repository.save(icon);
    }

}
