package com.example.library_be.service.impl;

import com.example.library_be.dto.request.student.StudentCreateRequest;
import com.example.library_be.dto.request.student.StudentImportRequest;
import com.example.library_be.dto.request.student.StudentSearchRequest;
import com.example.library_be.dto.request.student.StudentUpdateRequest;
import com.example.library_be.dto.response.PageResponse;
import com.example.library_be.dto.response.student.StudentResponse;
import com.example.library_be.entity.Student;
import com.example.library_be.entity.enums.Role;
import com.example.library_be.entity.enums.StudentStatus;
import com.example.library_be.exception.AppException;
import com.example.library_be.exception.ErrorCode;
import com.example.library_be.mapper.StudentMapper;
import com.example.library_be.repository.StudentRepository;
import com.example.library_be.repository.UserRepository;
import com.example.library_be.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Validated
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final StudentMapper studentMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public void importStudents(List<StudentImportRequest> requests) {

        List<Student> students = new ArrayList<>();

        for (StudentImportRequest r : requests) {

            if (userRepository.existsByEmail(r.getEmail())) {
                continue;
            }

            if (studentRepository.existsByStudentCode(r.getStudentCode())) {
                continue;
            }

            Student student = studentMapper.toStudent(r);

            String rawPassword = r.getDateOfBirth()
                    .format(DateTimeFormatter.ofPattern("ddMMyyyy"));

            student.setPassword(passwordEncoder.encode(rawPassword));
            student.setRole(Role.STUDENT);

            students.add(student);
        }

        studentRepository.saveAll(students);
    }

    @Transactional
    @Override
    public StudentResponse create(StudentCreateRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.USER_EMAIL_EXIST);
        }
        if (studentRepository.existsByStudentCode(request.getStudentCode())) {
            throw new AppException(ErrorCode.STUDENT_CODE_EXIST);
        }

        Student student = studentMapper.toStudent(request);

        // Mật khẩu mặc định = ngày sinh ddMMyyyy
        String rawPassword = student.getDateOfBirth().format(DateTimeFormatter.ofPattern("ddMMyyyy"));
        student.setPassword(passwordEncoder.encode(rawPassword));
        student.setRole(Role.STUDENT);
        if (student.getStatus() == null) {
            student.setStatus(StudentStatus.ACTIVE);
        }

        studentRepository.save(student);
        return studentMapper.toResponse(student);
    }

    @Override
    public StudentResponse getById(UUID id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));
        return studentMapper.toResponse(student);
    }

    @Override
    public PageResponse<StudentResponse> search(StudentSearchRequest request) {

        Sort sort = Sort.by(request.getSortBy());
        sort = "desc".equalsIgnoreCase(request.getSortDir())
                ? sort.descending()
                : sort.ascending();

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        // normalize dữ liệu
        String keyword   = (request.getKeyword() == null || request.getKeyword().isBlank()) ? null : request.getKeyword();
        String className = (request.getClassName() == null || request.getClassName().isBlank()) ? null : request.getClassName();
        String faculty   = (request.getFaculty() == null || request.getFaculty().isBlank()) ? null : request.getFaculty();

        Page<Student> page = studentRepository.searchStudents(keyword, className, faculty, pageable);

        return PageResponse.from(page.map(studentMapper::toResponse));
    }

    @Transactional
    @Override
    public StudentResponse update(UUID id, StudentUpdateRequest request) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));
        studentMapper.updateFromDto(request, student);
        return studentMapper.toResponse(student);
    }

    @Transactional
    @Override
    public void delete(UUID id) {
        if (!studentRepository.existsById(id)) {
            throw new AppException(ErrorCode.STUDENT_NOT_FOUND);
        }
        studentRepository.deleteById(id);
    }

    public List<String> getDistinctFaculties() {
        return studentRepository.findDistinctFaculties();
    }

    public List<String> getDistinctClasses(String faculty) {
        if (faculty == null || faculty.isBlank())
            return studentRepository.findDistinctClasses();
        return studentRepository.findDistinctClassesByFaculty(faculty);
    }
}