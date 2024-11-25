package de.yggdrasil128.factorial.model.icon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Service
public class IconDownloader {

    private static final Logger LOG = LoggerFactory.getLogger(IconDownloader.class);

    private final RestTemplate httpClient = new RestTemplate();

    public void downloadIcon(Icon icon, IconStandalone input) {
        if (null != input.imageUrl()) {
            LOG.debug("Downloading image data for icon {} from {}", input.name(), input.imageUrl());
            RequestEntity<Void> request = RequestEntity.get(URI.create(input.imageUrl())).build();
            ResponseEntity<byte[]> response = httpClient.exchange(request, byte[].class);
            icon.setMimeType(response.getHeaders().getContentType().toString());
            icon.setImageData(response.getBody());
        }
    }

}
