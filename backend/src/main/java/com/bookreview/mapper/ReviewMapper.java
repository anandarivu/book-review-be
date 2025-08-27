package com.bookreview.mapper;

import com.bookreview.model.Review;
import com.bookreview.dto.ReviewDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import com.bookreview.repository.UserRepository;

@Mapper(componentModel = "spring")
public abstract class ReviewMapper {
    @Autowired
    protected UserRepository userRepository;

    @Mapping(source = "book.id", target = "bookId")
    @Mapping(source = "userId", target = "userId")
    @Mapping(target = "userUuid", expression = "java(getUserUuid(review.getUserId()))")
    public abstract ReviewDto toDto(Review review);

    public java.util.UUID getUserUuid(String userId) {
        return userRepository.findByUserId(userId)
            .map(com.bookreview.model.User::getId)
            .orElse(null);
    }

    public abstract Review toEntity(ReviewDto dto);
}
