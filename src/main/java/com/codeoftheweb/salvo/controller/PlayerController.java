package com.codeoftheweb.salvo.controller;

import com.codeoftheweb.salvo.model.dto.PlayerDto;
import com.codeoftheweb.salvo.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/player")
@RequiredArgsConstructor
public class PlayerController {
    private final PlayerService playerService;

    @GetMapping
    public List<PlayerDto> findAll() {
        return this.playerService.findAll();
    }

    @GetMapping("/{playerId}")
    public PlayerDto findById(@PathVariable("playerId") UUID uuid) {
        return this.playerService.findById(uuid);
    }

    @PostMapping
    public PlayerDto save(@Valid @RequestBody PlayerDto playerDTO) {
        return this.playerService.save(playerDTO);
    }

    @PutMapping
    public PlayerDto updateUserNameById(@Valid @RequestBody PlayerDto playerDto,
                                            @RequestParam UUID uuid){
        return this.playerService.updateUserNameById(playerDto, uuid);
    }

    @DeleteMapping("/{playerId}")
    public void deleteById(@PathVariable("playerId") UUID uuid){
        this.playerService.deleteById(uuid);
    }

}
