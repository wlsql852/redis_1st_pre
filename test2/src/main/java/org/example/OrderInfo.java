package org.example;

import lombok.Getter;

@Getter
public class OrderInfo {
    private String productName;
    private Integer amount;
    private long Time;

    public OrderInfo(String productName, int amount, long l) {
        this.productName = productName;
        this.amount = amount;
        this.Time = l;
    }

}
