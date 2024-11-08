package de.yggdrasil128.factorial.controller;

import de.yggdrasil128.factorial.model.game.GameService;
import de.yggdrasil128.factorial.model.icon.Icon;
import de.yggdrasil128.factorial.model.icon.IconService;
import de.yggdrasil128.factorial.model.icon.IconStandalone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class IconController {

    private final GameService gameService;
    private final IconService iconService;

    @Autowired
    public IconController(GameService gameService, IconService iconService) {
        this.gameService = gameService;
        this.iconService = iconService;
    }

    @PostMapping("/game/icons")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void create(int gameId, @RequestBody IconStandalone input) {
        iconService.create(gameId, input);
    }

    @GetMapping("/game/icons")
    public List<IconStandalone> retrieveAll(int gameId) {
        return gameService.get(gameId).getIcons().stream().map(IconStandalone::of).toList();
    }

    @GetMapping("/icon")
    public IconStandalone retrieve(int iconId) {
        return IconStandalone.of(iconService.get(iconId));
    }

    /**
     * Retrieves the raw bytes for an {@link Icon}.
     * 
     * @param id the {@link Icon#getId() id} of the {@link Icon}
     * @return the raw image data in bytes
     */
    @GetMapping("/icon/raw")
    public ResponseEntity<byte[]> getRawIcon(int id) {
        Icon icon = iconService.get(id);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.CONTENT_TYPE, icon.getMimeType());
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, icon.getMimeType()).body(icon.getImageData());
    }

    @PatchMapping("/icon")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void update(int iconId, @RequestBody IconStandalone input) {
        iconService.update(iconId, input);
    }

    @DeleteMapping("/icon")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void delete(int iconId) {
        iconService.delete(iconId);
    }

}
