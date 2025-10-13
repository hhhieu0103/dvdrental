package com.hieu.dvdrental.country;

import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

public interface CountryTestSuit {

    static Stream<Arguments> invalidNameProvider() {
        return Stream.of(
                Arguments.of("name", null, "Country name must not be blank"),
                Arguments.of("name", "", "Country name must not be blank"),
                Arguments.of("name", "   ", "Country name must not be blank"),
                Arguments.of("name", "a".repeat(51), "Country name must have less than 50 characters")
        );
    }

    @SuppressWarnings("unused")
    void shouldReturnAPageOnGetAll() throws Exception;

    @SuppressWarnings("unused")
    void shouldReturnAPageWithDefaultPaginationOnGetAll() throws Exception;

    @SuppressWarnings("unused")
    void shouldReturnAPageOnGetByName() throws Exception;

    @SuppressWarnings("unused")
    void shouldReturnAPageWithDefaultPaginationOnGetByName() throws Exception;

    @SuppressWarnings("unused")
    void shouldReturnCountryOnGetById() throws Exception;

    @SuppressWarnings("unused")
    void shouldRejectNonExistingCountryOnGetById() throws Exception;

    @SuppressWarnings("unused")
    void shouldCreateNewCountry() throws Exception;

    @SuppressWarnings("unused")
    void shouldRejectCountryWithExistingIdOnCreate() throws Exception;

    @SuppressWarnings("unused")
    void shouldRejectCountryWithInvalidNameOnCreate(String field, String fieldValue, String message) throws Exception;

    @SuppressWarnings("unused")
    void shouldUpdateCountry() throws Exception;

    @SuppressWarnings("unused")
    void shouldRejectCountryWithInvalidNameOnUpdate(String field, String fieldValue, String message) throws Exception;

    @SuppressWarnings("unused")
    void shouldRejectDifferentIdsOnUpdate() throws Exception;

    @SuppressWarnings("unused")
    void shouldRejectNonExistingIdOnUpdate() throws Exception;

    @SuppressWarnings("unused")
    void shouldDeleteCountry() throws Exception;

    @SuppressWarnings("unused")
    void shouldRejectNonExistingIdOnDelete() throws Exception;

    @SuppressWarnings("unused")
    void shouldRejectCountryWithExistingForeignKeyOnDelete() throws Exception;
}
