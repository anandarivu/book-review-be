package com.bookreview.model;

import com.bookreview.audit.Auditable;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "reviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private java.util.UUID id;

    @Column(nullable = false, length = 128)
    private String title;

    @Column(nullable = false, length = 1024)
    private String reviewText;

    @Column(nullable = false)
    private Integer rating; // 1-5

    @Column(nullable = false)
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(nullable = false)
    private String userId; // The user who wrote the review

    @Column(nullable = false)
    private Boolean deleted = false;
}
