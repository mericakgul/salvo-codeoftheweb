package com.codeoftheweb.salvo.controller;

import com.codeoftheweb.salvo.model.dto.PlayerRequest;
import com.codeoftheweb.salvo.model.dto.PlayerResponse;
import com.codeoftheweb.salvo.service.SalvoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SalvoController {
    private final SalvoService salvoService;

    @GetMapping("/games")
    public Map<String, Object> getGames(Authentication authentication) {
        return this.salvoService.getGamesPageData(authentication);
    }

    @GetMapping("/game_view/{gamePlayerId}")
    public Map<String, Object> getGameView(@PathVariable Long gamePlayerId, Authentication authentication) {
        return this.salvoService.getGameView(gamePlayerId, authentication);
    }

    @PostMapping("/players")
    public PlayerResponse signUp(@Valid @RequestBody PlayerRequest playerRequest){
        return this.salvoService.signUp(playerRequest);
    }
}
