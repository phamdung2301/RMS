-- Inventory Items SQL data
INSERT INTO inventory_items (id, sku, name, unit, minimum_threshold) VALUES
(1, 'MAT-001', 'Bột mì', 'kg', 10.0),
(2, 'MAT-002', 'Thịt bò nạm', 'kg', 10.0),
(3, 'MAT-003', 'Thịt bò thăn', 'kg', 10.0),
(4, 'MAT-004', 'Hạt cà phê Robusta', 'kg', 10.0),
(5, 'MAT-005', 'Hạt cà phê Arabica', 'kg', 10.0),
(6, 'MAT-006', 'Rau thơm các loại', 'kg', 10.0),
(7, 'MAT-007', 'Giá đỗ sạch', 'kg', 10.0),
(8, 'MAT-008', 'Đào ngâm đóng hộp', 'hộp', 10.0),
(9, 'MAT-009', 'Sả tươi', 'kg', 10.0),
(10, 'MAT-010', 'Cam vàng Úc', 'kg', 10.0),
(11, 'MAT-011', 'Quả bơ sáp', 'kg', 10.0),
(12, 'MAT-012', 'Dưa hấu Long An', 'kg', 10.0),
(13, 'MAT-013', 'Bột matcha Nhật', 'kg', 10.0),
(14, 'MAT-014', 'Trân châu đen', 'túi', 10.0),
(15, 'MAT-015', 'Bắp bò Mỹ', 'kg', 10.0),
(16, 'MAT-016', 'Sườn heo non', 'kg', 10.0),
(17, 'MAT-017', 'Cua biển Cà Mau', 'kg', 10.0),
(18, 'MAT-018', 'Sườn sụn non', 'kg', 10.0),
(19, 'MAT-019', 'Trứng cút sạch', 'quả', 10.0),
(20, 'MAT-020', 'Bánh tráng phơi sương', 'kg', 10.0)
ON CONFLICT (id) DO NOTHING;
