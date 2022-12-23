package com.codeoftheweb.salvo.controller;

import com.codeoftheweb.salvo.model.dto.PlayerDto;
import com.codeoftheweb.salvo.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "http://localhost:63342/", maxAge = 3600)
@RestController
@RequestMapping("/api/player")
@RequiredArgsConstructor
public class PlayerController {
    private final PlayerService playerService;

    @GetMapping
    public List<PlayerDto> findAll() {
        return this.playerService.findAll();
    }

    @GetMapping("/{playerId}")
    public PlayerDto findById(@PathVariable("playerId") Long id) {
        return this.playerService.findById(id);
    }

    @PostMapping
    public PlayerDto save(@Valid @RequestBody PlayerDto playerDTO) {
        return this.playerService.save(playerDTO);
    }

    @PutMapping
    public PlayerDto updateUsernameById(@Valid @RequestBody PlayerDto playerDto,
                                            @RequestParam Long id){
        return this.playerService.updateUsernameById(playerDto, id);
    }

    @DeleteMapping("/{playerId}")
    public ResponseEntity<String> deleteById(@PathVariable("playerId") Long id){
        this.playerService.deleteById(id);
        return ResponseEntity.ok().body("The player with this id has been deleted: " + id);
    }

}
