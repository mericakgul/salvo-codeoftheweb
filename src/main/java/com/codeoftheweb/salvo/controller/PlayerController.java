package com.codeoftheweb.salvo.controller;

import com.codeoftheweb.salvo.model.dto.PlayerDto;
import com.codeoftheweb.salvo.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/player")
@RequiredArgsConstructor
public class PlayerController {
    private final PlayerService playerService;

    @GetMapping
    public ResponseEntity<List<PlayerDto>> findAll() {
        return ResponseEntity.ok(this.playerService.findAll());
    }

    @GetMapping("/{playerId}")
    public ResponseEntity<PlayerDto> findById(@PathVariable("playerId") UUID uuid) {
        return ResponseEntity.ok(this.playerService.findById(uuid));
    }

    @PostMapping
    public ResponseEntity<PlayerDto> save(@RequestBody PlayerDto playerDTO) {
        return ResponseEntity.ok(this.playerService.save(playerDTO));
    }

    @PutMapping
    public ResponseEntity<PlayerDto> updateUserNameById(@RequestBody PlayerDto playerDto,
                                            @RequestParam UUID uuid){
        return ResponseEntity.ok(this.playerService.updateUserNameById(playerDto, uuid));
    }

    @DeleteMapping("/{playerId}")
    public ResponseEntity<Void> deleteById(@PathVariable("playerId") UUID uuid){
        this.playerService.deleteById(uuid);
        return ResponseEntity.ok().build();
    }

}
