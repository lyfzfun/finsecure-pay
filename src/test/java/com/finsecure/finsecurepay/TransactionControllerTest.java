
package com.finsecure.finsecurepay;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void healthEndpointShouldReturnUp() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("FinSecure Pay"));
    }

    @Test
    void validTransactionShouldReturnSuccess() throws Exception {
        String request = """
                {
                    "transactionId": "TXN-2001",
                    "amount": 50.00,
                    "currency": "SGD",
                    "merchant": "Example Merchant"
                }
                """;

        mockMvc.perform(post("/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.transactionId").value("TXN-2001"));
    }

    @Test
    void zeroAmountShouldReturnBadRequest() throws Exception {
        String request = """
                {
                    "transactionId": "TXN-2002",
                    "amount": 0,
                    "currency": "SGD",
                    "merchant": "Example Merchant"
                }
                """;

        mockMvc.perform(post("/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }

    @Test
    void unsupportedCurrencyShouldReturnBadRequest() throws Exception {
        String request = """
                {
                    "transactionId": "TXN-2003",
                    "amount": 50.00,
                    "currency": "EUR",
                    "merchant": "Example Merchant"
                }
                """;

        mockMvc.perform(post("/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }
}
