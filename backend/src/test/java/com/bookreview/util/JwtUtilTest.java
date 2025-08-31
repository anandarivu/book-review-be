package com.bookreview.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Set;
import java.util.UUID;

public class JwtUtilTest {
    @Test
    void testGenerateAndParseToken() {
        JwtUtil util = new JwtUtil();
        String userId = "user1";
        UUID uuid = UUID.randomUUID();
        Set<String> roles = Set.of("USER", "ADMIN");
        String token = util.generateToken(userId, uuid, roles);
        assertNotNull(token);
        assertEquals(userId, util.getUserId(token));
        assertTrue(util.getRoles(token).contains("USER"));
        assertTrue(util.getRoles(token).contains("ADMIN"));
    }

    @Test
    void testGetRolesWithNoRoles() {
        JwtUtil util = new JwtUtil();
        String token = util.generateToken("user2", UUID.randomUUID(), null);
        assertTrue(util.getRoles(token).isEmpty());
    }
}
