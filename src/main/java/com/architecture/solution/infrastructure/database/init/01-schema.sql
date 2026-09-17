CREATE DATABASE IF NOT EXISTS hire_me DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE hire_me;

CREATE TABLE `users` (
  `id` varchar(36) PRIMARY KEY,
  `email` varchar(255) UNIQUE NOT NULL,
  `username` varchar(255) UNIQUE NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `displayed_name` varchar(255),
  `created_at` timestamp NOT NULL,
  `created_by` varchar(36) NOT NULL,
  `last_modified_at` timestamp,
  `last_modified_by` varchar(36),
  `is_deleted` boolean NOT NULL DEFAULT false
);

CREATE TABLE `roles` (
  `id` varchar(36) PRIMARY KEY,
  `name` ENUM ('ADMIN', 'RECRUITER', 'CANDIDATE') UNIQUE NOT NULL,
  `created_at` timestamp NOT NULL,
  `created_by` varchar(36) NOT NULL,
  `last_modified_at` timestamp,
  `last_modified_by` varchar(36),
  `is_deleted` boolean NOT NULL DEFAULT false
);

CREATE TABLE `user_roles` (
  `id` varchar(36) PRIMARY KEY,
  `user_id` varchar(36) NOT NULL,
  `role_id` varchar(36) NOT NULL,
  `created_at` timestamp NOT NULL,
  `created_by` varchar(36) NOT NULL,
  `last_modified_at` timestamp,
  `last_modified_by` varchar(36),
  `is_deleted` boolean NOT NULL DEFAULT false
);

CREATE TABLE `recruiters` (
  `user_id` varchar(36) PRIMARY KEY,
  `company_name` varchar(255) NOT NULL,
  `created_at` timestamp NOT NULL,
  `created_by` varchar(36) NOT NULL,
  `last_modified_at` timestamp,
  `last_modified_by` varchar(36),
  `is_deleted` boolean NOT NULL DEFAULT false
);

CREATE TABLE `candidates` (
  `user_id` varchar(36) PRIMARY KEY,
  `bio` text,
  `cv_url` varchar(255),
  `phone` varchar(255),
  `created_at` timestamp NOT NULL,
  `created_by` varchar(36) NOT NULL,
  `last_modified_at` timestamp,
  `last_modified_by` varchar(36),
  `is_deleted` boolean NOT NULL DEFAULT false
);

CREATE TABLE `jobs` (
  `id` varchar(36) PRIMARY KEY,
  `title` varchar(255) NOT NULL,
  `description` text,
  `recruiter_id` varchar(36) NOT NULL,
  `salary_min` decimal(12,2),
  `salary_max` decimal(12,2),
  `status` ENUM ('OPEN', 'FILLED', 'CLOSED') NOT NULL DEFAULT 'OPEN',
  `job_type` ENUM ('PART_TIME', 'FULL_TIME') NOT NULL,
  `created_at` timestamp NOT NULL,
  `created_by` varchar(36) NOT NULL,
  `last_modified_at` timestamp,
  `last_modified_by` varchar(36),
  `is_deleted` boolean NOT NULL DEFAULT false,
  CHECK ((salary_min IS NULL AND salary_max IS NULL) OR (salary_min IS NOT NULL
        AND salary_max IS NOT NULL
        AND salary_min >= 0
        AND salary_min <= salary_max))
);

CREATE TABLE `job_applications` (
  `id` varchar(36) PRIMARY KEY,
  `candidate_id` varchar(36) NOT NULL,
  `job_id` varchar(36) NOT NULL,
  `status` ENUM ('PENDING', 'ACCEPTED', 'REJECTED') NOT NULL DEFAULT 'PENDING',
  `cv_url` varchar(255) NOT NULL,
  `created_at` timestamp NOT NULL,
  `created_by` varchar(36) NOT NULL,
  `last_modified_at` timestamp,
  `last_modified_by` varchar(36),
  `is_deleted` boolean NOT NULL DEFAULT false
);

CREATE TABLE `categories` (
  `id` varchar(36) PRIMARY KEY,
  `name` varchar(255) UNIQUE NOT NULL,
  `created_at` timestamp NOT NULL,
  `created_by` varchar(36) NOT NULL,
  `last_modified_at` timestamp,
  `last_modified_by` varchar(36),
  `is_deleted` boolean NOT NULL DEFAULT false
);

CREATE TABLE `job_categories` (
  `id` varchar(36) PRIMARY KEY,
  `job_id` varchar(36) NOT NULL,
  `category_id` varchar(36) NOT NULL,
  `created_at` timestamp NOT NULL,
  `created_by` varchar(36) NOT NULL,
  `last_modified_at` timestamp,
  `last_modified_by` varchar(36),
  `is_deleted` boolean NOT NULL DEFAULT false
);

CREATE UNIQUE INDEX `user_roles_index_0` ON `user_roles` (`user_id`, `role_id`);

CREATE UNIQUE INDEX `job_applications_index_1` ON `job_applications` (`job_id`, `candidate_id`);

CREATE UNIQUE INDEX `job_categories_index_2` ON `job_categories` (`job_id`, `category_id`);

ALTER TABLE `recruiters` ADD FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

ALTER TABLE `user_roles` ADD FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

ALTER TABLE `user_roles` ADD FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`);

ALTER TABLE `candidates` ADD FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

ALTER TABLE `jobs` ADD FOREIGN KEY (`recruiter_id`) REFERENCES `recruiters` (`user_id`);

ALTER TABLE `job_applications` ADD FOREIGN KEY (`job_id`) REFERENCES `jobs` (`id`);

ALTER TABLE `job_applications` ADD FOREIGN KEY (`candidate_id`) REFERENCES `candidates` (`user_id`);

ALTER TABLE `job_categories` ADD FOREIGN KEY (`job_id`) REFERENCES `jobs` (`id`);

ALTER TABLE `job_categories` ADD FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`);
