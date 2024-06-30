package com.libraryapp.mapper;

import com.libraryapp.config.MapperConfig;
import com.libraryapp.dto.BookRequestDto;
import com.libraryapp.dto.BookResponseDto;
import com.libraryapp.model.Book;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface BookMapper {

    BookResponseDto toDto(Book book);

    Book toEntity(BookRequestDto bookRequestDto);

    void updateBookFromDto(@MappingTarget Book book, BookRequestDto dto);
}
