package com.example.library_be.entity;

import com.example.library_be.entity.enums.StudentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Entity
@Table(
        name = "students",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_student_code", columnNames = "student_code")
        }
)
@DiscriminatorValue("STUDENT")
@Data
public class Student extends User {

    // MSSV
    @Column(name = "student_code", nullable = false, length = 20)
    @NotBlank
    private String studentCode;

    // Lớp
    @Column(name = "class_name", nullable = false, length = 50)
    @NotBlank
    private String className;

    // Khoa
    @Column(name = "faculty", nullable = false, length = 100)
    @NotBlank
    private String faculty;

    // Ngày sinh
    @Column(name = "date_of_birth", nullable = false)
    @NotNull
    private LocalDate dateOfBirth;

    // Giới tính
    @Column(name = "gender", length = 10)
    private String gender;

    // Khóa (vd: 2021)
    @Column(name = "course_year")
    private Integer courseYear;

    // SĐT
    @Column(name = "phone", length = 15)
    private String phone;

    // Địa chỉ
    @Column(name = "address", length = 255)
    private String address;

    // Trạng thái
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StudentStatus status;

    @PrePersist
    public void prePersistStudent() {
        if (this.status == null) {
            this.status = StudentStatus.ACTIVE;
        }
    }
}
