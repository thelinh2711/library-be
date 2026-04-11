package com.example.library_be.controller;

import com.example.library_be.dto.request.student.StudentCreateRequest;
import com.example.library_be.dto.request.student.StudentImportRequest;
import com.example.library_be.dto.request.student.StudentSearchRequest;
import com.example.library_be.dto.request.student.StudentUpdateRequest;
import com.example.library_be.dto.response.ApiResponse;
import com.example.library_be.dto.response.PageResponse;
import com.example.library_be.dto.response.student.StudentResponse;
import com.example.library_be.entity.User;
import com.example.library_be.security.CustomUserDetails;
import com.example.library_be.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Tag(name = "Student", description = "Quản lý sinh viên")
public class StudentController {

    private final StudentService studentService;

    @Operation(summary = "Import sinh viên", description = "Tạo danh sách sinh viên hàng loạt")
    @PostMapping("/import")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ApiResponse<String> importStudents(@RequestBody @Valid List<StudentImportRequest> requests) {
        studentService.importStudents(requests);
        return ApiResponse.success("Import sinh viên thành công");
    }

    @Operation(summary = "Tạo sinh viên", description = "Tạo mới sinh viên")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ApiResponse<StudentResponse> create(@RequestBody @Valid StudentCreateRequest request) {
        return ApiResponse.success(studentService.create(request));
    }

    @Operation(summary = "Chi tiết sinh viên", description = "Lấy thông tin sinh viên theo ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ApiResponse<StudentResponse> getById(@PathVariable UUID id) {
        return ApiResponse.success(studentService.getById(id));
    }

    @Operation(summary = "Cập nhật sinh viên", description = "Cập nhật thông tin sinh viên")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ApiResponse<StudentResponse> update(
            @PathVariable UUID id,
            @RequestBody @Valid StudentUpdateRequest request
    ) {
        return ApiResponse.success(studentService.update(id, request));
    }

    @Operation(summary = "Xóa sinh viên", description = "Xóa sinh viên theo ID (ADMIN, LIBRARIAN)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ApiResponse<String> delete(@PathVariable UUID id) {
        studentService.delete(id);
        return ApiResponse.success("Xóa sinh viên thành công");
    }

    @Operation(summary = "Tìm kiếm sinh viên", description = "Phân trang, sắp xếp, tìm kiếm sinh viên")
    @GetMapping("/search")
    public ApiResponse<PageResponse<StudentResponse>> search(@Valid StudentSearchRequest request) {
        return ApiResponse.success(studentService.search(request));
    }

    @Operation(summary = "Danh sách khoa", description = "Lấy danh sách các khoa")
    @GetMapping("/faculties")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ApiResponse<List<String>> getFaculties() {
        return ApiResponse.success(studentService.getDistinctFaculties());
    }

    @Operation(summary = "Danh sách lớp", description = "Lấy danh sách lớp theo khoa")
    @GetMapping("/classes")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ApiResponse<List<String>> getClasses(
            @RequestParam(required = false) String faculty) {
        return ApiResponse.success(studentService.getDistinctClasses(faculty));
    }

    @Operation(summary = "Thông tin cá nhân", description = "Sinh viên xem thông tin của chính mình")
    @GetMapping("/me")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<StudentResponse> getMyProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        return ApiResponse.success(studentService.getMyProfile(userDetails.getUserId()));
    }
}
