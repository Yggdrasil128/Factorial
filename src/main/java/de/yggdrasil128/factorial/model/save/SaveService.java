package de.yggdrasil128.factorial.model.save;

import static java.util.Collections.emptyList;

import java.util.ArrayList;

import org.springframework.stereotype.Service;

import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.factory.FactoryMigration;
import de.yggdrasil128.factorial.model.factory.FactoryService;
import de.yggdrasil128.factorial.model.factory.FactoryStandalone;
import de.yggdrasil128.factorial.model.game.Game;
import de.yggdrasil128.factorial.model.game.GameService;
import de.yggdrasil128.factorial.model.gameversion.GameVersion;
import de.yggdrasil128.factorial.model.gameversion.GameVersionService;

@Service
public class SaveService extends ModelService<Save, SaveRepository> {

    private final GameService games;
    private final GameVersionService gameVersions;
    private final FactoryService factories;

    public SaveService(SaveRepository repository, GameService games, GameVersionService gameVersions,
        FactoryService factories) {
        super(repository);
        this.games = games;
        this.gameVersions = gameVersions;
        this.factories = factories;
    }

    public Save create(int gameVersionId, SaveStandalone input) {
        GameVersion gameVersion = gameVersions.get(gameVersionId);
        return repository.save(new Save(gameVersion, input.getName(), emptyList(), emptyList()));
    }

    public void addFactory(Save save, Factory factory) {
        save.getFactories().add(factory);
        repository.save(save);
    }

    public Factory addFactory(int saveId, FactoryStandalone input) {
        Save save = get(saveId);
        Factory factory = factories.create(save, input);
        save.getFactories().add(factory);
        repository.save(save);
        return factory;
    }

    public Save doImport(SaveMigration input) {
        Game game = games.get(input.getGame());
        GameVersion version = gameVersions.get(game, input.getVersion());
        Save save = new Save(version, input.getName(), new ArrayList<>(), new ArrayList<>());
        for (FactoryMigration entry : input.getFactories()) {
            save.getFactories().add(factories.doImport(save, entry));
        }
        repository.save(save);
        return save;
    }

}
