package com.hieu.dvdrental;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponsePageImpl<T> extends PageImpl<T> {

    private List<T> content;
    private int number;
    private int size;
    private long totalElements;
    private JsonNode pageable;
    private boolean last;
    private int totalPages;
    private JsonNode sort;
    private boolean first;
    private int numberOfElements;
    private boolean empty;

    public ResponsePageImpl() {
        super(List.of());
    }

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public ResponsePageImpl(
            @JsonProperty("content") List<T> content,
            @JsonProperty("number") int number,
            @JsonProperty("size") int size,
            @JsonProperty("totalElements") long totalElements,
            @JsonProperty("pageable") JsonNode pageable,
            @JsonProperty("last") boolean last,
            @JsonProperty("totalPages") int totalPages,
            @JsonProperty("sort") JsonNode sort,
            @JsonProperty("first") boolean first,
            @JsonProperty("numberOfElements") int numberOfElements,
            @JsonProperty("empty") boolean empty
    ) {
        super(content, PageRequest.of(number, size), totalElements);
        this.content = content;
        this.number = number;
        this.size = size;
        this.totalElements = totalElements;
        this.pageable = pageable;
        this.last = last;
        this.totalPages = totalPages;
        this.sort = sort;
        this.first = first;
        this.numberOfElements = numberOfElements;
        this.empty = empty;
    }

    @Override
    public List<T> getContent() {
        return content;
    }

    @Override
    public int getNumber() {
        return number;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public long getTotalElements() {
        return totalElements;
    }

    public JsonNode getPageableAsJsonNode() {
        return pageable;
    }

    @Override
    public boolean isLast() {
        return last;
    }

    @Override
    public int getTotalPages() {
        return totalPages;
    }

    public JsonNode getSortAsJsonNode() {
        return sort;
    }

    @Override
    public boolean isFirst() {
        return first;
    }

    @Override
    public int getNumberOfElements() {
        return numberOfElements;
    }

    @Override
    public boolean isEmpty() {
        return empty;
    }
}
