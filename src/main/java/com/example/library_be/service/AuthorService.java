package com.example.library_be.service;

import com.example.library_be.dto.request.author.AuthorCreateRequest;
import com.example.library_be.dto.request.author.AuthorUpdateRequest;
import com.example.library_be.dto.response.author.AuthorResponse;

import java.util.List;
import java.util.UUID;

public interface AuthorService {

    AuthorResponse create(AuthorCreateRequest request);

    List<AuthorResponse> getAll();

    AuthorResponse getById(UUID id);

    AuthorResponse update(UUID id, AuthorUpdateRequest request);

    void delete(UUID id);
}
