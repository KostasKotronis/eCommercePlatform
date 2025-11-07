package com.publicNext.eCommercePlatform.mapper;

import com.publicNext.eCommercePlatform.dto.*;
import com.publicNext.eCommercePlatform.entity.Order;
import com.publicNext.eCommercePlatform.entity.OrderLine;

import java.time.format.DateTimeFormatter;
import java.util.List;

public final class OrderMapper {
    private OrderMapper() {}
    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static OrderResponse toResponse(Order o) {
        return new OrderResponse(
                o.getId(),
                o.getCustomerName(),
                o.getStatus(),
                o.getOrderDate() != null ? o.getOrderDate().format(ISO) : null,
                o.getLines().stream()
                        .map(OrderMapper::toLineResponse)
                        .toList()
        );
    }

    private static OrderLineResponse toLineResponse(OrderLine l) {
        return new OrderLineResponse(
                l.getId(),
                l.getProductId(),
                l.getQuantity(),
                l.getPrice()
        );
    }

    public static Order toOrderEntity(OrderRequest req) {
        Order o = new Order();
        o.setCustomerName(req.customerName());
        o.setStatus("unprocessed");
        var lines = req.lines().stream()
                .map(OrderMapper::toOrderLineEntity)
                .toList();
        o.setLines(lines);
        lines.forEach(l -> l.setOrder(o));
        return o;
    }

    private static OrderLine toOrderLineEntity(OrderLineRequest r) {
        OrderLine l = new OrderLine();
        l.setProductId(r.productId());
        l.setQuantity(r.quantity());
        l.setPrice(r.price());
        return l;
    }
}