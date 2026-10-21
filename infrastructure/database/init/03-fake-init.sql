USE ecommerce;

SET @seed_now = NOW();

START TRANSACTION;

INSERT INTO `users` (`id`, `email`, `username`, `password_hash`, `displayed_name`, `created_at`, `created_by`, `last_modified_at`, `last_modified_by`, `is_deleted`)
VALUES
  ('00000000-0000-0000-0000-000000000002', 'seller01@ecommerce.test', 'seller01', '$2a$10$enNdLp5SbpoixE2do4MOd.fnadT2lahAu/.STUzKSksB3NmawNyJu', 'Lotus Shop', @seed_now, 'system', @seed_now, 'system', FALSE),
  ('00000000-0000-0000-0000-000000000003', 'customer01@ecommerce.test', 'customer01', '$2a$10$enNdLp5SbpoixE2do4MOd.fnadT2lahAu/.STUzKSksB3NmawNyJu', 'Demo Customer', @seed_now, 'system', @seed_now, 'system', FALSE);

INSERT INTO `user_roles` (`id`, `user_id`, `role_id`, `created_at`, `created_by`, `last_modified_at`, `last_modified_by`, `is_deleted`)
SELECT UUID(), u.id, r.id, @seed_now, 'system', @seed_now, 'system', FALSE
FROM `users` u
JOIN `roles` r ON r.name = CASE u.username WHEN 'seller01' THEN 'SELLER' ELSE 'CUSTOMER' END;

INSERT INTO `sellers` (`user_id`, `shop_name`, `created_at`, `created_by`, `last_modified_at`, `last_modified_by`, `is_deleted`)
VALUES
  ('00000000-0000-0000-0000-000000000002', 'Lotus Shop', @seed_now, 'system', @seed_now, 'system', FALSE);

INSERT INTO `customers` (`user_id`, `phone`, `shipping_address`, `created_at`, `created_by`, `last_modified_at`, `last_modified_by`, `is_deleted`)
VALUES
  ('00000000-0000-0000-0000-000000000003', '0901234567', 'Ho Chi Minh City', @seed_now, 'system', @seed_now, 'system', FALSE);

SET @campaign_start = DATE_ADD(@seed_now, INTERVAL 1 DAY);

SET @campaign_end = DATE_ADD(@campaign_start, INTERVAL 2 HOUR);

INSERT INTO `campaigns` (`id`, `name`, `description`, `start_at`, `end_at`, `seller_id`, `created_at`, `created_by`, `last_modified_at`, `last_modified_by`, `is_deleted`)
VALUES
  ('00000000-0000-0000-0000-000000000030', 'Daily Flash Sale', 'Upcoming demo campaign', @campaign_start, @campaign_end, '00000000-0000-0000-0000-000000000002', @seed_now, 'system', @seed_now, 'system', FALSE);

INSERT INTO `campaign_items` (`id`, `campaign_id`, `name`, `thumbnail_id`, `price`, `stock`, `created_at`, `created_by`, `last_modified_at`, `last_modified_by`, `is_deleted`)
VALUES
  ('00000000-0000-0000-0000-000000000031', '00000000-0000-0000-0000-000000000030', 'Wireless Headphones', NULL, 700000, 20, @seed_now, 'system', @seed_now, 'system', FALSE);

COMMIT;
