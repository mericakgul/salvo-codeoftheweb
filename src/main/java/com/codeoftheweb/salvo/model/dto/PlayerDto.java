package com.codeoftheweb.salvo.model.dto;

import com.codeoftheweb.salvo.model.entity.Player;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class PlayerDto {
    private UUID uuid;
    private String userName;
    public PlayerDto(Player player){
        this.uuid = player.getUuid();
        this.userName = player.getUserName();
    }
}
