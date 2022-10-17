package com.codeoftheweb.salvo.controller;

import com.codeoftheweb.salvo.model.dto.PlayerDTO;
import com.codeoftheweb.salvo.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/player")
@RequiredArgsConstructor
public class PlayerController {
    private final PlayerService playerService;

    @GetMapping
    public ResponseEntity<List<PlayerDTO>> findAll() {
        return ResponseEntity.ok(this.playerService.findAll());
    }

    @GetMapping("/{personId}")
    public ResponseEntity<PlayerDTO> findById(@PathVariable("personId") Long id) {
        return ResponseEntity.ok(this.playerService.findById(id));
    }
}
