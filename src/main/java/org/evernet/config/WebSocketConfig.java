package org.evernet.config;

import lombok.RequiredArgsConstructor;
import org.evernet.auth.WebSocketAuthInterceptor;
import org.evernet.controller.WebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final WebSocketAuthInterceptor webSocketAuthInterceptor;

    private final WebSocketHandler webSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketHandler, "/api/v1/ws")
                .addInterceptors(webSocketAuthInterceptor)
                .setAllowedOrigins("*");
    }
}