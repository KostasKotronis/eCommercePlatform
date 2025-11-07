package com.publicNext.eCommercePlatform.controller;

import com.publicNext.eCommercePlatform.dto.OrderRequest;
import com.publicNext.eCommercePlatform.dto.OrderResponse;
import com.publicNext.eCommercePlatform.entity.Order;
import com.publicNext.eCommercePlatform.mapper.OrderMapper;
import com.publicNext.eCommercePlatform.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/{id}")
    public OrderResponse get(@PathVariable Long id) {
        return OrderMapper.toResponse(orderService.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse create(@Valid @RequestBody OrderRequest req) {
        Order saved = orderService.create(OrderMapper.toOrderEntity(req));
        return OrderMapper.toResponse(saved);
    }

    @PutMapping("/{id}")
    public OrderResponse update(@PathVariable Long id, @Valid @RequestBody OrderRequest req) {
        Order incoming = OrderMapper.toOrderEntity(req);
        Order current = orderService.findById(id);
        incoming.setStatus(current.getStatus());
        Order saved = orderService.update(id, incoming);
        return OrderMapper.toResponse(saved);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        orderService.delete(id);
    }
}
