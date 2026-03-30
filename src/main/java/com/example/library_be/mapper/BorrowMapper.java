package com.example.library_be.mapper;

import com.example.library_be.dto.response.borrow.BorrowRecordResponse;
import com.example.library_be.entity.BorrowItem;
import com.example.library_be.entity.BorrowRecord;
import com.example.library_be.entity.Fine;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BorrowMapper {

    @Mapping(target = "studentId",    source = "student.id")
    @Mapping(target = "studentName",  source = "student.fullName")
    @Mapping(target = "reservationId", source = "reservation.id")
    @Mapping(target = "items",        source = "items")
    BorrowRecordResponse toResponse(BorrowRecord record);

    @Mapping(target = "bookId",    source = "book.id")
    @Mapping(target = "bookTitle", source = "book.title")
    @Mapping(target = "fines",     source = "fines")
    BorrowRecordResponse.BorrowItemResponse toItemResponse(BorrowItem item);

    @Mapping(target = "studentId",   source = "borrowItem.borrowRecord.student.id")
    @Mapping(target = "studentName", source = "borrowItem.borrowRecord.student.fullName")
    @Mapping(target = "studentCode", source = "borrowItem.borrowRecord.student.studentCode")
    @Mapping(target = "bookId",      source = "borrowItem.book.id")
    @Mapping(target = "bookTitle",   source = "borrowItem.book.title")
    BorrowRecordResponse.FineResponse toFineResponse(Fine fine);
}
