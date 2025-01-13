package com.store.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Review {
    private int id;
    private String productId;
    private String userId;
    private Integer rating;
    private String comment;
}
