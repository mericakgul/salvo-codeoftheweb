package com.codeoftheweb.salvo.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class GameDto {

    @NotNull
    @NotEmpty
    private Long id;

    private Date creationDate;
}
