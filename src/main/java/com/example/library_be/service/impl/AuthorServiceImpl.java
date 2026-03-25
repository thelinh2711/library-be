package com.example.library_be.service.impl;

import com.example.library_be.dto.request.author.AuthorCreateRequest;
import com.example.library_be.dto.request.author.AuthorSearchRequest;
import com.example.library_be.dto.request.author.AuthorUpdateRequest;
import com.example.library_be.dto.response.PageResponse;
import com.example.library_be.dto.response.author.AuthorResponse;
import com.example.library_be.entity.Author;
import com.example.library_be.exception.AppException;
import com.example.library_be.exception.ErrorCode;
import com.example.library_be.mapper.AuthorMapper;
import com.example.library_be.repository.AuthorRepository;
import com.example.library_be.service.AuthorService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public PageResponse<AuthorResponse> getAll(AuthorSearchRequest request) {
        Pageable pageable = PageRequest.of(
                request.getPage(),
                request.getSize(),
                Sort.by(request.getSort(), "name")
        );

        Page<Author> page = authorRepository.findByNameContainingIgnoreCase(request.getName(), pageable);

        return PageResponse.<AuthorResponse>builder()
                .content(page.getContent().stream()
                        .map(authorMapper::toResponse)
                        .toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
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
