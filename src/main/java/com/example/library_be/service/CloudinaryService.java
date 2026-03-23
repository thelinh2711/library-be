package com.example.library_be.service;

import com.example.library_be.dto.response.CloudinaryResponse;
import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {

    /**
     * Upload ảnh lên Cloudinary, trả về URL ảnh.
     */
    CloudinaryResponse uploadImage(MultipartFile file);

    /**
     * Xóa ảnh trên Cloudinary theo publicId.
     */
    void deleteImage(String publicId);
}
