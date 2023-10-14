package de.yggdrasil128.factorial.model.icon;

import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.gameversion.GameVersion;
import org.springframework.stereotype.Service;

@Service
public class IconService extends ModelService<Icon, IconRepository> {

    public IconService(IconRepository repository) {
        super(repository);
    }

    public Icon get(GameVersion gameVersion, String name) {
        return repository.findByGameVersionIdAndName(gameVersion.getId(), name)
                .orElseThrow(ModelService::reportNotFound);
    }

    public Icon create(GameVersion gameVersion, IconStandalone input) {
        return repository.save(new Icon(gameVersion, input.getName(), input.getImageData(), input.getMimeType()));
    }

    public Icon doImport(GameVersion gameVersion, String name, IconMigration input) {
        return new Icon(gameVersion, name, input.getImageData(), input.getMimeType());
    }

}
