package com.codeoftheweb.salvo.controller;

import com.codeoftheweb.salvo.service.SalvoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SalvoController {
    private final SalvoService salvoService;

    @GetMapping("/games")
    public List<Object> getGames() {
        return this.salvoService.getGames();
    }

    @GetMapping("/game_view/{gamePlayerId}")
    public Map<String, Object> getGameView(@PathVariable Long gamePlayerId) {
        return this.salvoService.getGameView(gamePlayerId);
    }
}
