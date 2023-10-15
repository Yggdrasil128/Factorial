package de.yggdrasil128.factorial.model.game;

import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.gameversion.GameVersion;
import org.springframework.stereotype.Service;

import static java.util.Collections.emptyList;

@Service
public class GameService extends ModelService<Game, GameRepository> {

    public GameService(GameRepository repository) {
        super(repository);
    }

    public Game create(GameInput input) {
        return repository.save(new Game(input.getName(), emptyList()));
    }

    public void addAttachedGameVersion(Game game, GameVersion gameVersion) {
        game.getGameVersions().add(gameVersion);
        repository.save(game);
    }

    public Game update(int id, GameInput input) {
        Game game = get(id);
        if (null != input.getName()) {
            game.setName(input.getName());
        }
        return repository.save(game);
    }

}
