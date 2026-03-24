package com.example.library_be.controller;

import com.example.library_be.dto.request.student.StudentImportRequest;
import com.example.library_be.dto.response.ApiResponse;
import com.example.library_be.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    // Import hàng loạt sinh viên
    @PostMapping("/import")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ApiResponse<String> importStudents(@RequestBody @Valid List<StudentImportRequest> requests) {
        studentService.importStudents(requests);
        return ApiResponse.success("Import sinh viên thành công");
    }

}
