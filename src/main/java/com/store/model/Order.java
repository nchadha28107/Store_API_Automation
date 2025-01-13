package com.store.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Order {
    private int id;
    private String productId;
    private String userId;
    private Integer quantity;
    private Double totalAmount;
}