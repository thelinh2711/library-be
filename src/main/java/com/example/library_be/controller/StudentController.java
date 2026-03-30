package com.example.library_be.controller;

import com.example.library_be.dto.request.student.StudentCreateRequest;
import com.example.library_be.dto.request.student.StudentImportRequest;
import com.example.library_be.dto.request.student.StudentSearchRequest;
import com.example.library_be.dto.request.student.StudentUpdateRequest;
import com.example.library_be.dto.response.ApiResponse;
import com.example.library_be.dto.response.PageResponse;
import com.example.library_be.dto.response.student.StudentResponse;
import com.example.library_be.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ApiResponse<StudentResponse> create(@RequestBody @Valid StudentCreateRequest request) {
        return ApiResponse.success(studentService.create(request));
    }

    // ==================== Lấy sinh viên theo ID ====================
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ApiResponse<StudentResponse> getById(@PathVariable UUID id) {
        return ApiResponse.success(studentService.getById(id));
    }

    // ==================== Cập nhật sinh viên ====================
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ApiResponse<StudentResponse> update(
            @PathVariable UUID id,
            @RequestBody @Valid StudentUpdateRequest request
    ) {
        return ApiResponse.success(studentService.update(id, request));
    }

    // ==================== Xóa sinh viên ====================
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ApiResponse<String> delete(@PathVariable UUID id) {
        studentService.delete(id);
        return ApiResponse.success("Xóa sinh viên thành công");
    }

    // ==================== Search + Pagination ====================
    @GetMapping("/search")
    public ApiResponse<PageResponse<StudentResponse>> search(@Valid StudentSearchRequest request) {
        return ApiResponse.success(studentService.search(request));
    }

    @GetMapping("/faculties")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ApiResponse<List<String>> getFaculties() {
        return ApiResponse.success(studentService.getDistinctFaculties());
    }

    @GetMapping("/classes")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ApiResponse<List<String>> getClasses(
            @RequestParam(required = false) String faculty) {
        return ApiResponse.success(studentService.getDistinctClasses(faculty));
    }

}
