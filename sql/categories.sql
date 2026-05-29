-- Categories SQL data
INSERT INTO categories (id, name) VALUES
(1, 'Món chính'),
(2, 'Đồ uống'),
(3, 'Tráng miệng'),
(4, 'Khai vị'),
(5, 'Món nước'),
(6, 'Món nướng'),
(7, 'Lẩu'),
(8, 'Ăn nhẹ')
ON CONFLICT (id) DO NOTHING;
