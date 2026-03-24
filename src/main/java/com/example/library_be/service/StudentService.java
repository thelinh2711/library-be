package com.example.library_be.service;

import com.example.library_be.dto.request.student.StudentCreateRequest;
import com.example.library_be.dto.request.student.StudentImportRequest;
import com.example.library_be.dto.request.student.StudentUpdateRequest;
import com.example.library_be.dto.response.student.StudentResponse;

import java.util.List;
import java.util.UUID;

public interface StudentService {
    StudentResponse create(StudentCreateRequest request);

    StudentResponse getById(UUID id);

    List<StudentResponse> getAll();

    StudentResponse update(UUID id, StudentUpdateRequest request);

    void delete(UUID id);
    void importStudents(List<StudentImportRequest> requests);
}
