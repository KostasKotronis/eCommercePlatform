package com.publicNext.eCommercePlatform.testutil;

import com.publicNext.eCommercePlatform.entity.Order;
import com.publicNext.eCommercePlatform.entity.OrderLine;

import java.math.BigDecimal;
import java.util.ArrayList;

public final class TestDataFactory {
    private TestDataFactory() {}

    public static Order newOrder(String customerName, String status, int linesCount) {
        Order order = new Order();
        order.setCustomerName(customerName);
        order.setStatus(status != null ? status : "unprocessed");
        order.setLines(new ArrayList<>());
        for (int i = 0; i < linesCount; i++) {
            OrderLine l = new OrderLine();
            l.setProductId(100L + i);
            l.setQuantity(1 + i);
            l.setPrice(new BigDecimal("9.90").add(BigDecimal.valueOf(i)));
            l.setOrder(order);
            order.getLines().add(l);
        }
        return order;
    }
}
