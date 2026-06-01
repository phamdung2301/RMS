# DANH SÁCH API ENDPOINTS CHI TIẾT (API DOCUMENTATION)

**Hệ Thống Quản Lý Nhà Hàng Chuỗi - SWP391**

---

| Thông Tin Tài Liệu | Chi Tiết |
| --- | --- |
| **Dự án** | Hệ Thống Quản Lý Nhà Hàng Chuỗi (RMS - Restaurant Management System) |
| **Môn học** | SWP391 - Học kỳ 5, Đại học FPT |
| **Tài liệu** | Tài Liệu Mô Tả Các Cổng Giao Tiếp API Chi Tiết (API Endpoints Documentation) |
| **Phiên bản** | 1.1.0 (Bản nâng cấp Epic) |
| **Tác giả** | Software Engineer & Tech Lead (20 năm kinh nghiệm) |
| **Trạng thái** | Sẵn sàng trình duyệt |

---

# 1. Tổng Quan Giao Tiếp Hệ Thống (API Integration Overview)

Hệ thống cung cấp hệ thống endpoint phong phú phục vụ cả giao diện Web (Controller trả về Thymeleaf Template) và các API dịch vụ RESTful (RestController trả về cấu trúc dữ liệu JSON). Toàn bộ các API đều được bảo mật bởi **Spring Security** với phân quyền dựa trên vai trò (Role-Based Access Control - RBAC). 

Các API được phân tách chặt chẽ theo các phân hệ nghiệp vụ (Epics) để nhóm phát triển dễ dàng chia Sprint thực hiện độc lập.

---

# 2. Chi Tiết Danh Sách API Endpoints Theo Từng Epic

### Epic 1: Quản lý Xác thực & Phân quyền (Authentication & Authorization)
*Mô tả*: Xử lý đăng nhập, đăng xuất, phân quyền và lưu vết audit log.

| Method | Đường dẫn (Path) | Quyền Truy Cập | Mô Tả Chức Năng Chi Tiết |
| --- | --- | --- | --- |
| GET | `/login` | Public | Trả về trang đăng nhập hệ thống. |
| POST | `/login` | Public | Spring Security tiếp nhận Email & Mật khẩu để xác thực người dùng. |
| GET | `/logout` | Authenticated | Đóng phiên làm việc hiện tại, thu hồi token. |
| GET | `/api/auth/audit-logs` | Admin | Lấy danh sách lịch sử kiểm toán hệ thống (Audit Trail) để giám sát bảo mật. |

---

### Epic 2: Quản lý Chi nhánh & Thiết lập Hệ thống (Branch Setup & Menu Mapping)
*Mô tả*: Quản lý thông tin chi nhánh và cấu hình thực đơn bán lẻ cùng giá bán tùy biến cho từng cơ sở.

| Method | Đường dẫn (Path) | Quyền Truy Cập | Mô Tả Chức Năng Chi Tiết |
| --- | --- | --- | --- |
| GET | `/api/branches` | Admin | Lấy danh sách toàn bộ các chi nhánh hoạt động trong chuỗi. |
| POST | `/api/branches/add` | Admin | Thêm mới một chi nhánh vào chuỗi hệ thống. |
| **GET** | `/api/branches/{branchId}/menu` | Manager, Cashier | **Lấy thực đơn áp dụng riêng cho chi nhánh (gồm giá bán tùy biến lẻ).** |
| **POST** | `/api/branches/{branchId}/menu/config` | Admin, Manager | **Cấu hình giá bán tùy biến lẻ (`custom_price`) và trạng thái bán món cho chi nhánh.** |

---

### Epic 3: Quản lý Nhân sự, Ca kíp & Tính công Lương (HR, Shifts & Payroll)
*Mô tả*: Quản lý hồ sơ nhân viên, phân ca làm việc, chấm công và tự động tính lương cứng/giờ công thực tế.

| Method | Đường dẫn (Path) | Quyền Truy Cập | Mô Tả Chức Năng Chi Tiết |
| --- | --- | --- | --- |
| GET | `/employees` | HR, Admin | Trả về trang danh sách nhân viên của chi nhánh. |
| GET | `/schedule` | HR, Manager | Trả về trang giao diện lịch làm việc và phân ca kíp của chi nhánh. |
| POST | `/api/hr/schedule/assign` | HR Officer | REST API phân lịch làm việc (Shift Template) cho nhân viên. |
| **POST** | `/api/employee/clock-in` | Employee | **REST API ghi nhận thời gian bắt đầu vào ca của nhân viên đăng nhập hiện tại.** |
| **POST** | `/api/employee/clock-out` | Employee | **REST API ghi nhận thời gian ra ca và tính số giờ công thực tế làm được.** |
| POST | `/api/hr/payroll/run` | HR Officer | Kích hoạt chạy tự động tính toán lương tháng cho nhân viên của kỳ lương chỉ định. |

---

### Epic 4: Quản lý Kho & Định lượng Thực đơn (Inventory, Recipes & Transfers)
*Mô tả*: Định lượng món ăn, kiểm kho thực tế, chuyển kho nội bộ và đặt hàng từ chi nhánh lên Kho tổng.

| Method | Đường dẫn (Path) | Quyền Truy Cập | Mô Tả Chức Năng Chi Tiết |
| --- | --- | --- | --- |
| GET | `/inventory` | Warehouse, Manager | Trả về trang giao diện quản lý kho nguyên vật liệu và thực đơn. |
| POST | `/api/inventory/adjust` | Warehouse | Thực hiện điều chỉnh số lượng tồn kho nguyên vật liệu do hao hụt thực tế. |
| POST | `/api/inventory/recipes` | Kitchen, Manager | Thiết lập định lượng nguyên vật liệu cần dùng (`ProductStock`) cho biến thể món. |
| **POST** | `/api/procurement/ipo/create` | Branch Manager | **Chi nhánh con tạo yêu cầu bổ sung nguyên vật liệu (IPO) lên Kho tổng.** |
| **POST** | `/api/procurement/ipo/{id}/approve` | Warehouse Manager | **Kho tổng phê duyệt và chuẩn bị hàng xuất kho để vận chuyển.** |
| **POST** | `/api/inventory/transfers/{id}/ship` | Source Manager | **Xác nhận xuất kho bắt đầu vận chuyển hàng chuyển kho nội bộ.** |
| **POST** | `/api/inventory/transfers/{id}/receive` | Target Manager | **Nhận hàng chuyển kho, nhập thực tế nhận, đối soát và ghi nhận hao hụt.** |

---

### Epic 5: Quản lý Bán hàng POS & Phiên Bàn ăn (POS & Cash Drawer Management)
*Mô tả*: Quản lý bán hàng trực tiếp tại quầy POS, kết nối thanh toán VNPay và đóng/mở két tiền mặt của thu ngân.

| Method | Đường dẫn (Path) | Quyền Truy Cập | Mô Tả Chức Năng Chi Tiết |
| --- | --- | --- | --- |
| GET | `/pos` | Cashier, Manager | Trả về trang giao diện bán hàng tại quầy POS. |
| GET | `/api/pos/session/active` | Cashier | Lấy thông tin phiên phục vụ (`TableSession`) đang hoạt động tại bàn ăn. |
| **POST** | `/api/pos/session/open` | Cashier | **Mở phiên bàn ăn mới (Liên kết số điện thoại khách hàng thành viên `customerPhone`).** |
| POST | `/api/pos/order/add` | Cashier | Thêm món ăn, biến thể và ghi chú chế biến vào giỏ hàng bàn ăn. |
| POST | `/api/pos/order/send` | Cashier | Xác nhận gửi đơn gọi món xuống bếp chế biến và đẩy WebSocket KDS. |
| POST | `/api/pos/bill/merge` | Cashier | Thực hiện gộp toàn bộ hóa đơn từ bàn nguồn sang bàn đích. |
| POST | `/api/pos/bill/split` | Cashier | Tách các món ăn đã chọn ra hóa đơn phiên ăn mới độc lập. |
| **POST** | `/api/pos/register/open` | Cashier | **Mở ca két tiền mặt mới đầu ngày, nhập số tiền mặt thối ban đầu.** |
| **POST** | `/api/pos/register/close` | Cashier | **Chốt ca két tiền mặt cuối ca, đối soát và ghi nhận chênh lệch doanh thu.** |
| POST | `/api/pos/checkout/vnpay` | Cashier | Tạo liên kết thanh toán quét mã QR qua cổng VNPay Sandbox dựa trên hóa đơn. |
| **POST** | `/api/pos/checkout/confirm` | Cashier | **Xác nhận thanh toán thành công (Tiền mặt/VNPay), cộng điểm tích lũy và in hóa đơn.** |

---

### Epic 6: Hệ thống hiển thị nhà bếp (Kitchen Display System - KDS)
*Mô tả*: Màn hình bếp KDS tiếp nhận thông tin và cập nhật tiến độ chế biến món ăn thời gian thực.

| Method | Đường dẫn (Path) | Quyền Truy Cập | Mô Tả Chức Năng Chi Tiết |
| --- | --- | --- | --- |
| GET | `/kds` | Kitchen, Manager | Trả về trang giao diện hiển thị danh sách các món ăn cần chế biến ở bếp. |
| POST | `/api/kds/status` | Kitchen | Cập nhật tiến độ món ăn (`PENDING` -> `COOKING` -> `READY`) và phát WebSocket. |

---

### Epic 7: Quản lý Khách hàng thành viên & Tích lũy (Loyalty & Customer Portal)
*Mô tả*: Đăng ký thành viên dựa trên SĐT làm ID chính và tra cứu điểm thưởng, hạng thẻ qua Customer Portal.

| Method | Đường dẫn (Path) | Quyền Truy Cập | Mô Tả Chức Năng Chi Tiết |
| --- | --- | --- | --- |
| **POST** | `/api/pos/customer/register` | Cashier | **REST API đăng ký hội viên mới (Nhận SĐT `phone` làm khóa chính Primary Key).** |
| **GET** | `/customer-portal/{phone}` | Public (Guest) | **Màn hình Cổng thông tin tra cứu điểm thưởng và hạng thẻ của khách hàng dựa trên SĐT.** |
| GET | `/api/loyalty/transactions/{phone}` | Cashier, Customer | Lấy lịch sử biến động điểm thưởng tích lũy/quy đổi của khách hàng. |

---

### Epic 8: Quản lý Thu mua & Đối chiếu 3 bên (Procurement & Three-way Match)
*Mô tả*: Quản lý đặt đơn hàng PO từ nhà cung cấp ngoài, lập phiếu nhập kho GRN đối soát tài chính.

| Method | Đường dẫn (Path) | Quyền Truy Cập | Mô Tả Chức Năng Chi Tiết |
| --- | --- | --- | --- |
| GET | `/procurement` | Procurement, Admin | Trả về trang giao diện quản lý thu mua nguyên vật liệu. |
| POST | `/api/procurement/po/approve` | Manager, Admin | Phê duyệt đơn mua hàng PO để chuyển trạng thái từ DRAFT sang APPROVED. |
| POST | `/api/procurement/po/grn` | Warehouse | Lập Phiếu nhập kho (GRN) ghi nhận thực nhận hàng từ nhà cung cấp ngoài. |
| POST | `/api/procurement/po/match` | Manager, Admin | Kích hoạt đối soát 3 bên (PO vs GRN vs Hóa đơn) tự động cộng dồn tồn kho. |

---

### Epic 9: Quản lý Khuyến mãi & Áp dụng tự động (Promotion Engine)
*Mô tả*: Thiết lập mã giảm giá, combo, B1G1 áp dụng theo chi nhánh cụ thể.

| Method | Đường dẫn (Path) | Quyền Truy Cập | Mô Tả Chức Năng Chi Tiết |
| --- | --- | --- | --- |
| GET | `/promotions` | Manager, Admin | Trả về trang giao diện quản lý các chương trình khuyến mãi. |
| POST | `/api/promotions/save` | Manager, Admin | Lưu mới hoặc cập nhật một chương trình khuyến mãi (bao gồm gán chi nhánh có hiệu lực). |

---

### Epic 10: Báo cáo Thống kê & Trợ lý AI Assistant (BI & AI Assistant)
*Mô tả*: Báo cáo doanh số đa cơ sở và chatbot AI tư vấn kinh doanh cho ban điều hành.

| Method | Đường dẫn (Path) | Quyền Truy Cập | Mô Tả Chức Năng Chi Tiết |
| --- | --- | --- | --- |
| GET | `/dashboard` | Admin, Manager | Trả về trang chủ dashboard báo cáo thống kê BI doanh thu, nhân sự và tồn kho. |
| POST | `/api/analytics/ai-chat` | Manager, Admin | Tiếp nhận câu hỏi phân tích dữ liệu kinh doanh của Quản lý gửi đến Gemini API. |
