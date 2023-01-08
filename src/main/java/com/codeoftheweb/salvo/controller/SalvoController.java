package com.codeoftheweb.salvo.controller;

import com.codeoftheweb.salvo.model.entity.Game;
import com.codeoftheweb.salvo.model.entity.GamePlayer;
import com.codeoftheweb.salvo.model.entity.ShipLocation;
import com.codeoftheweb.salvo.model.entity.Ship;
import com.codeoftheweb.salvo.repository.GamePlayerRepository;
import com.codeoftheweb.salvo.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SalvoController {

    private final GameRepository gameRepository;
    private final GamePlayerRepository gamePlayerRepository;

    @GetMapping("/games")
    public List<Object> getGames() {
        List<Game> games = this.gameRepository.findAll();
        return games.stream().map(this::makeGameMap).collect(Collectors.toList());
    }

    @GetMapping("/game_view/{gamePlayerId}")
    public Map<String, Object> getGameView(@PathVariable Long gamePlayerId){
        GamePlayer gamePlayer = this.gamePlayerRepository.findById(gamePlayerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no GamePlayer found with this id."));
        Game game = gamePlayer.getGame();
        Map<String, Object> mapOfGameView = makeGameMap(game);
        mapOfGameView.put("gamePlayerId", gamePlayerId);
        mapOfGameView.put("ships", createShipListOfPlayer(gamePlayer.getShips()));
        return new TreeMap<>(mapOfGameView);
    }

    private Map<String, Object> makeGameMap(Game game) {
        Map<String, Object> map = new HashMap<>();
        map.put("gameId", game.getId());
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

    private List<Object> createShipListOfPlayer(Set<Ship> ships){
        return ships.stream()
                .map(ship -> {
                    Map<String, Object> shipMap = new HashMap<>();
                    shipMap.put("type", ship.getShipType());
                    shipMap.put("shipLocations", ship.getShipLocations()
                            .stream()
                            .map(ShipLocation::getGridCell));
                    return shipMap;
                })
                .collect(Collectors.toList());
    }
}
