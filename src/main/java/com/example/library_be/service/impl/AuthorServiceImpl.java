package com.example.library_be.service.impl;

import com.example.library_be.dto.request.author.AuthorCreateRequest;
import com.example.library_be.dto.request.author.AuthorUpdateRequest;
import com.example.library_be.dto.response.author.AuthorResponse;
import com.example.library_be.entity.Author;
import com.example.library_be.exception.AppException;
import com.example.library_be.exception.ErrorCode;
import com.example.library_be.mapper.AuthorMapper;
import com.example.library_be.repository.AuthorRepository;
import com.example.library_be.service.AuthorService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;

    @Override
    public AuthorResponse create(AuthorCreateRequest request) {
        Author author = authorMapper.toEntity(request);
        return authorMapper.toResponse(authorRepository.save(author));
    }

    @Override
    public List<AuthorResponse> getAll() {
        return authorRepository.findAll()
                .stream()
                .map(authorMapper::toResponse)
                .toList();
    }

    @Override
    public AuthorResponse getById(UUID id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.AUTHOR_NOT_FOUND));

        return authorMapper.toResponse(author);
    }

    @Override
    public AuthorResponse update(UUID id, AuthorUpdateRequest request) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.AUTHOR_NOT_FOUND));

        authorMapper.update(author, request);

        return authorMapper.toResponse(authorRepository.save(author));
    }

    @Override
    public void delete(UUID id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.AUTHOR_NOT_FOUND));

        try {
            authorRepository.delete(author);
        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.AUTHOR_IN_USE);
        }
    }
}
