package de.yggdrasil128.factorial.api.simple;

import de.yggdrasil128.factorial.model.gameversion.GameVersion;
import de.yggdrasil128.factorial.model.gameversion.GameVersionService;
import de.yggdrasil128.factorial.model.icon.Icon;
import de.yggdrasil128.factorial.model.icon.IconInput;
import de.yggdrasil128.factorial.model.icon.IconOutput;
import de.yggdrasil128.factorial.model.icon.IconService;
import de.yggdrasil128.factorial.model.save.SaveService;
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
    private final SaveService saveService;

    @Autowired
    public IconController(GameVersionService gameVersionService, IconService iconService, SaveService saveService) {
        this.gameVersionService = gameVersionService;
        this.iconService = iconService;
        this.saveService = saveService;
    }

    @PostMapping("/save/icons")
    public IconOutput createFomSave(int saveId, @RequestBody IconInput input) {
        return create(saveService.get(saveId).getGameVersion(), input);
    }

    @PostMapping("/gameVersion/icons")
    public IconOutput create(int gameVersionId, @RequestBody IconInput input) {
        return create(gameVersionService.get(gameVersionId), input);
    }

    private IconOutput create(GameVersion gameVersion, IconInput input) {
        Icon icon = iconService.create(gameVersion, input);
        gameVersionService.addAttachedIcon(gameVersion, icon);
        return IconOutput.of(icon);
    }

    @GetMapping("/save/icons")
    public List<IconOutput> retrieveAllFromSave(int saveId) {
        return retrieveAll(saveService.get(saveId).getGameVersion());
    }

    @GetMapping("/gameVersion/icons")
    public List<IconOutput> retrieveAll(int gameVersionId) {
        return retrieveAll(gameVersionService.get(gameVersionId));
    }

    private static List<IconOutput> retrieveAll(GameVersion gameVersion) {
        return gameVersion.getIcons().stream().map(IconOutput::of).toList();
    }

    @GetMapping("/icon")
    public IconOutput retrieve(int iconId) {
        return IconOutput.of(iconService.get(iconId));
    }

    // Exception: the raw data of the icon
    @GetMapping("/icons")
    public ResponseEntity<byte[]> getRawIcon(int id) {
        Icon icon = iconService.get(id);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.CONTENT_TYPE, icon.getMimeType());
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, icon.getMimeType()).body(icon.getImageData());
    }

    @PatchMapping("/icon")
    public IconOutput update(int iconId, @RequestBody IconInput input) {
        return IconOutput.of(iconService.update(iconId, input));
    }

    @DeleteMapping("/icon")
    public void delete(int iconId) {
        iconService.delete(iconId);
    }

}
