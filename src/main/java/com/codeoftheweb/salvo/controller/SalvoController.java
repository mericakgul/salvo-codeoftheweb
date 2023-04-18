package com.codeoftheweb.salvo.controller;

import com.codeoftheweb.salvo.model.dto.PlayerRequest;
import com.codeoftheweb.salvo.model.dto.PlayerResponse;
import com.codeoftheweb.salvo.model.dto.SalvoDto;
import com.codeoftheweb.salvo.model.dto.ShipDtoListWrapper;
import com.codeoftheweb.salvo.service.SalvoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
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
    public PlayerResponse signUp(@Valid @RequestBody PlayerRequest playerRequest) {
        return this.salvoService.signUp(playerRequest);
    }

    @PostMapping("/games")
    public ResponseEntity<Map<String, Long>> createGame(Authentication authentication) {
        return ResponseEntity.created(URI.create("/game_view/gamePlayerId")).body(this.salvoService.createGame(authentication));
    }

    @PostMapping("/game/{gameId}/players")
    public ResponseEntity<Map<String, Long>> joinGame(@PathVariable Long gameId, Authentication authentication) {
        return ResponseEntity.created(URI.create("game_view/gamePlayerId")).body(this.salvoService.joinGame(gameId, authentication));
    }

    @GetMapping("/games/players/{gamePlayerId}/ships")
    public ShipDtoListWrapper getShips(@PathVariable Long gamePlayerId, Authentication authentication){
        return this.salvoService.getShips(gamePlayerId, authentication);
    }

    @PostMapping("/games/players/{gamePlayerId}/ships")
    public ResponseEntity<Void> saveShips(@PathVariable Long gamePlayerId,
                                          @Valid @RequestBody ShipDtoListWrapper shipDtoListWrapper,
                                          Authentication authentication) {
        this.salvoService.saveShips(gamePlayerId, shipDtoListWrapper, authentication);
        return ResponseEntity.created(URI.create("game_view/gamePlayerId")).build();
    }

    @GetMapping("/games/players/{gamePlayerId}/salvoes")
    public List<SalvoDto> getOwnerSalvoes(@PathVariable Long gamePlayerId, Authentication authentication){
        return this.salvoService.getOwnerSalvoes(gamePlayerId, authentication);
    }

    @PostMapping("/games/players/{gamePlayerId}/salvoes")
    public ResponseEntity<Void> saveSalvoes(@PathVariable Long gamePlayerId,
                                            @Valid @RequestBody SalvoDto salvoDto,
                                            Authentication authentication){
        this.salvoService.saveSalvo(gamePlayerId, salvoDto, authentication);
        return ResponseEntity.created(URI.create("game_view/gamePlayerId")).build();
    }

    // Another way for creating games by using ResponseEntity.created()
//    @PostMapping("/games")
//    public ResponseEntity<Map<String, Long>> createGame(Authentication authentication){
//        Map<String, Long> responseBody = this.salvoService.createGame(authentication);
//        URI locationUri = getGameUri(responseBody.get("gpid"));
//        return ResponseEntity.created(locationUri).body(responseBody);
//    }
//
//    private URI getGameUri(Long gamePlayerId) {
//        return ServletUriComponentsBuilder.fromCurrentContextPath()
//                .path("/game_view/{id}")
//                .buildAndExpand(gamePlayerId)
//                .toUri();
//    }

}
