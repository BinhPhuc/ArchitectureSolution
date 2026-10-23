CREATE DATABASE IF NOT EXISTS ecommerce DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE ecommerce;

CREATE TABLE `users` (
  `id` varchar(36) PRIMARY KEY,
  `email` varchar(255) NOT NULL UNIQUE,
  `username` varchar(255) NOT NULL UNIQUE,
  `password_hash` varchar(255) NOT NULL,
  `displayed_name` varchar(255),
  `role` enum ('SELLER', 'CUSTOMER') NOT NULL,
  `refresh_token` varchar(255) UNIQUE,
  `created_at` timestamp(6) NOT NULL,
  `created_by` varchar(255) NOT NULL,
  `last_modified_at` timestamp(6) NULL,
  `last_modified_by` varchar(255)
);

CREATE TABLE `customers` (
  `user_id` varchar(36) PRIMARY KEY,
  `phone` varchar(255),
  `shipping_address` varchar(255),
  `created_at` timestamp(6) NOT NULL,
  `created_by` varchar(255) NOT NULL,
  `last_modified_at` timestamp(6) NULL,
  `last_modified_by` varchar(255)
);

CREATE TABLE `sellers` (
  `user_id` varchar(36) PRIMARY KEY,
  `shop_name` varchar(255) NOT NULL,
  `created_at` timestamp(6) NOT NULL,
  `created_by` varchar(255) NOT NULL,
  `last_modified_at` timestamp(6) NULL,
  `last_modified_by` varchar(255)
);

CREATE TABLE `files` (
  `id` varchar(36) PRIMARY KEY,
  `object_key` varchar(1024) NOT NULL,
  `original_filename` varchar(255) NOT NULL,
  `content_type` varchar(255) NOT NULL,
  `size_bytes` bigint NOT NULL,
  `owner_id` varchar(36) NOT NULL,
  `created_at` timestamp(6) NOT NULL,
  `created_by` varchar(255) NOT NULL,
  `last_modified_at` timestamp(6) NULL,
  `last_modified_by` varchar(255)
);

CREATE TABLE `campaigns` (
  `id` varchar(36) PRIMARY KEY,
  `name` varchar(255),
  `description` text,
  `start_at` timestamp(6) NOT NULL,
  `end_at` timestamp(6) NOT NULL,
  `seller_id` varchar(36),
  `created_at` timestamp(6) NOT NULL,
  `created_by` varchar(255) NOT NULL,
  `last_modified_at` timestamp(6) NULL,
  `last_modified_by` varchar(255)
);

CREATE TABLE `campaign_items` (
  `id` varchar(36) PRIMARY KEY,
  `campaign_id` varchar(36),
  `name` varchar(255),
  `thumbnail_id` varchar(36),
  `price` decimal(19,2),
  `stock` bigint NOT NULL,
  `created_at` timestamp(6) NOT NULL,
  `created_by` varchar(255) NOT NULL,
  `last_modified_at` timestamp(6) NULL,
  `last_modified_by` varchar(255)
);

CREATE TABLE `orders` (
  `id` varchar(36) PRIMARY KEY,
  `customer_id` varchar(36),
  `total_amount` decimal(19,2),
  `status` enum ('PENDING', 'PREPARED', 'SHIPPED', 'DELIVERED', 'CANCELED'),
  `created_at` timestamp(6) NOT NULL,
  `created_by` varchar(255) NOT NULL,
  `last_modified_at` timestamp(6) NULL,
  `last_modified_by` varchar(255)
);

CREATE TABLE `order_items` (
  `id` varchar(36) PRIMARY KEY,
  `order_id` varchar(36),
  `campaign_item_id` varchar(36),
  `quantity` int NOT NULL,
  `created_at` timestamp(6) NOT NULL,
  `created_by` varchar(255) NOT NULL,
  `last_modified_at` timestamp(6) NULL,
  `last_modified_by` varchar(255)
);

ALTER TABLE `customers` ADD CONSTRAINT `fk_customers_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

ALTER TABLE `sellers` ADD CONSTRAINT `fk_sellers_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

ALTER TABLE `campaigns` ADD CONSTRAINT `fk_campaigns_seller_id` FOREIGN KEY (`seller_id`) REFERENCES `sellers` (`user_id`);

ALTER TABLE `campaign_items` ADD CONSTRAINT `fk_campaign_items_campaign_id` FOREIGN KEY (`campaign_id`) REFERENCES `campaigns` (`id`);

ALTER TABLE `campaign_items` ADD CONSTRAINT `fk_campaign_items_thumbnail_id` FOREIGN KEY (`thumbnail_id`) REFERENCES `files` (`id`);

ALTER TABLE `orders` ADD CONSTRAINT `fk_orders_customer_id` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`user_id`);

ALTER TABLE `order_items` ADD CONSTRAINT `fk_order_items_order_id` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`);

ALTER TABLE `order_items` ADD CONSTRAINT `fk_order_items_campaign_item_id` FOREIGN KEY (`campaign_item_id`) REFERENCES `campaign_items` (`id`);

ALTER TABLE `files` ADD CONSTRAINT `fk_files_owner_id` FOREIGN KEY (`owner_id`) REFERENCES `sellers` (`user_id`);
