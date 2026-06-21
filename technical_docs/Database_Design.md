# THIẾT KẾ CƠ SỞ DỮ LIỆU TOÀN DIỆN (COMPREHENSIVE DATABASE DESIGN)

**Hệ Thống Quản Lý Nhà Hàng Chuỗi - SWP391**

---

| Thông Tin Tài Liệu | Chi Tiết |
| --- | --- |
| **Dự án** | Hệ Thống Quản Lý Nhà Hàng Chuỗi (RMS - Restaurant Management System) |
| **Môn học** | SWP391 - Học kỳ 5, Đại học FPT |
| **Tài liệu** | Thiết Kế Cơ Sở Dữ Liệu & Từ Điển Dữ Liệu Toàn Diện (Database Design & Dictionary) |
| **Phiên bản** | 2.0.0 (Bản Toàn Diện Đầy Đủ 43 Bảng) |
| **Tác giả** | Nhóm phát triển dự án SWP391 |
| **Trạng thái** | Hoàn tất & Sẵn sàng báo cáo |

---

# 1. Tổng Quan Mô Hình Cơ Sở Dữ Liệu (Database Design Overview)

Hệ thống sử dụng cơ sở dữ liệu quan hệ **PostgreSQL 16+** làm nền tảng lưu trữ chính thức. Thiết kế cơ sở dữ liệu tuân thủ nghiêm ngặt chuẩn chuẩn hóa **3NF (Third Normal Form)** nhằm triệt tiêu dư thừa dữ liệu và đảm bảo tính toàn vẹn dữ liệu ở mức tối đa.

Tài liệu này đặc tả chi tiết cấu trúc vật lý của **toàn bộ 43 bảng dữ liệu** đang hoạt động trong hệ thống. Thiết kế cơ sở dữ liệu được xây dựng hoàn thiện xoay quanh các phân hệ cốt lõi sau:
1.  **Hội viên lấy Số điện thoại (SĐT) làm định danh Khóa chính**: Loại bỏ surrogate ID `id` của bảng `customers`. Khóa chính chính thức là `phone VARCHAR(15)`. Các bảng tham chiếu ngoại như `table_sessions`, `promotion_usage`, `loyalty_transactions` đều đổi cột khóa ngoại thành `customer_phone VARCHAR(15)` tham chiếu trực tiếp đến `customers(phone)`.
2.  **Động cơ Tích lũy & Thăng hạng tự động**: Hệ thống lưu trữ `total_spent` và `loyalty_points` để tự động nâng cấp hạng thẻ (Bronze, Silver, Gold, Platinum).
3.  **Vận hành Chuỗi Đa chi nhánh (Multi-Branch)**: Các thực thể kho hàng, bàn ăn, nhân sự, hóa đơn đều được phân tách logic bằng khóa ngoại `branch_id`. Bổ sung các bảng nâng cao như `branch_product_prices` (giá chi nhánh), `internal_purchase_orders` (đơn hàng nội bộ Kho tổng), `cash_drawer_sessions` (ca két tiền mặt thu ngân), `user_branches` (gán quản lý khu vực) và `promotion_branches` (khuyến mãi theo chi nhánh).

---

# 2. Ánh Xạ Nhóm Bảng Theo Phân Hệ Nghiệp Vụ (Epic Table Mapping)

Bảng dưới đây thống kê chi tiết sự phân chia **43 bảng dữ liệu** theo từng phân hệ (Epic) để nhóm dễ dàng giải trình và thuyết trình trước giảng viên môn học:

| Nhóm Bảng Dữ Liệu | Số Bảng | Phục Vụ Cho Epic Nghiệp Vụ | Mô Tả Chức Năng Cốt Lõi |
| --- | --- | --- | --- |
| `roles`, `users`, `user_roles`, `user_sessions`, `audit_logs`, `user_branches` | 6 | **Epic 1**: Xác thực & Phân quyền | Quản lý tài khoản, mã hóa mật khẩu, phân quyền vai trò (RBAC), theo dõi phiên làm việc và lịch sử audit logs. |
| `branches`, `branch_product_prices` | 2 | **Epic 2**: Quản lý Chi nhánh | Khai báo chi nhánh chuỗi, cấu hình thực đơn và giá bán tùy chỉnh riêng lẻ theo từng cơ sở địa phương. |
| `employees`, `shift_templates`, `employee_shift_assignments`, `employee_attendances`, `leave_requests`, `forgot_clock_requests`, `payroll_runs`, `payroll_entries` | 8 | **Epic 3**: Nhân sự & Tính lương | Hồ sơ nhân viên, phân lịch trực tuần, chấm công Clock In/Out, duyệt nghỉ phép, bổ sung công và chạy lương tháng. |
| `categories`, `products`, `product_variants`, `inventory_items`, `branch_inventory`, `product_stocks` (recipes), `branch_transfers`, `branch_transfer_items`, `inventory_logs`, `internal_purchase_orders`, `internal_purchase_order_items` | 11 | **Epic 4**: Quản lý Kho & Thực đơn | Quản lý thực đơn tổng, kho nguyên vật liệu thô của từng chi nhánh, định lượng công thức (Recipe), chuyển kho nội bộ và IPO lên Kho tổng. |
| `rooms`, `tables`, `table_sessions`, `orders`, `order_details`, `cash_drawer_sessions` | 6 | **Epic 5**: Bán hàng POS & Bàn ăn | Quản lý sơ đồ bàn, phiên phục vụ, hóa đơn bán hàng POS, gộp/tách bàn ăn, cổng thanh toán VNPay và ca két tiền mặt. |
| Không có bảng riêng (Dữ liệu WebSocket đẩy trực tiếp từ `order_details` sang KDS) | 0 | **Epic 6**: Hiển thị nhà bếp KDS | Đồng bộ WebSocket đẩy thông tin món cần làm xuống bếp thời gian thực. |
| `customers`, `loyalty_transactions` | 2 | **Epic 7**: Khách hàng thành viên | Quản lý hội viên **lấy SĐT làm khóa chính**, lưu lịch sử tích điểm và thăng hạng thẻ tự động theo doanh số. |
| `suppliers`, `purchase_orders`, `purchase_order_items`, `goods_receipts`, `goods_receipt_items` | 5 | **Epic 8**: Thu mua & Đối chiếu 3 bên | Quản lý nhà cung cấp ngoài, đơn đặt mua PO, phiếu nhập kho GRN, thuật toán đối chiếu 3 bên tránh thất thoát tài chính. |
| `promotions`, `promotion_branches`, `promotion_usage` | 3 | **Epic 9**: Khuyến mãi tự động | Thiết lập mã giảm giá, combo, B1G1 có giới hạn chi nhánh và lưu vết lịch sử áp dụng coupon của khách hàng. |
| Không có bảng riêng (Truy vấn dữ liệu tổng hợp qua `AIService`) | 0 | **Epic 10**: Báo cáo & Trợ lý AI | Biểu đồ doanh thu chuỗi, báo cáo hao hụt, chatbot AI Assistant tư vấn kinh doanh tích hợp Gemini. |

---

# 3. Chi Tiết Từ Điển Dữ Liệu Của Toàn Bộ 43 Bảng (Data Dictionary)

Dưới đây là đặc tả chi tiết cấu trúc vật lý của tất cả 43 bảng trong hệ thống, được phân nhóm theo 11 phân hệ nghiệp vụ để bảo đảm tính khoa học cao nhất.

---

## 3.1 PHÂN HỆ 1: XÁC THỰC & BẢO MẬT (AUTH)

### Bảng: roles (Vai trò phân quyền)
| Tên Cột (Column) | Kiểu Dữ Liệu | Khóa | Ràng Buộc | Mô Tả Chi Tiết |
| --- | --- | --- | --- | --- |
| id | BIGINT | PK | NOT NULL, Auto-Increment | Khóa chính tự tăng |
| name | VARCHAR(255) |  | NOT NULL, UNIQUE | Tên vai trò phân quyền (ADMIN, MANAGER, CASHIER...) |

### Bảng: users (Tài khoản người dùng)
| Tên Cột (Column) | Kiểu Dữ Liệu | Khóa | Ràng Buộc | Mô Tả Chi Tiết |
| --- | --- | --- | --- | --- |
| id | BIGINT | PK | NOT NULL, Auto-Increment | Khóa chính tự tăng |
| email | VARCHAR(255) |  | NOT NULL, UNIQUE | Địa chỉ email dùng để đăng nhập hệ thống |
| password | VARCHAR(255) |  | NOT NULL | Mật khẩu đã được băm mã hóa BCrypt |
| name | VARCHAR(255) |  | NOT NULL | Họ tên đầy đủ của nhân viên |
| two_factor_secret | VARCHAR(255) |  | NULL | Mã khóa bí mật dùng cho ứng dụng xác thực 2FA |
| is_two_factor_enabled | BOOLEAN |  | NOT NULL, DEFAULT FALSE | Trạng thái bật/tắt bảo mật xác thực 2 yếu tố 2FA |
| is_active | BOOLEAN |  | NOT NULL, DEFAULT TRUE | Trạng thái hoạt động của tài khoản |
| failed_login_attempts | INTEGER |  | NOT NULL, DEFAULT 0 | Số lần đăng nhập sai liên tiếp để khóa tài khoản |
| branch_id | VARCHAR(36) | FK | NULL, FK -> branches(branch_id) | Chi nhánh nhân viên trực thuộc (Wildcard NULL = Admin tổng) |

### Bảng: user_roles (Bảng liên kết Nhiều-Nhiều)
| Tên Cột (Column) | Kiểu Dữ Liệu | Khóa | Ràng Buộc | Mô Tả Chi Tiết |
| --- | --- | --- | --- | --- |
| user_id | BIGINT | PK, FK | NOT NULL, FK -> users(id) | Mã định danh tài khoản người dùng |
| role_id | BIGINT | PK, FK | NOT NULL, FK -> roles(id) | Mã định danh vai trò phân quyền |

### Bảng: user_sessions (Phiên làm việc đăng nhập)
| Tên Cột (Column) | Kiểu Dữ Liệu | Khóa | Ràng Buộc | Mô Tả Chi Tiết |
| --- | --- | --- | --- | --- |
| id | BIGINT | PK | NOT NULL, Auto-Increment | Khóa chính tự tăng |
| user_id | BIGINT | FK | NOT NULL, FK -> users(id) | Tài khoản người dùng đăng nhập |
| token | VARCHAR(255) |  | NOT NULL, UNIQUE | Chuỗi Access Token xác thực phiên truy cập |
| ip_address | VARCHAR(50) |  | NULL | Địa chỉ IP thiết bị khi người dùng đăng nhập |
| created_at | TIMESTAMP |  | NOT NULL | Thời điểm khởi tạo phiên đăng nhập |
| expires_at | TIMESTAMP |  | NOT NULL | Thời điểm phiên đăng nhập hết hiệu lực |

### Bảng: audit_logs (Nhật ký hoạt động bảo mật)
| Tên Cột (Column) | Kiểu Dữ Liệu | Khóa | Ràng Buộc | Mô Tả Chi Tiết |
| --- | --- | --- | --- | --- |
| id | BIGINT | PK | NOT NULL, Auto-Increment | Khóa chính tự tăng |
| user_id | BIGINT | FK | NULL, FK -> users(id) | Tài khoản nhân viên thực hiện hành động |
| user_name | VARCHAR(255) |  | NULL | Tên người dùng lưu vết trong nhật ký |
| action | VARCHAR(255) |  | NOT NULL | Tên hành động tác động (LOGIN, UPDATE_STOCK, DELETE_ORDER...) |
| object_type | VARCHAR(100) |  | NULL | Kiểu đối tượng bị tác động (USER, INVENTORY, ORDER...) |
| object_id | VARCHAR(100) |  | NULL | Mã khóa chính của đối tượng bị tác động |
| description | VARCHAR(500) |  | NULL | Mô tả chi tiết hành vi thay đổi dữ liệu |
| ip_address | VARCHAR(50) |  | NULL | Địa chỉ IP thiết bị phát sinh yêu cầu |
| created_at | TIMESTAMP |  | NOT NULL | Thời điểm phát sinh hành động ghi log |

### Bảng: user_branches (Liên kết Quản lý khu vực - Nhiều-Nhiều)
| Tên Cột (Column) | Kiểu Dữ Liệu | Khóa | Ràng Buộc | Mô Tả Chi Tiết |
| --- | --- | --- | --- | --- |
| user_id | BIGINT | PK, FK | NOT NULL, FK -> users(id) | Mã định danh tài khoản Quản lý khu vực |
| branch_id | VARCHAR(36) | PK, FK | NOT NULL, FK -> branches(branch_id) | Chi nhánh được giao quyền giám sát |

---

## 3.2 PHÂN HỆ 2: QUẢN LÝ CHI NHÁNH (BRANCH)

### Bảng: branches (Danh mục chi nhánh cửa hàng)
| Tên Cột (Column) | Kiểu Dữ Liệu | Khóa | Ràng Buộc | Mô Tả Chi Tiết |
| --- | --- | --- | --- | --- |
| branch_id | VARCHAR(36) | PK | NOT NULL | Khóa chính, mã UUID định danh chi nhánh |
| name | VARCHAR(255) |  | NOT NULL | Tên gọi hiển thị chi nhánh (Ví dụ: Chi nhánh Quận 1) |
| address | VARCHAR(255) |  | NULL | Địa chỉ vật lý chi tiết của chi nhánh |
| phone | VARCHAR(20) |  | NULL | Số điện thoại hotline liên hệ chi nhánh |
| is_active | BOOLEAN |  | NOT NULL, DEFAULT TRUE | Trạng thái hoạt động của chi nhánh |
| is_warehouse | BOOLEAN |  | NOT NULL, DEFAULT FALSE | Đánh dấu chi nhánh đóng vai trò là Kho tổng trung tâm |

### Bảng: branch_product_prices (Thực đơn và Giá bán riêng theo chi nhánh)
| Tên Cột (Column) | Kiểu Dữ Liệu | Khóa | Ràng Buộc | Mô Tả Chi Tiết |
| --- | --- | --- | --- | --- |
| id | BIGINT | PK | NOT NULL, Auto-Increment | Khóa chính tự tăng |
| branch_id | VARCHAR(36) | FK | NOT NULL, FK -> branches(branch_id) | Chi nhánh áp dụng thực đơn |
| variant_id | BIGINT | FK | NOT NULL, FK -> product_variants(id) | Biến thể món ăn cấu hình |
| custom_price | DOUBLE PRECISION |  | NOT NULL | Giá bán lẻ áp dụng riêng cho chi nhánh |
| is_available | BOOLEAN |  | NOT NULL, DEFAULT TRUE | Trạng thái hiển thị món bán tại chi nhánh |

---

## 3.3 PHÂN HỆ 3: NHÂN SỰ & CHẤM CÔNG (HR)

### Bảng: employees (Hồ sơ nhân viên)
| Tên Cột (Column) | Kiểu Dữ Liệu | Khóa | Ràng Buộc | Mô Tả Chi Tiết |
| --- | --- | --- | --- | --- |
| id | BIGINT | PK | NOT NULL, Auto-Increment | Khóa chính tự tăng |
| user_id | BIGINT | FK | NOT NULL, UNIQUE, FK -> users(id) | Tài khoản hệ thống liên kết với nhân viên |
| department | VARCHAR(100) |  | NOT NULL | Bộ phận nhân sự làm việc (Bàn, Thu ngân, Bếp...) |
| title | VARCHAR(100) |  | NOT NULL | Chức danh cụ thể (Quản lý, Phục vụ, Đầu bếp...) |
| hire_date | DATE |  | NOT NULL | Ngày nhân viên bắt đầu ký hợp đồng vào làm |
| base_salary | DOUBLE PRECISION |  | NOT NULL | Lương cơ bản làm căn cứ tính lương |
| salary_type | VARCHAR(30) |  | NOT NULL | Hình thức lương (Fixed: cố định, Hourly: tính theo giờ) |
| branch_id | VARCHAR(36) | FK | NOT NULL, FK -> branches(branch_id) | Chi nhánh làm việc chính thức của nhân viên |

### Bảng: shift_templates (Mẫu ca trực chuẩn)
| Tên Cột (Column) | Kiểu Dữ Liệu | Khóa | Ràng Buộc | Mô Tả Chi Tiết |
| --- | --- | --- | --- | --- |
| id | BIGINT | PK | NOT NULL, Auto-Increment | Khóa chính tự tăng |
| name | VARCHAR(100) |  | NOT NULL | Tên ca làm việc (Ca sáng, Ca chiều, Ca gãy...) |
| start_time | VARCHAR(10) |  | NOT NULL | Giờ bắt đầu ca trực (Định dạng HH:mm) |
| end_time | VARCHAR(10) |  | NOT NULL | Giờ kết thúc ca trực (Định dạng HH:mm) |
| duration_hours | DOUBLE PRECISION |  | NOT NULL | Số giờ công quy đổi làm việc của ca trực |

### Bảng: employee_shift_assignments (Phân lịch làm việc cho nhân viên)
| Tên Cột (Column) | Kiểu Dữ Liệu | Khóa | Ràng Buộc | Mô Tả Chi Tiết |
| --- | --- | --- | --- | --- |
| id | BIGINT | PK | NOT NULL, Auto-Increment | Khóa chính tự tăng |
| employee_id | BIGINT | FK | NOT NULL, FK -> employees(id) | Nhân viên được phân lịch trực |
| shift_template_id | BIGINT | FK | NOT NULL, FK -> shift_templates(id) | Ca trực được phân công |
| date | DATE |  | NOT NULL | Ngày trực cụ thể |

### Bảng: employee_attendances (Nhật ký chấm công thực tế)
| Tên Cột (Column) | Kiểu Dữ Liệu | Khóa | Ràng Buộc | Mô Tả Chi Tiết |
| --- | --- | --- | --- | --- |
| id | BIGINT | PK | NOT NULL, Auto-Increment | Khóa chính tự tăng |
| employee_id | BIGINT | FK | NOT NULL, FK -> employees(id) | Nhân viên thực hiện chấm công |
| date | DATE |  | NOT NULL | Ngày chấm công |
| clock_in | TIMESTAMP |  | NULL | Thời điểm ghi nhận vào ca làm việc |
| clock_out | TIMESTAMP |  | NULL | Thời điểm ghi nhận ra ca làm việc |
| is_late | BOOLEAN |  | NOT NULL, DEFAULT FALSE | Đánh dấu nhân viên đi muộn so với giờ ca mẫu |
| is_early_leave | BOOLEAN |  | NOT NULL, DEFAULT FALSE | Đánh dấu nhân viên về sớm trước giờ ca mẫu |
| hours_worked | DOUBLE PRECISION |  | NOT NULL, DEFAULT 0.0 | Số giờ làm việc thực tế ghi nhận được |

### Bảng: leave_requests (Đơn xin nghỉ phép trực tuyến)
| Tên Cột (Column) | Kiểu Dữ Liệu | Khóa | Ràng Buộc | Mô Tả Chi Tiết |
| --- | --- | --- | --- | --- |
| id | BIGINT | PK | NOT NULL, Auto-Increment | Khóa chính tự tăng |
| employee_id | BIGINT | FK | NOT NULL, FK -> employees(id) | Nhân viên xin nghỉ phép |
| start_date | DATE |  | NOT NULL | Ngày bắt đầu xin nghỉ |
| end_date | DATE |  | NOT NULL | Ngày kết thúc xin nghỉ |
| leave_type | VARCHAR(30) |  | NOT NULL | Loại nghỉ phép (ANNUAL: phép năm, SICK: ốm, UNPAID: không lương) |
| reason | VARCHAR(500) |  | NOT NULL | Lý do xin phép nghỉ phép |
| status | VARCHAR(30) |  | NOT NULL, DEFAULT 'PENDING' | Trạng thái duyệt đơn (`PENDING`, `APPROVED`, `REJECTED`) |

### Bảng: forgot_clock_requests (Đơn giải trình quên chấm công)
| Tên Cột (Column) | Kiểu Dữ Liệu | Khóa | Ràng Buộc | Mô Tả Chi Tiết |
| --- | --- | --- | --- | --- |
| id | BIGINT | PK | NOT NULL, Auto-Increment | Khóa chính tự tăng |
| employee_id | BIGINT | FK | NOT NULL, FK -> employees(id) | Nhân viên làm đơn giải trình |
| date | DATE |  | NOT NULL | Ngày quên chấm công |
| time_proposed | VARCHAR(10) |  | NOT NULL | Giờ đề xuất bổ sung công (Định dạng HH:mm) |
| clock_type | VARCHAR(10) |  | NOT NULL | Loại quên chấm công (`IN` - Chấm vào, `OUT` - Chấm ra) |
| reason | VARCHAR(500) |  | NOT NULL | Giải trình lý do quên chấm công |
| status | VARCHAR(30) |  | NOT NULL, DEFAULT 'PENDING' | Trạng thái duyệt đơn (`PENDING`, `APPROVED`, `REJECTED`) |

### Bảng: payroll_runs (Đợt chạy chốt lương tháng)
| Tên Cột (Column) | Kiểu Dữ Liệu | Khóa | Ràng Buộc | Mô Tả Chi Tiết |
| --- | --- | --- | --- | --- |
| id | BIGINT | PK | NOT NULL, Auto-Increment | Khóa chính tự tăng |
| period | VARCHAR(10) |  | NOT NULL | Kỳ chốt lương tháng (Định dạng YYYY-MM) |
| run_date | TIMESTAMP |  | NOT NULL | Ngày giờ thực hiện tính lương |
| run_by | VARCHAR(255) |  | NOT NULL | Tên tài khoản quản trị chạy chốt bảng lương |

### Bảng: payroll_entries (Phiếu tính lương chi tiết nhân viên)
| Tên Cột (Column) | Kiểu Dữ Liệu | Khóa | Ràng Buộc | Mô Tả Chi Tiết |
| --- | --- | --- | --- | --- |
| id | BIGINT | PK | NOT NULL, Auto-Increment | Khóa chính tự tăng |
| payroll_run_id | BIGINT | FK | NOT NULL, FK -> payroll_runs(id) | Thuộc đợt chạy tính lương |
| employee_id | BIGINT | FK | NOT NULL, FK -> employees(id) | Nhân viên được tính lương |
| base_pay | DOUBLE PRECISION |  | NOT NULL | Lương tính theo ca trực thực tế ghi nhận |
| allowances | DOUBLE PRECISION |  | NOT NULL, DEFAULT 0.0 | Tổng các khoản phụ cấp lương cộng thêm |
| deductions | DOUBLE PRECISION |  | NOT NULL, DEFAULT 0.0 | Tổng các khoản khấu trừ lương (phạt đi trễ, nghỉ...) |
| net_pay | DOUBLE PRECISION |  | NOT NULL | Lương thực lĩnh cuối cùng nhân viên được nhận |

---

## 3.4 PHÂN HỆ 4: QUẢN LÝ KHO & THỰC ĐƠN (INVENTORY & MENU)

### Bảng: categories (Danh mục món ăn thực đơn)
| Tên Cột (Column) | Kiểu Dữ Liệu | Khóa | Ràng Buộc | Mô Tả Chi Tiết |
| --- | --- | --- | --- | --- |
| id | BIGINT | PK | NOT NULL, Auto-Increment | Khóa chính tự tăng |
| name | VARCHAR(255) |  | NOT NULL, UNIQUE | Tên danh mục món ăn (Ví dụ: Khai vị, Lẩu, Đồ uống...) |

### Bảng: products (Danh mục món ăn gốc)
| Tên Cột (Column) | Kiểu Dữ Liệu | Khóa | Ràng Buộc | Mô Tả Chi Tiết |
| --- | --- | --- | --- | --- |
| id | BIGINT | PK | NOT NULL, Auto-Increment | Khóa chính tự tăng |
| name | VARCHAR(255) |  | NOT NULL | Tên gọi chính thức của món ăn |
| description | TEXT |  | NULL | Mô tả chi tiết nguyên liệu, cách làm món |
| image_path | VARCHAR(255) |  | NULL | Đường dẫn ảnh của món ăn lưu trữ trên server |
| category_id | BIGINT | FK | NOT NULL, FK -> categories(id) | Thuộc danh mục món ăn nào |
| is_active | BOOLEAN |  | NOT NULL, DEFAULT TRUE | Trạng thái kinh doanh phục vụ món ăn |

### Bảng: product_variants (Biến thể món ăn & Giá bán gốc)
| Tên Cột (Column) | Kiểu Dữ Liệu | Khóa | Ràng Buộc | Mô Tả Chi Tiết |
| --- | --- | --- | --- | --- |
| id | BIGINT | PK | NOT NULL, Auto-Increment | Khóa chính tự tăng |
| product_id | BIGINT | FK | NOT NULL, FK -> products(id) | Liên kết với sản phẩm món ăn gốc |
| name | VARCHAR(255) |  | NOT NULL | Tên biến thể (Size M, Size L, Thêm thạch...) |
| price | DOUBLE PRECISION |  | NOT NULL | Giá bán lẻ mặc định chưa chiết khấu |
| original_price | DOUBLE PRECISION |  | NOT NULL | Giá vốn sản xuất ước tính của món ăn |
| sku | VARCHAR(100) |  | NOT NULL, UNIQUE | Mã định danh quản lý bán hàng của biến thể |
| is_topping | BOOLEAN |  | NOT NULL, DEFAULT FALSE | Đánh dấu biến thể là món thêm ăn kèm |

### Bảng: inventory_items (Danh mục nguyên liệu thô)
| Tên Cột (Column) | Kiểu Dữ Liệu | Khóa | Ràng Buộc | Mô Tả Chi Tiết |
| --- | --- | --- | --- | --- |
| id | BIGINT | PK | NOT NULL, Auto-Increment | Khóa chính tự tăng |
| sku | VARCHAR(100) |  | NOT NULL, UNIQUE | Mã quản lý kho hàng của nguyên liệu |
| name | VARCHAR(255) |  | NOT NULL | Tên gọi nguyên vật liệu thô (Bột mì, Thịt bò...) |
| unit | VARCHAR(50) |  | NOT NULL | Đơn vị tính kho (kg, lít, lon, quả...) |
| minimum_threshold | DOUBLE PRECISION |  | NOT NULL, DEFAULT 5.0 | Ngưỡng tồn kho tối thiểu an toàn chung |

### Bảng: branch_inventory (Tồn kho thực tế của từng chi nhánh)
| Tên Cột (Column) | Kiểu Dữ Liệu | Khóa | Ràng Buộc | Mô Tả Chi Tiết |
| --- | --- | --- | --- | --- |
| id | BIGINT | PK | NOT NULL, Auto-Increment | Khóa chính tự tăng |
| branch_id | VARCHAR(36) | FK | NOT NULL, FK -> branches(branch_id) | Chi nhánh sở hữu kho |
| item_id | BIGINT | FK | NOT NULL, FK -> inventory_items(id) | Nguyên vật liệu quản lý tồn |
| quantity | DOUBLE PRECISION |  | NOT NULL, DEFAULT 0.0 | Số lượng thực tế đang tồn trong kho chi nhánh |
| reorder_point | DOUBLE PRECISION |  | NOT NULL, DEFAULT 10.0 | Mức cảnh báo tồn kho tối thiểu của chi nhánh |

### Bảng: product_stocks (Công thức định lượng - Recipes)
| Tên Cột (Column) | Kiểu Dữ Liệu | Khóa | Ràng Buộc | Mô Tả Chi Tiết |
| --- | --- | --- | --- | --- |
| id | BIGINT | PK | NOT NULL, Auto-Increment | Khóa chính tự tăng |
| variant_id | BIGINT | FK | NOT NULL, FK -> product_variants(id) | Biến thể món ăn áp dụng công thức |
| item_id | BIGINT | FK | NOT NULL, FK -> inventory_items(id) | Nguyên vật liệu thô sử dụng |
| quantity_needed | DOUBLE PRECISION |  | NOT NULL | Số lượng nguyên liệu cần hao phí cho 1 phần ăn |

### Bảng: branch_transfers (Phiếu điều chuyển kho liên chi nhánh)
| Tên Cột (Column) | Kiểu Dữ Liệu | Khóa | Ràng Buộc | Mô Tả Chi Tiết |
| --- | --- | --- | --- | --- |
| id | BIGINT | PK | NOT NULL, Auto-Increment | Khóa chính tự tăng |
| source_branch_id | VARCHAR(36) | FK | NOT NULL, FK -> branches(branch_id) | Chi nhánh xuất kho nguyên liệu đi |
| target_branch_id | VARCHAR(36) | FK | NOT NULL, FK -> branches(branch_id) | Chi nhánh nhận kho nguyên liệu đến |
| status | VARCHAR(30) |  | NOT NULL, DEFAULT 'PENDING' | Trạng thái chuyển (`PENDING`, `SHIPPED`, `RECEIVED`) |
| request_date | TIMESTAMP |  | NOT NULL | Ngày lập yêu cầu điều chuyển kho |
| approve_date | TIMESTAMP |  | NULL | Ngày quản lý phê duyệt phiếu xuất kho |
| shipped_date | TIMESTAMP |  | NULL | Ngày bắt đầu xuất kho vận chuyển |

### Bảng: branch_transfer_items (Chi tiết số lượng điều chuyển kho)
| Tên Cột (Column) | Kiểu Dữ Liệu | Khóa | Ràng Buộc | Mô Tả Chi Tiết |
| --- | --- | --- | --- | --- |
| id | BIGINT | PK | NOT NULL, Auto-Increment | Khóa chính tự tăng |
| transfer_id | BIGINT | FK | NOT NULL, FK -> branch_transfers(id) | Thu thuộc phiếu điều chuyển kho nào |
| item_id | BIGINT | FK | NOT NULL, FK -> inventory_items(id) | Nguyên vật liệu điều chuyển |
| quantity_shipped | DOUBLE PRECISION |  | NOT NULL | Số lượng thực tế gửi đi từ kho nguồn |
| quantity_received | DOUBLE PRECISION |  | NULL | Số lượng thực tế nhận được tại kho đích |
| loss_quantity | DOUBLE PRECISION |  | NULL, DEFAULT 0.0 | Hao hụt trong quá trình vận chuyển |
| loss_reason | VARCHAR(255) |  | NULL | Lý do chênh lệch hao hụt hàng hóa |

### Bảng: inventory_logs (Nhật ký biến động tồn kho chi nhánh)
| Tên Cột (Column) | Kiểu Dữ Liệu | Khóa | Ràng Buộc | Mô Tả Chi Tiết |
| --- | --- | --- | --- | --- |
| id | BIGINT | PK | NOT NULL, Auto-Increment | Khóa chính tự tăng |
| branch_id | VARCHAR(36) | FK | NOT NULL, FK -> branches(branch_id) | Chi nhánh phát sinh biến động kho |
| item_id | BIGINT | FK | NOT NULL, FK -> inventory_items(id) | Nguyên liệu thô biến động số lượng |
| change_quantity | DOUBLE PRECISION |  | NOT NULL | Số lượng biến động (+/-) |
| type | VARCHAR(30) |  | NOT NULL | Loại biến động (`STOCKIN` - Nhập, `STOCKOUT` - Xuất) |
| reason | VARCHAR(255) |  | NULL | Lý do thay đổi (Bán hàng, Điều chỉnh thủ công, Hỏng...) |
| log_date | TIMESTAMP |  | NOT NULL | Thời điểm lưu nhật ký |

### Bảng: internal_purchase_orders (Đơn đặt hàng bổ sung nội bộ Kho tổng)
| Tên Cột (Column) | Kiểu Dữ Liệu | Khóa | Ràng Buộc | Mô Tả Chi Tiết |
| --- | --- | --- | --- | --- |
| id | BIGINT | PK | NOT NULL, Auto-Increment | Khóa chính đơn đặt hàng nội bộ |
| ipo_code | VARCHAR(50) |  | NOT NULL, UNIQUE | Mã đơn hàng nội bộ (Ví dụ: `IPO-2026-042`) |
| requester_branch_id | VARCHAR(36) | FK | NOT NULL, FK -> branches(branch_id) | Chi nhánh con yêu cầu bổ sung hàng |
| warehouse_id | VARCHAR(36) | FK | NOT NULL, FK -> branches(branch_id) | Chi nhánh Kho tổng xử lý yêu cầu |
| status | VARCHAR(30) |  | NOT NULL, DEFAULT 'SUBMITTED' | Trạng thái đơn (`DRAFT`, `SUBMITTED`, `APPROVED`, `SHIPPED`, `COMPLETED`) |
| order_date | TIMESTAMP |  | NOT NULL | Ngày tạo phiếu yêu cầu |

### Bảng: internal_purchase_order_items (Chi tiết đặt hàng nội bộ)
| Tên Cột (Column) | Kiểu Dữ Liệu | Khóa | Ràng Buộc | Mô Tả Chi Tiết |
| --- | --- | --- | --- | --- |
| id | BIGINT | PK | NOT NULL, Auto-Increment | Khóa chính tự tăng |
| ipo_id | BIGINT | FK | NOT NULL, FK -> internal_purchase_orders(id) | Liên kết với đơn đặt nội bộ gốc |
| item_id | BIGINT | FK | NOT NULL, FK -> inventory_items(id) | Mặt hàng nguyên liệu yêu cầu |
| quantity | DOUBLE PRECISION |  | NOT NULL | Số lượng yêu cầu bổ sung |

---

## 3.5 PHÂN HỆ 5: BÁN HÀNG POS & BÀN ĂN (POS & BILLING)

### Bảng: rooms (Khu vực / Phòng ăn nhà hàng)
| Tên Cột (Column) | Kiểu Dữ Liệu | Khóa | Ràng Buộc | Mô Tả Chi Tiết |
| --- | --- | --- | --- | --- |
| id | BIGINT | PK | NOT NULL, Auto-Increment | Khóa chính tự tăng |
| name | VARCHAR(100) |  | NOT NULL | Tên khu vực (Sân trước, Phòng lạnh lầu 1...) |
| branch_id | VARCHAR(36) | FK | NOT NULL, FK -> branches(branch_id) | Thuộc chi nhánh nào sở hữu |

### Bảng: tables (Danh mục bàn ăn chi tiết)
| Tên Cột (Column) | Kiểu Dữ Liệu | Khóa | Ràng Buộc | Mô Tả Chi Tiết |
| --- | --- | --- | --- | --- |
| id | BIGINT | PK | NOT NULL, Auto-Increment | Khóa chính tự tăng |
| name | VARCHAR(100) |  | NOT NULL | Tên bàn ăn (Ví dụ: Bàn 01) |
| room_id | BIGINT | FK | NOT NULL, FK -> rooms(id) | Thuộc khu vực phòng ăn nào |
| status | VARCHAR(30) |  | NOT NULL, DEFAULT 'EMPTY' | Trạng thái bàn trực quan (`EMPTY`, `OCCUPIED`, `RESERVED`) |
| capacity | INTEGER |  | NOT NULL, DEFAULT 4 | Sức chứa số lượng ghế khách của bàn ăn |
| guest_count | INTEGER |  | NOT NULL, DEFAULT 0 | Số khách thực tế đang ngồi ăn tại bàn |

### Bảng: table_sessions (Phiên phục vụ khách tại bàn ăn)
| Tên Cột (Column) | Kiểu Dữ Liệu | Khóa | Ràng Buộc | Mô Tả Chi Tiết |
| --- | --- | --- | --- | --- |
| id | BIGINT | PK | NOT NULL, Auto-Increment | Khóa chính tự tăng |
| table_id | BIGINT | FK | NOT NULL, FK -> tables(id) | Liên kết với bàn ăn vật lý |
| **customer_phone** | VARCHAR(15) | FK | NULL, FK -> customers(phone) | **Khóa ngoại liên kết số điện thoại khách hàng hội viên** |
| check_in_time | TIMESTAMP |  | NOT NULL | Thời điểm bắt đầu mở bàn phục vụ |
| check_out_time | TIMESTAMP |  | NULL | Thời điểm thanh toán đóng phiên bàn ăn |
| status | VARCHAR(30) |  | NOT NULL, DEFAULT 'ACTIVE' | Trạng thái phiên phục vụ (`ACTIVE`, `COMPLETED`, `CANCELLED`) |
| payment_status | VARCHAR(30) |  | NOT NULL, DEFAULT 'UNPAID' | Trạng thái thanh toán (`PAID`, `UNPAID`) |

### Bảng: orders (Hóa đơn bán hàng POS)
| Tên Cột (Column) | Kiểu Dữ Liệu | Khóa | Ràng Buộc | Mô Tả Chi Tiết |
| --- | --- | --- | --- | --- |
| id | BIGINT | PK | NOT NULL, Auto-Increment | Khóa chính tự tăng |
| session_id | BIGINT | FK | NOT NULL, FK -> table_sessions(id) | Thuộc phiên bàn ăn phục vụ nào |
| order_date | TIMESTAMP |  | NOT NULL | Thời gian bắt đầu gọi món |
| status | VARCHAR(30) |  | NOT NULL, DEFAULT 'PENDING' | Trạng thái hóa đơn (`PENDING`, `COOKING`, `SERVED`, `CANCELLED`) |
| total_amount | DOUBLE PRECISION |  | NOT NULL, DEFAULT 0.0 | Tổng tiền hóa đơn thanh toán cuối cùng (sau giảm) |
| branch_id | VARCHAR(36) | FK | NOT NULL, FK -> branches(branch_id) | Chi nhánh phát sinh giao dịch bán hàng |

### Bảng: order_details (Giỏ hàng món ăn chi tiết hóa đơn)
| Tên Cột (Column) | Kiểu Dữ Liệu | Khóa | Ràng Buộc | Mô Tả Chi Tiết |
| --- | --- | --- | --- | --- |
| id | BIGINT | PK | NOT NULL, Auto-Increment | Khóa chính tự tăng |
| order_id | BIGINT | FK | NOT NULL, FK -> orders(id) | Thuộc hóa đơn bán hàng nào |
| variant_id | BIGINT | FK | NOT NULL, FK -> product_variants(id) | Biến thể món ăn khách gọi |
| quantity | INTEGER |  | NOT NULL | Số lượng phần ăn đặt mua |
| status | VARCHAR(30) |  | NOT NULL, DEFAULT 'PENDING' | Trạng thái làm món ở bếp (`PENDING`, `COOKING`, `READY`, `SERVED`) |
| notes | VARCHAR(255) |  | NULL | Ghi chú chế biến đặc thù (Không hành, cay...) |
| price | DOUBLE PRECISION |  | NOT NULL | Đơn giá tại thời điểm giao dịch bán hàng |
| is_deducted | BOOLEAN |  | NOT NULL, DEFAULT FALSE | Trạng thái khấu trừ nguyên liệu trong kho chi nhánh |

### Bảng: cash_drawer_sessions (Phiên ca két tiền thu ngân đối soát)
| Tên Cột (Column) | Kiểu Dữ Liệu | Khóa | Ràng Buộc | Mô Tả Chi Tiết |
| --- | --- | --- | --- | --- |
| id | BIGINT | PK | NOT NULL, Auto-Increment | Khóa chính tự tăng |
| branch_id | VARCHAR(36) | FK | NOT NULL, FK -> branches(branch_id) | Chi nhánh thu ngân trực thuộc |
| cashier_id | BIGINT | FK | NOT NULL, FK -> users(id) | Nhân viên thu ngân mở ca két |
| opening_time | TIMESTAMP |  | NOT NULL | Thời điểm mở ca bắt đầu két |
| closing_time | TIMESTAMP |  | NULL | Thời điểm chốt ca bàn giao két |
| opening_balance | DOUBLE PRECISION |  | NOT NULL | Tiền mặt ban đầu bàn giao thối thỏi |
| expected_cash | DOUBLE PRECISION |  | NOT NULL, DEFAULT 0.0 | Tiền mặt hệ thống tính toán |
| actual_cash | DOUBLE PRECISION |  | NULL | Tiền mặt thực tế thu ngân đếm cuối ca |
| discrepancy | DOUBLE PRECISION |  | NULL | Chênh lệch tiền mặt đối soát (`Actual - Expected`) |
| notes | TEXT |  | NULL | Ghi chú giải trình chênh lệch thất thoát |

---

## 3.6 PHÂN HỆ 7: KHÁCH HÀNG THÀNH VIÊN (LOYALTY)

### Bảng: customers (Hồ sơ khách hàng thành viên)
| Tên Cột (Column) | Kiểu Dữ Liệu | Khóa | Ràng Buộc | Mô Tả Chi Tiết |
| --- | --- | --- | --- | --- |
| **phone** | VARCHAR(15) | **PK** | NOT NULL, UNIQUE | **Số điện thoại khách hàng, đóng vai trò làm Khóa chính duy nhất.** |
| name | VARCHAR(255) |  | NOT NULL | Họ tên đầy đủ của hội viên |
| birth_date | DATE |  | NULL | Ngày sinh nhật hội viên |
| membership_tier | VARCHAR(20) |  | NOT NULL, DEFAULT 'Bronze' | Hạng thẻ thành viên (Bronze, Silver, Gold, Platinum) |
| loyalty_points | INTEGER |  | NOT NULL, DEFAULT 0 | Điểm tích lũy hiện có khả dụng của khách |
| total_spent | DOUBLE PRECISION |  | NOT NULL, DEFAULT 0.0 | **Tổng chi tiêu tích lũy qua các hóa đơn (để nâng hạng thẻ)** |

### Bảng: loyalty_transactions (Lịch sử biến động điểm thưởng)
| Tên Cột (Column) | Kiểu Dữ Liệu | Khóa | Ràng Buộc | Mô Tả Chi Tiết |
| --- | --- | --- | --- | --- |
| id | BIGINT | PK | NOT NULL, Auto-Increment | Khóa chính tự tăng |
| **customer_phone** | VARCHAR(15) | FK | NOT NULL, FK -> customers(phone) | **Khóa ngoại liên kết số điện thoại khách hàng** |
| points | INTEGER |  | NOT NULL | Số điểm biến động (+/-) |
| type | VARCHAR(30) |  | NOT NULL | Loại biến động (`EARNED` - cộng, `REDEEMED` - quy đổi tiền) |
| transaction_date | TIMESTAMP |  | NOT NULL | Thời điểm biến động điểm |
| order_id | BIGINT | FK | NULL, FK -> orders(id) | Hóa đơn liên kết phát sinh điểm thưởng |

---

## 3.7 PHÂN HỆ 8: THU MUA & NHÀ CUNG CẤP (PROCUREMENT)

### Bảng: suppliers (Danh mục nhà cung cấp ngoài)
| Tên Cột (Column) | Kiểu Dữ Liệu | Khóa | Ràng Buộc | Mô Tả Chi Tiết |
| --- | --- | --- | --- | --- |
| id | BIGINT | PK | NOT NULL, Auto-Increment | Khóa chính tự tăng |
| code | VARCHAR(50) |  | NOT NULL, UNIQUE | Mã viết tắt nhà cung cấp (Ví dụ: `SUP-001`) |
| name | VARCHAR(255) |  | NOT NULL | Tên gọi nhà cung cấp thô |
| contact_email | VARCHAR(255) |  | NULL | Email liên hệ đặt hàng chính thức |
| phone | VARCHAR(20) |  | NULL | Số điện thoại liên hệ |
| address | VARCHAR(255) |  | NULL | Địa chỉ trụ sở nhà cung cấp |

### Bảng: purchase_orders (Đơn đặt mua PO gửi nhà cung cấp ngoài)
| Tên Cột (Column) | Kiểu Dữ Liệu | Khóa | Ràng Buộc | Mô Tả Chi Tiết |
| --- | --- | --- | --- | --- |
| id | BIGINT | PK | NOT NULL, Auto-Increment | Khóa chính tự tăng |
| po_code | VARCHAR(50) |  | NOT NULL, UNIQUE | Mã phiếu đặt hàng tự phát sinh (Ví dụ: `PO-2026-001`) |
| supplier_id | BIGINT | FK | NOT NULL, FK -> suppliers(id) | Nhà cung cấp đặt mua hàng |
| branch_id | VARCHAR(36) | FK | NOT NULL, FK -> branches(branch_id) | Chi nhánh con nhận hàng |
| order_date | TIMESTAMP |  | NOT NULL | Ngày lập đơn mua hàng |
| delivery_deadline | DATE |  | NOT NULL | Hạn chót nhà cung cấp phải giao hàng |
| status | VARCHAR(30) |  | NOT NULL, DEFAULT 'DRAFT' | Trạng thái đơn (`DRAFT`, `APPROVED`, `RECEIVED`, `CANCELLED`) |
| total_amount | DOUBLE PRECISION |  | NOT NULL | Tổng tiền tạm tính đơn hàng nhập |

### Bảng: purchase_order_items (Chi tiết đặt mua nguyên liệu PO)
| Tên Cột (Column) | Kiểu Dữ Liệu | Khóa | Ràng Buộc | Mô Tả Chi Tiết |
| --- | --- | --- | --- | --- |
| id | BIGINT | PK | NOT NULL, Auto-Increment | Khóa chính tự tăng |
| purchase_order_id | BIGINT | FK | NOT NULL, FK -> purchase_orders(id) | Thuộc đơn mua hàng PO nào |
| item_id | BIGINT | FK | NOT NULL, FK -> inventory_items(id) | Nguyên vật liệu thô đặt nhập |
| quantity | DOUBLE PRECISION |  | NOT NULL | Số lượng nguyên liệu đặt mua |
| unit_price | DOUBLE PRECISION |  | NOT NULL | Đơn giá nhập khẩu cam kết trong hợp đồng |

### Bảng: goods_receipts (Phiếu kiểm nhận hàng nhập kho GRN)
| Tên Cột (Column) | Kiểu Dữ Liệu | Khóa | Ràng Buộc | Mô Tả Chi Tiết |
| --- | --- | --- | --- | --- |
| id | BIGINT | PK | NOT NULL, Auto-Increment | Khóa chính tự tăng |
| purchase_order_id | BIGINT | FK | NOT NULL, FK -> purchase_orders(id) | Liên kết với đơn đặt mua PO gốc đối soát |
| received_date | TIMESTAMP |  | NOT NULL | Ngày giờ thực nhận hàng tại kho chi nhánh |
| received_by | VARCHAR(255) |  | NOT NULL | Tên nhân viên kho thực hiện kiểm nhận |

### Bảng: goods_receipt_items (Chi tiết kiểm nhận nhập kho GRN thực tế)
| Tên Cột (Column) | Kiểu Dữ Liệu | Khóa | Ràng Buộc | Mô Tả Chi Tiết |
| --- | --- | --- | --- | --- |
| id | BIGINT | PK | NOT NULL, Auto-Increment | Khóa chính tự tăng |
| goods_receipt_id | BIGINT | FK | NOT NULL, FK -> goods_receipts(id) | Thuộc phiếu nhập kho GRN nào |
| item_id | BIGINT | FK | NOT NULL, FK -> inventory_items(id) | Nguyên vật liệu thô thực nhận |
| quantity_received | DOUBLE PRECISION |  | NOT NULL | Số lượng thực tế đạt chất lượng nhập kho |
| quantity_rejected | DOUBLE PRECISION |  | NOT NULL, DEFAULT 0.0 | Số lượng bị lỗi từ chối nhập |
| notes | VARCHAR(255) |  | NULL | Ghi chú lý do từ chối lỗi hàng hóa |

---

## 3.8 PHÂN HỆ 9: CHƯƠNG TRÌNH KHUYẾN MÃI (PROMOTION)

### Bảng: promotions (Cấu hình chương trình khuyến mãi)
| Tên Cột (Column) | Kiểu Dữ Liệu | Khóa | Ràng Buộc | Mô Tả Chi Tiết |
| --- | --- | --- | --- | --- |
| id | BIGINT | PK | NOT NULL, Auto-Increment | Khóa chính tự tăng |
| name | VARCHAR(255) |  | NOT NULL | Tên chương trình tiếp thị khuyến mãi |
| promo_code | VARCHAR(100) |  | NOT NULL, UNIQUE | Mã coupon giảm giá khách hàng nhập |
| type | VARCHAR(30) |  | NOT NULL | Loại ưu đãi (`PercentDiscount`, `FlatDiscount`, `Buy1Get1`) |
| discount_value | DOUBLE PRECISION |  | NOT NULL | Số tiền giảm hoặc tỷ lệ % giảm giá |
| min_order_value | DOUBLE PRECISION |  | NOT NULL, DEFAULT 0.0 | Giá trị hóa đơn tối thiểu bắt buộc để áp mã |
| max_usage_count | INTEGER |  | NOT NULL, DEFAULT 100 | Lượt dùng giới hạn tối đa của coupon |
| current_usage_count | INTEGER |  | NOT NULL, DEFAULT 0 | Lượt thực tế khách hàng đã sử dụng |
| start_date | DATE |  | NOT NULL | Ngày khuyến mãi bắt đầu có hiệu lực |
| end_date | DATE |  | NOT NULL | Ngày khuyến mãi hết hiệu lực |
| is_active | BOOLEAN |  | NOT NULL, DEFAULT TRUE | Trạng thái kích hoạt chương trình |
| trigger_product_id | BIGINT |  | NULL | Mã sản phẩm mua điều kiện kích hoạt B1G1 |
| reward_product_id | BIGINT |  | NULL | Mã sản phẩm được tặng kèm miễn phí trong B1G1 |

### Bảng: promotion_branches (Khuyến mãi áp dụng theo chi nhánh - Nhiều-Nhiều)
| Tên Cột (Column) | Kiểu Dữ Liệu | Khóa | Ràng Buộc | Mô Tả Chi Tiết |
| --- | --- | --- | --- | --- |
| promotion_id | BIGINT | PK, FK | NOT NULL, FK -> promotions(id) | Chương trình khuyến mãi cấu hình |
| branch_id | VARCHAR(36) | PK, FK | NOT NULL, FK -> branches(branch_id) | Chi nhánh có hiệu lực áp dụng chương trình |

### Bảng: promotion_usage (Lịch sử sử dụng khuyến mãi của khách)
| Tên Cột (Column) | Kiểu Dữ Liệu | Khóa | Ràng Buộc | Mô Tả Chi Tiết |
| --- | --- | --- | --- | --- |
| id | BIGINT | PK | NOT NULL, Auto-Increment | Khóa chính tự tăng |
| promotion_id | BIGINT | FK | NOT NULL, FK -> promotions(id) | Chương trình khuyến mãi áp dụng |
| order_id | BIGINT | FK | NOT NULL, FK -> orders(id) | Hóa đơn được chiết khấu giảm giá |
| **customer_phone** | VARCHAR(15) | FK | NULL, FK -> customers(phone) | **Khóa ngoại liên kết số điện thoại khách hàng** |
| discount_applied | DOUBLE PRECISION |  | NOT NULL | Số tiền được giảm giá thực tế |
| used_date | TIMESTAMP |  | NOT NULL | Thời điểm áp dụng chiết khấu |

---

# 4. Các Ràng Buộc Toàn Vẹn Tham Chiếu Hệ Thống (Referential Integrity Constraints)

Để bảo đảm tính nhất quán dữ liệu giao dịch chéo giữa các phân hệ (Epics), cơ sở dữ liệu thiết lập các cơ chế ràng buộc khóa ngoại (Referential Integrity) cực kỳ nghiêm ngặt:

1.  ** branches -> table_sessions / inventory / employees (`ON DELETE RESTRICT`)**: Ngăn chặn tuyệt đối việc xóa thông tin Chi nhánh khi đang có đơn hàng, tồn kho nguyên vật liệu thô hoặc nhân sự hoạt động gán cho chi nhánh đó. Điều này bảo vệ an toàn tính nhất quán dữ liệu của phân hệ Đa chi nhánh.
2.  ** orders -> order_details (`ON DELETE CASCADE`)**: Khi một hóa đơn bán hàng POS bị xóa (hoặc hủy phiên lỗi), toàn bộ các món ăn chi tiết trong hóa đơn đó tự động được xóa sạch để giải phóng bộ nhớ và tránh dữ liệu rác.
3.  ** customers -> loyalty_transactions / table_sessions (`ON DELETE RESTRICT`)**: Không cho phép xóa thông tin hội viên ra khỏi cơ sở dữ liệu nếu hội viên đó đã từng phát sinh các giao dịch tích lũy điểm thưởng hoặc có lịch sử thanh toán hóa đơn cũ nhằm bảo toàn dữ liệu báo cáo kế toán.
4.  ** purchase_orders -> goods_receipts (`ON DELETE RESTRICT`)**: Bảo toàn hồ sơ thu mua ngoài, ngăn cấm xóa đơn hàng PO khi đã lập phiếu kiểm nhận nhập kho GRN thực tế.
