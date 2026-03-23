package com.example.library_be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CloudinaryResponse {
    private String url;
    private String publicId;
}
