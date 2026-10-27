
package com.finsecure.finsecurepay;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping
public class TransactionController {

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of(
                "status", "UP",
                "service", "FinSecure Pay"
        );
    }

    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validate(
            @Valid @RequestBody TransactionRequest request) {

        return ResponseEntity.ok(Map.of(
                "valid", true,
                "message", "Transaction passed validation",
                "transactionId", request.transactionId()
        ));
    }
}
