package com.codeoftheweb.salvo.core.helper;

import com.codeoftheweb.salvo.model.entity.Player;
import com.codeoftheweb.salvo.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@RequiredArgsConstructor
public class ObjectExistence {
    private final PlayerRepository playerRepository;

    public Player checkIfPlayerExistsAndReturn(Long id){
        return this.playerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no player found with this id."));
    }
}
