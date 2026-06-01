# LỘ TRÌNH PHÁT TRIỂN & PHÂN CHIA NHIỆM VỤ DỰ ÁN (PROJECT ROADMAP)

**Hệ Thống Quản Lý Nhà Hàng Chuỗi - SWP391**

---

| Thông Tin Tài Liệu | Chi Tiết |
| --- | --- |
| **Dự án** | Hệ Thống Quản Lý Nhà Hàng Chuỗi (RMS - Restaurant Management System) |
| **Môn học** | SWP391 - Học kỳ 5, Đại học FPT |
| **Tài liệu** | Kế hoạch Phân công Nhiệm vụ & Lộ trình Phát triển (Project Roadmap & Task Allocation) |
| **Phiên bản** | 1.0.0 (Bản lập kế hoạch dự án) |
| **Tác giả** | Tech Lead / Project Manager (20 năm kinh nghiệm) |
| **Trạng thái** | Sẵn sàng trình duyệt |

---

# 1. Định Nghĩa Các Vai Trò Trong Dự Án (Project Roles)

Để tối ưu hóa hiệu quả làm việc nhóm và đảm bảo tính liên thông dữ liệu, các thành viên trong nhóm dự án SWP391 được phân vai với trách nhiệm kỹ thuật rõ ràng như sau:

1. **Nhóm trưởng / Tech Lead (TL)**:
   - Chịu trách nhiệm quản lý tiến độ, thiết kế cấu trúc cơ sở dữ liệu vật lý.
   - Cấu hình bảo mật hệ thống Spring Security (đăng nhập, phân quyền, mã hóa BCrypt).
   - Thiết lập hạ tầng truyền thông WebSocket thời gian thực cho phân hệ nhà bếp KDS.
   - Tích hợp dịch vụ API Gemini/ChatGPT cho phân hệ Trợ lý AI Assistant.
2. **Lập trình viên Backend 1 (BE-01)**:
   - Phụ trách logic nghiệp vụ bán hàng POS: Sơ đồ phòng/bàn, phiên phục vụ, gộp/tách bàn, tính hóa đơn.
   - Phụ trách phân hệ **Khách hàng thành viên**: Redesign bảng `customers` (SĐT làm PK), dịch vụ tích lũy điểm thưởng thăng hạng hội viên tự động, trang Customer Portal.
   - Phụ trách phân hệ **Khuyến mãi**: Động cơ tính toán giảm giá tự động tối ưu nhất cho hóa đơn của khách.
3. **Lập trình viên Backend 2 (BE-02)**:
   - Phụ trách logic nghiệp vụ **Kho hàng**: Định lượng công thức món ăn (Recipe), quản lý danh mục thực đơn.
   - Phụ trách phân hệ **Quản trị đa chi nhánh**: Cấu hình giá bán tùy biến theo chi nhánh, đơn đặt hàng bổ sung nội bộ (IPO) Kho tổng, quy trình chuyển kho liên chi nhánh (Ship -> In-transit -> Receive & Đối soát hao hụt).
   - Phụ trách phân hệ Nhân sự (HR): Phân lịch làm việc, chấm công Clock In/Out, duyệt phép và tự động chạy bảng lương.
4. **Lập trình viên Frontend / Fullstack (FE)**:
   - Thiết kế giao diện HTML/Thymeleaf cho toàn bộ màn hình hệ thống (POS, KDS, Dashboard, Nhân viên, Kho).
   - Viết CSS thuần theo phong cách hiện đại (Glassmorphism, Dark Mode, Micro-animations) và đảm bảo tính responsive trên máy tính bảng/màn hình POS.
   - Viết Javascript thực hiện các yêu cầu Ajax/Fetch API gửi dữ liệu mượt mà lên Back-end.
5. **Kiểm thử viên / Đảm bảo Chất lượng (QA/QC)**:
   - Thiết lập các bộ Unit Test kiểm tra tính đúng đắn của các lớp Service xử lý logic nghiệp vụ.
   - Thực hiện kiểm thử liên thông (End-to-End Test) toàn bộ luồng nghiệp vụ trên trình duyệt.
   - Soạn thảo tài liệu hướng dẫn sử dụng (User Guide) và hỗ trợ chuẩn bị slide thuyết trình cho nhóm.

---

# 2. Lộ Trình Phát Triển 5 Giai Đoạn Chi Tiết (5-Phase Roadmap)

Dưới đây là chi tiết công việc, tệp tin code liên quan và sự phân công nhiệm vụ cụ thể cho từng thành viên qua 5 giai đoạn phát triển dự án.

---

### Giai đoạn 1: Thiết lập Cơ sở dữ liệu, Cấu trúc dự án & Xác thực (Tuần 1 - 2)
*Mục tiêu*: Khởi tạo cơ sở dữ liệu mẫu, thiết lập khung dự án Spring Boot và tích hợp bộ lọc đăng nhập bảo mật.
*Epic trọng tâm*: **Epic 1: Quản lý Xác thực & Phân quyền**

**Bảng phân công nhiệm vụ chi tiết:**

| Thành Viên | Công Việc Chi Tiết | Tệp Tin Tác Động / Code Liên Quan | Kết Quả Đầu Ra |
| --- | --- | --- | --- |
| **Tech Lead** | - Cấu hình Maven `pom.xml`. <br> - Thiết lập kết nối cơ sở dữ liệu PostgreSQL / H2. <br> - Viết cấu hình bảo mật `SecurityConfig` quản lý đăng nhập và phân quyền RBAC. <br> - Viết Entity `User`, `Role`, `UserSession`, `AuditLog`. | `pom.xml`, `SecurityConfig.java`, `User.java`, `Role.java`, `AuditLog.java`, `application.properties` | Khung dự án chạy không lỗi, chặn quyền truy cập trái phép. |
| **BE-02** | - Khai báo cấu trúc bảng dữ liệu trong các file SQL. <br> - Viết lớp `DataSeeder.java` tự động đọc 37 file SQL để chèn dữ liệu mẫu khi ứng dụng khởi chạy lần đầu. | `d:/swp/sql/`, `DataSeeder.java` | Dữ liệu mẫu (roles, users, branches, products) tự động nạp thành công vào database. |
| **FE** | - Thiết kế giao diện trang Đăng nhập (`/login`). <br> - Thiết lập hệ thống CSS stylesheet tổng thể cho toàn dự án. | `login.html`, `styles.css` | Trang đăng nhập có giao diện premium, đẹp mắt và phản hồi tốt. |
| **QA/QC** | - Lập tài liệu Test Plan kiểm tra bảo mật đăng nhập. <br> - Viết Unit Test cho quá trình mã hóa mật khẩu BCrypt. | `LiteFlowServiceTests.java` | Kiểm thử thành công logic băm mật khẩu và khóa tài khoản khi nhập sai 5 lần. |

---

### Giai đoạn 2: Phát triển các Chức năng Vận hành Cốt lõi (Tuần 3 - 4)
*Mục tiêu*: Xây dựng hoàn chỉnh logic quản lý nhân viên, chấm công và nghiệp vụ bán hàng POS cơ bản.
*Epic trọng tâm*: **Epic 3: Nhân sự & Tính lương** & **Epic 5: Bán hàng POS & Bàn ăn**

**Bảng phân công nhiệm vụ chi tiết:**

| Thành Viên | Công Việc Chi Tiết | Tệp Tin Tác Động / Code Liên Quan | Kết Quả Đầu Ra |
| --- | --- | --- | --- |
| **BE-01** | - Viết Service và Controller quản lý Bàn ăn, Khu vực. <br> - Viết logic mở phiên bàn ăn (`table_sessions.status = 'ACTIVE'`). <br> - Viết logic gọi món và thêm giỏ hàng bán lẻ. | `OrderService.java`, `PosController.java`, `TableSessionRepository.java`, `OrderDetail.java` | Hoàn thành API mở bàn ăn, thêm món vào hóa đơn chi tiết. |
| **BE-02** | - Viết Entity, Repository và Service quản lý Nhân viên (`Employee`). <br> - Viết logic chấm công `Clock In` (đầu ca) và `Clock Out` (cuối ca). <br> - Viết logic tính giờ công và tự động đánh dấu đi trễ/về sớm. | `Employee.java`, `EmployeeAttendance.java`, `HrService.java`, `HrController.java` | Nhân viên có thể chấm công bằng tài khoản và tự động ghi nhận giờ công. |
| **FE** | - Thiết kế màn hình bán hàng POS tại quầy (Sơ đồ bàn, danh mục món ăn, giỏ hàng, bảng thanh toán). <br> - Viết Ajax/Fetch JS để đồng bộ thay đổi giỏ hàng. | `pos.html`, `pos.js` | Giao diện POS động, chọn bàn ăn và thêm món mượt mà không cần tải lại trang. |
| **QA/QC** | - Viết bộ dữ liệu kiểm thử chấm công. <br> - Thực hiện kiểm thử thủ công chức năng thêm/sửa/xóa giỏ hàng trên màn hình POS. | `LiteFlowServiceTests.java` | Kiểm soát chính xác tính toán giờ công của ca gãy và ca cố định. |

---

### Giai đoạn 3: Triển khai Nâng cấp 3 Epic Đặc thù (Tuần 5 - 6)
*Mục tiêu*: Tích hợp logic khách hàng hội viên sử dụng SĐT làm khóa chính, thăng hạng thẻ tự động và phân hệ đa chi nhánh.
*Epic trọng tâm*: **Epic 7: Khách hàng thành viên** & **Epic 2 & 4: Quản lý Chi nhánh & Chuyển kho**

**Bảng phân công nhiệm vụ chi tiết:**

| Thành Viên | Công Việc Chi Tiết | Tệp Tin Tác Động / Code Liên Quan | Kết Quả Đầu Ra |
| --- | --- | --- | --- |
| **BE-01** | - **Cơ sở dữ liệu**: Đổi khóa chính bảng `Customer` thành `phone`. Cập nhật các bảng liên kết dùng khóa ngoại `customer_phone`. <br> - **Tích lũy**: Viết hàm `accumulateSpend` tự động cộng dồn doanh số khi thanh toán. <br> - **Thăng hạng**: Viết bộ logic tự động kiểm tra nâng cấp thẻ thành viên (Bronze -> Platinum) và tính điểm thưởng (1%). | `Customer.java`, `LoyaltyService.java`, `LoyaltyTransaction.java`, `CustomerPortalController.java` | Khách hàng đăng ký bằng SĐT. Thanh toán xong hệ thống tự động cộng dồn tiền và thăng hạng thẻ. |
| **BE-02** | - **Giá chi nhánh**: Viết logic `BranchProductPrice` để cấu hình thực đơn và giá bán tùy chỉnh riêng biệt theo chi nhánh. <br> - **Chuyển kho**: Viết Service chuyển kho liên chi nhánh (`ship` giảm kho nguồn, hàng đi vào trạng thái In-transit; `receive` cộng kho đích và đối soát ghi nhận hao hụt). <br> - **Két tiền**: Viết logic mở/đóng két tiền mặt thu ngân đầu/cuối ca. | `BranchProductPrice.java`, `BranchTransfer.java`, `InventoryService.java`, `CashDrawerSession.java` | Chi nhánh con tự đặt hàng lên Kho tổng. Quy trình chuyển kho ghi nhận chính xác hao hụt rơi vỡ. |
| **FE** | - Thiết kế giao diện **Customer Portal** tra cứu điểm thưởng bằng SĐT. <br> - Thiết kế biểu mẫu mở/đóng ca két tiền của thu ngân trên màn hình POS. <br> - Thiết kế giao diện lập phiếu chuyển kho và đối soát hao hụt. | `customer-portal.html`, `transfers.html`, `cash-drawer.html` | Cổng thông tin hội viên trực quan. Quản lý chi nhánh có màn hình đối soát chênh lệch két tiền tiện lợi. |
| **QA/QC** | - Viết Unit Test liên thông kiểm tra thăng hạng thẻ tự động dựa trên tổng tiền chi tiêu. <br> - Viết bộ dữ liệu kiểm tra hao hụt chuyển kho nội bộ. | `LiteFlowServiceTests.java` | Đảm bảo thăng hạng Silver/Gold/Platinum diễn ra chính xác theo mốc tiền chi tiêu. |

---

### Giai đoạn 4: Tích hợp WebSocket, Màn hình Bếp KDS & Trợ lý AI (Tuần 7 - 8)
*Mục tiêu*: Thiết lập kênh truyền thông thời gian thực giữa POS và nhà bếp (KDS), tích hợp trợ lý AI phân tích doanh thu kinh doanh.
*Epic trọng tâm*: **Epic 6: Hiển thị nhà bếp KDS** & **Epic 10: Trợ lý AI Assistant**

**Bảng phân công nhiệm vụ chi tiết:**

| Thành Viên | Công Việc Chi Tiết | Tệp Tin Tác Động / Code Liên Quan | Kết Quả Đầu Ra |
| --- | --- | --- | --- |
| **Tech Lead** | - Cấu hình WebSocket Server. <br> - Viết `KdsWebSocketHandler` điều phối tin nhắn gửi món từ POS xuống KDS. <br> - Thực hiện cô lập topic theo chi nhánh: `/topic/kds/branch-{branchId}`. <br> - Tích hợp Gemini API trong `AIService.java` để phân tích dữ liệu bán hàng. | `WebSocketConfig.java`, `KdsWebSocketHandler.java`, `AIService.java`, `AnalyticsController.java` | Hoàn thành WebSocket đẩy món xuống bếp theo từng chi nhánh. Trợ lý AI chat phân tích kinh doanh mượt mà. |
| **BE-01** | - Viết logic API cập nhật trạng thái chế biến món ăn ở bếp (`PENDING` -> `COOKING` -> `READY`). <br> - Gọi trigger gửi thông báo WebSocket ngược lại POS báo phục vụ đem món ra cho khách. | `KdsService.java`, `KdsController.java` | Bếp nhấn xong món lập tức POS nhận được thông báo để nhân viên bưng món đi. |
| **FE** | - Thiết kế giao diện màn hình nhà bếp KDS (Các thẻ món ăn hiển thị thời gian chờ). <br> - Thiết kế khung chat Trợ lý AI trên trang Dashboard quản trị. <br> - Viết Stomp JS kết nối WebSocket trên POS và KDS. | `kds.html`, `dashboard.html`, `kds.js` | Giao diện bếp KDS cập nhật thời gian thực không giật lag. Khung chat AI đẹp mắt có hiệu ứng gõ chữ sinh động. |
| **QA/QC** | - Thực hiện kiểm thử hiệu năng kết nối WebSocket đồng thời. <br> - Kiểm thử độ chính xác của câu trả lời từ AI Assistant khi hỏi đáp về doanh số và tồn kho chi nhánh. | `LiteFlowServiceTests.java` | Tin nhắn truyền tải tức thời dưới 200ms, AI trả về kết quả phân tích chuẩn xác. |

---

### Giai đoạn 5: Kiểm thử liên thông E2E, Vá lỗi & Biên tập Tài liệu (Tuần 9 - 10)
*Mục tiêu*: Tiến hành kiểm thử toàn trình hệ thống (UAT), sửa lỗi phát sinh, chốt mã nguồn và xuất bản tài liệu `.docx` chính thức.

**Bảng phân công nhiệm vụ chi tiết:**

| Thành Viên | Công Việc Chi Tiết | Tệp Tin Tác Động / Code Liên Quan | Kết Quả Đầu Ra |
| --- | --- | --- | --- |
| **Tech Lead** | - Chạy kịch bản UAT với dữ liệu thực tế của chuỗi 3 chi nhánh. <br> - Biên tập tài liệu Kiến trúc hệ thống (`Architecture.docx`). | `Architecture.docx` | Mã nguồn ổn định không lỗi bảo mật, tài liệu kiến trúc sẵn sàng. |
| **BE-01 & BE-02** | - Khắc phục các lỗi kiểm thử (Bug fixing) do QA phát hiện. <br> - Biên tập tài liệu Đặc tả yêu cầu (`SRS.docx`) và Danh sách API (`API_Docs.docx`). | `SRS.docx`, `API_Docs.docx` | Sửa sạch lỗi nghiệp vụ, hoàn thành tài liệu SRS và API chất lượng cao. |
| **FE** | - Rà soát giao diện toàn bộ hệ thống, tối ưu CSS, vá lỗi hiển thị layout trên các thiết bị. | `styles.css` | Giao diện toàn chuỗi đồng bộ, chuyên nghiệp và có tính mỹ thuật cực cao. |
| **QA/QC** | - Thực thi toàn bộ Test Suite E2E. <br> - Biên tập tài liệu Thiết kế Cơ sở dữ liệu (`Database_Design.docx`). <br> - Hỗ trợ chuẩn bị Slide thuyết trình bảo vệ dự án. | `Database_Design.docx` | Đạt tỷ lệ kiểm thử thành công 100%, tài liệu Database dictionary hoàn tất. |
