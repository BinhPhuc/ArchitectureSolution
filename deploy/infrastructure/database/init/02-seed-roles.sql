USE hire_me;

INSERT INTO `roles` (`id`, `name`, `created_at`, `created_by`)
VALUES (UUID(), 'ADMIN', NOW(), 'system'),
       (UUID(), 'RECRUITER', NOW(), 'system'),
       (UUID(), 'CANDIDATE', NOW(), 'system');
