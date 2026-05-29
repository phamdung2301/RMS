-- Leave Requests SQL data
INSERT INTO leave_requests (id, employee_id, start_date, end_date, reason, status, leave_type) VALUES
(1, 2, '2026-06-12', '2026-06-13', 'Lý do xin nghỉ phép 1', 'PENDING', 'SICK'),
(2, 3, '2026-06-13', '2026-06-14', 'Lý do xin nghỉ phép 2', 'APPROVED', 'ANNUAL'),
(3, 4, '2026-06-14', '2026-06-15', 'Lý do xin nghỉ phép 3', 'PENDING', 'SICK'),
(4, 5, '2026-06-15', '2026-06-16', 'Lý do xin nghỉ phép 4', 'APPROVED', 'ANNUAL'),
(5, 1, '2026-06-16', '2026-06-17', 'Lý do xin nghỉ phép 5', 'PENDING', 'SICK'),
(6, 2, '2026-06-17', '2026-06-18', 'Lý do xin nghỉ phép 6', 'APPROVED', 'ANNUAL'),
(7, 3, '2026-06-18', '2026-06-19', 'Lý do xin nghỉ phép 7', 'PENDING', 'SICK'),
(8, 4, '2026-06-19', '2026-06-20', 'Lý do xin nghỉ phép 8', 'APPROVED', 'ANNUAL'),
(9, 5, '2026-06-20', '2026-06-21', 'Lý do xin nghỉ phép 9', 'PENDING', 'SICK'),
(10, 1, '2026-06-21', '2026-06-22', 'Lý do xin nghỉ phép 10', 'APPROVED', 'ANNUAL'),
(11, 2, '2026-06-22', '2026-06-23', 'Lý do xin nghỉ phép 11', 'PENDING', 'SICK'),
(12, 3, '2026-06-23', '2026-06-24', 'Lý do xin nghỉ phép 12', 'APPROVED', 'ANNUAL'),
(13, 4, '2026-06-24', '2026-06-25', 'Lý do xin nghỉ phép 13', 'PENDING', 'SICK'),
(14, 5, '2026-06-25', '2026-06-26', 'Lý do xin nghỉ phép 14', 'APPROVED', 'ANNUAL'),
(15, 1, '2026-06-26', '2026-06-27', 'Lý do xin nghỉ phép 15', 'PENDING', 'SICK'),
(16, 2, '2026-06-27', '2026-06-28', 'Lý do xin nghỉ phép 16', 'APPROVED', 'ANNUAL'),
(17, 3, '2026-06-28', '2026-06-1', 'Lý do xin nghỉ phép 17', 'PENDING', 'SICK'),
(18, 4, '2026-06-1', '2026-06-2', 'Lý do xin nghỉ phép 18', 'APPROVED', 'ANNUAL'),
(19, 5, '2026-06-2', '2026-06-3', 'Lý do xin nghỉ phép 19', 'PENDING', 'SICK'),
(20, 1, '2026-06-3', '2026-06-4', 'Lý do xin nghỉ phép 20', 'APPROVED', 'ANNUAL')
ON CONFLICT (id) DO NOTHING;
