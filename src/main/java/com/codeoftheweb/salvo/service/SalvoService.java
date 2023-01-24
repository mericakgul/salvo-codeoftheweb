package com.codeoftheweb.salvo.service;

import com.codeoftheweb.salvo.model.dto.LoginDto;
import com.codeoftheweb.salvo.model.entity.*;
import com.codeoftheweb.salvo.repository.GamePlayerRepository;
import com.codeoftheweb.salvo.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalvoService {

    private final GameRepository gameRepository;
    private final GamePlayerRepository gamePlayerRepository;
    private final AuthenticationManager authenticationManager;

    public List<Object> getGames() {
        List<Game> games = this.gameRepository.findAll();
        return games.stream()
                .map(this::makeGameMap)
                .collect(Collectors.toList());
    }

    public Map<String, Object> getGameView(Long gamePlayerId) {
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
        Map<String, Object> gameMap = new HashMap<>();
        gameMap.put("gameId", game.getId());
        gameMap.put("created", game.getCreationDate());
        gameMap.put("gamePlayers", this.makeMapOfGamePlayers(game.getGamePlayers()));
        return gameMap;
    }

    private List<Object> makeMapOfGamePlayers(Set<GamePlayer> gamePlayers) {
        return gamePlayers.stream()
                .map(gamePlayer -> {
                    Map<String, Object> gamePlayerMap = new HashMap<>();
                    gamePlayerMap.put("id", gamePlayer.getId());
                    gamePlayerMap.put("player", gamePlayer.getPlayer());
                    gamePlayerMap.put("score", this.gatherGamePlayerScore(gamePlayer));
                    return gamePlayerMap;
                })
                .collect(Collectors.toList());
    }

    private Double gatherGamePlayerScore(GamePlayer gamePlayer) {
        List<Score> scoresOfGame = gamePlayer.getGame().getScores();
        Long playerId = gamePlayer.getPlayer().getId();
        Optional<Score> scoreOfGamePlayer = scoresOfGame.stream()
                .filter(score -> Objects.equals(score.getPlayer().getId(), playerId))
                .findFirst();
        return scoreOfGamePlayer.map(Score::getScoreNumber).orElse(null);
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

    public String login(LoginDto loginDto) {
        this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));
        return "Login Success";
    }
}
