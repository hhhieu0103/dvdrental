package com.hieu.dvdrental.film;

import com.hieu.dvdrental.actor.ActorSummaryDto;
import java.util.List;

public class FilmDto extends FilmSummaryDto {
    private List<ActorSummaryDto> actors;

    public List<ActorSummaryDto> getActors() {
        return actors;
    }

    public void setActors(List<ActorSummaryDto> actors) {
        this.actors = actors;
    }
}
