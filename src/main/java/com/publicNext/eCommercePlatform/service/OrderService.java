package com.publicNext.eCommercePlatform.service;

import com.publicNext.eCommercePlatform.entity.Order;
import com.publicNext.eCommercePlatform.repository.OrderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class OrderService {
    public final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }


    public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
    }

    public Order create(Order order) {
        if (order.getLines() != null) {
            order.getLines().forEach(l -> l.setOrder(order)); // δέσε back-reference
        }
        return orderRepository.save(order);
    }

    public Order update(Long id, Order updatedOrder) {
        Order existing = findById(id);

        existing.setCustomerName(updatedOrder.getCustomerName());
        existing.setStatus(updatedOrder.getStatus());

        existing.getLines().clear();

        if (updatedOrder.getLines() != null) {
            updatedOrder.getLines().forEach(line -> {
                line.setOrder(existing);
                existing.getLines().add(line);
            });
        }

        return orderRepository.save(existing);
    }

    public void delete(Long id) {
        orderRepository.deleteById(id);
    }

}
