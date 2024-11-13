package de.yggdrasil128.factorial.controller;

import de.yggdrasil128.factorial.model.AsyncHelper;
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
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api")
public class IconController {

    private final AsyncHelper asyncHelper;
    private final GameService gameService;
    private final IconService iconService;

    @Autowired
    public IconController(AsyncHelper asyncHelper, GameService gameService, IconService iconService) {
        this.asyncHelper = asyncHelper;
        this.gameService = gameService;
        this.iconService = iconService;
    }

    @PostMapping("/game/icons")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public CompletableFuture<Void> create(int gameId, @RequestBody List<IconStandalone> input) {
        return asyncHelper.submit(result -> iconService.create(gameId, input, result));
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
    public CompletableFuture<ResponseEntity<byte[]>> getRawIcon(int id) {
        return asyncHelper.submit(() -> iconService.getStandalone(id)).thenApply(IconController::rawBytesResponse);
    }

    private static ResponseEntity<byte[]> rawBytesResponse(IconStandalone icon) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.CONTENT_TYPE, icon.mimeType());
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, icon.mimeType()).body(icon.imageData());
    }

    @PatchMapping("/icons")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public CompletableFuture<Void> update(@RequestBody List<IconStandalone> input) {
        return asyncHelper.submit(result -> iconService.update(input, result));
    }

    @DeleteMapping("/icons")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public CompletableFuture<Void> delete(List<Integer> iconIds) {
        return asyncHelper.submit(result -> iconService.delete(iconIds, result));
    }

}
