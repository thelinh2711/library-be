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
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;

    @Override
    public AuthorResponse create(AuthorCreateRequest request) {
        log.info("Creating new author with name={}", request.getName());

        Author author = authorMapper.toEntity(request);
        Author saved = authorRepository.save(author);

        log.info("Author created successfully with id={}", saved.getId());

        return authorMapper.toResponse(saved);
    }

    @Override
    public PageResponse<AuthorResponse> getAll(AuthorSearchRequest request) {
        log.info("Fetching authors page={}, size={}, keyword={}",
                request.getPage(), request.getSize(), request.getName());

        Pageable pageable = PageRequest.of(
                request.getPage(),
                request.getSize(),
                Sort.by(request.getSort(), "name")
        );

        Page<Author> page = authorRepository.findByNameContainingIgnoreCase(request.getName(), pageable);

        log.debug("Fetched {} authors", page.getTotalElements());

        return PageResponse.from(page.map(authorMapper::toResponse));
    }

    @Override
    public AuthorResponse getById(UUID id) {
        log.info("Fetching author by id={}", id);

        Author author = authorRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Author not found with id={}", id);
                    return new AppException(ErrorCode.AUTHOR_NOT_FOUND);
                });

        return authorMapper.toResponse(author);
    }

    @Override
    public AuthorResponse update(UUID id, AuthorUpdateRequest request) {
        log.info("Updating author id={}", id);

        Author author = authorRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Author not found for update id={}", id);
                    return new AppException(ErrorCode.AUTHOR_NOT_FOUND);
                });

        authorMapper.update(author, request);
        Author updated = authorRepository.save(author);

        log.info("Author updated successfully id={}", id);

        return authorMapper.toResponse(updated);
    }

    @Override
    public void delete(UUID id) {
        log.info("Deleting author id={}", id);

        Author author = authorRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Author not found for delete id={}", id);
                    return new AppException(ErrorCode.AUTHOR_NOT_FOUND);
                });

        try {
            authorRepository.delete(author);
            log.info("Author deleted successfully id={}", id);
        } catch (DataIntegrityViolationException e) {
            log.error("Cannot delete author id={} because it is in use", id);
            throw new AppException(ErrorCode.AUTHOR_IN_USE);
        }
    }
}
