package com.codeoftheweb.salvo.core.helper;

import com.codeoftheweb.salvo.model.entity.GamePlayer;
import com.codeoftheweb.salvo.repository.GamePlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@RequiredArgsConstructor
public class ObjectExistence {
    private final GamePlayerRepository gamePlayerRepository;

    public GamePlayer checkIfGamePlayerExistAndReturn(Long gamePlayerId){
        return this.gamePlayerRepository.findById(gamePlayerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no GamePlayer found with this id."));
    }
}
