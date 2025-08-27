package com.bookreview.dto;

import lombok.Data;
import java.util.Set;

@Data
public class BookRequest {
    private String title;
    private String author;
    private String description;
    private String coverImageUrl;
    private Set<String> genres;
    private Integer publishedYear;
}
