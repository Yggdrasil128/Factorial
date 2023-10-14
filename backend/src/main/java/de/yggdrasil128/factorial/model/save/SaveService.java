package de.yggdrasil128.factorial.model.save;

import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.changelist.ChangeListStandalone;
import de.yggdrasil128.factorial.model.changelist.Changelist;
import de.yggdrasil128.factorial.model.changelist.ChangelistService;
import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.factory.FactoryMigration;
import de.yggdrasil128.factorial.model.factory.FactoryService;
import de.yggdrasil128.factorial.model.factory.FactoryStandalone;
import de.yggdrasil128.factorial.model.game.Game;
import de.yggdrasil128.factorial.model.game.GameService;
import de.yggdrasil128.factorial.model.gameversion.GameVersion;
import de.yggdrasil128.factorial.model.gameversion.GameVersionService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

import static java.util.Collections.emptyList;

@Service
public class SaveService extends ModelService<Save, SaveRepository> {

    private final GameService games;
    private final GameVersionService gameVersions;
    private final FactoryService factories;
    private final ChangelistService changelists;

    public SaveService(SaveRepository repository, GameService games, GameVersionService gameVersions,
        FactoryService factories, ChangelistService changelists) {
        super(repository);
        this.games = games;
        this.gameVersions = gameVersions;
        this.factories = factories;
        this.changelists = changelists;
    }

    public Save create(int gameVersionId, SaveStandalone input) {
        GameVersion gameVersion = gameVersions.get(gameVersionId);
        return repository.save(new Save(gameVersion, input.getName(), emptyList(), emptyList(), emptyList()));
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

    public Changelist addChangelist(int saveId, ChangeListStandalone input) {
        Save save = get(saveId);
        Changelist changelist = changelists.create(save, input);
        save.getChangelists().add(changelist);
        repository.save(save);
        return changelist;
    }

    public Save doImport(SaveMigration input) {
        Game game = games.get(input.getGame());
        GameVersion version = gameVersions.get(game, input.getVersion());
        Save save = new Save(version, input.getName(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        for (FactoryMigration entry : input.getFactories()) {
            save.getFactories().add(factories.doImport(save, entry));
        }
        inferDefaultChangelist(save);
        repository.save(save);
        return save;
    }

    private static void inferDefaultChangelist(Save save) {
        if (!save.getChangelists().isEmpty()) {
            return;
        }
        Changelist primary = new Changelist();
        primary.setSave(save);
        primary.setName("default");
        primary.setPrimary(true);
        primary.setActive(true);
        save.getChangelists().add(primary);
    }

}
