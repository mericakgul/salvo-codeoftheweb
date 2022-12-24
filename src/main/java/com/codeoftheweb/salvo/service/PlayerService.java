//package com.codeoftheweb.salvo.service;
//
//import com.codeoftheweb.salvo.core.helper.ObjectExistence;
//import com.codeoftheweb.salvo.model.dto.PlayerDto;
//import com.codeoftheweb.salvo.model.entity.Player;
//import com.codeoftheweb.salvo.repository.PlayerRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.server.ResponseStatusException;
//
//import java.util.Date;
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//public class PlayerService {
//    private final PlayerRepository playerRepository;
//    private final ObjectExistence objectExistence;
////    private final DTOMapper dtoMapper;
//
//    public List<PlayerDto> findAll() {
//        List<Player> playerList = this.playerRepository.findAll();
//        if (playerList.isEmpty()) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is not any Player yet.");
//        } else {
//            return playerList.stream()
//                    .map(PlayerDto::new)
//                    .collect(Collectors.toList());
////            return this.dtoMapper.mapListModel(playerList, PlayerDTO.class);
//        }
//    }
//
//    public PlayerDto findById(Long id) {
//        Player player = this.objectExistence.checkIfPlayerExistsAndReturn(id);
//        return new PlayerDto(player);
//        //return this.dtoMapper.mapModel(player, PlayerDTO.class);
//    }
//
//    @Transactional
//    public PlayerDto save(PlayerDto playerDto) {
//        Optional<Player> playerOptional = this.playerRepository.findByUsername(playerDto.getUsername());
//        if (playerOptional.isPresent()) {
//            throw new ResponseStatusException(HttpStatus.CONFLICT, "There is already a player with this username.");
//        } else {
//            Player newPlayer = new Player(playerDto);
//            Player playerSaved = this.playerRepository.save(newPlayer);
//            return new PlayerDto(playerSaved);
//        }
//    }
//
//    @Transactional
//    public PlayerDto updateUsernameById(PlayerDto playerDto, Long id) {
//        Player playerToBeUpdated = this.objectExistence.checkIfPlayerExistsAndReturn(id);
//        Optional<Player> playerPotentiallyExistWithSameUsername = this.playerRepository.findByUsername(playerDto.getUsername());
//        if (playerPotentiallyExistWithSameUsername.isPresent()) {
//            throw new ResponseStatusException(HttpStatus.CONFLICT, "There is already a player with this username.");
//        } else {
//            playerToBeUpdated.setUsername(playerDto.getUsername());
//            Player playerSaved = this.playerRepository.save(playerToBeUpdated);
//            return new PlayerDto(playerSaved);
//        }
//    }
//
//    @Transactional
//    public void deleteById(Long id) {
//        Player player = this.objectExistence.checkIfPlayerExistsAndReturn(id);
//        player.setDeletedDate(new Date());
//        this.playerRepository.save(player);
//    }
//}
