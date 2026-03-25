package com.example.library_be.service.impl;

import com.example.library_be.dto.request.book.BookCreateRequest;
import com.example.library_be.dto.request.book.BookSearchRequest;
import com.example.library_be.dto.request.book.BookUpdateRequest;
import com.example.library_be.dto.response.PageResponse;
import com.example.library_be.dto.response.book.BookDetailResponse;
import com.example.library_be.dto.response.book.BookResponse;
import com.example.library_be.entity.Book;
import com.example.library_be.entity.BookAuthor;
import com.example.library_be.entity.BookCategory;
import com.example.library_be.exception.AppException;
import com.example.library_be.exception.ErrorCode;
import com.example.library_be.mapper.BookMapper;
import com.example.library_be.repository.*;
import com.example.library_be.service.BookService;
import com.example.library_be.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    private final BookAuthorRepository bookAuthorRepository;
    private final BookCategoryRepository bookCategoryRepository;
    private final BookMapper bookMapper;
    private final CloudinaryService cloudinaryService;

    @Override
    public BookResponse create(BookCreateRequest request) {

        // map basic info
        Book book = bookMapper.toEntity(request);

        if (request.getAvailableQuantity() != null &&
                request.getAvailableQuantity() > request.getQuantity()) {
            throw new AppException(ErrorCode.INVALID_QUANTITY);
        }

        // upload ảnh nếu có
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            var upload = cloudinaryService.uploadImage(request.getImage());

            book.setImageUrl(upload.getUrl());
            book.setImagePublicId(upload.getPublicId());
        }

        book = bookRepository.save(book);

        // authors
        if (request.getAuthors() != null) {
            for (var item : request.getAuthors()) {
                var author = authorRepository.findById(item.getAuthorId())
                        .orElseThrow(() -> new AppException(ErrorCode.AUTHOR_NOT_FOUND));

                BookAuthor ba = new BookAuthor();
                ba.setBook(book);
                ba.setAuthor(author);
                ba.setRole(item.getRole());

                bookAuthorRepository.save(ba);
            }
        }

        // categories
        if (request.getCategoryIds() != null) {
            for (var cateId : request.getCategoryIds()) {
                var category = categoryRepository.findById(cateId)
                        .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

                BookCategory bc = new BookCategory();
                bc.setBook(book);
                bc.setCategory(category);

                bookCategoryRepository.save(bc);
            }
        }

        // load lại để có relations
        book = bookRepository.findById(book.getId()).get();

        return bookMapper.toResponse(book);
    }

    @Transactional
    @Override
    public BookResponse update(UUID id, BookUpdateRequest request) {

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));

        int quantity = request.getQuantity() != null
                ? request.getQuantity()
                : book.getQuantity();

        int available = request.getAvailableQuantity() != null
                ? request.getAvailableQuantity()
                : book.getAvailableQuantity();

        if (available > quantity) {
            throw new AppException(ErrorCode.INVALID_QUANTITY);
        }

        bookMapper.update(book, request);

        if (request.getImage() != null && !request.getImage().isEmpty()) {

            // xóa ảnh cũ nếu có
            if (book.getImagePublicId() != null) {
                cloudinaryService.deleteImage(book.getImagePublicId());
            }

            // upload ảnh mới
            var upload = cloudinaryService.uploadImage(request.getImage());

            book.setImageUrl(upload.getUrl());
            book.setImagePublicId(upload.getPublicId());
        }

        // clear bằng collection (tận dụng orphanRemoval)
        book.getBookAuthors().clear();
        book.getBookCategories().clear();

        // add authors
        if (request.getAuthors() != null) {
            for (var item : request.getAuthors()) {
                var author = authorRepository.findById(item.getAuthorId())
                        .orElseThrow(() -> new AppException(ErrorCode.AUTHOR_NOT_FOUND));

                BookAuthor ba = new BookAuthor();
                ba.setBook(book);
                ba.setAuthor(author);
                ba.setRole(item.getRole());

                book.getBookAuthors().add(ba);
            }
        }

        // add categories
        if (request.getCategoryIds() != null) {
            for (var cateId : request.getCategoryIds()) {
                var category = categoryRepository.findById(cateId)
                        .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

                BookCategory bc = new BookCategory();
                bc.setBook(book);
                bc.setCategory(category);

                book.getBookCategories().add(bc);
            }
        }

        return bookMapper.toResponse(book);
    }

    @Transactional
    @Override
    public void delete(UUID id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));

        bookAuthorRepository.deleteByBook(book);
        bookCategoryRepository.deleteByBook(book);

        bookRepository.delete(book);
    }

    @Override
    public PageResponse<BookResponse> search(BookSearchRequest request) {

        String keyword = request.getKeyword();
        if (keyword != null && keyword.trim().isEmpty()) {
            keyword = null;
        }

        Pageable pageable = PageRequest.of(
                request.getPage(),
                request.getSize(),
                Sort.by(request.getSort(), "title")
        );

        Page<Book> page = bookRepository.searchBooks(
                keyword,
                request.getCategory(),
                pageable
        );

        return PageResponse.<BookResponse>builder()
                .content(page.getContent().stream()
                        .map(bookMapper::toResponse)
                        .toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    @Override
    public BookDetailResponse getById(UUID id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));
        System.out.println(book.getBookAuthors().size());
        return bookMapper.toDetailResponse(book);
    }
}
