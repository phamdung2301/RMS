-- Users SQL data
INSERT INTO users (id, email, password, name, is_active, failed_login_attempts, branch_id, is_two_factor_enabled) VALUES
(1, 'admin@liteflow.com', '$2a$10$7eq/t4lk9f9gu4CNmWPIZOJ4ktC6wwMeSazvIZsiP8l.ceOVCVe4y', 'Chủ chuỗi Admin', true, 0, NULL, false),
(2, 'manager@liteflow.com', '$2a$10$7eq/t4lk9f9gu4CNmWPIZOJ4ktC6wwMeSazvIZsiP8l.ceOVCVe4y', 'Quản lý Trung tâm', true, 0, 'branch-1', false),
(3, 'cashier@liteflow.com', '$2a$10$7eq/t4lk9f9gu4CNmWPIZOJ4ktC6wwMeSazvIZsiP8l.ceOVCVe4y', 'Thu ngân A', true, 0, 'branch-1', false),
(4, 'kitchen@liteflow.com', '$2a$10$7eq/t4lk9f9gu4CNmWPIZOJ4ktC6wwMeSazvIZsiP8l.ceOVCVe4y', 'Đầu bếp B', true, 0, 'branch-1', false),
(5, 'hr@liteflow.com', '$2a$10$7eq/t4lk9f9gu4CNmWPIZOJ4ktC6wwMeSazvIZsiP8l.ceOVCVe4y', 'HR Officer', true, 0, 'branch-1', false),
(6, 'employee@liteflow.com', '$2a$10$7eq/t4lk9f9gu4CNmWPIZOJ4ktC6wwMeSazvIZsiP8l.ceOVCVe4y', 'Phục vụ F', true, 0, 'branch-1', false),
(7, 'staff7@liteflow.com', '$2a$10$7eq/t4lk9f9gu4CNmWPIZOJ4ktC6wwMeSazvIZsiP8l.ceOVCVe4y', 'Nhân viên 7', true, 0, 'branch-2', false),
(8, 'staff8@liteflow.com', '$2a$10$7eq/t4lk9f9gu4CNmWPIZOJ4ktC6wwMeSazvIZsiP8l.ceOVCVe4y', 'Nhân viên 8', true, 0, 'branch-3', false),
(9, 'staff9@liteflow.com', '$2a$10$7eq/t4lk9f9gu4CNmWPIZOJ4ktC6wwMeSazvIZsiP8l.ceOVCVe4y', 'Nhân viên 9', true, 0, 'branch-1', false),
(10, 'staff10@liteflow.com', '$2a$10$7eq/t4lk9f9gu4CNmWPIZOJ4ktC6wwMeSazvIZsiP8l.ceOVCVe4y', 'Nhân viên 10', true, 0, 'branch-2', false),
(11, 'staff11@liteflow.com', '$2a$10$7eq/t4lk9f9gu4CNmWPIZOJ4ktC6wwMeSazvIZsiP8l.ceOVCVe4y', 'Nhân viên 11', true, 0, 'branch-3', false),
(12, 'staff12@liteflow.com', '$2a$10$7eq/t4lk9f9gu4CNmWPIZOJ4ktC6wwMeSazvIZsiP8l.ceOVCVe4y', 'Nhân viên 12', true, 0, 'branch-1', false),
(13, 'staff13@liteflow.com', '$2a$10$7eq/t4lk9f9gu4CNmWPIZOJ4ktC6wwMeSazvIZsiP8l.ceOVCVe4y', 'Nhân viên 13', true, 0, 'branch-2', false),
(14, 'staff14@liteflow.com', '$2a$10$7eq/t4lk9f9gu4CNmWPIZOJ4ktC6wwMeSazvIZsiP8l.ceOVCVe4y', 'Nhân viên 14', true, 0, 'branch-3', false),
(15, 'staff15@liteflow.com', '$2a$10$7eq/t4lk9f9gu4CNmWPIZOJ4ktC6wwMeSazvIZsiP8l.ceOVCVe4y', 'Nhân viên 15', true, 0, 'branch-1', false),
(16, 'staff16@liteflow.com', '$2a$10$7eq/t4lk9f9gu4CNmWPIZOJ4ktC6wwMeSazvIZsiP8l.ceOVCVe4y', 'Nhân viên 16', true, 0, 'branch-2', false),
(17, 'staff17@liteflow.com', '$2a$10$7eq/t4lk9f9gu4CNmWPIZOJ4ktC6wwMeSazvIZsiP8l.ceOVCVe4y', 'Nhân viên 17', true, 0, 'branch-3', false),
(18, 'staff18@liteflow.com', '$2a$10$7eq/t4lk9f9gu4CNmWPIZOJ4ktC6wwMeSazvIZsiP8l.ceOVCVe4y', 'Nhân viên 18', true, 0, 'branch-1', false),
(19, 'staff19@liteflow.com', '$2a$10$7eq/t4lk9f9gu4CNmWPIZOJ4ktC6wwMeSazvIZsiP8l.ceOVCVe4y', 'Nhân viên 19', true, 0, 'branch-2', false),
(20, 'staff20@liteflow.com', '$2a$10$7eq/t4lk9f9gu4CNmWPIZOJ4ktC6wwMeSazvIZsiP8l.ceOVCVe4y', 'Nhân viên 20', true, 0, 'branch-3', false),
(21, 'phamhadacdung2301@gmail.com', '$2a$10$7eq/t4lk9f9gu4CNmWPIZOJ4ktC6wwMeSazvIZsiP8l.ceOVCVe4y', 'Phạm Hà Đắc Dũng (Google Admin)', true, 0, NULL, false)
ON CONFLICT (id) DO NOTHING;
