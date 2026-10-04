USE hire_me;

INSERT INTO `roles` (`id`, `name`, `created_at`, `created_by`, `last_modified_at`, `last_modified_by`)
VALUES (UUID(), 'ADMIN', NOW(), 'system', NOW(), 'system'),
       (UUID(), 'RECRUITER', NOW(), 'system', NOW(), 'system'),
       (UUID(), 'CANDIDATE', NOW(), 'system', NOW(), 'system');
