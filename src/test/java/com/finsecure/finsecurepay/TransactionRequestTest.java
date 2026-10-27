
package com.finsecure.finsecurepay;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransactionRequestTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        factory.close();
    }

    @Test
    void validTransactionShouldPassValidation() {
        TransactionRequest request = new TransactionRequest(
                "TXN-1001",
                new BigDecimal("50.00"),
                "SGD",
                "Example Merchant"
        );

        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void zeroAmountShouldFailValidation() {
        TransactionRequest request = new TransactionRequest(
                "TXN-1002",
                BigDecimal.ZERO,
                "SGD",
                "Example Merchant"
        );

        assertEquals(1, validator.validate(request).size());
    }

    @Test
    void unsupportedCurrencyShouldFailValidation() {
        TransactionRequest request = new TransactionRequest(
                "TXN-1003",
                new BigDecimal("50.00"),
                "EUR",
                "Example Merchant"
        );

        assertEquals(1, validator.validate(request).size());
    }

    @Test
    void blankTransactionIdShouldFailValidation() {
        TransactionRequest request = new TransactionRequest(
                " ",
                new BigDecimal("50.00"),
                "SGD",
                "Example Merchant"
        );

        assertEquals(1, validator.validate(request).size());
    }
}

