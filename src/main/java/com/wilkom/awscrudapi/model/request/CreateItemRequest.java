package com.wilkom.awscrudapi.model.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wilkom.awscrudapi.model.Item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonAutoDetect
public class CreateItemRequest {
    private String id;
    private String label;
    private Long price;

    @JsonIgnore
    public Item getItem() {
        return new Item(id, label, price);
    }
}
