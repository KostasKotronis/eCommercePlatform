package com.publicNext.eCommercePlatform.dto;

import java.util.List;

public record OrderResponse(
        Long id,
        String customerName,
        String status,
        String orderDate,
        List<OrderLineResponse> lines
) {}