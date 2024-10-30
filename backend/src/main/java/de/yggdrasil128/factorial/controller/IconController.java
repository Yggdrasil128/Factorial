package de.yggdrasil128.factorial.controller;

import de.yggdrasil128.factorial.model.OptionalInputField;
import de.yggdrasil128.factorial.model.gameversion.GameVersion;
import de.yggdrasil128.factorial.model.gameversion.GameVersionService;
import de.yggdrasil128.factorial.model.icon.Icon;
import de.yggdrasil128.factorial.model.icon.IconService;
import de.yggdrasil128.factorial.model.icon.IconStandalone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class IconController {

    private final GameVersionService gameVersionService;
    private final IconService iconService;

    @Autowired
    public IconController(GameVersionService gameVersionService, IconService iconService) {
        this.gameVersionService = gameVersionService;
        this.iconService = iconService;
    }

    @PostMapping("/gameVersion/icons")
    public IconStandalone create(int gameVersionId, @RequestBody IconStandalone input) {
        GameVersion gameVersion = gameVersionService.get(gameVersionId);
        Icon icon = new Icon(gameVersion, input);
        icon = iconService.create(icon);
        gameVersionService.addAttachedIcon(gameVersion, icon);
        return IconStandalone.of(icon);
    }

    @GetMapping("/gameVersion/icons")
    public List<IconStandalone> retrieveAll(int gameVersionId) {
        return gameVersionService.get(gameVersionId).getIcons().stream().map(IconStandalone::of).toList();
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
    public IconStandalone update(int iconId, @RequestBody IconStandalone input) {
        Icon icon = iconService.get(iconId);
        applyBasics(input, icon);
        return IconStandalone.of(iconService.update(icon));
    }

    private static void applyBasics(IconStandalone input, Icon icon) {
        OptionalInputField.of(input.name()).apply(icon::setName);
        OptionalInputField.of(input.imageData()).apply(icon::setImageData);
        OptionalInputField.of(input.mimeType()).apply(icon::setMimeType);
        OptionalInputField.of(input.category()).apply(icon::setCategory);
    }

    @DeleteMapping("/icon")
    public void delete(int iconId) {
        iconService.delete(iconId);
    }

}
