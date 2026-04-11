package com.example.library_be.mapper;

import com.example.library_be.dto.response.borrow.ReservationResponse;
import com.example.library_be.entity.BookReservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReservationMapper {

    @Mapping(target = "studentId",   source = "student.id")
    @Mapping(target = "studentName", source = "student.fullName")
    @Mapping(target = "bookId",      source = "book.id")
    @Mapping(target = "bookTitle",   source = "book.title")
    ReservationResponse toResponse(BookReservation reservation);
}
