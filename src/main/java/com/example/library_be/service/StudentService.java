package com.example.library_be.service;

import com.example.library_be.dto.request.student.StudentCreateRequest;
import com.example.library_be.dto.request.student.StudentImportRequest;
import com.example.library_be.dto.request.student.StudentSearchRequest;
import com.example.library_be.dto.request.student.StudentUpdateRequest;
import com.example.library_be.dto.response.PageResponse;
import com.example.library_be.dto.response.student.StudentResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface StudentService {
    StudentResponse create(StudentCreateRequest request);

    StudentResponse getById(UUID id);

    PageResponse<StudentResponse> search(StudentSearchRequest request);

    StudentResponse update(UUID id, StudentUpdateRequest request);

    void delete(UUID id);
    void importStudents(List<@Valid StudentImportRequest> requests);

    List<String> getDistinctFaculties();
    List<String> getDistinctClasses(String faculty);
}
