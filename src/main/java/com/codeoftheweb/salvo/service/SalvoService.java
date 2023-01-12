package com.codeoftheweb.salvo.service;

import com.codeoftheweb.salvo.model.entity.*;
import com.codeoftheweb.salvo.repository.GamePlayerRepository;
import com.codeoftheweb.salvo.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalvoService {

    private final GameRepository gameRepository;
    private final GamePlayerRepository gamePlayerRepository;

    public List<Object> getGames() {
        List<Game> games = this.gameRepository.findAll();
        return games.stream().map(this::makeGameMap).collect(Collectors.toList());
    }

    public Map<String, Object> getGameView(Long gamePlayerId){
        GamePlayer gamePlayer = this.gamePlayerRepository.findById(gamePlayerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no GamePlayer found with this id."));
        Game game = gamePlayer.getGame();
        Map<String, Object> mapOfGameView = this.makeGameMap(game);
        mapOfGameView.put("gamePlayerId", gamePlayerId);
        mapOfGameView.put("ships", this.createShipListOfPlayer(gamePlayer.getShips()));
        mapOfGameView.put("salvoes", this.createSalvoesOfGameForEachPlayer(gamePlayer));
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

    private List<Object> createShipListOfPlayer(Set<Ship> ships) {
        return ships.stream()
                .map(ship -> {
                    Map<String, Object> shipMap = new HashMap<>();
                    shipMap.put("shipType", ship.getShipType());
                    shipMap.put("shipLocations", ship.getShipLocations()
                            .stream()
                            .map(ShipLocation::getGridCell));
                    return shipMap;
                })
                .collect(Collectors.toList());
    }

    private Map<Long, Object> createSalvoesOfGameForEachPlayer(GamePlayer gamePlayerRequested) {
        Map<Long, Object> mapOfSalvoes = new HashMap<>();
        Set<GamePlayer> gamePlayers = gamePlayerRequested.getGame().getGamePlayers();
        gamePlayers.forEach(gamePlayer ->
                mapOfSalvoes.put(gamePlayer.getPlayer().getId(), this.createSalvoTurnsAndLocations(gamePlayer.getSalvoes())));
        return mapOfSalvoes;
    }

    private Map<Integer, Object> createSalvoTurnsAndLocations(Set<Salvo> salvoes) {
        Map<Integer, Object> mapOfLocations = new HashMap<>();
        salvoes.forEach(salvo ->
                mapOfLocations.put(salvo.getTurnNumber(), salvo.getSalvoLocations()
                        .stream()
                        .map(SalvoLocation::getGridCell)));
        return mapOfLocations;
    }
}
