package com.codeoftheweb.salvo.service;

import com.codeoftheweb.salvo.model.dto.PlayerDto;
import com.codeoftheweb.salvo.model.entity.Player;
import com.codeoftheweb.salvo.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;
//    private final DTOMapper dtoMapper;

    public List<PlayerDto> findAll() {
        List<Player> playerList = this.playerRepository.findAll();
        if (playerList.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is not any Player yet.");
        } else {
            return playerList.stream()
                    .map(PlayerDto::new)
                    .collect(Collectors.toList());
//            return this.dtoMapper.mapListModel(playerList, PlayerDTO.class);
        }
    }

    public PlayerDto findById(UUID uuid) {
        Optional<Player> playerOptional = this.playerRepository.findByUuid(uuid);
        if (playerOptional.isPresent()) {
            Player player = playerOptional.get();
            return new PlayerDto(player);
//            return this.dtoMapper.mapModel(player, PlayerDTO.class);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no Player with this id.");
        }
    }

    @Transactional
    public PlayerDto save(PlayerDto playerDto) {
        Optional<Player> playerOptional = this.playerRepository.findByUserName(playerDto.getUserName());
        Player newPlayer = new Player(playerDto);
        return trySavingPlayer(playerOptional, newPlayer);
    }

    @Transactional
    public PlayerDto updateUserNameById(PlayerDto playerDto, UUID uuid) {
        Optional<Player> playerToBeUpdatedOptional = this.playerRepository.findByUuid(uuid);
        Optional<Player> playerPotentiallyExistWithSameUserName = this.playerRepository.findByUserName(playerDto.getUserName());
        if (playerToBeUpdatedOptional.isPresent()) {
            Player playerToBeUpdated = playerToBeUpdatedOptional.get();
            playerToBeUpdated.setUserName(playerDto.getUserName());
            return trySavingPlayer(playerPotentiallyExistWithSameUserName, playerToBeUpdated);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no Player with this id to be updated.");
        }
    }

    @Transactional
    public void deleteById(UUID uuid) {
        Optional<Player> playerOptional = this.playerRepository.findByUuid(uuid);
        if (playerOptional.isPresent()) {
            Player player = playerOptional.get();
            player.setDeletedDate(new Date());
            this.playerRepository.save(player);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is not a player to delete with this id yet.");
        }
    }

    private PlayerDto trySavingPlayer(Optional<Player> playerOptional, Player playerToBeSaved) {
        if (playerOptional.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "There is already a player with this username.");
        } else {
            Player playerSaved = this.playerRepository.save(playerToBeSaved);
            return new PlayerDto(playerSaved);
        }
    }
}
