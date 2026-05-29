-- Branch Transfers SQL data
INSERT INTO branch_transfers (id, source_branch_id, target_branch_id, request_date, approve_date, status) VALUES
(1, 'branch-2', 'branch-3', '2026-05-11 10:00:00', '2026-05-11 14:00:00', 'COMPLETED'),
(2, 'branch-1', 'branch-3', '2026-05-12 10:00:00', '2026-05-12 14:00:00', 'COMPLETED'),
(3, 'branch-2', 'branch-3', '2026-05-13 10:00:00', '2026-05-13 14:00:00', 'COMPLETED'),
(4, 'branch-1', 'branch-3', '2026-05-14 10:00:00', '2026-05-14 14:00:00', 'COMPLETED'),
(5, 'branch-2', 'branch-3', '2026-05-15 10:00:00', '2026-05-15 14:00:00', 'COMPLETED'),
(6, 'branch-1', 'branch-3', '2026-05-16 10:00:00', '2026-05-16 14:00:00', 'COMPLETED'),
(7, 'branch-2', 'branch-3', '2026-05-17 10:00:00', '2026-05-17 14:00:00', 'COMPLETED'),
(8, 'branch-1', 'branch-3', '2026-05-18 10:00:00', '2026-05-18 14:00:00', 'COMPLETED'),
(9, 'branch-2', 'branch-3', '2026-05-19 10:00:00', '2026-05-19 14:00:00', 'COMPLETED'),
(10, 'branch-1', 'branch-3', '2026-05-20 10:00:00', '2026-05-20 14:00:00', 'COMPLETED'),
(11, 'branch-2', 'branch-3', '2026-05-21 10:00:00', '2026-05-21 14:00:00', 'COMPLETED'),
(12, 'branch-1', 'branch-3', '2026-05-22 10:00:00', '2026-05-22 14:00:00', 'COMPLETED'),
(13, 'branch-2', 'branch-3', '2026-05-23 10:00:00', '2026-05-23 14:00:00', 'COMPLETED'),
(14, 'branch-1', 'branch-3', '2026-05-24 10:00:00', '2026-05-24 14:00:00', 'COMPLETED'),
(15, 'branch-2', 'branch-3', '2026-05-25 10:00:00', '2026-05-25 14:00:00', 'COMPLETED'),
(16, 'branch-1', 'branch-3', '2026-05-26 10:00:00', '2026-05-26 14:00:00', 'COMPLETED'),
(17, 'branch-2', 'branch-3', '2026-05-27 10:00:00', '2026-05-27 14:00:00', 'COMPLETED'),
(18, 'branch-1', 'branch-3', '2026-05-28 10:00:00', '2026-05-28 14:00:00', 'COMPLETED'),
(19, 'branch-2', 'branch-3', '2026-05-29 10:00:00', '2026-05-29 14:00:00', 'COMPLETED'),
(20, 'branch-1', 'branch-3', '2026-05-30 10:00:00', '2026-05-30 14:00:00', 'COMPLETED')
ON CONFLICT (id) DO NOTHING;
