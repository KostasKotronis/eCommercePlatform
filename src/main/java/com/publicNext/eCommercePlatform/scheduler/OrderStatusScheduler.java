package com.publicNext.eCommercePlatform.scheduler;

import com.publicNext.eCommercePlatform.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderStatusScheduler {
    private final OrderService orderService;

    public OrderStatusScheduler(OrderService orderService) {
        this.orderService = orderService;
    }

    @Scheduled(fixedRate = 60000)
    public void processUnprocessedOrders() {
        try {
            int updated = orderService.processOrders();
            if (updated > 0) {
                log.info("Scheduler processed {} unprocessed orders", updated);
            } else {
                log.debug("No unprocessed orders found");
            }
        } catch (Exception e) {
            log.error("Scheduler failed to process orders", e);
        }
    }
}