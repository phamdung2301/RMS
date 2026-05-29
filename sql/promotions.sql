-- Promotions SQL data
INSERT INTO promotions (id, name, promo_code, type, discount_value, min_order_value, max_usage_count, current_usage_count, start_date, end_date, is_active) VALUES
(1, 'Chương trình khuyến mãi 1', 'CODE01', 'FlatDiscount', 20000.0, 100000.0, 100, 0, '2026-05-01', '2026-08-30', true),
(2, 'Chương trình khuyến mãi 2', 'CODE02', 'Buy1Get1', 10.0, 100000.0, 100, 0, '2026-05-01', '2026-08-30', true),
(3, 'Chương trình khuyến mãi 3', 'CODE03', 'PercentDiscount', 10.0, 100000.0, 100, 0, '2026-05-01', '2026-08-30', true),
(4, 'Chương trình khuyến mãi 4', 'CODE04', 'FlatDiscount', 20000.0, 100000.0, 100, 0, '2026-05-01', '2026-08-30', true),
(5, 'Chương trình khuyến mãi 5', 'CODE05', 'Buy1Get1', 10.0, 100000.0, 100, 0, '2026-05-01', '2026-08-30', true),
(6, 'Chương trình khuyến mãi 6', 'CODE06', 'PercentDiscount', 10.0, 100000.0, 100, 0, '2026-05-01', '2026-08-30', true),
(7, 'Chương trình khuyến mãi 7', 'CODE07', 'FlatDiscount', 20000.0, 100000.0, 100, 0, '2026-05-01', '2026-08-30', true),
(8, 'Chương trình khuyến mãi 8', 'CODE08', 'Buy1Get1', 10.0, 100000.0, 100, 0, '2026-05-01', '2026-08-30', true),
(9, 'Chương trình khuyến mãi 9', 'CODE09', 'PercentDiscount', 10.0, 100000.0, 100, 0, '2026-05-01', '2026-08-30', true),
(10, 'Chương trình khuyến mãi 10', 'CODE10', 'FlatDiscount', 20000.0, 100000.0, 100, 0, '2026-05-01', '2026-08-30', true),
(11, 'Chương trình khuyến mãi 11', 'CODE11', 'Buy1Get1', 10.0, 100000.0, 100, 0, '2026-05-01', '2026-08-30', true),
(12, 'Chương trình khuyến mãi 12', 'CODE12', 'PercentDiscount', 10.0, 100000.0, 100, 0, '2026-05-01', '2026-08-30', true),
(13, 'Chương trình khuyến mãi 13', 'CODE13', 'FlatDiscount', 20000.0, 100000.0, 100, 0, '2026-05-01', '2026-08-30', true),
(14, 'Chương trình khuyến mãi 14', 'CODE14', 'Buy1Get1', 10.0, 100000.0, 100, 0, '2026-05-01', '2026-08-30', true),
(15, 'Chương trình khuyến mãi 15', 'CODE15', 'PercentDiscount', 10.0, 100000.0, 100, 0, '2026-05-01', '2026-08-30', true),
(16, 'Chương trình khuyến mãi 16', 'CODE16', 'FlatDiscount', 20000.0, 100000.0, 100, 0, '2026-05-01', '2026-08-30', true),
(17, 'Chương trình khuyến mãi 17', 'CODE17', 'Buy1Get1', 10.0, 100000.0, 100, 0, '2026-05-01', '2026-08-30', true),
(18, 'Chương trình khuyến mãi 18', 'CODE18', 'PercentDiscount', 10.0, 100000.0, 100, 0, '2026-05-01', '2026-08-30', true),
(19, 'Chương trình khuyến mãi 19', 'CODE19', 'FlatDiscount', 20000.0, 100000.0, 100, 0, '2026-05-01', '2026-08-30', true),
(20, 'Chương trình khuyến mãi 20', 'CODE20', 'Buy1Get1', 10.0, 100000.0, 100, 0, '2026-05-01', '2026-08-30', true)
ON CONFLICT (id) DO NOTHING;
