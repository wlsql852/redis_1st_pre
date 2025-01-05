package org.example;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class BeforeOrderServiceJava {

    // 상품 DB
    private final Map<String, Integer> productDatabase = new ConcurrentHashMap<>();
    // 가장 최근 주문 정보를 저장하는 DB
    private final Map<String, OrderInfo> latestOrderDatabase = new HashMap<>();

    public BeforeOrderServiceJava() {
        // 초기 상품 데이터
        productDatabase.put("apple", 100);
        productDatabase.put("banana", 50);
        productDatabase.put("orange", 75);
    }

    // 주문 처리 메서드
    public void order(String customer, String productName, int amount) throws IllegalArgumentException {
        Integer currentStock = productDatabase.getOrDefault(productName, 0);

        try {
            Thread.sleep(1); // 동시성 이슈 유발을 위한 인위적 지연
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }

        if (currentStock >= amount) {
//            System.out.println("Current Thread : " + Thread.currentThread().getName() +
//                    " - CurrentStock : " + currentStock + " - Order : " + amount);
            log.info("Thread {} 주문자 : {} 주문정보 : {} 1 건 ([{}])", Thread.currentThread().getName(), customer, productName, amount);
            productDatabase.put(productName, currentStock - amount);
            latestOrderDatabase.put(customer, new OrderInfo(productName, amount, System.currentTimeMillis()));
        }
    }

    // 재고 조회
    public int getStock(String productName) {
        return productDatabase.getOrDefault(productName, 0);
    }

}

