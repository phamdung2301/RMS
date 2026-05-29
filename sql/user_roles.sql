-- User Roles SQL data
INSERT INTO user_roles (user_id, role_id) VALUES
(1, 1),
(2, 2),
(3, 3),
(4, 4),
(5, 5),
(6, 8),
(7, 3),
(8, 8),
(9, 3),
(10, 8),
(11, 3),
(12, 8),
(13, 3),
(14, 8),
(15, 3),
(16, 8),
(17, 3),
(18, 8),
(19, 3),
(20, 8)
ON CONFLICT (user_id, role_id) DO NOTHING;
