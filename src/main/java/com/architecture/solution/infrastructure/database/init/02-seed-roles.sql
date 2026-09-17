USE hire_me;

INSERT INTO `roles` (`id`, `name`, `created_at`, `created_by`)
VALUES (UUID(), 'RECRUITER', NOW(), 'system'),
       (UUID(), 'CANDIDATE', NOW(), 'system');
