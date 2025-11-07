package com.publicNext.eCommercePlatform.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record OrderLineRequest(
        @NotNull Long productId,
        @NotNull @Positive Integer quantity,
        @NotNull @DecimalMin("0.00") BigDecimal price
) {}