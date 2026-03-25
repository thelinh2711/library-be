package com.example.library_be.dto.request.author;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Sort;

@Getter
@Setter
public class AuthorSearchRequest {
    private String name = "";

    @Min(0)
    private int page = 0;

    @Min(1)
    @Max(100)
    private int size = 10;

    private Sort.Direction sort = Sort.Direction.ASC;
}
