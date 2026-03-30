package com.example.library_be.dto.request.fine;

import com.example.library_be.entity.enums.PaymentStatus;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

@Data
public class FineSearchRequest {
    private PaymentStatus status;
    private String keyword = "";
    private int page = 0;
    private int size = 20;
}
