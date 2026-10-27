
package com.finsecure.finsecurepay;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

public record TransactionRequest(
        @NotBlank(message = "Transaction ID is required")
        String transactionId,

        @DecimalMin(value = "0.0", inclusive = false,
                message = "Amount must be greater than zero")
        @DecimalMax(value = "10000.0",
                message = "Amount must not exceed 10000")
        BigDecimal amount,

        @NotBlank(message = "Currency is required")
        @Pattern(regexp = "SGD|USD",
                message = "Currency must be SGD or USD")
        String currency,

        @NotBlank(message = "Merchant is required")
        String merchant
) {}
