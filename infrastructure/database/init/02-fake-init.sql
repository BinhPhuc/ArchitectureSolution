USE ecommerce;

SET @seed_now = NOW();

START TRANSACTION;

INSERT INTO `users` (`id`, `email`, `username`, `password_hash`, `displayed_name`, `role`, `created_at`, `created_by`, `last_modified_at`, `last_modified_by`)
VALUES
  ('00000000-0000-0000-0000-000000000002', 'seller01@ecommerce.test', 'seller01', '$2a$10$enNdLp5SbpoixE2do4MOd.fnadT2lahAu/.STUzKSksB3NmawNyJu', 'Lotus Shop', 'SELLER', @seed_now, 'system', @seed_now, 'system'),
  ('00000000-0000-0000-0000-000000000003', 'customer01@ecommerce.test', 'customer01', '$2a$10$enNdLp5SbpoixE2do4MOd.fnadT2lahAu/.STUzKSksB3NmawNyJu', 'Demo Customer', 'CUSTOMER', @seed_now, 'system', @seed_now, 'system');

INSERT INTO `sellers` (`user_id`, `shop_name`, `created_at`, `created_by`, `last_modified_at`, `last_modified_by`)
VALUES
  ('00000000-0000-0000-0000-000000000002', 'Lotus Shop', @seed_now, 'system', @seed_now, 'system');

INSERT INTO `customers` (`user_id`, `phone`, `shipping_address`, `created_at`, `created_by`, `last_modified_at`, `last_modified_by`)
VALUES
  ('00000000-0000-0000-0000-000000000003', '0901234567', 'Ho Chi Minh City', @seed_now, 'system', @seed_now, 'system');

SET @campaign_start = DATE_ADD(@seed_now, INTERVAL 1 DAY);

SET @campaign_end = DATE_ADD(@campaign_start, INTERVAL 2 HOUR);

INSERT INTO `campaigns` (`id`, `name`, `description`, `start_at`, `end_at`, `seller_id`, `created_at`, `created_by`, `last_modified_at`, `last_modified_by`)
VALUES
  ('00000000-0000-0000-0000-000000000030', 'Daily Flash Sale', 'Upcoming demo campaign', @campaign_start, @campaign_end, '00000000-0000-0000-0000-000000000002', @seed_now, 'system', @seed_now, 'system');

INSERT INTO `campaign_items` (`id`, `campaign_id`, `name`, `thumbnail_id`, `price`, `stock`, `created_at`, `created_by`, `last_modified_at`, `last_modified_by`)
VALUES
  ('00000000-0000-0000-0000-000000000031', '00000000-0000-0000-0000-000000000030', 'Wireless Headphones', NULL, 700000, 20, @seed_now, 'system', @seed_now, 'system');

COMMIT;
