package com.codeoftheweb.salvo.service;

import com.codeoftheweb.salvo.helper.DTOMapper;
import com.codeoftheweb.salvo.model.dto.PlayerDTO;
import com.codeoftheweb.salvo.model.entity.Player;
import com.codeoftheweb.salvo.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;
//    private final DTOMapper dtoMapper;

    public List<PlayerDTO> findAll() {
        List<Player> playerList = this.playerRepository.findAll();
        if(playerList.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is not any Person yet.");
        } else {
            List<PlayerDTO> playerDTOS = playerList.stream()
                    .map(PlayerDTO::new)
                    .collect(Collectors.toList());
            return playerDTOS;
//            return this.dtoMapper.mapListModel(playerList, PlayerDTO.class);
        }
    }

    public PlayerDTO findById(Long personId) {
        Optional<Player> playerOptional = this.playerRepository.findById(personId);
        if(playerOptional.isPresent()){
            Player player = playerOptional.get();
            return new PlayerDTO(player);
//            return this.dtoMapper.mapModel(player, PlayerDTO.class);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "There is no Player with this id.");
        }
    }

    public PlayerDTO save(PlayerDTO playerDTO) {
        Optional<Player> player = this.playerRepository.findByUserName(playerDTO.getUserName());
        if(player.isPresent()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "There is already a player with this username.");
        } else {
            Player newPlayer = new Player(playerDTO);
            Player savedPlayer = this.playerRepository.save(newPlayer);
            return new PlayerDTO(savedPlayer);
        }
    }
}
