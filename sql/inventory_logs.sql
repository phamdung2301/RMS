-- Inventory Logs SQL data
INSERT INTO inventory_logs (id, branch_id, item_id, type, change_quantity, reason, log_date) VALUES
(1, 'branch-2', 2, 'STOCKOUT', 1.5, 'REF-001', '2026-05-29 12:01:00'),
(2, 'branch-3', 3, 'STOCKIN', 3.0, 'REF-002', '2026-05-29 12:02:00'),
(3, 'branch-1', 4, 'STOCKOUT', 4.5, 'REF-003', '2026-05-29 12:03:00'),
(4, 'branch-2', 5, 'STOCKIN', 6.0, 'REF-004', '2026-05-29 12:04:00'),
(5, 'branch-3', 6, 'STOCKOUT', 7.5, 'REF-005', '2026-05-29 12:05:00'),
(6, 'branch-1', 7, 'STOCKIN', 9.0, 'REF-006', '2026-05-29 12:06:00'),
(7, 'branch-2', 8, 'STOCKOUT', 10.5, 'REF-007', '2026-05-29 12:07:00'),
(8, 'branch-3', 9, 'STOCKIN', 12.0, 'REF-008', '2026-05-29 12:08:00'),
(9, 'branch-1', 10, 'STOCKOUT', 13.5, 'REF-009', '2026-05-29 12:09:00'),
(10, 'branch-2', 1, 'STOCKIN', 15.0, 'REF-010', '2026-05-29 12:10:00'),
(11, 'branch-3', 2, 'STOCKOUT', 16.5, 'REF-011', '2026-05-29 12:11:00'),
(12, 'branch-1', 3, 'STOCKIN', 18.0, 'REF-012', '2026-05-29 12:12:00'),
(13, 'branch-2', 4, 'STOCKOUT', 19.5, 'REF-013', '2026-05-29 12:13:00'),
(14, 'branch-3', 5, 'STOCKIN', 21.0, 'REF-014', '2026-05-29 12:14:00'),
(15, 'branch-1', 6, 'STOCKOUT', 22.5, 'REF-015', '2026-05-29 12:15:00'),
(16, 'branch-2', 7, 'STOCKIN', 24.0, 'REF-016', '2026-05-29 12:16:00'),
(17, 'branch-3', 8, 'STOCKOUT', 25.5, 'REF-017', '2026-05-29 12:17:00'),
(18, 'branch-1', 9, 'STOCKIN', 27.0, 'REF-018', '2026-05-29 12:18:00'),
(19, 'branch-2', 10, 'STOCKOUT', 28.5, 'REF-019', '2026-05-29 12:19:00'),
(20, 'branch-3', 1, 'STOCKIN', 30.0, 'REF-020', '2026-05-29 12:20:00')
ON CONFLICT (id) DO NOTHING;
