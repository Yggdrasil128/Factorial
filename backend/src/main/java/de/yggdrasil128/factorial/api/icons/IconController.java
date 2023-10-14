package de.yggdrasil128.factorial.api.icons;

import de.yggdrasil128.factorial.model.icon.Icon;
import de.yggdrasil128.factorial.model.icon.IconService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/icons")
public class IconController {

    private final IconService icons;

    @Autowired
    public IconController(IconService icons) {
        this.icons = icons;
    }

    @GetMapping
    public ResponseEntity<byte[]> getRawIcon(int id) {
        Icon icon = icons.get(id);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.CONTENT_TYPE, icon.getMimeType());
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, icon.getMimeType()).body(icon.getImageData());
    }

}
