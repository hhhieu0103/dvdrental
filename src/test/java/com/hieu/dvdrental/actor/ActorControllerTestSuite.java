package com.hieu.dvdrental.actor;

public interface ActorControllerTestSuite {

    public void shouldReturnActors() throws Exception;

    public void shouldReturnActorsWithDefaultPagination() throws Exception;

    public void shouldReturnMatchedNameActors() throws Exception;

    public void shouldReturnMatchedNameActorsWithDefaultPagination()  throws Exception;

    public void shouldReturnActorsFromFilm() throws Exception;

    public void shouldReturnActorsFromFilmWithDefaultPagination() throws Exception;

    public void shouldRejectNonExistedFilmId() throws Exception;

    public void shouldReturnActorWithExistedActorId() throws Exception;

    public void shouldRejectNonExistedActorId() throws Exception;

    public void shouldCreateNewActor() throws Exception;

    public void shouldRejectExistedIdOnCreate() throws Exception;

    public void shouldRejectInvalidStringsOnCreate(String fieldName, String invalidValue, String message) throws Exception;

    public void shouldUpdateActor() throws Exception;

    public void shouldRejectNonExistedIdOnUpdate() throws Exception;

    public void shouldRejectInvalidStringsOnUpdate(String fieldName, String invalidValue, String message) throws Exception;

    public void shouldDeleteExistedActor() throws Exception;

    public void shouldRejectedNonExistedIdOnDelete() throws Exception;

}
