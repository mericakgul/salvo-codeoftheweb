package com.codeoftheweb.salvo.service;

import com.codeoftheweb.salvo.model.dto.PlayerRequest;
import com.codeoftheweb.salvo.model.dto.PlayerResponse;
import com.codeoftheweb.salvo.model.entity.*;
import com.codeoftheweb.salvo.repository.GamePlayerRepository;
import com.codeoftheweb.salvo.repository.GameRepository;
import com.codeoftheweb.salvo.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalvoService {

    private final GameRepository gameRepository;
    private final GamePlayerRepository gamePlayerRepository;
    private final PlayerRepository playerRepository;
    private final PasswordEncoder passwordEncoder;

    public Map<String, Object> getGamesPageData(Authentication authentication) {
        Map<String, Object> mapOfGamesPage = new HashMap<>();
        Map<String, Object> mappedPlayer = new HashMap<>();
        if (authentication != null) {
            Player authenticatedPlayer = this.getAuthenticatedUser(authentication);
            mappedPlayer = this.makePlayerMap(authenticatedPlayer);
        }
        mapOfGamesPage.put("player", mappedPlayer);
        mapOfGamesPage.put("games", this.getGames());
        return mapOfGamesPage;
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

    public PlayerResponse signUp(PlayerRequest playerRequest) {
        try {
            playerRequest.setPassword(this.passwordEncoder.encode(playerRequest.getPassword()));
            Player player = new Player(playerRequest.getUsername(), playerRequest.getPassword());
            Player savedPlayer = this.playerRepository.save(player);
            return new PlayerResponse(savedPlayer.getUsername());
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "error: Name in use");
        }
    }

    private Player getAuthenticatedUser(Authentication authentication) {
        return this.playerRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No user found with this username"));
    }

    private List<Object> getGames() {
        List<Game> games = this.gameRepository.findAll();
        return games.stream()
                .map(this::makeGameMap)
                .collect(Collectors.toList());
    }

    private Map<String, Object> makeGameMap(Game game) {
        Map<String, Object> gameMap = new HashMap<>();
        gameMap.put("gameId", game.getId());
        gameMap.put("created", game.getCreationDate());
        gameMap.put("gamePlayers", this.makeMapOfGamePlayers(game.getGamePlayers()));
        return gameMap;
    }

    private List<Map<String, Object>> makeMapOfGamePlayers(Set<GamePlayer> gamePlayers) {
        return gamePlayers.stream()
                .map(gamePlayer -> {
                    Map<String, Object> gamePlayerMap = new HashMap<>();
                    gamePlayerMap.put("id", gamePlayer.getId());
                    gamePlayerMap.put("player", makePlayerMap(gamePlayer.getPlayer()));
                    gamePlayerMap.put("score", this.getGamePlayerScore(gamePlayer));
                    return gamePlayerMap;
                })
                .sorted(Comparator.comparingLong((Map<String, Object> gamePlayerMap) -> (Long) gamePlayerMap.get("id"))) //This sorted() method has been added to be able to send the gamePlayers sorted depending on their id's because the gamePlayer that has the smaller id means that the player in that gamePlayer is the creator of the game. We want to show the creator before the second player on frontend.
               // .sorted(Comparator.comparingLong((Map<String, Object> gamePlayerMap) -> (Long) gamePlayerMap.get("id")).reversed()) // This line is added for practice reason. It sorts in descending.
                .collect(Collectors.toList());
    }

    private Map<String, Object> makePlayerMap(Player player) {
        Map<String, Object> playerMap = new HashMap<>();
        playerMap.put("id", player.getId());
        playerMap.put("username", player.getUsername());
        return playerMap;
    }

    private Double getGamePlayerScore(GamePlayer gamePlayer) {
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
}
