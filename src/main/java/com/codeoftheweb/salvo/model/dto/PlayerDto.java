package com.codeoftheweb.salvo.model.dto;

import com.codeoftheweb.salvo.model.entity.Player;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class PlayerDto {
    private Long id;
    @NotNull
    @NotEmpty
    @Email
    private String username;
    public PlayerDto(Player player){
        this.id = player.getId();
        this.username = player.getUsername();
    }
}
