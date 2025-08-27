package com.bookreview.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class ReviewDto {
    private UUID id;
    private UUID bookId;
    private String userId;
    private UUID userUuid;
    private String title;
    private String reviewText;
    private int rating;
    private String createdAt;
    private String updatedAt;
    private BookDto book;
}
