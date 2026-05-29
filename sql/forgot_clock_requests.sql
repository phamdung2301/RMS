-- Forgot Clock Requests SQL data
INSERT INTO forgot_clock_requests (id, employee_id, date, clock_type, reason, status, time_proposed) VALUES
(1, 2, '2026-05-11', 'IN', 'Quên chấm công 1', 'PENDING', '08:00'),
(2, 3, '2026-05-12', 'IN', 'Quên chấm công 2', 'APPROVED', '08:00'),
(3, 4, '2026-05-13', 'IN', 'Quên chấm công 3', 'PENDING', '08:00'),
(4, 5, '2026-05-14', 'IN', 'Quên chấm công 4', 'APPROVED', '08:00'),
(5, 1, '2026-05-15', 'IN', 'Quên chấm công 5', 'PENDING', '08:00'),
(6, 2, '2026-05-16', 'IN', 'Quên chấm công 6', 'APPROVED', '08:00'),
(7, 3, '2026-05-17', 'IN', 'Quên chấm công 7', 'PENDING', '08:00'),
(8, 4, '2026-05-18', 'IN', 'Quên chấm công 8', 'APPROVED', '08:00'),
(9, 5, '2026-05-19', 'IN', 'Quên chấm công 9', 'PENDING', '08:00'),
(10, 1, '2026-05-20', 'IN', 'Quên chấm công 10', 'APPROVED', '08:00'),
(11, 2, '2026-05-21', 'IN', 'Quên chấm công 11', 'PENDING', '08:00'),
(12, 3, '2026-05-22', 'IN', 'Quên chấm công 12', 'APPROVED', '08:00'),
(13, 4, '2026-05-23', 'IN', 'Quên chấm công 13', 'PENDING', '08:00'),
(14, 5, '2026-05-24', 'IN', 'Quên chấm công 14', 'APPROVED', '08:00'),
(15, 1, '2026-05-25', 'IN', 'Quên chấm công 15', 'PENDING', '08:00'),
(16, 2, '2026-05-26', 'IN', 'Quên chấm công 16', 'APPROVED', '08:00'),
(17, 3, '2026-05-27', 'IN', 'Quên chấm công 17', 'PENDING', '08:00'),
(18, 4, '2026-05-28', 'IN', 'Quên chấm công 18', 'APPROVED', '08:00'),
(19, 5, '2026-05-29', 'IN', 'Quên chấm công 19', 'PENDING', '08:00'),
(20, 1, '2026-05-30', 'IN', 'Quên chấm công 20', 'APPROVED', '08:00')
ON CONFLICT (id) DO NOTHING;
