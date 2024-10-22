package de.yggdrasil128.factorial;

import de.yggdrasil128.factorial.websocket.WebSocket;
import de.yggdrasil128.factorial.websocket.WebSocket.Placeholder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedMethods("*");
    }

    @Bean
    public WebSocket webSocket() {
        return new Placeholder();
    }

}
