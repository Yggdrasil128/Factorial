package de.yggdrasil128.factorial.model.icon;

import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.gameversion.GameVersion;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Collections.emptyList;

@Service
public class IconService extends ModelService<Icon, IconRepository> {

    public IconService(IconRepository repository) {
        super(repository);
    }

    public Icon create(GameVersion gameVersion, IconInput input) {
        List<String> category = null == input.getCategory() ? emptyList() : input.getCategory();
        return repository
                .save(new Icon(gameVersion, input.getName(), input.getImageData(), input.getMimeType(), category));
    }

    public Icon update(int id, IconInput input) {
        Icon icon = get(id);
        if (null != input.getName()) {
            icon.setName(input.getName());
        }
        if (null != input.getImageData()) {
            icon.setImageData(input.getImageData());
        }
        if (null != input.getMimeType()) {
            icon.setMimeType(input.getMimeType());
        }
        if (null != input.getCategory()) {
            icon.setCategory(input.getCategory());
        }
        return repository.save(icon);
    }

}
