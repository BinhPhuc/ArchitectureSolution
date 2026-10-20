CREATE DATABASE IF NOT EXISTS ecommerce DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE ecommerce;

CREATE TABLE `users` (
  `id` varchar(36) PRIMARY KEY,
  `email` varchar(255) NOT NULL UNIQUE,
  `username` varchar(255) NOT NULL UNIQUE,
  `password_hash` varchar(255) NOT NULL,
  `displayed_name` varchar(255),
  `refresh_token` varchar(255) UNIQUE,
  `created_at` timestamp(6) NOT NULL,
  `created_by` varchar(255) NOT NULL,
  `last_modified_at` timestamp(6) NULL,
  `last_modified_by` varchar(255),
  `is_deleted` boolean DEFAULT false
);

CREATE TABLE `roles` (
  `id` varchar(36) PRIMARY KEY,
  `name` enum ('ADMIN', 'SELLER', 'CUSTOMER') NOT NULL UNIQUE,
  `created_at` timestamp(6) NOT NULL,
  `created_by` varchar(255) NOT NULL,
  `last_modified_at` timestamp(6) NULL,
  `last_modified_by` varchar(255),
  `is_deleted` boolean DEFAULT false
);

CREATE TABLE `user_roles` (
  `id` varchar(36) PRIMARY KEY,
  `user_id` varchar(36) NOT NULL,
  `role_id` varchar(36) NOT NULL,
  `created_at` timestamp(6) NOT NULL,
  `created_by` varchar(255) NOT NULL,
  `last_modified_at` timestamp(6) NULL,
  `last_modified_by` varchar(255),
  `is_deleted` boolean DEFAULT false,
  UNIQUE KEY `uk_user_roles` (`user_id`, `role_id`)
);

CREATE TABLE `customers` (
  `user_id` varchar(36) PRIMARY KEY,
  `phone` varchar(255),
  `shipping_address` varchar(255),
  `created_at` timestamp(6) NOT NULL,
  `created_by` varchar(255) NOT NULL,
  `last_modified_at` timestamp(6) NULL,
  `last_modified_by` varchar(255),
  `is_deleted` boolean DEFAULT false
);

CREATE TABLE `sellers` (
  `user_id` varchar(36) PRIMARY KEY,
  `shop_name` varchar(255) NOT NULL,
  `created_at` timestamp(6) NOT NULL,
  `created_by` varchar(255) NOT NULL,
  `last_modified_at` timestamp(6) NULL,
  `last_modified_by` varchar(255),
  `is_deleted` boolean DEFAULT false
);

CREATE TABLE `files` (
  `id` varchar(36) PRIMARY KEY,
  `bucket` varchar(255) NOT NULL,
  `object_key` varchar(1024) NOT NULL,
  `original_filename` varchar(255) NOT NULL,
  `content_type` varchar(255) NOT NULL,
  `size_bytes` bigint NOT NULL,
  `status` enum ('PENDING', 'READY', 'FAILED', 'DELETING') NOT NULL DEFAULT 'PENDING',
  `owner_id` varchar(36) NOT NULL,
  `created_at` timestamp(6) NOT NULL,
  `created_by` varchar(255) NOT NULL,
  `last_modified_at` timestamp(6) NULL,
  `last_modified_by` varchar(255),
  `is_deleted` boolean DEFAULT false
);

CREATE TABLE `products` (
  `id` varchar(36) PRIMARY KEY,
  `name` varchar(255),
  `description` text,
  `seller_id` varchar(36),
  `status` enum ('DRAFT', 'ACTIVE', 'INACTIVE', 'OUT_OF_STOCK', 'ARCHIVED'),
  `price` decimal(19,2),
  `stock` bigint NOT NULL DEFAULT 0,
  `units_sold` bigint NOT NULL,
  `created_at` timestamp(6) NOT NULL,
  `created_by` varchar(255) NOT NULL,
  `last_modified_at` timestamp(6) NULL,
  `last_modified_by` varchar(255),
  `is_deleted` boolean DEFAULT false
);

CREATE TABLE `campaigns` (
  `id` varchar(36) PRIMARY KEY,
  `name` varchar(255),
  `description` text,
  `started_at` timestamp(6) NOT NULL,
  `ended_at` timestamp(6) NOT NULL,
  `created_at` timestamp(6) NOT NULL,
  `created_by` varchar(255) NOT NULL,
  `last_modified_at` timestamp(6) NULL,
  `last_modified_by` varchar(255),
  `is_deleted` boolean DEFAULT false
);

CREATE TABLE `campaign_items` (
  `id` varchar(36) PRIMARY KEY,
  `campaign_id` varchar(36),
  `product_id` varchar(36),
  `price` decimal(19,2),
  `stock` bigint NOT NULL,
  `sold_quantity` bigint NOT NULL,
  `created_at` timestamp(6) NOT NULL,
  `created_by` varchar(255) NOT NULL,
  `last_modified_at` timestamp(6) NULL,
  `last_modified_by` varchar(255),
  `is_deleted` boolean DEFAULT false,
  UNIQUE KEY `uk_flash_sale_items_product` (`campaign_id`, `product_id`)
);

CREATE TABLE `orders` (
  `id` varchar(36) PRIMARY KEY,
  `user_id` varchar(36),
  `total_amount` decimal(19,2),
  `status` enum ('PENDING', 'PREPARED', 'SHIPPED', 'DELIVERED', 'CANCELED'),
  `payment_method` enum ('COD', 'BANK_TRANSFER', 'CREDIT_CARD', 'E_WALLET'),
  `idempotency_key` varchar(36) UNIQUE,
  `created_at` timestamp(6) NOT NULL,
  `created_by` varchar(255) NOT NULL,
  `last_modified_at` timestamp(6) NULL,
  `last_modified_by` varchar(255),
  `is_deleted` boolean DEFAULT false
);

CREATE TABLE `order_items` (
  `id` varchar(36) PRIMARY KEY,
  `order_id` varchar(36),
  `product_id` varchar(36),
  `quantity` int NOT NULL,
  `created_at` timestamp(6) NOT NULL,
  `created_by` varchar(255) NOT NULL,
  `last_modified_at` timestamp(6) NULL,
  `last_modified_by` varchar(255),
  `is_deleted` boolean DEFAULT false
);

ALTER TABLE `user_roles` ADD CONSTRAINT `fk_user_roles_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

ALTER TABLE `user_roles` ADD CONSTRAINT `fk_user_roles_role_id` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`);

ALTER TABLE `customers` ADD CONSTRAINT `fk_customers_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

ALTER TABLE `sellers` ADD CONSTRAINT `fk_sellers_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

ALTER TABLE `products` ADD CONSTRAINT `fk_products_seller_id` FOREIGN KEY (`seller_id`) REFERENCES `sellers` (`user_id`);

ALTER TABLE `campaign_items` ADD CONSTRAINT `fk_campaign_items_campaign_id` FOREIGN KEY (`campaign_id`) REFERENCES `campaigns` (`id`);

ALTER TABLE `campaign_items` ADD CONSTRAINT `fk_campaign_items_product_id` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`);

ALTER TABLE `orders` ADD CONSTRAINT `fk_orders_user_id` FOREIGN KEY (`user_id`) REFERENCES `customers` (`user_id`);

ALTER TABLE `order_items` ADD CONSTRAINT `fk_order_items_order_id` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`);

ALTER TABLE `order_items` ADD CONSTRAINT `fk_order_items_product_id` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`);

ALTER TABLE `files` ADD CONSTRAINT `fk_files_owner_id` FOREIGN KEY (`owner_id`) REFERENCES `sellers` (`user_id`);
