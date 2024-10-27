package de.yggdrasil128.factorial.controller;

import de.yggdrasil128.factorial.model.Exporter;
import de.yggdrasil128.factorial.model.OptionalInputField;
import de.yggdrasil128.factorial.model.RelationRepresentation;
import de.yggdrasil128.factorial.model.gameversion.GameVersion;
import de.yggdrasil128.factorial.model.gameversion.GameVersionService;
import de.yggdrasil128.factorial.model.gameversion.GameVersionStandalone;
import de.yggdrasil128.factorial.model.gameversion.GameVersionSummary;
import de.yggdrasil128.factorial.model.icon.IconService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class GameVersionController {

    private final GameVersionService gameVersionService;
    private final IconService iconService;

    @Autowired
    public GameVersionController(GameVersionService gameVersionService, IconService iconService) {
        this.gameVersionService = gameVersionService;
        this.iconService = iconService;
    }

    @PostMapping("/gameVersions")
    public GameVersionStandalone create(@RequestBody GameVersionStandalone input) {
        GameVersion gameVersion = new GameVersion(input);
        applyRelations(input, gameVersion);
        return new GameVersionStandalone(gameVersionService.create(gameVersion));
    }

    @GetMapping("/gameVersions")
    public List<GameVersionStandalone> retrieveAll() {
        return gameVersionService.stream().map(GameVersionStandalone::new).toList();
    }

    @GetMapping("/gameVersion")
    public GameVersionStandalone retrieve(int gameVersionId) {
        return new GameVersionStandalone(gameVersionService.get(gameVersionId));
    }

    @GetMapping("/gameVersion/summary")
    public GameVersionSummary retrieveSummary(int gameVersionId) {
        return Exporter.exportGameVersion(gameVersionService.get(gameVersionId), RelationRepresentation.ID);
    }

    @PatchMapping("/gameVersion")
    public GameVersionStandalone update(int gameVersionId, @RequestBody GameVersionStandalone input) {
        GameVersion gameVersion = gameVersionService.get(gameVersionId);
        applyBasics(input, gameVersion);
        applyRelations(input, gameVersion);
        return new GameVersionStandalone(gameVersionService.update(gameVersion));
    }

    private static void applyBasics(GameVersionStandalone input, GameVersion gameVersion) {
        OptionalInputField.of(input.getName()).apply(gameVersion::setName);
    }

    private void applyRelations(GameVersionStandalone input, GameVersion gameVersion) {
        OptionalInputField.ofId((int) input.getIconId(), iconService::get).apply(gameVersion::setIcon);
    }

    @DeleteMapping("/gameVersion")
    public void delete(int gameVersionId) {
        gameVersionService.delete(gameVersionId);
    }

}
