# DANH SÁCH BẢNG & TRƯỜNG DỮ LIỆU TOÀN DIỆN (TABLES & FIELDS DEFINITIONS)

**Hệ Thống Quản Lý Nhà Hàng Chuỗi - SWP391**

---

| Thông Tin Tài Liệu | Chi Tiết |
| --- | --- |
| **Dự án** | Hệ Thống Quản Lý Nhà Hàng Chuỗi (RMS - Restaurant Management System) |
| **Môn học** | SWP391 - Học kỳ 5, Đại học FPT |
| **Tài liệu** | Đặc Tả Chi Tiết Toàn Bộ Bảng & Trường Dữ Liệu (Comprehensive Tables & Fields Specification) |
| **Phiên bản** | 1.0.0 (Bản Toàn Diện) |
| **Tác giả** | Tech Lead (20 năm kinh nghiệm) |
| **Trạng thái** | Hoàn tất |

---

# 1. Hướng Dẫn Sử Dụng Bản Đặc Tả Này (How To Use)

Đoạn văn bản thô bên dưới được thiết kế với cấu trúc phân nhóm cực kỳ sạch sẽ và chuẩn hóa. Bạn chỉ cần nhấp nút **"Copy"** ở góc trên cùng của khối mã màu đen bên dưới và dán trực tiếp vào các ô chat của AI (như ChatGPT, Claude, hay Gemini Advanced) với câu lệnh:

> *"Dựa trên đặc tả 43 bảng và đầy đủ các cột dữ liệu kèm theo này, hãy viết lại kịch bản DBML (Database Markup Language) để tôi dán trực tiếp vào dbdiagram.io nhằm tạo sơ đồ cơ sở dữ liệu ERD tự động. Hãy bảo đảm liên kết khóa ngoại (relationship lines) chính xác."*

---

# 2. Chi Tiết Danh Sách 43 Bảng & Đầy Đủ Các Trường Dữ Liệu (All 43 Tables & Columns)

```text
DANH SÁCH TOÀN BỘ 43 BẢNG VÀ TRƯỜNG DỮ LIỆU HỆ THỐNG RMS

1. Bảng: branches (Chi nhánh cửa hàng)
   - branch_id: VARCHAR(36) [Khóa chính - PRIMARY KEY]
   - name: VARCHAR(255) [Không null - Tên chi nhánh]
   - address: VARCHAR(255) [Null - Địa chỉ chi nhánh]
   - phone: VARCHAR(20) [Null - Số điện thoại chi nhánh]
   - is_active: BOOLEAN [Không null, Default True - Trạng thái hoạt động]
   - is_warehouse: BOOLEAN [Không null, Default False - Đánh dấu là Kho tổng]

2. Bảng: roles (Vai trò phân quyền)
   - id: BIGINT [Khóa chính - PRIMARY KEY, Tự tăng]
   - name: VARCHAR(255) [Không null, Unique - Tên vai trò (ADMIN, CASHIER...)]

3. Bảng: categories (Danh mục món ăn)
   - id: BIGINT [Khóa chính - PRIMARY KEY, Tự tăng]
   - name: VARCHAR(255) [Không null, Unique - Tên danh mục thực đơn]

4. Bảng: inventory_items (Danh mục nguyên liệu thô)
   - id: BIGINT [Khóa chính - PRIMARY KEY, Tự tăng]
   - sku: VARCHAR(100) [Không null, Unique - Mã quản lý kho hàng]
   - name: VARCHAR(255) [Không null - Tên nguyên liệu thô]
   - unit: VARCHAR(50) [Không null - Đơn vị tính kho]
   - minimum_threshold: DOUBLE [Không null, Default 5.0 - Ngưỡng tồn tối thiểu]

5. Bảng: suppliers (Nhà cung cấp ngoài)
   - id: BIGINT [Khóa chính - PRIMARY KEY, Tự tăng]
   - code: VARCHAR(50) [Không null, Unique - Mã viết tắt nhà cung cấp]
   - name: VARCHAR(255) [Không null - Tên nhà cung cấp thô]
   - contact_email: VARCHAR(255) [Null - Email liên hệ]
   - phone: VARCHAR(20) [Null - Số điện thoại]
   - address: VARCHAR(255) [Null - Địa chỉ trụ sở]

6. Bảng: shift_templates (Mẫu ca trực nhân sự)
   - id: BIGINT [Khóa chính - PRIMARY KEY, Tự tăng]
   - name: VARCHAR(100) [Không null - Tên ca mẫu (Ca sáng, Ca chiều...)]
   - start_time: VARCHAR(10) [Không null - Giờ bắt đầu (HH:mm)]
   - end_time: VARCHAR(10) [Không null - Giờ kết thúc (HH:mm)]
   - duration_hours: DOUBLE [Không null - Số giờ công quy đổi]

7. Bảng: payroll_runs (Đợt tính lương)
   - id: BIGINT [Khóa chính - PRIMARY KEY, Tự tăng]
   - period: VARCHAR(10) [Không null - Kỳ lương chốt (YYYY-MM)]
   - run_date: TIMESTAMP [Không null - Ngày tính lương]
   - run_by: VARCHAR(255) [Không null - Tài khoản chạy tính lương]

8. Bảng: promotions (Chương trình khuyến mãi)
   - id: BIGINT [Khóa chính - PRIMARY KEY, Tự tăng]
   - name: VARCHAR(255) [Không null - Tên chương trình ưu đãi]
   - promo_code: VARCHAR(100) [Không null, Unique - Mã coupon khách nhập]
   - type: VARCHAR(30) [Không null - PercentDiscount, FlatDiscount, Buy1Get1]
   - discount_value: DOUBLE [Không null - Giá trị chiết khấu]
   - min_order_value: DOUBLE [Không null, Default 0.0 - Trị giá hóa đơn tối thiểu]
   - max_usage_count: INTEGER [Không null, Default 100 - Giới hạn lượt dùng tối đa]
   - current_usage_count: INTEGER [Không null, Default 0 - Lượt đã sử dụng thực tế]
   - start_date: DATE [Không null - Ngày khuyến mãi có hiệu lực]
   - end_date: DATE [Không null - Ngày khuyến mãi hết hiệu lực]
   - is_active: BOOLEAN [Không null, Default True - Trạng thái kích hoạt]
   - trigger_product_id: BIGINT [Null - Mã món mua điều kiện B1G1]
   - reward_product_id: BIGINT [Null - Mã món tặng kèm miễn phí B1G1]

9. Bảng: customers (Hồ sơ hội viên)
   - phone: VARCHAR(15) [Khóa chính - PRIMARY KEY, Unique - Số điện thoại khách]
   - name: VARCHAR(255) [Không null - Họ tên hội viên]
   - birth_date: DATE [Null - Ngày sinh nhật]
   - membership_tier: VARCHAR(20) [Không null, Default 'Bronze' - Hạng thẻ thành viên]
   - loyalty_points: INTEGER [Không null, Default 0 - Điểm thưởng hiện có]
   - total_spent: DOUBLE [Không null, Default 0.0 - Tổng chi tiêu tích lũy]

10. Bảng: users (Tài khoản nhân viên)
    - id: BIGINT [Khóa chính - PRIMARY KEY, Tự tăng]
    - email: VARCHAR(255) [Không null, Unique - Email đăng nhập]
    - password: VARCHAR(255) [Không null - Mật khẩu đã băm BCrypt]
    - name: VARCHAR(255) [Không null - Họ tên nhân viên]
    - two_factor_secret: VARCHAR(255) [Null - Mã khóa bảo mật 2FA]
    - is_two_factor_enabled: BOOLEAN [Không null, Default False - Trạng thái 2FA]
    - is_active: BOOLEAN [Không null, Default True - Trạng thái hoạt động]
    - failed_login_attempts: INTEGER [Không null, Default 0 - Số lần nhập sai pass]
    - branch_id: VARCHAR(36) [Khóa ngoại - FOREIGN KEY references branches(branch_id)]

11. Bảng: user_roles (Liên kết vai trò tài khoản)
    - user_id: BIGINT [Khóa chính, Khóa ngoại - PRIMARY & FOREIGN KEY references users(id)]
    - role_id: BIGINT [Khóa chính, Khóa ngoại - PRIMARY & FOREIGN KEY references roles(id)]

12. Bảng: user_sessions (Phiên đăng nhập người dùng)
    - id: BIGINT [Khóa chính - PRIMARY KEY, Tự tăng]
    - user_id: BIGINT [Khóa ngoại - FOREIGN KEY references users(id)]
    - token: VARCHAR(255) [Không null, Unique - Token xác thực phiên]
    - ip_address: VARCHAR(50) [Null - IP thiết bị đăng nhập]
    - created_at: TIMESTAMP [Không null - Giờ đăng nhập]
    - expires_at: TIMESTAMP [Không null - Giờ hết hạn phiên]

13. Bảng: user_branches (Giao quản lý chi nhánh)
    - user_id: BIGINT [Khóa chính, Khóa ngoại - FOREIGN KEY references users(id)]
    - branch_id: VARCHAR(36) [Khóa chính, Khóa ngoại - FOREIGN KEY references branches(branch_id)]

14. Bảng: audit_logs (Nhật ký kiểm toán hệ thống)
    - id: BIGINT [Khóa chính - PRIMARY KEY, Tự tăng]
    - user_id: BIGINT [Khóa ngoại - FOREIGN KEY references users(id) ON DELETE SET NULL]
    - user_name: VARCHAR(255) [Null - Tên người dùng tại thời điểm lưu log]
    - action: VARCHAR(255) [Không null - Tên hành động]
    - object_type: VARCHAR(100) [Null - Kiểu dữ liệu bị thay đổi]
    - object_id: VARCHAR(100) [Null - ID bản ghi bị thay đổi]
    - description: VARCHAR(500) [Null - Mô tả thay đổi chi tiết]
    - ip_address: VARCHAR(50) [Null - IP thiết bị thực hiện]
    - created_at: TIMESTAMP [Không null - Thời điểm hành động]

15. Bảng: employees (Hồ sơ nhân sự)
    - id: BIGINT [Khóa chính - PRIMARY KEY, Tự tăng]
    - user_id: BIGINT [Không null, Unique, Khóa ngoại - FOREIGN KEY references users(id)]
    - department: VARCHAR(100) [Không null - Bộ phận làm việc (Bàn, Bếp...)]
    - title: VARCHAR(100) [Không null - Chức danh nhân viên]
    - hire_date: DATE [Không null - Ngày vào làm chính thức]
    - base_salary: DOUBLE [Không null - Lương cứng cơ bản hoặc đơn giá giờ]
    - salary_type: VARCHAR(30) [Không null - Hình thức tính lương (Fixed hoặc Hourly)]
    - branch_id: VARCHAR(36) [Không null, Khóa ngoại - FOREIGN KEY references branches(branch_id)]

16. Bảng: employee_shift_assignments (Bảng phân ca trực)
    - id: BIGINT [Khóa chính - PRIMARY KEY, Tự tăng]
    - employee_id: BIGINT [Không null, Khóa ngoại - FOREIGN KEY references employees(id)]
    - shift_template_id: BIGINT [Không null, Khóa ngoại - FOREIGN KEY references shift_templates(id)]
    - date: DATE [Không null - Ngày trực]

17. Bảng: employee_attendances (Bảng chấm công)
    - id: BIGINT [Khóa chính - PRIMARY KEY, Tự tăng]
    - employee_id: BIGINT [Không null, Khóa ngoại - FOREIGN KEY references employees(id)]
    - date: DATE [Không null - Ngày chấm công]
    - clock_in: TIMESTAMP [Null - Thời điểm vào ca trực]
    - clock_out: TIMESTAMP [Null - Thời điểm ra ca trực]
    - is_late: BOOLEAN [Không null, Default False - Bị đi trễ]
    - is_early_leave: BOOLEAN [Không null, Default False - Bị về sớm]
    - hours_worked: DOUBLE [Không null, Default 0.0 - Số giờ làm việc thực tế]

18. Bảng: leave_requests (Đơn xin nghỉ phép)
    - id: BIGINT [Khóa chính - PRIMARY KEY, Tự tăng]
    - employee_id: BIGINT [Không null, Khóa ngoại - FOREIGN KEY references employees(id)]
    - start_date: DATE [Không null - Ngày bắt đầu nghỉ]
    - end_date: DATE [Không null - Ngày kết thúc nghỉ]
    - leave_type: VARCHAR(30) [Không null - ANNUAL, SICK, UNPAID]
    - reason: VARCHAR(500) [Không null - Lý do xin nghỉ phép]
    - status: VARCHAR(30) [Không null, Default 'PENDING' - Trạng thái duyệt]

19. Bảng: forgot_clock_requests (Đơn báo quên chấm công)
    - id: BIGINT [Khóa chính - PRIMARY KEY, Tự tăng]
    - employee_id: BIGINT [Không null, Khóa ngoại - FOREIGN KEY references employees(id)]
    - date: DATE [Không null - Ngày bị quên chấm công]
    - time_proposed: VARCHAR(10) [Không null - Giờ đề xuất bổ sung công]
    - clock_type: VARCHAR(10) [Không null - IN hoặc OUT]
    - reason: VARCHAR(500) [Không null - Giải trình quên chấm]
    - status: VARCHAR(30) [Không null, Default 'PENDING' - Trạng thái duyệt]

20. Bảng: payroll_entries (Bảng chốt lương nhân viên)
    - id: BIGINT [Khóa chính - PRIMARY KEY, Tự tăng]
    - payroll_run_id: BIGINT [Không null, Khóa ngoại - FOREIGN KEY references payroll_runs(id)]
    - employee_id: BIGINT [Không null, Khóa ngoại - FOREIGN KEY references employees(id)]
    - base_pay: DOUBLE [Không null - Lương ca làm thực tế chốt được]
    - allowances: DOUBLE [Không null, Default 0.0 - Tổng phụ cấp]
    - deductions: DOUBLE [Không null, Default 0.0 - Tổng tiền phạt/khấu trừ]
    - net_pay: DOUBLE [Không null - Thực lĩnh thực tế nhận được]

21. Bảng: products (Danh mục món ăn gốc)
    - id: BIGINT [Khóa chính - PRIMARY KEY, Tự tăng]
    - name: VARCHAR(255) [Không null - Tên món ăn]
    - description: TEXT [Null - Mô tả món ăn]
    - image_path: VARCHAR(255) [Null - Ảnh món ăn]
    - category_id: BIGINT [Không null, Khóa ngoại - FOREIGN KEY references categories(id)]
    - is_active: BOOLEAN [Không null, Default True - Trạng thái phục vụ]

22. Bảng: product_variants (Biến thể kích thước/topping món ăn)
    - id: BIGINT [Khóa chính - PRIMARY KEY, Tự tăng]
    - product_id: BIGINT [Không null, Khóa ngoại - FOREIGN KEY references products(id)]
    - name: VARCHAR(255) [Không null - Tên biến thể (Size M, Size L...)]
    - price: DOUBLE [Không null - Giá bán lẻ mặc định]
    - original_price: DOUBLE [Không null - Giá vốn sản xuất món ăn]
    - sku: VARCHAR(100) [Không null, Unique - Mã SKU quản lý bán]
    - is_topping: BOOLEAN [Không null, Default False - Là topping ăn kèm]

23. Bảng: branch_product_prices (Giá bán riêng của từng chi nhánh)
    - id: BIGINT [Khóa chính - PRIMARY KEY, Tự tăng]
    - branch_id: VARCHAR(36) [Không null, Khóa ngoại - FOREIGN KEY references branches(branch_id)]
    - variant_id: BIGINT [Không null, Khóa ngoại - FOREIGN KEY references product_variants(id)]
    - custom_price: DOUBLE [Không null - Giá bán lẻ áp dụng riêng chi nhánh]
    - is_available: BOOLEAN [Không null, Default True - Phục vụ món tại chi nhánh]

24. Bảng: branch_inventory (Tồn kho thực tế tại chi nhánh)
    - id: BIGINT [Khóa chính - PRIMARY KEY, Tự tăng]
    - branch_id: VARCHAR(36) [Không null, Khóa ngoại - FOREIGN KEY references branches(branch_id)]
    - item_id: BIGINT [Không null, Khóa ngoại - FOREIGN KEY references inventory_items(id)]
    - quantity: DOUBLE [Không null, Default 0.0 - Số lượng thực tồn]
    - reorder_point: DOUBLE [Không null, Default 10.0 - Ngưỡng tối thiểu của chi nhánh]

25. Bảng: product_stocks (Công thức định lượng - Recipes)
    - id: BIGINT [Khóa chính - PRIMARY KEY, Tự tăng]
    - variant_id: BIGINT [Không null, Khóa ngoại - FOREIGN KEY references product_variants(id)]
    - item_id: BIGINT [Không null, Khóa ngoại - FOREIGN KEY references inventory_items(id)]
    - quantity_needed: DOUBLE [Không null - Số lượng nguyên liệu hao phí cho 1 phần]

26. Bảng: branch_transfers (Phiếu chuyển kho liên cơ sở)
    - id: BIGINT [Khóa chính - PRIMARY KEY, Tự tăng]
    - source_branch_id: VARCHAR(36) [Không null, Khóa ngoại - FOREIGN KEY references branches(branch_id)]
    - target_branch_id: VARCHAR(36) [Không null, Khóa ngoại - FOREIGN KEY references branches(branch_id)]
    - status: VARCHAR(30) [Không null, Default 'PENDING' - Trạng thái chuyển PENDING, SHIPPED, RECEIVED]
    - request_date: TIMESTAMP [Không null - Ngày lập yêu cầu chuyển kho]
    - approve_date: TIMESTAMP [Null - Ngày được quản lý duyệt phiếu]
    - shipped_date: TIMESTAMP [Null - Ngày xuất hàng vận chuyển]

27. Bảng: branch_transfer_items (Chi tiết nguyên liệu chuyển kho)
    - id: BIGINT [Khóa chính - PRIMARY KEY, Tự tăng]
    - transfer_id: BIGINT [Không null, Khóa ngoại - FOREIGN KEY references branch_transfers(id)]
    - item_id: BIGINT [Không null, Khóa ngoại - FOREIGN KEY references inventory_items(id)]
    - quantity_shipped: DOUBLE [Không null - Số lượng thực xuất]
    - quantity_received: DOUBLE [Null - Số lượng thực nhận ở kho đích]
    - loss_quantity: DOUBLE [Null, Default 0.0 - Hao hụt vận chuyển]
    - loss_reason: VARCHAR(255) [Null - Lý do hao hụt]

28. Bảng: inventory_logs (Nhật ký biến động xuất nhập kho)
    - id: BIGINT [Khóa chính - PRIMARY KEY, Tự tăng]
    - branch_id: VARCHAR(36) [Không null, Khóa ngoại - FOREIGN KEY references branches(branch_id)]
    - item_id: BIGINT [Không null, Khóa ngoại - FOREIGN KEY references inventory_items(id)]
    - change_quantity: DOUBLE [Không null - Số lượng thay đổi]
    - type: VARCHAR(30) [Không null - STOCKIN, STOCKOUT]
    - reason: VARCHAR(255) [Null - Lý do thay đổi]
    - log_date: TIMESTAMP [Không null - Thời gian thay đổi]

29. Bảng: internal_purchase_orders (Đặt hàng nội bộ lên Kho tổng)
    - id: BIGINT [Khóa chính - PRIMARY KEY, Tự tăng]
    - ipo_code: VARCHAR(50) [Không null, Unique - Mã đơn đặt hàng nội bộ]
    - requester_branch_id: VARCHAR(36) [Không null, Khóa ngoại - FOREIGN KEY references branches(branch_id)]
    - warehouse_id: VARCHAR(36) [Không null, Khóa ngoại - FOREIGN KEY references branches(branch_id)]
    - status: VARCHAR(30) [Không null, Default 'SUBMITTED' - Trạng thái IPO]
    - order_date: TIMESTAMP [Không null - Ngày tạo phiếu]

30. Bảng: internal_purchase_order_items (Chi tiết đặt nội bộ)
    - id: BIGINT [Khóa chính - PRIMARY KEY, Tự tăng]
    - ipo_id: BIGINT [Không null, Khóa ngoại - FOREIGN KEY references internal_purchase_orders(id)]
    - item_id: BIGINT [Không null, Khóa ngoại - FOREIGN KEY references inventory_items(id)]
    - quantity: DOUBLE [Không null - Số lượng yêu cầu]

31. Bảng: rooms (Khu vực phòng ăn)
    - id: BIGINT [Khóa chính - PRIMARY KEY, Tự tăng]
    - name: VARCHAR(100) [Không null - Tên khu vực]
    - branch_id: VARCHAR(36) [Không null, Khóa ngoại - FOREIGN KEY references branches(branch_id)]

32. Bảng: tables (Bàn ăn chi tiết)
    - id: BIGINT [Khóa chính - PRIMARY KEY, Tự tăng]
    - name: VARCHAR(100) [Không null - Tên bàn]
    - room_id: BIGINT [Không null, Khóa ngoại - FOREIGN KEY references rooms(id)]
    - status: VARCHAR(30) [Không null, Default 'EMPTY' - EMPTY, OCCUPIED, RESERVED]
    - capacity: INTEGER [Không null, Default 4 - Sức chứa ghế]
    - guest_count: INTEGER [Không null, Default 0 - Số khách ngồi ăn thực tế]

33. Bảng: table_sessions (Phiên ăn phục vụ tại bàn)
    - id: BIGINT [Khóa chính - PRIMARY KEY, Tự tăng]
    - table_id: BIGINT [Không null, Khóa ngoại - FOREIGN KEY references tables(id)]
    - customer_phone: VARCHAR(15) [Null, Khóa ngoại - FOREIGN KEY references customers(phone)]
    - check_in_time: TIMESTAMP [Không null - Giờ mở bàn phục vụ]
    - check_out_time: TIMESTAMP [Null - Giờ thanh toán đóng bàn]
    - status: VARCHAR(30) [Không null, Default 'ACTIVE' - ACTIVE, COMPLETED, CANCELLED]
    - payment_status: VARCHAR(30) [Không null, Default 'UNPAID' - PAID, UNPAID]

34. Bảng: orders (Hóa đơn bán lẻ POS)
    - id: BIGINT [Khóa chính - PRIMARY KEY, Tự tăng]
    - session_id: BIGINT [Không null, Khóa ngoại - FOREIGN KEY references table_sessions(id)]
    - order_date: TIMESTAMP [Không null - Giờ tạo order]
    - status: VARCHAR(30) [Không null, Default 'PENDING' - PENDING, COOKING, SERVED...]
    - total_amount: DOUBLE [Không null, Default 0.0 - Tổng thanh toán cuối hóa đơn]
    - branch_id: VARCHAR(36) [Không null, Khóa ngoại - FOREIGN KEY references branches(branch_id)]

35. Bảng: order_details (Giỏ món ăn chi tiết hóa đơn)
    - id: BIGINT [Khóa chính - PRIMARY KEY, Tự tăng]
    - order_id: BIGINT [Không null, Khóa ngoại - FOREIGN KEY references orders(id)]
    - variant_id: BIGINT [Không null, Khóa ngoại - FOREIGN KEY references product_variants(id)]
    - quantity: INTEGER [Không null - Số lượng phần gọi mua]
    - status: VARCHAR(30) [Không null, Default 'PENDING' - PENDING, COOKING, READY...]
    - notes: VARCHAR(255) [Null - Ghi chú đặc biệt]
    - price: DOUBLE [Không null - Đơn giá món bán tại thời điểm mua]
    - is_deducted: BOOLEAN [Không null, Default False - Đã trừ tồn kho chi nhánh]

36. Bảng: cash_drawer_sessions (Ca két tiền thu ngân chi nhánh)
    - id: BIGINT [Khóa chính - PRIMARY KEY, Tự tăng]
    - branch_id: VARCHAR(36) [Không null, Khóa ngoại - FOREIGN KEY references branches(branch_id)]
    - cashier_id: BIGINT [Không null, Khóa ngoại - FOREIGN KEY references users(id)]
    - opening_time: TIMESTAMP [Không null - Thời điểm bắt đầu ca két]
    - closing_time: TIMESTAMP [Null - Thời điểm đóng ca két bàn giao]
    - opening_balance: DOUBLE [Không null - Tiền mặt thối thỏi bàn giao đầu ca]
    - expected_cash: DOUBLE [Không null, Default 0.0 - Tiền mặt hệ thống tính toán]
    - actual_cash: DOUBLE [Null - Tiền mặt thu ngân thực tế đếm được]
    - discrepancy: DOUBLE [Null - Chênh lệch đối soát (Actual - Expected)]
    - notes: TEXT [Null - Giải trình chênh lệch]

37. Bảng: loyalty_transactions (Lịch sử điểm tích lũy)
    - id: BIGINT [Khóa chính - PRIMARY KEY, Tự tăng]
    - customer_phone: VARCHAR(15) [Không null, Khóa ngoại - FOREIGN KEY references customers(phone)]
    - points: INTEGER [Không null - Điểm biến động (+/-)]
    - type: VARCHAR(30) [Không null - EARNED, REDEEMED]
    - transaction_date: TIMESTAMP [Không null - Thời điểm phát sinh điểm]
    - order_id: BIGINT [Null, Khóa ngoại - FOREIGN KEY references orders(id)]

38. Bảng: purchase_orders (Đơn mua hàng ngoài PO gửi Supplier)
    - id: BIGINT [Khóa chính - PRIMARY KEY, Tự tăng]
    - po_code: VARCHAR(50) [Không null, Unique - Mã đơn mua PO]
    - supplier_id: BIGINT [Không null, Khóa ngoại - FOREIGN KEY references suppliers(id)]
    - branch_id: VARCHAR(36) [Không null, Khóa ngoại - FOREIGN KEY references branches(branch_id)]
    - order_date: TIMESTAMP [Không null - Ngày đặt mua hàng]
    - delivery_deadline: DATE [Không null - Hạn giao hàng]
    - status: VARCHAR(30) [Không null, Default 'DRAFT' - DRAFT, APPROVED, RECEIVED...]
    - total_amount: DOUBLE [Không null - Trị giá đơn nhập PO]

39. Bảng: purchase_order_items (Chi tiết số lượng đặt mua PO)
    - id: BIGINT [Khóa chính - PRIMARY KEY, Tự tăng]
    - purchase_order_id: BIGINT [Không null, Khóa ngoại - FOREIGN KEY references purchase_orders(id)]
    - item_id: BIGINT [Không null, Khóa ngoại - FOREIGN KEY references inventory_items(id)]
    - quantity: DOUBLE [Không null - Số lượng đặt mua]
    - unit_price: DOUBLE [Không null - Đơn giá nhập hợp đồng]

40. Bảng: goods_receipts (Phiếu nhập kho thực tế kiểm nhận GRN)
    - id: BIGINT [Khóa chính - PRIMARY KEY, Tự tăng]
    - purchase_order_id: BIGINT [Không null, Khóa ngoại - FOREIGN KEY references purchase_orders(id)]
    - received_date: TIMESTAMP [Không null - Ngày thực tế nhận hàng]
    - received_by: VARCHAR(255) [Không null - Nhân viên kho nhận kiểm]

41. Bảng: goods_receipt_items (Chi tiết kiểm nhận nhập kho GRN)
    - id: BIGINT [Khóa chính - PRIMARY KEY, Tự tăng]
    - goods_receipt_id: BIGINT [Không null, Khóa ngoại - FOREIGN KEY references goods_receipts(id)]
    - item_id: BIGINT [Không null, Khóa ngoại - FOREIGN KEY references inventory_items(id)]
    - quantity_received: DOUBLE [Không null - Số lượng kiểm đạt chất lượng nhập]
    - quantity_rejected: DOUBLE [Không null, Default 0.0 - Số lượng bị loại trả]
    - notes: VARCHAR(255) [Null - Lý do hàng lỗi trả]

42. Bảng: promotion_branches (Liên kết Nhiều-Nhiều chi nhánh chạy ưu đãi)
    - promotion_id: BIGINT [Khóa ngoại - FOREIGN KEY references promotions(id)]
    - branch_id: VARCHAR(36) [Khóa ngoại - FOREIGN KEY references branches(branch_id)]

43. Bảng: promotion_usage (Lịch sử sử dụng coupon của khách)
    - id: BIGINT [Khóa chính - PRIMARY KEY, Tự tăng]
    - promotion_id: BIGINT [Không null, Khóa ngoại - FOREIGN KEY references promotions(id)]
    - order_id: BIGINT [Không null, Khóa ngoại - FOREIGN KEY references orders(id)]
    - customer_phone: VARCHAR(15) [Null, Khóa ngoại - FOREIGN KEY references customers(phone)]
    - discount_applied: DOUBLE [Không null - Tiền thực được giảm]
    - used_date: TIMESTAMP [Không null - Thời điểm áp dụng mã]
```
