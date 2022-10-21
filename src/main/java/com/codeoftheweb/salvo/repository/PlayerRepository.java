package com.codeoftheweb.salvo.repository;

import com.codeoftheweb.salvo.model.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    Optional<Player> findByUserName(String username);

    Optional<Player> findByUuid(UUID personId);
}
