package com.bookreview.mapper;

import com.bookreview.model.Book;
import com.bookreview.dto.BookDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface BookMapper {
    BookMapper INSTANCE = Mappers.getMapper(BookMapper.class);
    BookDto toDto(Book book);
}
