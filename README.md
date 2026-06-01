# HƯỚNG DẪN CÀI ĐẶT VÀ CẤU HÌNH HỆ THỐNG LÀM VIỆC LOCAL (RESTAURANT MANAGEMENT SYSTEM - RMS)

Tài liệu này hướng dẫn chi tiết từng bước cho các thành viên phát triển dự án cài đặt, cấu hình môi trường local và thực hiện kiểm thử nhanh (Smoke Test) hệ thống RMS sau khi clone dự án về máy cá nhân.

---

## 1. Giới thiệu chung & Công nghệ sử dụng
Dự án SWP (Restaurant Management System - RMS) là một hệ thống quản lý chuỗi nhà hàng được xây dựng theo mô hình Modular Monolith, bao gồm các phân hệ chính sau:
- **analytics**: Phân tích thống kê & báo cáo doanh thu.
- **auth**: Quản lý tài khoản, phân quyền (Role-based Access Control - RBAC) & bảo mật.
- **branch**: Quản lý thông tin và trạng thái các chi nhánh.
- **hr**: Quản lý nhân sự, lịch làm việc, chấm công & tính lương.
- **inventory**: Quản lý nguyên vật liệu, định lượng món ăn & kiểm kho.
- **kds** (Kitchen Display System): Hệ thống hiển thị và quản lý chế biến tại bếp theo thời gian thực.
- **loyalty**: Quản lý khách hàng thân thiết & điểm tích lũy.
- **pos** (Point of Sale): Màn hình bán hàng, đặt món & thanh toán hóa đơn.
- **procurement**: Quản lý nhà cung cấp, đơn mua hàng & nhập kho nguyên vật liệu.
- **promotion**: Quản lý các chương trình khuyến mãi, giảm giá.

**Công nghệ cốt lõi:**
- **Backend:** Java 21, Spring Boot 4.0.6, Spring Data JPA, Spring Security (Form Login + Google OAuth2), Spring Mail.
- **Frontend:** Thymeleaf (giao diện HTML5 kết hợp CSS3/JS thuần).
- **Thời gian thực (Real-time):** Spring WebSocket.
- **Database:** PostgreSQL (Primary), H2 Database (In-Memory hỗ trợ dev/test nhanh).
- **Caching & Session:** Redis (thông qua Spring Data Redis).

---

## 2. Các yêu cầu tiên quyết (Prerequisites)
Hãy đảm bảo máy tính cá nhân của bạn đã được cài đặt và cấu hình đầy đủ các công cụ sau trước khi tiến hành:

1. **Java Development Kit (JDK) 21**:
   - Sử dụng phiên bản LTS (khuyên dùng [Eclipse Temurin JDK 21](https://adoptium.net/) hoặc Oracle JDK 21).
   - Kiểm tra bằng terminal:
     ```bash
     java -version
     ```
     *(Yêu cầu kết quả trả về hiển thị phiên bản `21.x.x`)*.

2. **PostgreSQL Server**:
   - Phiên bản khuyến nghị: **15** hoặc **16**.
   - Cổng mặc định: `5432`.

3. **Redis Server**:
   - Phiên bản khuyến nghị: **6.x** hoặc **7.x**.
   - Cổng mặc định: `6379`.
   - *Mẹo cho người dùng Windows:* Bạn có thể sử dụng Docker để chạy Redis nhanh chóng hoặc cài đặt thông qua WSL2.

4. **Python 3** *(Không bắt buộc)*:
   - Dùng để chạy script sinh mã SQL dữ liệu mẫu nếu bạn muốn thay đổi hoặc cập nhật cấu trúc dữ liệu demo.

5. **IDE**:
   - Khuyến nghị sử dụng **IntelliJ IDEA** (phiên bản Community hoặc Ultimate).
   - Hãy cài đặt/kích hoạt plugin **Lombok**.
   - **BẮT BUỘC**: Kích hoạt chế độ biên dịch nâng cao trong IntelliJ bằng cách tick chọn `Enable annotation processing` tại đường dẫn:
     `File > Settings > Build, Execution, Deployment > Compiler > Annotation Processors`.

---

## 3. Các bước cài đặt & cấu hình từng bước (Step-by-Step Setup)

### Bước 1: Clone mã nguồn dự án
Sử dụng git để lấy phiên bản mã nguồn mới nhất:
```bash
git clone <URL_REPOS_CỦA_BẠN>
cd swp
```

### Bước 2: Tạo Cơ sở dữ liệu PostgreSQL & Cấu hình kết nối
1. Đảm bảo dịch vụ PostgreSQL đang chạy trên máy local của bạn.
2. Truy cập vào công cụ quản lý DB (như pgAdmin, DBeaver, hoặc CLI psql) và tạo một cơ sở dữ liệu trống có tên là `swp`:
   ```sql
   CREATE DATABASE swp;
   ```
3. Mở file cấu hình ứng dụng chính tại đường dẫn: [application.properties](file:///d:/swp/src/main/resources/application.properties)
4. Chỉnh sửa thông tin đăng nhập PostgreSQL khớp với cấu hình máy cá nhân của bạn:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/swp
   spring.datasource.driver-class-name=org.postgresql.Driver
   spring.datasource.username=YOUR_POSTGRES_USERNAME  # Thường là: postgres
   spring.datasource.password=YOUR_POSTGRES_PASSWORD  # Ví dụ: phamdung.2301
   ```
   > [!NOTE]
   > Cấu hình `spring.jpa.hibernate.ddl-auto=create` được bật sẵn giúp Spring Boot tự động khởi tạo lại cấu trúc bảng mới mỗi lần chạy. Khi cấu trúc bảng đã ổn định, bạn có thể chuyển về `update`.

### Bước 3: Khởi động Redis Server
Dự án sử dụng Redis làm cơ sở dữ liệu lưu cache và session. Bạn phải khởi động Redis trước khi chạy ứng dụng:
- **Chạy nhanh bằng Docker**:
  ```bash
  docker run --name swp-redis -p 6379:6379 -d redis:alpine
  ```
- **Chạy dịch vụ Redis cục bộ**:
  Đảm bảo Redis đang lắng nghe ở cổng mặc định `6379` và không yêu cầu mật khẩu kết nối.

### Bước 4: Kiểm tra cấu hình bên thứ ba (Google OAuth2, Gmail SMTP, Gemini API)
Các thông tin cấu hình phục vụ phát triển (Dev) đã được thiết lập sẵn trong file `application.properties`:
- **Google OAuth2**: Hỗ trợ tính năng đăng nhập bằng Google. Client ID và Client Secret phục vụ dev local đã được điền sẵn.
- **Spring Mail**: Sử dụng Gmail SMTP của tài khoản dev kèm App Password (`jbyt ittu bmgi qxkc`) để gửi email chấm công và thông tin lương.
- **Gemini API Key**: Được thiết lập tại biến `openai.api.key`.

> [!WARNING]
> Các khóa cấu hình này chỉ dùng để chạy thử nghiệm môi trường local. Không được sử dụng hoặc chia sẻ bừa bãi và bắt buộc phải thay thế bằng các khóa bảo mật riêng thông qua biến môi trường khi đưa lên Production.

### Bước 5: Tìm hiểu cơ chế sinh dữ liệu mẫu (Database Seeding)
Dự án sử dụng cơ chế tự động nạp dữ liệu mẫu (Seed data) cực kỳ trực quan và thông qua code Java:
1. Thư mục `/sql` tại gốc dự án chứa sẵn **37 file dữ liệu SQL mẫu** (như chi nhánh, nhân sự, tài khoản, món ăn, khuyến mãi...).
2. Khi bạn chạy ứng dụng lần đầu tiên, class [DataSeeder.java](file:///d:/swp/src/main/java/web/restaurant/swp/config/DataSeeder.java) sẽ tự động kiểm tra bảng `roles`. Nếu số lượng bản ghi bằng `0` (database trống), chương trình sẽ tự động import tuần tự cả 37 file SQL này vào database PostgreSQL của bạn và reset sequence ID tự động.
3. *Mẹo:* Nếu bạn muốn chỉnh sửa dữ liệu mẫu hoặc tái sinh toàn bộ file SQL, hãy điều chỉnh script Python `generate_sql.py` và chạy lệnh:
   ```bash
   python generate_sql.py
   ```

---

## 4. Cách khởi chạy dự án (Run the Application)

### Cách 1: Chạy trực tiếp từ IDE (Khuyến nghị khi Dev)
1. Mở thư mục dự án bằng IntelliJ IDEA.
2. Đợi IDE tải xuống các dependencies trong file `pom.xml`.
3. Tìm đến class chạy chính tại: `src/main/java/web/restaurant/swp/SwpApplication.java`.
4. Click chuột phải vào file hoặc icon của class và chọn **Run 'SwpApplication'**.

### Cách 2: Chạy thông qua Maven Wrapper (Dòng lệnh Terminal)
Mở terminal ở thư mục gốc dự án và chạy các lệnh tương ứng:
- **Hệ điều hành Windows**:
  ```powershell
  .\mvnw spring-boot:run
  ```
- **Hệ điều hành Linux / macOS**:
  ```bash
  chmod +x mvnw
  ./mvnw spring-boot:run
  ```

*Khi terminal xuất hiện dòng log `Tomcat started on port 8080 (http)` và `Database seeding successfully completed.`, dự án của bạn đã sẵn sàng hoạt động.*

---

## 5. Tài khoản thử nghiệm & Hướng dẫn kiểm thử (Testing Guide)

Mở trình duyệt web của bạn và truy cập địa chỉ: `http://localhost:8080` (Hệ thống sẽ tự động dẫn đến trang đăng nhập `/login`).

Hệ thống đã cấu hình mật khẩu mặc định chung cho toàn bộ các tài khoản thử nghiệm:
- **Mật khẩu dùng chung:** `Admin123!`

### Bảng danh sách tài khoản kiểm thử theo phân quyền:

| Vai trò (Role) | Email đăng nhập | Trang chuyển hướng sau đăng nhập | Phạm vi chức năng chính |
| :--- | :--- | :--- | :--- |
| **Chủ chuỗi / Admin** | `admin@liteflow.com` | `/dashboard` | Xem Dashboard tổng quan chuỗi, quản lý chi nhánh, cấu hình hệ thống, phân quyền tài khoản. |
| **Quản lý chi nhánh / Manager** | `manager@liteflow.com` | `/dashboard` | Quản lý ca làm việc, hóa đơn, doanh thu và nhân sự thuộc chi nhánh phụ trách. |
| **Thu ngân / Cashier** | `cashier@liteflow.com` | `/pos` | Giao diện bán hàng POS, đặt món cho bàn, thanh toán hóa đơn, in hóa đơn. |
| **Đầu bếp / Kitchen** | `kitchen@liteflow.com` | `/kds` | Xem màn hình bếp KDS nhận đơn từ POS, chuyển đổi trạng thái món ăn (Đang nấu, Đã xong). |
| **Nhân sự / HR Officer** | `hr@liteflow.com` | `/employees` | Quản lý nhân sự, lịch làm việc, duyệt phép nghỉ, tính công và phát lương. |
| **Nhân viên / Employee** | `employee@liteflow.com` | `/employees` | Màn hình cá nhân của nhân viên: Xem ca làm, xem bảng lương, tạo yêu cầu nghỉ phép. |

### Các kịch bản kiểm thử nhanh khuyến nghị (Quick Smoke Tests):

1. **Kiểm thử phân quyền truy cập (RBAC)**:
   - Đăng nhập tài khoản Admin (`admin@liteflow.com`) -> Đảm bảo truy cập được `/dashboard`.
   - Vẫn trong phiên đăng nhập đó, thử truy cập `/pos` hoặc `/kds` để đảm bảo hệ thống nhận diện đúng vai trò.
   - Thử đăng nhập bằng tài khoản phục vụ (`employee@liteflow.com`), sau đó gõ thủ công URL `/dashboard` trên thanh địa chỉ -> Trình duyệt phải trả về mã lỗi **403 Forbidden** (hoặc trang báo lỗi truy cập).

2. **Kiểm thử Real-time đặt món (KDS & POS integration)**:
   - *Bước 1:* Mở tab trình duyệt ẩn danh 1 (hoặc trình duyệt Chrome) -> Đăng nhập tài khoản Thu ngân (`cashier@liteflow.com`), truy cập vào màn hình `/pos`. Chọn một bàn trống, gọi một vài món ăn (ví dụ: *Cơm Tấm Sườn Bì Chả*) và bấm **Gửi bếp (Send)**.
   - *Bước 2:* Mở tab trình duyệt ẩn danh 2 (hoặc trình duyệt Firefox) -> Đăng nhập tài khoản Đầu bếp (`kitchen@liteflow.com`), truy cập vào `/kds`.
   - *Kiểm tra:* Ngay sau khi bấm Gửi bếp tại tab 1, tab KDS phải hiển thị đơn món ăn vừa gọi mà không cần bạn phải tải lại trang (F5). Đồng thời, thử chuyển đổi trạng thái món từ `Đang nấu` sang `Hoàn thành` trên KDS và kiểm tra cập nhật trạng thái tại POS.

3. **Kiểm thử Đồng bộ Cơ sở dữ liệu (PostgreSQL Check)**:
   - Kết nối DB và chạy lệnh `SELECT COUNT(*) FROM products;` hoặc `SELECT COUNT(*) FROM users;`.
   - Đảm bảo số lượng dữ liệu trùng khớp với các file insert trong thư mục `/sql`.

---

## 6. Các lỗi thường gặp và cách xử lý (Troubleshooting)

- **Lỗi 1: `Connection refused` đến cổng 5432**
  - *Nguyên nhân:* PostgreSQL chưa chạy hoặc đang chạy ở cổng khác 5432.
  - *Cách sửa:* Kiểm tra trạng thái service PostgreSQL trên máy (như qua Services.msc trên Windows hoặc `sudo service postgresql status` trên Linux).

- **Lỗi 2: Ứng dụng crash ngay khi khởi động do Redis (`RedisConnectionFailureException`)**
  - *Nguyên nhân:* Ứng dụng không tìm thấy cổng `6379` của Redis.
  - *Cách sửa:* Bắt buộc phải bật Redis Server trước rồi mới khởi động dự án Java.

- **Lỗi 3: Lỗi trùng khóa chính (`Duplicate key value violates unique constraint`)**
  - *Nguyên nhân:* Xảy ra nếu CSDL bị chỉnh sửa thủ công làm lệch sequence ID, hoặc do chạy lại tính năng seeding khi DB đã có dữ liệu cũ mà không dọn dẹp.
  - *Cách sửa:* Cách nhanh nhất là drop database `swp`, tạo lại database trống và khởi động lại Spring Boot để hệ thống tái thiết lập tự động.

- **Lỗi 4: Thay đổi giao diện HTML/Thymeleaf nhưng không cập nhật trên trình duyệt**
  - *Nguyên nhân:* Do cache trình duyệt hoặc cache của Thymeleaf.
  - *Cách sửa:* Nhấn tổ hợp `Ctrl + F5` để ép trình duyệt tải lại trang mới. Hoặc nhấn `Ctrl + F9` (Rebuild Project) trong IntelliJ để DevTools tự động cập nhật hot reload.

---
*Chúc các thành viên cài đặt hệ thống thành công và có trải nghiệm phát triển dự án hiệu quả!*
