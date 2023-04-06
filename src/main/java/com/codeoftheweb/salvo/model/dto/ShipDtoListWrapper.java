package com.codeoftheweb.salvo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShipDtoListWrapper {

    @Valid
    @NotNull
    @NotEmpty
    private List<ShipDto> shipDtoList;
}
