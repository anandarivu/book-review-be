package com.bookreview.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class ReviewRequest {
    private String title;
    private String reviewText;
    private Integer rating;
    private UUID bookId;
}
