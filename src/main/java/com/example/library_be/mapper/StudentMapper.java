package com.example.library_be.mapper;

import com.example.library_be.dto.request.student.StudentImportRequest;
import com.example.library_be.dto.request.student.StudentUpdateRequest;
import com.example.library_be.dto.response.student.StudentResponse;
import com.example.library_be.entity.Student;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface StudentMapper {

    Student toStudent(StudentImportRequest request);

    StudentResponse toResponse(Student student);

    void updateFromDto(StudentUpdateRequest request, @MappingTarget Student student);

}
