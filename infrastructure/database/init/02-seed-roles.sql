USE ecommerce;

INSERT INTO `roles` (`id`, `name`, `created_at`, `created_by`, `last_modified_at`, `last_modified_by`)
VALUES (UUID(), 'SELLER', NOW(), 'system', NOW(), 'system'),
       (UUID(), 'CUSTOMER', NOW(), 'system', NOW(), 'system');
