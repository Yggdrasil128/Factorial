package de.yggdrasil128.factorial.model.game;

import static java.util.Collections.emptyList;

import java.util.ArrayList;
import java.util.Map;

import org.springframework.stereotype.Service;

import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.gameversion.GameVersion;
import de.yggdrasil128.factorial.model.gameversion.GameVersionMigration;
import de.yggdrasil128.factorial.model.gameversion.GameVersionService;
import de.yggdrasil128.factorial.model.gameversion.GameVersionStandalone;

@Service
public class GameService extends ModelService<Game, GameRepository> {

    private final GameVersionService gameVersions;

    public GameService(GameRepository repository, GameVersionService gameVersions) {
        super(repository);
        this.gameVersions = gameVersions;
    }

    public Game get(String name) {
        return repository.findByName(name).orElseThrow(ModelService::reportNotFound);
    }

    public Game create(GameStandalone input) {
        return repository.save(new Game(input.getName(), emptyList()));
    }

    public Game doImport(GameMigration input) {
        Game game = new Game(input.getName(), new ArrayList<>());
        for (Map.Entry<String, GameVersionMigration> entry : input.getVersions().entrySet()) {
            game.getVersions().add(gameVersions.doImport(game, entry.getKey(), entry.getValue()));
        }
        repository.save(game);
        return game;
    }

    public Game create(Game game) {
        return repository.save(game);
    }

    public void addVersion(Game game, GameVersion version) {
        game.getVersions().add(version);
        repository.save(game);
    }

    public GameVersion addGameVersion(int gameId, GameVersionStandalone input) {
        Game game = get(gameId);
        GameVersion gameVersion = gameVersions.create(game, input);
        game.getVersions().add(gameVersion);
        repository.save(game);
        return gameVersion;
    }

}
