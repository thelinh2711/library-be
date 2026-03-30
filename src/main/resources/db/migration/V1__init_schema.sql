-- MySQL dump 10.13  Distrib 8.0.38, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: library_db
-- ------------------------------------------------------
-- Server version	8.4.5

CREATE TABLE `users` (
  `type` varchar(31) COLLATE utf8mb4_unicode_ci NOT NULL,
  `id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `email` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `full_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `is_active` bit(1) NOT NULL,
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `role` enum('ADMIN','LIBRARIAN','STUDENT') COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `students` (
  `address` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `class_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `course_year` int DEFAULT NULL,
  `date_of_birth` date NOT NULL,
  `faculty` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `gender` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `phone` varchar(15) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` enum('ACTIVE','DROPPED_OUT','GRADUATED','SUSPENDED') COLLATE utf8mb4_unicode_ci NOT NULL,
  `student_code` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_student_code` (`student_code`),
  CONSTRAINT `FK7xqmtv7r2eb5axni3jm0a80su` FOREIGN KEY (`id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `authors` (
  `id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `bio` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `date_of_birth` date DEFAULT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `categories` (
  `id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKt8o6pivur7nn124jehx7cygw5` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `books` (
  `id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `available_quantity` int NOT NULL,
  `description` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `image_public_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `image_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `isbn` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `publish_year` int NOT NULL,
  `publisher` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `quantity` int NOT NULL,
  `title` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `price` decimal(12,2) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKkibbepcitr0a3cpk3rfr7nihn` (`isbn`),
  CONSTRAINT `books_chk_1` CHECK ((`available_quantity` >= 0)),
  CONSTRAINT `books_chk_2` CHECK ((`quantity` >= 0)),
  CONSTRAINT `books_chk_3` CHECK ((`price` >= 0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `fine_policies` (
  `id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `amount_per_day` decimal(12,2) DEFAULT NULL,
  `damage_level` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `day_from` int DEFAULT NULL,
  `day_to` int DEFAULT NULL,
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_active` bit(1) NOT NULL,
  `multiplier` decimal(5,2) DEFAULT NULL,
  `type` enum('DAMAGED','LATE','LOST') COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `book_reservations` (
  `id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `expired_at` datetime(6) DEFAULT NULL,
  `reserved_at` datetime(6) NOT NULL,
  `status` enum('CANCELLED','COMPLETED','CONFIRMED','EXPIRED','PENDING') COLLATE utf8mb4_unicode_ci NOT NULL,
  `book_id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `student_id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_reservation_student` (`student_id`),
  KEY `idx_reservation_book` (`book_id`),
  KEY `idx_reservation_status` (`status`),
  CONSTRAINT `FKaymfayeibqp9fvgd8w7e3fn0e` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`),
  CONSTRAINT `FKg5kli21h3rfndgsed1pkgnucc` FOREIGN KEY (`book_id`) REFERENCES `books` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `borrow_records` (
  `id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `borrowed_at` datetime(6) NOT NULL,
  `staff_note` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` enum('BORROWING','COMPLETED','OVERDUE') COLLATE utf8mb4_unicode_ci NOT NULL,
  `reservation_id` varchar(36) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `student_id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKcsis807ht19499lycnpyqd57g` (`reservation_id`),
  KEY `idx_borrow_record_student` (`student_id`),
  KEY `idx_borrow_record_status` (`status`),
  CONSTRAINT `FKb7jr4fqq1gw81357ssskimhcx` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`),
  CONSTRAINT `FKp8qk7d17or5awf3tqv6378210` FOREIGN KEY (`reservation_id`) REFERENCES `book_reservations` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `borrow_items` (
  `id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `due_date` date NOT NULL,
  `return_date` date DEFAULT NULL,
  `status` enum('BORROWING','DAMAGED','LOST','RETURNED') COLLATE utf8mb4_unicode_ci NOT NULL,
  `book_id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `borrow_record_id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_borrow_item_record` (`borrow_record_id`),
  KEY `idx_borrow_item_book` (`book_id`),
  KEY `idx_borrow_item_status` (`status`),
  CONSTRAINT `FKamlbtar04p4djb7nj5sywghhs` FOREIGN KEY (`book_id`) REFERENCES `books` (`id`),
  CONSTRAINT `FKeh8vl3hjdog2kbo8yw3c1fjy5` FOREIGN KEY (`borrow_record_id`) REFERENCES `borrow_records` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `fines` (
  `id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `amount` decimal(12,2) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `note` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `paid_at` datetime(6) DEFAULT NULL,
  `payment_status` enum('PAID','UNPAID') COLLATE utf8mb4_unicode_ci NOT NULL,
  `type` enum('DAMAGED','LATE','LOST') COLLATE utf8mb4_unicode_ci NOT NULL,
  `borrow_item_id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `fine_policy_id` varchar(36) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_fine_borrow_item` (`borrow_item_id`),
  KEY `idx_fine_payment_status` (`payment_status`),
  KEY `FKmxr0gajqxbtmdrh6dy5tue41m` (`fine_policy_id`),
  CONSTRAINT `FKmb66wxfxm621p9awhi2f7cpvn` FOREIGN KEY (`borrow_item_id`) REFERENCES `borrow_items` (`id`),
  CONSTRAINT `FKmxr0gajqxbtmdrh6dy5tue41m` FOREIGN KEY (`fine_policy_id`) REFERENCES `fine_policies` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `book_authors` (
  `id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `role` enum('CO_AUTHOR','MAIN_AUTHOR') COLLATE utf8mb4_unicode_ci NOT NULL,
  `author_id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `book_id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKo86065vktj3hy1m7syr9cn7va` (`author_id`),
  KEY `FKbhqtkv2cndf10uhtknaqbyo0a` (`book_id`),
  CONSTRAINT `FKbhqtkv2cndf10uhtknaqbyo0a` FOREIGN KEY (`book_id`) REFERENCES `books` (`id`),
  CONSTRAINT `FKo86065vktj3hy1m7syr9cn7va` FOREIGN KEY (`author_id`) REFERENCES `authors` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `book_categories` (
  `id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `book_id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `category_id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK3k3ahp5vqlgmrr9swqqprmbxy` (`book_id`),
  KEY `FKrg2xlmc92mm2y5b1wmhd2g0y0` (`category_id`),
  CONSTRAINT `FK3k3ahp5vqlgmrr9swqqprmbxy` FOREIGN KEY (`book_id`) REFERENCES `books` (`id`),
  CONSTRAINT `FKrg2xlmc92mm2y5b1wmhd2g0y0` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;