package com.example.library_be.mapper;

import com.example.library_be.dto.request.book.BookCreateRequest;
import com.example.library_be.dto.request.book.BookUpdateRequest;
import com.example.library_be.dto.response.author.AuthorInfoResponse;
import com.example.library_be.dto.response.book.BookDetailResponse;
import com.example.library_be.dto.response.book.BookResponse;
import com.example.library_be.dto.response.category.CategoryResponse;
import com.example.library_be.entity.Book;
import com.example.library_be.entity.BookAuthor;
import com.example.library_be.entity.BookCategory;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface BookMapper {

    // ===== CREATE =====
    Book toEntity(BookCreateRequest request);

    // ===== UPDATE =====
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "price", source = "price")
    @Mapping(target = "version", ignore = true)
    void update(@MappingTarget Book book, BookUpdateRequest request);

    // ===== RESPONSE =====
    BookResponse toResponse(Book book);

    // ===== DETAIL RESPONSE =====
    @Mapping(target = "authors", expression = "java(mapAuthors(book.getBookAuthors()))")
    @Mapping(target = "categories", expression = "java(mapCategories(book.getBookCategories()))")
    BookDetailResponse toDetailResponse(Book book);

    // ===== CUSTOM =====
    default List<AuthorInfoResponse> mapAuthors(Set<BookAuthor> bookAuthors) {
        if (bookAuthors == null) return List.of();

        return bookAuthors.stream()
                .map(ba -> AuthorInfoResponse.builder()
                        .id(ba.getAuthor().getId())
                        .name(ba.getAuthor().getName())
                        .role(ba.getRole())
                        .build())
                .toList();
    }

    @Named("mapCategories")
    default List<CategoryResponse> mapCategories(Set<BookCategory> bookCategories) {
        if (bookCategories == null) return List.of();

        return bookCategories.stream()
                .map(bc -> CategoryResponse.builder()
                        .id(bc.getCategory().getId())
                        .name(bc.getCategory().getName())
                        .description(bc.getCategory().getDescription())
                        .build())
                .toList();
    }
}
