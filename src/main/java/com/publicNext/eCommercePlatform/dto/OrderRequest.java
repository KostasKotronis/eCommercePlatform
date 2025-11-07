package com.publicNext.eCommercePlatform.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

public record OrderRequest(
        @NotBlank String customerName,
        @NotEmpty List<@Valid OrderLineRequest> lines
) {}