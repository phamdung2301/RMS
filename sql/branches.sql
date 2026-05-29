-- Branches SQL data
INSERT INTO branches (branch_id, name, address, phone, is_active) VALUES
('branch-1', 'Chi nhánh Trung Tâm', '123 Đường Láng, Hà Nội', '0243123456', true),
('branch-2', 'Chi nhánh Quận 1', '45 Lê Lợi, TP. Hồ Chí Minh', '0283123456', true),
('branch-3', 'Chi nhánh Tân Bình', '78 Cộng Hòa, TP. Hồ Chí Minh', '0283987654', true),
('branch-4', 'Chi nhánh Quận 3', '200 Nguyễn Thị Minh Khai, TP. Hồ Chí Minh', '0283555444', true),
('branch-5', 'Chi nhánh Hoàn Kiếm', '15 Lý Thái Tổ, Hà Nội', '0243888777', true),
('branch-6', 'Chi nhánh Ba Đình', '88 Kim Mã, Hà Nội', '0243999000', true),
('branch-7', 'Chi nhánh Hải Phòng', '50 Lạch Tray, Hải Phòng', '02253123456', true),
('branch-8', 'Chi nhánh Đà Nẵng', '100 Hùng Vương, Đà Nẵng', '02363123456', true),
('branch-9', 'Chi nhánh Cần Thơ', '12 Đại lộ Hòa Bình, Cần Thơ', '02923123456', true),
('branch-10', 'Chi nhánh Biên Hòa', '250 Phạm Văn Thuận, Đồng Nai', '02513123456', true),
('branch-11', 'Chi nhánh Nha Trang', '80 Trần Phú, Khánh Hòa', '02583123456', true),
('branch-12', 'Chi nhánh Vũng Tàu', '10 Hạ Long, Bà Rịa - Vũng Tàu', '02543123456', true),
('branch-13', 'Chi nhánh Buôn Ma Thuột', '45 Nguyễn Tất Thành, Đắk Lắk', '02623123456', true),
('branch-14', 'Chi nhánh Đà Lạt', '5 Trần Hưng Đạo, Lâm Đồng', '02633123456', true),
('branch-15', 'Chi nhánh Vinh', '30 Quang Trung, Nghệ An', '02383123456', true),
('branch-16', 'Chi nhánh Huế', '18 Lê Lợi, Thừa Thiên Huế', '02343123456', true),
('branch-17', 'Chi nhánh Quy Nhơn', '95 An Dương Vương, Bình Định', '02563123456', true),
('branch-18', 'Chi nhánh Phan Thiết', '120 Nguyễn Đình Chiểu, Bình Thuận', '02523123456', true),
('branch-19', 'Chi nhánh Rạch Giá', '35 Tôn Đức Thắng, Kiên Giang', '02973123456', true),
('branch-20', 'Chi nhánh Long Xuyên', '10 Trần Hưng Đạo, An Giang', '02963123456', true)
ON CONFLICT (branch_id) DO NOTHING;
