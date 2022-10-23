package com.codeoftheweb.salvo.model.dto;

import com.codeoftheweb.salvo.model.entity.Player;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class PlayerDto {
    private UUID uuid;
    @NotNull
    @NotEmpty
    @Email
    private String userName;
    public PlayerDto(Player player){
        this.uuid = player.getUuid();
        this.userName = player.getUserName();
    }
}
