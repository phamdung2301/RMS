KẾ HOẠCH NÂNG CẤP HỆ THỐNG PHÂN CHI NHÁNH
(MULTI-BRANCH UPGRADE PLAN)

Tài liệu đặc tả kiến trúc, cơ sở dữ liệu và API nâng cấp chuỗi nhà hàng LiteFlow POS


# I. GIỚI THIỆU TỔNG QUAN

Nhằm đáp ứng nhu cầu mở rộng quy mô kinh doanh từ một cửa hàng đơn lẻ thành chuỗi nhà hàng (F&B Chain) hoạt động đồng bộ, tài liệu này đặc tả kế hoạch nâng cấp các phân hệ nghiệp vụ liên quan đến quản lý đa chi nhánh (Multi-branch). Kế hoạch tập trung giải quyết các bài toán về tối ưu hóa dòng tiền, kiểm soát chặt chẽ hao hụt nguyên vật liệu, điều phối chuỗi cung ứng nội bộ và nâng cao tính bảo mật dữ liệu giữa các chi nhánh.


# II. CHI TIẾT CÁC PHÂN HỆ NÂNG CẤP


## 1. Thực Đơn & Giá Bán Theo Chi Nhánh (Branch-Specific Menu & Pricing)

Nghiệp vụ chi tiết:

Thiết lập danh mục món ăn riêng biệt: Cho phép mỗi chi nhánh cấu hình trạng thái hiển thị món ăn tùy thuộc vào nguồn cung nguyên liệu tại chỗ.

Chính sách giá linh hoạt: Hỗ trợ cấu hình giá bán (bán lẻ, giá gốc) của từng biến thể món ăn (`ProductVariant`) theo từng chi nhánh để bù đắp sự chênh lệch chi phí mặt bằng hoặc vận chuyển giữa các vùng miền.

Thiết kế Cơ sở Dữ liệu (Database Schema):

Tạo bảng mới `branch_product_prices` để lưu thông tin cấu hình giá và trạng thái món ăn theo chi nhánh:


| Trường (Field) | Kiểu dữ liệu | Ràng buộc | Mô tả |
| --- | --- | --- | --- |
| id | BIGINT | PK, Auto Increment | Khoá chính |
| branch_id | VARCHAR(36) | FK references branches | Mã chi nhánh áp dụng |
| variant_id | BIGINT | FK references product_variants | Mã biến thể món ăn |
| custom_price | DOUBLE PRECISION | NOT NULL | Giá bán áp dụng riêng cho chi nhánh |
| is_available | BOOLEAN | DEFAULT TRUE | Trạng thái phục vụ món tại chi nhánh |

Thiết kế API (REST Endpoints):


| HTTP Method | Path | Quyền truy cập | Mô tả chức năng |
| --- | --- | --- | --- |
| GET | /api/branches/{branchId}/menu | Manager, Cashier | Lấy thực đơn áp dụng riêng cho chi nhánh |
| POST | /api/branches/{branchId}/menu/config | Admin, Manager | Cấu hình giá bán và trạng thái món ăn cho chi nhánh |


## 2. Quản Lý Kho Tổng (Central Warehouse & Supply Chain)

Nghiệp vụ chi tiết:

Vai trò Kho tổng: Khai báo thực thể Kho tổng (Central Warehouse) với chức năng nhận hàng số lượng lớn từ nhà cung cấp, lưu trữ, sơ chế và phân phối nguyên liệu xuống các chi nhánh con.

Quy trình Đặt hàng nội bộ (Internal Purchase Order - IPO): Chi nhánh con tạo yêu cầu bổ sung nguyên liệu gửi lên Kho tổng thay vì đặt hàng trực tiếp từ nhà cung cấp ngoài. Kho tổng duyệt yêu cầu và lên kế hoạch xuất kho vận chuyển.

Thiết kế Cơ sở Dữ liệu (Database Schema):

Bổ sung cột `is_warehouse` (BOOLEAN) vào bảng `branches` hiện tại để định nghĩa một chi nhánh là Kho tổng. Đồng thời tạo bảng mới `internal_purchase_orders` và `internal_purchase_order_items`:


| Trường (Field) | Kiểu dữ liệu | Ràng buộc | Mô tả |
| --- | --- | --- | --- |
| id | BIGINT | PK, Auto Increment | Khoá chính đơn hàng nội bộ |
| ipo_code | VARCHAR(50) | UNIQUE, NOT NULL | Mã đơn hàng nội bộ (Ví dụ: IPO-17823) |
| requester_branch_id | VARCHAR(36) | FK references branches | Chi nhánh con yêu cầu bổ sung |
| warehouse_id | VARCHAR(36) | FK references branches | Mã kho tổng xử lý đơn hàng |
| status | VARCHAR(30) | NOT NULL | Trạng thái (DRAFT, SUBMITTED, APPROVED, SHIPPED, COMPLETED) |
| order_date | TIMESTAMP | NOT NULL | Ngày tạo yêu cầu |

Thiết kế API (REST Endpoints):


| HTTP Method | Path | Quyền truy cập | Mô tả chức năng |
| --- | --- | --- | --- |
| POST | /api/procurement/ipo/create | Branch Manager | Tạo phiếu đặt hàng nội bộ lên Kho tổng |
| POST | /api/procurement/ipo/{id}/approve | Warehouse Manager | Kho tổng phê duyệt và chuẩn bị hàng xuất kho |


## 3. Quy Trình Chuyển Kho Nâng Cao (Advanced Stock Transfer)

Nghiệp vụ chi tiết:

Trạng thái trung gian vận chuyển (In-transit): Khi xuất hàng, kho nguồn giảm ngay lập tức. Lượng hàng này được đưa vào trạng thái 'Đang vận chuyển' và chịu trách nhiệm bởi bên giao nhận.

Xác nhận thực nhận và ghi nhận hao hụt: Khi hàng đến nơi, chi nhánh nhận sẽ kiểm đếm thực tế. Hệ thống cho phép ghi nhận chênh lệch hao hụt (do rơi vỡ, hư hỏng trong quá trình vận chuyển) trước khi cộng vào kho đích.

Thiết kế Cơ sở Dữ liệu (Database Schema):

Cập nhật bảng `branch_transfers` và `branch_transfer_items` hiện tại:


| Trường (Field) | Kiểu dữ liệu | Ràng buộc | Mô tả |
| --- | --- | --- | --- |
| status | VARCHAR(30) | Change type/constraint | Cập nhật các trạng thái: PENDING, SHIPPED, RECEIVED |
| shipped_date | TIMESTAMP | Nullable | Thời gian xuất kho bắt đầu vận chuyển |
| quantity_shipped | DOUBLE PRECISION | In item table | Số lượng thực tế gửi đi từ kho nguồn |
| quantity_received | DOUBLE PRECISION | In item table | Số lượng thực tế kiểm nhận tại kho đích |
| loss_quantity | DOUBLE PRECISION | In item table | Hao hụt vận chuyển (Shipped - Received) |
| loss_reason | VARCHAR(255) | In item table | Lý do hao hụt (Ví dụ: Dập nát, hỏng hóc) |

Thiết kế API (REST Endpoints):


| HTTP Method | Path | Quyền truy cập | Mô tả chức năng |
| --- | --- | --- | --- |
| POST | /api/inventory/transfers/{id}/ship | Source Branch Manager | Xác nhận xuất kho và bắt đầu vận chuyển |
| POST | /api/inventory/transfers/{id}/receive | Target Branch Manager | Xác nhận thực nhận, đối soát hao hụt và hoàn tất nhập kho |


## 4. Quản Lý Két Tiền & Ca Làm Việc Của Thu Ngân (Cash Drawer & Shift Management)

Nghiệp vụ chi tiết:

Mở ca két tiền (Open Session): Đầu ca làm việc, thu ngân khai báo số tiền mặt ban đầu được bàn giao làm tiền thối nước (Opening Balance).

Chốt ca đối soát (Register Closure): Cuối ca, thu ngân đếm tiền mặt thực tế trong két. Hệ thống đối soát với doanh thu ghi nhận qua máy POS (gồm tiền mặt, chuyển khoản, thẻ), phát hiện chênh lệch và yêu cầu giải trình nếu có thất thoát.

Thiết kế Cơ sở Dữ liệu (Database Schema):

Tạo bảng mới `cash_drawer_sessions`:


| Trường (Field) | Kiểu dữ liệu | Ràng buộc | Mô tả |
| --- | --- | --- | --- |
| id | BIGINT | PK, Auto Increment | Khoá chính |
| branch_id | VARCHAR(36) | FK references branches | Mã chi nhánh hoạt động |
| cashier_id | BIGINT | FK references users | Nhân viên thu ngân trực ca |
| opening_time | TIMESTAMP | NOT NULL | Thời điểm bắt đầu mở két |
| closing_time | TIMESTAMP | Nullable | Thời điểm chốt ca |
| opening_balance | DOUBLE PRECISION | NOT NULL | Tiền mặt ban đầu |
| expected_cash | DOUBLE PRECISION | DEFAULT 0.0 | Tiền mặt tính toán theo hệ thống |
| actual_cash | DOUBLE PRECISION | Nullable | Tiền mặt thực tế kiểm đếm |
| discrepancy | DOUBLE PRECISION | Nullable | Chênh lệch tiền mặt (Actual - Expected) |
| notes | TEXT | Nullable | Giải trình chênh lệch |

Thiết kế API (REST Endpoints):


| HTTP Method | Path | Quyền truy cập | Mô tả chức năng |
| --- | --- | --- | --- |
| POST | /api/pos/register/open | Cashier | Mở ca két tiền mặt mới |
| POST | /api/pos/register/close | Cashier | Chốt ca két tiền mặt và đối soát |


## 5. Chương Trình Khuyến Mãi Theo Chi Nhánh (Branch-Specific Promotions)

Nghiệp vụ chi tiết:

Phạm vi áp dụng khuyến mãi: Cho phép cấu hình giới hạn chương trình khuyến mãi (giảm giá, tặng món, combo) chỉ có hiệu lực tại một số chi nhánh được chỉ định trước.

Thiết kế Cơ sở Dữ liệu (Database Schema):

Tạo bảng trung gian `promotion_branches` để liên kết nhiều-nhiều giữa chương trình khuyến mãi và chi nhánh:


| Trường (Field) | Kiểu dữ liệu | Ràng buộc | Mô tả |
| --- | --- | --- | --- |
| promotion_id | BIGINT | FK references promotions | Khoá ngoại liên kết bảng khuyến mãi |
| branch_id | VARCHAR(36) | FK references branches | Khoá ngoại liên kết bảng chi nhánh |


## 6. Phân Quyền Quản Lý Khu Vực (Regional Manager Role)

Nghiệp vụ chi tiết:

Vai trò Quản lý khu vực (Regional Manager): Người dùng cấp trung có quyền xem báo cáo, quản lý nhân sự, duyệt đơn chuyển kho của một nhóm chi nhánh cụ thể trong khu vực được phân công.

Thiết kế Cơ sở Dữ liệu (Database Schema):

Tạo bảng liên kết `user_branches` cho phép một quản lý giám sát nhiều chi nhánh:


| Trường (Field) | Kiểu dữ liệu | Ràng buộc | Mô tả |
| --- | --- | --- | --- |
| user_id | BIGINT | FK references users | Mã định danh quản lý khu vực |
| branch_id | VARCHAR(36) | FK references branches | Mã chi nhánh được giao quản lý |


## 7. Báo Cáo Hợp Nhất & So Sánh Hiệu Năng (Consolidated Analytics)

Nghiệp vụ chi tiết:

So sánh đa chiều: Hệ thống tổng hợp biểu đồ so sánh doanh thu trực quan, tần suất khách hàng, số bàn lấp đầy và hiệu suất quay vòng nguyên liệu giữa toàn bộ các chi nhánh.

Phân tích hao hụt chuỗi: Đưa ra thống kê chi tiết tỷ lệ thất thoát hàng hóa trên đường chuyển kho hoặc chênh lệch kiểm kê định kỳ để ban quản trị điều chỉnh quy trình.

Thiết kế API (REST Endpoints):


| HTTP Method | Path | Quyền truy cập | Mô tả chức năng |
| --- | --- | --- | --- |
| GET | /api/analytics/consolidated/revenue | Admin, Regional Manager | Lấy dữ liệu doanh thu so sánh giữa các chi nhánh |
| GET | /api/analytics/consolidated/loss | Admin, Regional Manager | Báo cáo hao hụt, thất thoát kho toàn hệ thống |


# III. KẾ HOẠCH TRIỂN KHAI CHI TIẾT


| Giai đoạn | Công việc thực hiện | Kết quả đầu ra | Thời gian dự kiến |
| --- | --- | --- | --- |
| Giai đoạn 1 | Nâng cấp cơ sở dữ liệu (Migration) & Viết thực thể JPA | Các bảng mới được tạo thành công trên DB | Tuần 1 |
| Giai đoạn 2 | Xây dựng các Service xử lý nghiệp vụ (Chuyển kho, Chốt ca két, IPO) | Business logic vượt qua Unit Test | Tuần 2 - 3 |
| Giai đoạn 3 | Viết API controller & Tích hợp phân quyền Spring Security | Bộ API chạy tốt qua Postman/Swagger | Tuần 4 |
| Giai đoạn 4 | Thiết kế giao diện Front-end (Thực đơn chi nhánh, Mở/Đóng ca, Chuyển kho) | Giao diện POS và Dashboard cập nhật tính năng mới | Tuần 5 - 6 |
| Giai đoạn 5 | Kiểm thử liên thông (E2E Test) & Triển khai thực tế (UAT) | Hệ thống vận hành trơn tru tại 2 chi nhánh chạy thử | Tuần 7 |

