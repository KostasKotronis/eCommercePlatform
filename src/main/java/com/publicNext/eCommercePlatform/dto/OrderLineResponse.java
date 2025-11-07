package com.publicNext.eCommercePlatform.dto;

import java.math.BigDecimal;

public record OrderLineResponse(
        Long id,
        Long productId,
        Integer quantity,
        BigDecimal price
) {}