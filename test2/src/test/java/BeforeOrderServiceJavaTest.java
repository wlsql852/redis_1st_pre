import org.example.BeforeOrderServiceJava;
import org.example.OrderInfo;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.jupiter.api.Assertions.*;

class BeforeOrderServiceJavaTest {

    private final BeforeOrderServiceJava service = new BeforeOrderServiceJava();


    @Test
    void testConcurrentOrdersCauseStockMismatch() throws InterruptedException {
        String productName = "apple";
        int initialStock = service.getStock(productName);
        //손님 5명
        String[] customers = {"April", "May","June", "July","December"};

        int orderAmount = 8;
        int threadCount = 100;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // 각 스레드에서 주문을 수행하는 작업 생성
        for (int i = 0; i < threadCount; i++) {
            executor.execute(() -> {

                try {
                    service.order(customers[(int) (Math.random() * 5)],productName, orderAmount);
                } finally {
                    latch.countDown(); // 작업 완료 후 카운트 감소

                }
            });
        }

        // 모든 스레드가 작업을 완료할 때까지 대기
        latch.await();
        executor.shutdown();



        // 최종 재고 값 확인
        int expectedStock = initialStock % orderAmount;
        int actualStock = service.getStock(productName);

        System.out.println("Expected Stock: " + expectedStock + ", Actual Stock: " + actualStock);

        // 동시성 이슈로 인해 재고가 맞지 않는 경우를 확인
        assertEquals(expectedStock, actualStock, "재고 불일치 발생!");
    }

    @Test
    //각 손님의 마지막 주문 내역이 latestOrderDatabase에 제대로 들어갔는지 확인
    void testLatestOrder() throws InterruptedException {
        String[] products = {"apple","banana", "orange"};
        String[] customers = {"April", "May","June", "July","December"};
        HashMap<String, String> lastOrder = new HashMap<>();
        boolean result = true;

        int threadCount = 20;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // 각 스레드에서 주문을 수행하는 작업 생성
        for (int i = 0; i < threadCount; i++) {
            executor.execute(() -> {
                try {
                    String customer = customers[(int) (Math.random() * 5)];
                    String product = products[(int) (Math.random() * 3)];
                    int amount = (int) (Math.random() * 10)+1;
                    service.order(customer,product,amount);
                    lastOrder.put(customer, product+amount);
                } finally {
                    latch.countDown(); // 작업 완료 후 카운트 감소
                }
            });
        }

        // 모든 스레드가 작업을 완료할 때까지 대기
        latch.await();
        executor.shutdown();



        for(String customer: customers) {
            OrderInfo order = service.getLastOrder(customer);
            System.out.println("주문자 : "+customer + "마지막 주문 내역 : " +order.getProductName()+" "+order.getAmount()+" 개");
            //실제 서비스속의 마지막 주문과 test를 위해 만든 lastOrder의 주문을 비교
            result = (order.getProductName() + order.getAmount()).equals(lastOrder.get(customer)) && result;
        }
        //모든 주문이 같다면 result는 true, 하나라도 틀리면 false
        assertTrue(result);
    }
}


