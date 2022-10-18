package com.codeoftheweb.salvo.helper;

import lombok.RequiredArgsConstructor;
//import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DTOMapper {

//    private final ModelMapper modelMapper;

//    public <S, T> T mapModel(S source, Class<T> target){
//        return this.modelMapper.map(source, target);
//    }

//    public <S, T> List<T> mapListModel(List<S> sourceList, Class<T> target){
//        List<T> mappedList = sourceList
//                .stream()
//                .map(source -> this.mapModel(source, target))
//                .collect(Collectors.toList());
//        return mappedList;
//    }
}
