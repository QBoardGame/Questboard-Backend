// src/main/java/com/techienello/questboard/infrastructure/config/WebSocketConfig.java
package com.Questboard.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.Questboard.backend.infrastructure.GameEventWebSocketHandler;
import com.Questboard.backend.infrastructure.WebSocketAuthInterceptor;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Value("${frontend.url}")
    private String frontendUrl;

    private final GameEventWebSocketHandler gameEventWebSocketHandler;
    private final WebSocketAuthInterceptor webSocketAuthInterceptor;

    public WebSocketConfig(GameEventWebSocketHandler gameEventWebSocketHandler,
            WebSocketAuthInterceptor webSocketAuthInterceptor) {
        this.gameEventWebSocketHandler = gameEventWebSocketHandler;
        this.webSocketAuthInterceptor = webSocketAuthInterceptor;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(gameEventWebSocketHandler, "/ws/game-events")
                .setAllowedOrigins(frontendUrl, "http://localhost:3000", "https://www.overwolf-extension://ohicgomoikkmgfkfkgninjaeieikgnkalpldkcdf", "*") // In production, restrict this to your app's
                                                                         // environment
                .addInterceptors(webSocketAuthInterceptor);
    }

}