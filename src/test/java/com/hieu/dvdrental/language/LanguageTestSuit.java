package com.hieu.dvdrental.language;

import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

public interface LanguageTestSuit {

    static Stream<Arguments> invalidNameProvider() {
        return Stream.of(
                Arguments.of("name", null, "Language name must not be blank"),
                Arguments.of("name", "", "Language name must not be blank"),
                Arguments.of("name", "   ", "Language name must not be blank"),
                Arguments.of("name", "a".repeat(21), "Language name must has less than 20 characters")
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
    void shouldReturnLanguageOnGetById() throws Exception;

    @SuppressWarnings("unused")
    void shouldRejectNonExistingLanguageOnGetById() throws Exception;

    @SuppressWarnings("unused")
    void shouldCreateNewLanguage() throws Exception;

    @SuppressWarnings("unused")
    void shouldRejectLanguageWithExistingIdOnCreate() throws Exception;

    @SuppressWarnings("unused")
    void shouldRejectLanguageWithInvalidNameOnCreate(String field, String fieldValue, String message) throws Exception;

    @SuppressWarnings("unused")
    void shouldUpdateLanguage() throws Exception;

    @SuppressWarnings("unused")
    void shouldRejectLanguageWithInvalidNameOnUpdate(String field, String fieldValue, String message) throws Exception;

    @SuppressWarnings("unused")
    void shouldRejectDifferentIdsOnUpdate() throws Exception;

    @SuppressWarnings("unused")
    void shouldRejectNonExistingIdOnUpdate() throws Exception;

    @SuppressWarnings("unused")
    void shouldDeleteLanguage() throws Exception;

    @SuppressWarnings("unused")
    void shouldRejectNonExistingIdOnDelete() throws Exception;

    @SuppressWarnings("unused")
    void shouldRejectLanguageWithExistingForeignKeyOnDelete() throws Exception;
}
