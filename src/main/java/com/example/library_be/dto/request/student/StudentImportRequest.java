package com.example.library_be.dto.request.student;

import com.example.library_be.entity.enums.StudentStatus;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class StudentImportRequest {

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    @Size(max = 50, message = "Email tối đa 50 ký tự")
    private String email;

    @NotBlank(message = "Tên không được để trống")
    @Size(max = 100, message = "Tên tối đa 100 ký tự")
    private String fullName;

    @NotBlank(message = "MSSV không được để trống")
    @Size(max = 20, message = "MSSV tối đa 20 ký tự")
    private String studentCode;

    @NotBlank(message = "Lớp không được để trống")
    @Size(max = 50, message = "Lớp tối đa 50 ký tự")
    private String className;

    @NotBlank(message = "Khoa không được để trống")
    @Size(max = 100, message = "Khoa tối đa 100 ký tự")
    private String faculty;

    @NotNull(message = "Ngày sinh không được để trống")
    @Past(message = "Ngày sinh phải là ngày trong quá khứ")
    private LocalDate dateOfBirth;

    @Pattern(regexp = "Nam|Nữ|Khác", message = "Giới tính phải là 'Nam', 'Nữ' hoặc 'Khác'")
    private String gender;

    @Min(value = 2000, message = "Khóa phải từ 2000 trở đi")
    @Max(value = 2100, message = "Khóa phải nhỏ hơn 2100")
    private Integer courseYear;

    @Pattern(regexp = "\\d{9,15}", message = "SĐT chỉ được chứa số và từ 9 đến 15 chữ số")
    private String phone;

    @Size(max = 255, message = "Địa chỉ tối đa 255 ký tự")
    private String address;

    private StudentStatus status; // default ACTIVE nếu null trong logic service
}
