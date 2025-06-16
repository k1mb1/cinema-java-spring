package com.github.k1mb1.cinema_java_spring.mappers;

import com.github.k1mb1.cinema_java_spring.models.user.UserEntity;
import com.github.k1mb1.cinema_java_spring.models.user.UserRequestDto;
import com.github.k1mb1.cinema_java_spring.models.user.UserResponseDto;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    UserResponseDto toDto(UserEntity userEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createAt", ignore = true)
    @Mapping(target = "updateAt", ignore = true)
    @Mapping(target = "watchedMovies", ignore = true)
    UserEntity toEntity(UserRequestDto userRequestDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createAt", ignore = true)
    @Mapping(target = "updateAt", ignore = true)
    @Mapping(target = "watchedMovies", ignore = true)
    void partialUpdate(UserRequestDto userRequestDto, @MappingTarget UserEntity user);
}
