package com.bookreview.util;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Component;

@Component
public class XssSanitizer {
    public String sanitize(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        return Jsoup.clean(input, Safelist.basic());
    }
}
