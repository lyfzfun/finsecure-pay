package com.finsecure.finsecurepay;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FailingPipelineTest {

    @Test
    void intentionalFailureForPipelineDemonstration() {
        assertTrue(false, "Intentional failure to verify Jenkins pipeline protection");
    }
}
