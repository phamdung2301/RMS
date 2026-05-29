-- Rooms SQL data
INSERT INTO rooms (id, name, branch_id) VALUES
(1, 'Sân Trước', 'branch-2'),
(2, 'Phòng Lạnh', 'branch-3'),
(3, 'Lầu 1', 'branch-1'),
(4, 'Lầu 2', 'branch-2'),
(5, 'Khu Vực 5', 'branch-3'),
(6, 'Khu Vực 6', 'branch-1'),
(7, 'Khu Vực 7', 'branch-2'),
(8, 'Khu Vực 8', 'branch-3'),
(9, 'Khu Vực 9', 'branch-1'),
(10, 'Khu Vực 10', 'branch-2'),
(11, 'Khu Vực 11', 'branch-3'),
(12, 'Khu Vực 12', 'branch-1'),
(13, 'Khu Vực 13', 'branch-2'),
(14, 'Khu Vực 14', 'branch-3'),
(15, 'Khu Vực 15', 'branch-1'),
(16, 'Khu Vực 16', 'branch-2'),
(17, 'Khu Vực 17', 'branch-3'),
(18, 'Khu Vực 18', 'branch-1'),
(19, 'Khu Vực 19', 'branch-2'),
(20, 'Khu Vực 20', 'branch-3')
ON CONFLICT (id) DO NOTHING;
