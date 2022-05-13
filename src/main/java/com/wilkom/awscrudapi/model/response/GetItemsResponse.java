package com.wilkom.awscrudapi.model.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.wilkom.awscrudapi.model.Item;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@JsonAutoDetect
public class GetItemsResponse {
    private final String lastEvaluatedKey;
    private final List<Item> items;
}
