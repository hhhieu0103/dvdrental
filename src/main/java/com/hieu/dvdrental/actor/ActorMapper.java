package com.hieu.dvdrental.actor;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ActorMapper {

    ActorSummaryDto toSummaryDto(Actor actor);

    ActorDto toDto(Actor actor);

    List<ActorSummaryDto> toSummaryDtoList(List<Actor> actors);

    List<ActorDto> toDtoList(List<Actor> actors);

    Actor fromDto(ActorDto actorDto);

    Actor fromSummaryDto(ActorSummaryDto actorSummaryDto);
}
