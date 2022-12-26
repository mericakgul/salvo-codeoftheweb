package com.codeoftheweb.salvo.controller;

import com.codeoftheweb.salvo.model.entity.Game;
import com.codeoftheweb.salvo.model.entity.GamePlayer;
import com.codeoftheweb.salvo.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SalvoController {

    private final GameRepository gameRepository;

    @GetMapping("/games")
    public List<Object> getGames() {
        List<Game> games = this.gameRepository.findAll();
        return games.stream().map(this::makeMap).collect(Collectors.toList());
    }

    private Map<String, Object> makeMap(Game game) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", game.getId());
        map.put("created", game.getCreationDate());
        map.put("gamePlayers", this.makeMapOfGamePlayers(game.getGamePlayers()));
        return map;
    }

    private List<Object> makeMapOfGamePlayers(Set<GamePlayer> gamePlayers) {
        return gamePlayers.stream()
                .map(gamePlayer -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", gamePlayer.getId());
                    map.put("player", gamePlayer.getPlayer());
                    return map;
                })
                .collect(Collectors.toList());
    }
}
