package com.example.library_be.dto.response.student;

import com.example.library_be.entity.enums.StudentStatus;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class StudentResponse {
    private UUID id;
    private String email;
    private String fullName;
    private String studentCode;
    private String className;
    private String faculty;
    private LocalDate dateOfBirth;
    private String gender;
    private Integer courseYear;
    private String phone;
    private String address;
    private StudentStatus status;
}
