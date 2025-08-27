package com.bookreview.dto;

import lombok.Data;
import java.util.Set;
import java.util.UUID;

@Data
public class BookDto {
    private UUID id;
    private String title;
    private String author;
    private String description;
    private String coverImageUrl;
    private Set<String> genres;
    private Integer publishedYear;
    private Double averageRating;
}
