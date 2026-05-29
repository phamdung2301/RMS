-- Employees SQL data
INSERT INTO employees (id, user_id, branch_id, department, title, base_salary, salary_type, hire_date) VALUES
(1, 2, 'branch-2', 'Thu ngân', 'Thu ngân chính', 6100000.0, 'Fixed', '2024-01-15'),
(2, 3, 'branch-3', 'Bếp', 'Đầu bếp chính', 6200000.0, 'Fixed', '2024-01-15'),
(3, 4, 'branch-1', 'Nhân sự', 'Nhân viên nhân sự', 6300000.0, 'Fixed', '2024-01-15'),
(4, 5, 'branch-2', 'Bàn', 'Nhân viên phục vụ', 25000.0, 'Hourly', '2024-01-15'),
(5, 6, 'branch-3', 'Hành chính', 'Quản lý', 15000000.0, 'Fixed', '2024-01-15'),
(6, 7, 'branch-1', 'Thu ngân', 'Thu ngân chính', 6600000.0, 'Fixed', '2024-01-15'),
(7, 8, 'branch-2', 'Bếp', 'Đầu bếp chính', 6700000.0, 'Fixed', '2024-01-15'),
(8, 9, 'branch-3', 'Nhân sự', 'Nhân viên nhân sự', 6800000.0, 'Fixed', '2024-01-15'),
(9, 10, 'branch-1', 'Bàn', 'Nhân viên phục vụ', 25000.0, 'Hourly', '2024-01-15'),
(10, 11, 'branch-2', 'Hành chính', 'Quản lý', 15000000.0, 'Fixed', '2024-01-15'),
(11, 12, 'branch-3', 'Thu ngân', 'Thu ngân chính', 7100000.0, 'Fixed', '2024-01-15'),
(12, 13, 'branch-1', 'Bếp', 'Đầu bếp chính', 7200000.0, 'Fixed', '2024-01-15'),
(13, 14, 'branch-2', 'Nhân sự', 'Nhân viên nhân sự', 7300000.0, 'Fixed', '2024-01-15'),
(14, 15, 'branch-3', 'Bàn', 'Nhân viên phục vụ', 25000.0, 'Hourly', '2024-01-15'),
(15, 16, 'branch-1', 'Hành chính', 'Quản lý', 15000000.0, 'Fixed', '2024-01-15'),
(16, 17, 'branch-2', 'Thu ngân', 'Thu ngân chính', 7600000.0, 'Fixed', '2024-01-15'),
(17, 18, 'branch-3', 'Bếp', 'Đầu bếp chính', 7700000.0, 'Fixed', '2024-01-15'),
(18, 19, 'branch-1', 'Nhân sự', 'Nhân viên nhân sự', 7800000.0, 'Fixed', '2024-01-15'),
(19, 20, 'branch-2', 'Bàn', 'Nhân viên phục vụ', 25000.0, 'Hourly', '2024-01-15')
ON CONFLICT (id) DO NOTHING;
