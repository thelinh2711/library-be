package com.example.library_be.dto.request.student;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class StudentSearchRequest {

    private String keyword; // search email, fullName, studentCode
    private String className;
    private String faculty;

    @Min(0)
    private Integer page = 0;

    @Min(1)
    private Integer size = 20;

    private String sortBy = "fullName"; // default sort
    private String sortDir = "asc"; // asc hoặc desc
}