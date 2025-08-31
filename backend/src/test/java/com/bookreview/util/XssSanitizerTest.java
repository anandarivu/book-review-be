package com.bookreview.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class XssSanitizerTest {
    @Test
    void testSanitizeRemovesScriptTags() {
        XssSanitizer sanitizer = new XssSanitizer();
        String input = "<script>alert('xss')</script><b>bold</b>";
        String sanitized = sanitizer.sanitize(input);
        assertFalse(sanitized.contains("<script>"));
        assertTrue(sanitized.contains("<b>bold</b>"));
    }

    @Test
    void testSanitizeAllowsBasicTags() {
        XssSanitizer sanitizer = new XssSanitizer();
        String input = "<b>bold</b> <i>italic</i> <u>underline</u>";
        String sanitized = sanitizer.sanitize(input);
        assertTrue(sanitized.contains("<b>bold</b>"));
        assertTrue(sanitized.contains("<i>italic</i>"));
        assertTrue(sanitized.contains("<u>underline</u>"));
    }

    @Test
    void testSanitizeNullOrEmpty() {
        XssSanitizer sanitizer = new XssSanitizer();
        assertEquals("", sanitizer.sanitize(null));
        assertEquals("", sanitizer.sanitize(""));
    }
}
