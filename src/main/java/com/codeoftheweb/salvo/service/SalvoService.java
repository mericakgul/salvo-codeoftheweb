package com.codeoftheweb.salvo.service;

import com.codeoftheweb.salvo.core.util.ShipValidation;
import com.codeoftheweb.salvo.model.dto.PlayerRequest;
import com.codeoftheweb.salvo.model.dto.PlayerResponse;
import com.codeoftheweb.salvo.model.dto.ShipDto;
import com.codeoftheweb.salvo.model.dto.ShipDtoListWrapper;
import com.codeoftheweb.salvo.model.entity.*;
import com.codeoftheweb.salvo.repository.*;
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
    private final ShipRepository shipRepository;
    private final ShipLocationRepository shipLocationRepository;
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

    public Map<String, Object> getGameView(Long gamePlayerId, Authentication authentication) {
        GamePlayer gamePlayer = this.gamePlayerRepository.findById(gamePlayerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no GamePlayer found with this id."));

        boolean isPlayerAuthorized = authentication != null && this.isPlayerAuthenticatedForTheGame(gamePlayerId, authentication);
        if (isPlayerAuthorized) {
            Game game = gamePlayer.getGame();
            Map<String, Object> mapOfGameView = this.makeGameMap(game);
            mapOfGameView.put("gamePlayerId", gamePlayerId);
            mapOfGameView.put("ships", this.createShipListOfPlayer(gamePlayer.getShips()));
            mapOfGameView.put("salvoes", this.createSalvoesOfGameForEachPlayer(gamePlayer));
            return new TreeMap<>(mapOfGameView);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not allowed to see this games details.");
        }
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

    public Map<String, Long> createGame(Authentication authentication) {
        if (authentication != null) {
            Player authenticatedPlayer = this.getAuthenticatedUser(authentication);
            Game createdGame = new Game(new Date());
            Game savedGame = this.gameRepository.save(createdGame);
            return this.createGamePlayer(savedGame, authenticatedPlayer, createdGame.getCreationDate());
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You need to log in to create a game.");
        }
    }

    public Map<String, Long> joinGame(Long gameId, Authentication authentication) {
        if (authentication != null) {
            Player authenticatedPlayer = this.getAuthenticatedUser(authentication);
            Game gameRequestedToJoin = this.gameRepository.findById(gameId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "No such game with this id."));
            int playerNumberInTheGame = gameRequestedToJoin.getGamePlayers().size();
            if (playerNumberInTheGame > 1) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Game is full!");
            } else if (isPlayerAlreadyInTheGame(gameRequestedToJoin, authenticatedPlayer.getUsername())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are already in this game");
            } else {
                return this.createGamePlayer(gameRequestedToJoin, authenticatedPlayer, new Date());
            }
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You need to log in to join a game.");
        }
    }

    public ShipDtoListWrapper getShips(Long gamePlayerId, Authentication authentication) {
        GamePlayer gamePlayer = this.gamePlayerRepository.findById(gamePlayerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no GamePlayer found with this id."));
        boolean isPlayerAuthorizedToGetShips = authentication != null && this.isPlayerAuthenticatedForTheGame(gamePlayerId, authentication);
        if (!isPlayerAuthorizedToGetShips) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authorized to place ships.");
        } else {
            List<ShipDto> ships = this.createShipListOfPlayer(gamePlayer.getShips());
            return new ShipDtoListWrapper(ships);
        }
    }

    public void placeShips(Long gamePlayerId, ShipDtoListWrapper shipDtoListWrapper, Authentication authentication) {
        GamePlayer gamePlayer = this.gamePlayerRepository.findById(gamePlayerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no GamePlayer found with this id."));
        boolean isPlayerAuthorizedToPlaceShips = authentication != null && this.isPlayerAuthenticatedForTheGame(gamePlayerId, authentication);
        boolean tryingToPlaceExistingShip = isThereAnyShipAlreadyPlaced(shipDtoListWrapper, gamePlayer);
        if (!isPlayerAuthorizedToPlaceShips) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authorized to place ships.");
        }
        if (tryingToPlaceExistingShip) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "One or more of the ships that you try to place was already placed.");
        }
        try {
            ShipValidation.checkIfShipTypesAndLocationsValid(shipDtoListWrapper, gamePlayer);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        shipDtoListWrapper.getShips().forEach(shipDto -> {
            Ship savedShip = this.saveAndReturnShip(shipDto, gamePlayer);
            shipDto.getShipLocations()
                    .forEach(gridCell -> this.saveShipLocation(savedShip, gridCell));
        });
    }

    private Ship saveAndReturnShip(ShipDto shipDto, GamePlayer gamePlayer) {
        Ship ship = new Ship(shipDto.getShipType(), gamePlayer);
        return this.shipRepository.save(ship);
    }

    private void saveShipLocation(Ship ship, String gridCell) {
        ShipLocation shipLocation = new ShipLocation(ship, gridCell);
        this.shipLocationRepository.save(shipLocation);
    }

    private Map<String, Long> createGamePlayer(Game game, Player player, Date joinDate) {
        GamePlayer createdGamePlayer = new GamePlayer(game, player, joinDate);
        GamePlayer savedGamePlayer = this.gamePlayerRepository.save(createdGamePlayer);
        Long gpid = savedGamePlayer.getId();
        return Collections.singletonMap("gpid", gpid);
    }

    private Player getAuthenticatedUser(Authentication authentication) {
        return this.playerRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No user found with this username"));
    }

    private Boolean isPlayerAuthenticatedForTheGame(Long gamePlayerId, Authentication authentication) {
        Player authenticatedPlayer = this.getAuthenticatedUser(authentication);
        Set<GamePlayer> gamePlayersOfAuthPlayer = authenticatedPlayer.getGamePlayers();
        Set<Long> gamePlayerIdsOfAuthPlayer = gamePlayersOfAuthPlayer.stream()
                .map(GamePlayer::getId)
                .collect(Collectors.toSet());
        return gamePlayerIdsOfAuthPlayer.contains(gamePlayerId);
    }

    private Boolean isThereAnyShipAlreadyPlaced(ShipDtoListWrapper shipDtoListWrapper, GamePlayer gamePlayer) {
        List<String> shipTypesOfShipDtoList = ShipValidation.getShipTypesOfShipDtoList(shipDtoListWrapper);
        List<String> existingShipsOfGamePlayer = gamePlayer.getShips().stream()
                .map(Ship::getShipType)
                .map(String::toLowerCase)
                .toList();
        return shipTypesOfShipDtoList.stream()
                .anyMatch(existingShipsOfGamePlayer::contains);
    }

    private Boolean isPlayerAlreadyInTheGame(Game game, String loggedInPlayerUsername) {
        Set<GamePlayer> gamePlayersOfTheGame = game.getGamePlayers();
        GamePlayer[] gamePlayersOfTheGame_Array = gamePlayersOfTheGame.toArray(new GamePlayer[0]);
        String usernameOfPlayerAlreadyInGame = gamePlayersOfTheGame_Array[0].getPlayer().getUsername();
        return Objects.equals(usernameOfPlayerAlreadyInGame, loggedInPlayerUsername);
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

    private List<ShipDto> createShipListOfPlayer(Set<Ship> ships) {
        return ships.stream()
                .map(ship -> {
                    String shipType = ship.getShipType();
                    List<String> shipLocations = ship.getShipLocations()
                            .stream()
                            .map(ShipLocation::getGridCell)
                            .toList();
                    return new ShipDto(shipType, shipLocations);
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
