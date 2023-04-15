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
// This class has been created to be able to validate each ShipDto object. Previously we were taking List<ShipDto> in our request body,
// and we placed @Valid annotation before @RequestBody (like @Valid @RequestBody List<ShipDto> ships) annotation in our controller,
// but it was not validating the values in ShipDto object, but just validating if there is a valid List or not. Now because we also put @Valid annotation on top of List<ShipDto> property of
// ShipDtoListWrapper class (besides @Valid @RequestBody ShipDtoListWrapper shipDtoListWrapper in our controller) , it validates each ShipDto objects. And because in ShipDto class we placed @NotNull and @NotEmpty annotations
// on top of shipType and shipLocations properties, request body has to have at least one ShipDto object with valid values.
public class ShipDtoListWrapper {

    @Valid
    @NotNull
    @NotEmpty
    private List<ShipDto> ships;
}
