package com.bookreview.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PasswordUtilTest {
    @Test
    void testHashAndMatchPassword() {
        PasswordUtil util = new PasswordUtil();
        String raw = "myPassword123";
        String hashed = util.hashPassword(raw);
        assertNotEquals(raw, hashed);
        assertTrue(util.matches(raw, hashed));
        assertFalse(util.matches("wrongPassword", hashed));
    }
}
