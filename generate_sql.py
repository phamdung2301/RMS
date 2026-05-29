import os
from datetime import datetime, timedelta

def generate_all_sql():
    sql_dir = "d:/swp/sql"
    if not os.path.exists(sql_dir):
        os.makedirs(sql_dir)

    print("Generating SQL files...")

    # 1. roles.sql
    with open(f"{sql_dir}/roles.sql", "w", encoding="utf-8") as f:
        f.write("-- Roles SQL data\n")
        f.write("INSERT INTO roles (id, name) VALUES\n")
        roles = ["ADMIN", "MANAGER", "CASHIER", "KITCHEN", "HR", "PROCUREMENT", "WAREHOUSE", "EMPLOYEE"]
        values = []
        for i, role in enumerate(roles, 1):
            values.append(f"({i}, '{role}')")
        f.write(",\n".join(values) + "\nON CONFLICT (id) DO NOTHING;\n")

    # 2. branches.sql
    with open(f"{sql_dir}/branches.sql", "w", encoding="utf-8") as f:
        f.write("-- Branches SQL data\n")
        f.write("INSERT INTO branches (branch_id, name, address, phone, is_active) VALUES\n")
        branches = [
            ("branch-1", "Chi nhánh Trung Tâm", "123 Đường Láng, Hà Nội", "0243123456", "true"),
            ("branch-2", "Chi nhánh Quận 1", "45 Lê Lợi, TP. Hồ Chí Minh", "0283123456", "true"),
            ("branch-3", "Chi nhánh Tân Bình", "78 Cộng Hòa, TP. Hồ Chí Minh", "0283987654", "true"),
            ("branch-4", "Chi nhánh Quận 3", "200 Nguyễn Thị Minh Khai, TP. Hồ Chí Minh", "0283555444", "true"),
            ("branch-5", "Chi nhánh Hoàn Kiếm", "15 Lý Thái Tổ, Hà Nội", "0243888777", "true"),
            ("branch-6", "Chi nhánh Ba Đình", "88 Kim Mã, Hà Nội", "0243999000", "true"),
            ("branch-7", "Chi nhánh Hải Phòng", "50 Lạch Tray, Hải Phòng", "02253123456", "true"),
            ("branch-8", "Chi nhánh Đà Nẵng", "100 Hùng Vương, Đà Nẵng", "02363123456", "true"),
            ("branch-9", "Chi nhánh Cần Thơ", "12 Đại lộ Hòa Bình, Cần Thơ", "02923123456", "true"),
            ("branch-10", "Chi nhánh Biên Hòa", "250 Phạm Văn Thuận, Đồng Nai", "02513123456", "true"),
            ("branch-11", "Chi nhánh Nha Trang", "80 Trần Phú, Khánh Hòa", "02583123456", "true"),
            ("branch-12", "Chi nhánh Vũng Tàu", "10 Hạ Long, Bà Rịa - Vũng Tàu", "02543123456", "true"),
            ("branch-13", "Chi nhánh Buôn Ma Thuột", "45 Nguyễn Tất Thành, Đắk Lắk", "02623123456", "true"),
            ("branch-14", "Chi nhánh Đà Lạt", "5 Trần Hưng Đạo, Lâm Đồng", "02633123456", "true"),
            ("branch-15", "Chi nhánh Vinh", "30 Quang Trung, Nghệ An", "02383123456", "true"),
            ("branch-16", "Chi nhánh Huế", "18 Lê Lợi, Thừa Thiên Huế", "02343123456", "true"),
            ("branch-17", "Chi nhánh Quy Nhơn", "95 An Dương Vương, Bình Định", "02563123456", "true"),
            ("branch-18", "Chi nhánh Phan Thiết", "120 Nguyễn Đình Chiểu, Bình Thuận", "02523123456", "true"),
            ("branch-19", "Chi nhánh Rạch Giá", "35 Tôn Đức Thắng, Kiên Giang", "02973123456", "true"),
            ("branch-20", "Chi nhánh Long Xuyên", "10 Trần Hưng Đạo, An Giang", "02963123456", "true"),
        ]
        values = []
        for bid, name, addr, ph, active in branches:
            values.append(f"('{bid}', '{name}', '{addr}', '{ph}', {active})")
        f.write(",\n".join(values) + "\nON CONFLICT (branch_id) DO NOTHING;\n")

    # 3. users.sql
    with open(f"{sql_dir}/users.sql", "w", encoding="utf-8") as f:
        f.write("-- Users SQL data\n")
        f.write("INSERT INTO users (id, email, password, name, is_active, failed_login_attempts, branch_id, is_two_factor_enabled) VALUES\n")
        # Password bcrypt for "Admin123!"
        pwd = "$2a$10$7eq/t4lk9f9gu4CNmWPIZOJ4ktC6wwMeSazvIZsiP8l.ceOVCVe4y"
        users = []
        # Create 20 users
        for i in range(1, 21):
            name = f"Nhân viên {i}"
            email = f"staff{i}@liteflow.com"
            branch = f"branch-{(i % 3) + 1}"
            if i == 1:
                name, email, branch = "Chủ chuỗi Admin", "admin@liteflow.com", "NULL"
            elif i == 2:
                name, email, branch = "Quản lý Trung tâm", "manager@liteflow.com", "'branch-1'"
            elif i == 3:
                name, email, branch = "Thu ngân A", "cashier@liteflow.com", "'branch-1'"
            elif i == 4:
                name, email, branch = "Đầu bếp B", "kitchen@liteflow.com", "'branch-1'"
            elif i == 5:
                name, email, branch = "HR Officer", "hr@liteflow.com", "'branch-1'"
            elif i == 6:
                name, email, branch = "Phục vụ F", "employee@liteflow.com", "'branch-1'"
            else:
                branch = f"'{branch}'"

            users.append(f"({i}, '{email}', '{pwd}', '{name}', true, 0, {branch}, false)")
        f.write(",\n".join(users) + "\nON CONFLICT (id) DO NOTHING;\n")

    # 4. user_roles.sql (Join Table)
    with open(f"{sql_dir}/user_roles.sql", "w", encoding="utf-8") as f:
        f.write("-- User Roles SQL data\n")
        f.write("INSERT INTO user_roles (user_id, role_id) VALUES\n")
        mappings = [
            (1, 1), # admin
            (2, 2), # manager
            (3, 3), # cashier
            (4, 4), # kitchen
            (5, 5), # hr
            (6, 8), # employee
        ]
        # Rest map to employee or cashier
        for i in range(7, 21):
            rid = 8 if i % 2 == 0 else 3
            mappings.append((i, rid))
        
        values = []
        for uid, rid in mappings:
            values.append(f"({uid}, {rid})")
        f.write(",\n".join(values) + "\nON CONFLICT (user_id, role_id) DO NOTHING;\n")

    # 5. employees.sql
    with open(f"{sql_dir}/employees.sql", "w", encoding="utf-8") as f:
        f.write("-- Employees SQL data\n")
        f.write("INSERT INTO employees (id, user_id, branch_id, department, title, base_salary, salary_type, hire_date) VALUES\n")
        employees = []
        depts = ["Hành chính", "Thu ngân", "Bếp", "Nhân sự", "Bàn"]
        titles = ["Quản lý", "Thu ngân chính", "Đầu bếp chính", "Nhân viên nhân sự", "Nhân viên phục vụ"]
        
        # User 1 is admin/owner, so starts from user 2
        for i in range(2, 21):
            emp_id = i - 1
            branch = f"branch-{(emp_id % 3) + 1}"
            dept = depts[emp_id % 5]
            title = titles[emp_id % 5]
            base_sal = 15000000.0 if emp_id % 5 == 0 else (6000000.0 + (emp_id * 100000))
            sal_type = "Fixed" if emp_id % 5 != 4 else "Hourly"
            if sal_type == "Hourly":
                base_sal = 25000.0
            
            employees.append(f"({emp_id}, {i}, '{branch}', '{dept}', '{title}', {base_sal}, '{sal_type}', '2024-01-15')")
        f.write(",\n".join(employees) + "\nON CONFLICT (id) DO NOTHING;\n")

    # 6. categories.sql
    with open(f"{sql_dir}/categories.sql", "w", encoding="utf-8") as f:
        f.write("-- Categories SQL data\n")
        f.write("INSERT INTO categories (id, name) VALUES\n")
        categories = ["Món chính", "Đồ uống", "Tráng miệng", "Khai vị", "Món nước", "Món nướng", "Lẩu", "Ăn nhẹ"]
        values = []
        for i, cat in enumerate(categories, 1):
            values.append(f"({i}, '{cat}')")
        f.write(",\n".join(values) + "\nON CONFLICT (id) DO NOTHING;\n")

    # 7. products.sql
    with open(f"{sql_dir}/products.sql", "w", encoding="utf-8") as f:
        f.write("-- Products SQL data (Recreate Table DDL + Inserts)\n")
        f.write("-- Recreate Table containing category_id column\n")
        f.write("DROP TABLE IF EXISTS products CASCADE;\n")
        f.write("""CREATE TABLE products (
    id bigserial PRIMARY KEY,
    name varchar(255) NOT NULL,
    description text,
    image_path varchar(255),
    category_id bigint REFERENCES categories(id),
    is_active boolean DEFAULT true NOT NULL
);\n\n""")
        
        f.write("INSERT INTO products (id, name, description, image_path, category_id, is_active) VALUES\n")
        
        # 30 high-quality realistic menu products
        products = [
            # Main Dishes (category_id = 1)
            (1, "Cơm Tấm Sườn Bì Chả", "Cơm tấm đặc sản miền Nam ăn kèm sườn nướng mật ong ngon tuyệt cú mèo.", "https://images.unsplash.com/photo-1509722747041-616f39b57569?w=500", 1),
            (2, "Bún Chả Hà Nội", "Bún chả nướng than hoa thơm nức mũi ăn kèm nước chấm đu đủ xanh chua ngọt.", "https://images.unsplash.com/photo-1596797038530-2c107229654b?w=500", 1),
            (3, "Gỏi Cuốn Tôm Thịt", "Gỏi cuốn nhân tôm tươi và thịt heo, hành hẹ ăn cùng tương đậu phộng.", "https://images.unsplash.com/photo-1534422298391-e4f8c172dddb?w=500", 1),
            (4, "Bánh Mì Đặc Biệt", "Bánh mì giòn rụm kẹp pate gan, chả lụa, thịt xá xíu, sốt bơ trứng.", "https://images.unsplash.com/photo-1509722747041-616f39b57569?w=500", 1),
            (5, "Cơm Chiên Dương Châu", "Cơm chiên lạp xưởng, hạt sen, tôm tươi và rau củ thái lựu giòn ngọt.", "https://images.unsplash.com/photo-1608897013039-887f21d8c804?w=500", 1),
            
            # Drinks (category_id = 2)
            (6, "Cà Phê Sữa Đá", "Cà phê Robusta Đắk Lắk pha phin truyền thống thơm lừng quyện cùng sữa đặc.", "https://images.unsplash.com/photo-1509042239860-f550ce710b93?w=500", 2),
            (7, "Trà Đào Cam Sả", "Trà đào thơm nức quyện sả tươi và lát cam vàng thanh lọc cơ thể.", "https://images.unsplash.com/photo-1497515114629-f71d768fd07c?w=500", 2),
            (8, "Sinh Tố Bơ Sáp", "Sinh tố bơ Đắk Nông dẻo mịn béo ngậy xay cùng sữa tươi hạt điều.", "https://images.unsplash.com/photo-1553530666-ba11a7da3888?w=500", 2),
            (9, "Nước Ép Dưa Hấu", "Nước ép dưa hấu tươi mát nguyên chất giải nhiệt cho những ngày nóng bức.", "https://images.unsplash.com/photo-1589733901241-5e5148685df5?w=500", 2),
            (10, "Trà Sữa Matcha Trân Châu", "Trà sữa Matcha Nhật Bản béo ngậy kèm trân châu đen dai giòn ngọt lịm.", "https://images.unsplash.com/photo-1576092768241-dec231879fc3?w=500", 2),
            
            # Desserts (category_id = 3)
            (11, "Chè Khúc Bạch", "Chè khúc bạch thanh mát với thạch phô mai dẻo bùi, nhãn tươi và hạnh nhân.", "https://images.unsplash.com/photo-1563729784474-d77dbb933a9e?w=500", 3),
            (12, "Bánh Flan Ca Cao", "Bánh caramen mịn màng ăn kèm đá bào và sốt cacao thơm đắng nhẹ.", "https://images.unsplash.com/photo-1541795795328-f073b763494e?w=500", 3),
            (13, "Rau Câu Dừa Trái", "Rau câu nước dừa xiêm thanh ngọt tự nhiên mát lịm giòn giòn.", "https://images.unsplash.com/photo-1551024601-bec78aea704b?w=500", 3),
            (14, "Chè Thái Sầu Riêng", "Chè Thái trái cây thập cẩm kèm múi sầu riêng RI6 béo ngậy thơm phức.", "https://images.unsplash.com/photo-1563729784474-d77dbb933a9e?w=500", 3),
            
            # Appetizers / Khai vị (category_id = 4)
            (15, "Khoai Tây Chiên Bơ Tỏi", "Khoai tây chiên giòn lắc bơ tỏi và sốt phô mai cay.", "https://images.unsplash.com/photo-1573080496219-bb080dd4f877?w=500", 4),
            (16, "Súp Măng Tây Cua", "Súp cua biển thịt băm nấu cùng măng tây trắng thanh mát, bổ dưỡng.", "https://images.unsplash.com/photo-1547592165-e1d17fed6005?w=500", 4),
            (17, "Salad Ức Gà Áp Chảo", "Rau xà lách hỗn hợp kèm ức gà áp chảo và sốt dầu giấm balsamic.", "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=500", 4),
            
            # Soups / Món Nước (category_id = 5)
            (18, "Phở Bò Đặc Biệt", "Phở bò chín, tái, gầu, gân, bò viên nước dùng ngọt lịm hầm từ xương bò 24h.", "https://images.unsplash.com/photo-1582878826629-29b7ad1cdc43?w=500", 5),
            (19, "Bún Bò Huế Cổ Đô", "Bún bò huế đậm đà hương mắm ruốc, sả, giò heo, chả cua, thịt nạm.", "https://images.unsplash.com/photo-1625398407796-82650a8c135f?w=500", 5),
            (20, "Mì Quảng Tôm Thịt", "Mì quảng tôm thịt heo, trứng cút ăn kèm bánh đa nướng giòn rụm.", "https://images.unsplash.com/photo-1569718212165-3a8278d5f624?w=500", 5),
            (21, "Bánh Canh Cua Bột Gạo", "Bánh canh cua biển đặc sệt gạch cua thơm béo ngọt lịm.", "https://images.unsplash.com/photo-1569718212165-3a8278d5f624?w=500", 5),
            
            # Grilled / Món Nướng (category_id = 6)
            (22, "Sườn Cừu Nướng Sốt Thảo Mộc", "Sườn cừu áp chảo nướng sốt hương thảo rosemary kèm khoai tây nghiền.", "https://images.unsplash.com/photo-1544025162-d76694265947?w=500", 6),
            (23, "Bò Mỹ Nướng Đá", "Bò Mỹ nướng trực tiếp trên phiến đá nóng kèm sốt tiêu xanh Phú Quốc.", "https://images.unsplash.com/photo-1544025162-d76694265947?w=500", 6),
            (24, "Gà Nướng Mắc Khén", "Gà ta nướng lá chanh mắc khén Tây Bắc thơm lừng cay nhẹ.", "https://images.unsplash.com/photo-1598515214211-89d3c73ae83b?w=500", 6),
            
            # Hotpot / Lẩu (category_id = 7)
            (25, "Lẩu Thái Hải Sản Cay", "Nồi lẩu Thái chua cay thơm cốt dừa kèm tôm hùm, mực, ngao, nấm kim châm.", "https://images.unsplash.com/photo-1547928576-a4a3323dce9d?w=500", 7),
            (26, "Lẩu Riêu Cua Sườn Sụn", "Lẩu riêu cua đồng nguyên chất sườn sụn, bắp bò, giò tai, đậu hũ chiên.", "https://images.unsplash.com/photo-1547928576-a4a3323dce9d?w=500", 7),
            (27, "Lẩu Nấm Chim Bồ Câu", "Lẩu nấm tươi thiên nhiên bổ dưỡng ăn cùng chim bồ câu hầm hạt sen.", "https://images.unsplash.com/photo-1547928576-a4a3323dce9d?w=500", 7),
            
            # Snacks / Ăn Nhẹ (category_id = 8)
            (28, "Nem Chua Rán Hà Nội", "Nem chua bọc bột chiên xù giòn tan ăn kèm tương ớt cay nồng.", "https://images.unsplash.com/photo-1544025162-d76694265947?w=500", 8),
            (29, "Bánh Tráng Trộn Tây Ninh", "Bánh tráng trộn khô bò, mực xé, trứng cút, xoài xanh, rau răm, tắc tươi.", "https://images.unsplash.com/photo-1544025162-d76694265947?w=500", 8),
            (30, "Chân Gà Sả Tắc Cay", "Chân gà rút xương ngâm sả tắc ớt chua ngọt giòn sần sật cực bén.", "https://images.unsplash.com/photo-1544025162-d76694265947?w=500", 8),
        ]
        values = []
        for pid, name, desc, img, cat_id in products:
            values.append(f"({pid}, '{name}', '{desc}', '{img}', {cat_id}, true)")
        f.write(",\n".join(values) + ";\n")

    # 8. product_variants.sql
    with open(f"{sql_dir}/product_variants.sql", "w", encoding="utf-8") as f:
        f.write("-- Product Variants SQL data\n")
        f.write("INSERT INTO product_variants (id, product_id, name, price, original_price, sku, is_topping) VALUES\n")
        
        # Real-life restaurant pricing
        # We generate 1-3 variants per product
        variants = []
        vid = 1
        for pid in range(1, 31):
            if pid in [1, 2, 5, 18, 19, 20, 21]: # Main meals and soups: Size Vừa, Size Lớn
                variants.append(f"({vid}, {pid}, 'Size Vừa', 45000.0, 25000.0, 'SKU-{pid}-M', false)")
                vid += 1
                variants.append(f"({vid}, {pid}, 'Size Lớn', 55000.0, 30000.0, 'SKU-{pid}-L', false)")
                vid += 1
            elif pid in [6, 7, 8, 9, 10]: # Drinks: Đá, Nóng
                variants.append(f"({vid}, {pid}, 'Đá', 28000.0, 9000.0, 'SKU-{pid}-ICE', false)")
                vid += 1
                variants.append(f"({vid}, {pid}, 'Nóng', 25000.0, 8000.0, 'SKU-{pid}-HOT', false)")
                vid += 1
            elif pid in [22, 23, 24, 25, 26, 27]: # Grilled and Hotpots (High price)
                variants.append(f"({vid}, {pid}, 'Phần Nhỏ', 180000.0, 90000.0, 'SKU-{pid}-S', false)")
                vid += 1
                variants.append(f"({vid}, {pid}, 'Phần Lớn', 290000.0, 150000.0, 'SKU-{pid}-L', false)")
                vid += 1
            else: # Standard single size
                price = 35000.0
                if pid in [11, 12, 13, 14]: # Desserts
                    price = 30000.0
                elif pid in [15, 16, 17]: # Appetizers
                    price = 40000.0
                elif pid in [28, 29, 30]: # Snacks
                    price = 35000.0
                
                variants.append(f"({vid}, {pid}, 'Tiêu Chuẩn', {price}, {price * 0.4}, 'SKU-{pid}-STD', false)")
                vid += 1
                
        f.write(",\n".join(variants) + "\nON CONFLICT (id) DO NOTHING;\n")

    # 9. customers.sql
    with open(f"{sql_dir}/customers.sql", "w", encoding="utf-8") as f:
        f.write("-- Customers SQL data\n")
        f.write("INSERT INTO customers (id, name, phone, birth_date, membership_tier, loyalty_points, total_spent) VALUES\n")
        customers = []
        names = ["Nguyễn Văn Hùng", "Trần Thị Lan", "Lê Hoàng Nam", "Phạm Minh Tuấn", "Hoàng Thu Thảo", "Vũ Quốc Anh", "Đặng Thùy Chi", "Bùi Tiến Dũng", "Ngô Thanh Hằng", "Đỗ Minh Đức", "Hồ Bảo Trâm", "Dương Chí Kiên", "Lý Kim Ngọc", "Phan Thanh Sơn", "Võ Hồng Nhung", "Trịnh Gia Huy", "Đinh Ngọc Diệp", "Lâm Quốc Khánh", "Cao Mỹ Linh", "Mai Hữu Phước"]
        tiers = ["Bronze", "Silver", "Gold", "Platinum"]
        for i in range(1, 21):
            phone = f"09123456{i:02d}"
            bdate = f"199{i % 10}-05-{10 + i}"
            tier = tiers[i % 4]
            pts = i * 120
            spent = float(pts * 10000.0)
            customers.append(f"({i}, '{names[i-1]}', '{phone}', '{bdate}', '{tier}', {pts}, {spent})")
        f.write(",\n".join(customers) + "\nON CONFLICT (id) DO NOTHING;\n")

    # 10. rooms.sql
    with open(f"{sql_dir}/rooms.sql", "w", encoding="utf-8") as f:
        f.write("-- Rooms SQL data\n")
        f.write("INSERT INTO rooms (id, name, branch_id) VALUES\n")
        rooms = []
        for i in range(1, 21):
            branch = f"branch-{(i % 3) + 1}"
            name = f"Khu Vực {i}"
            if i == 1: name = "Sân Trước"
            elif i == 2: name = "Phòng Lạnh"
            elif i == 3: name = "Lầu 1"
            elif i == 4: name = "Lầu 2"
            rooms.append(f"({i}, '{name}', '{branch}')")
        f.write(",\n".join(rooms) + "\nON CONFLICT (id) DO NOTHING;\n")

    # 11. tables.sql
    with open(f"{sql_dir}/tables.sql", "w", encoding="utf-8") as f:
        f.write("-- Tables SQL data\n")
        f.write("INSERT INTO tables (id, name, room_id, status, capacity, guest_count) VALUES\n")
        tables = []
        statuses = ["EMPTY", "OCCUPIED", "RESERVED"]
        for i in range(1, 21):
            room_id = (i % 4) + 1
            status = statuses[i % 3]
            guest = 0
            if status == "OCCUPIED":
                guest = (i % 4) + 1
            capacity = 4 if i % 2 == 0 else 6
            tables.append(f"({i}, 'Bàn {i}', {room_id}, '{status}', {capacity}, {guest})")
        f.write(",\n".join(tables) + "\nON CONFLICT (id) DO NOTHING;\n")

    # 12. table_sessions.sql
    with open(f"{sql_dir}/table_sessions.sql", "w", encoding="utf-8") as f:
        f.write("-- Table Sessions SQL data\n")
        f.write("INSERT INTO table_sessions (id, table_id, customer_id, check_in_time, check_out_time, status, payment_status) VALUES\n")
        sessions = []
        for i in range(1, 21):
            table_id = i
            cust_id = i if i % 2 == 0 else "NULL"
            if table_id % 3 == 1:  # OCCUPIED -> ACTIVE session
                cin = "2026-05-29 12:00:00"
                cout = "NULL"
                status = "ACTIVE"
                pay_status = "UNPAID"
            elif table_id % 3 == 2:  # RESERVED -> COMPLETED session
                cin = "2026-05-29 10:00:00"
                cout = "2026-05-29 11:30:00"
                status = "COMPLETED"
                pay_status = "PAID"
            else:  # EMPTY -> COMPLETED session
                cin = "2026-05-29 08:00:00"
                cout = "2026-05-29 09:30:00"
                status = "COMPLETED"
                pay_status = "PAID"
            
            sessions.append(f"({i}, {table_id}, {cust_id}, '{cin}', {f'\'{cout}\'' if cout != 'NULL' else 'NULL'}, '{status}', '{pay_status}')")
        f.write(",\n".join(sessions) + "\nON CONFLICT (id) DO NOTHING;\n")

    # 13. orders.sql
    with open(f"{sql_dir}/orders.sql", "w", encoding="utf-8") as f:
        f.write("-- Orders SQL data\n")
        f.write("INSERT INTO orders (id, session_id, order_date, status, total_amount, branch_id) VALUES\n")
        orders = []
        for i in range(1, 21):
            sess_id = i
            odate = f"2026-05-29 12:{i:02d}:00"
            if sess_id % 3 == 1:
                status = "SENT" if i % 2 == 0 else "PENDING"
            else:
                status = "SERVED"
            amt = i * 45000.0
            room_id = (i % 4) + 1
            branch_num = (room_id % 3) + 1
            branch = f"branch-{branch_num}"
            orders.append(f"({i}, {sess_id}, '{odate}', '{status}', {amt}, '{branch}')")
        f.write(",\n".join(orders) + "\nON CONFLICT (id) DO NOTHING;\n")

    # 14. order_details.sql
    with open(f"{sql_dir}/order_details.sql", "w", encoding="utf-8") as f:
        f.write("-- Order Details SQL data\n")
        f.write("INSERT INTO order_details (id, order_id, variant_id, quantity, status, notes, price, is_deducted) VALUES\n")
        details = []
        for i in range(1, 41):
            order_id = (i % 20) + 1
            variant_id = (i % 20) + 1
            qty = (i % 3) + 1
            if order_id % 3 == 1:
                status = "READY" if i % 3 == 0 else "COOKING"
            else:
                status = "SERVED"
            price = 45000.0
            details.append(f"({i}, {order_id}, {variant_id}, {qty}, '{status}', 'Ghi chú {i}', {price}, false)")
        f.write(",\n".join(details) + "\nON CONFLICT (id) DO NOTHING;\n")

    # 15. shift_templates.sql
    with open(f"{sql_dir}/shift_templates.sql", "w", encoding="utf-8") as f:
        f.write("-- Shift Templates SQL data\n")
        f.write("INSERT INTO shift_templates (id, name, start_time, end_time, duration_hours) VALUES\n")
        templates = [
            (1, "Ca Sáng", "08:00", "16:00", 8.0),
            (2, "Ca Chiều", "16:00", "00:00", 8.0),
            (3, "Ca Đêm", "00:00", "08:00", 8.0),
            (4, "Ca Gãy 1", "10:00", "14:00", 4.0),
            (5, "Ca Gãy 2", "18:00", "22:00", 4.0),
        ]
        values = []
        for idVal, name, start, end, dur in templates:
            values.append(f"({idVal}, '{name}', '{start}', '{end}', {dur})")
        
        # Extend to 20 templates
        for i in range(6, 21):
            values.append(f"({i}, 'Ca Tùy Biến {i}', '09:00', '17:00', 8.0)")
            
        f.write(",\n".join(values) + "\nON CONFLICT (id) DO NOTHING;\n")

    # 16. employee_shift_assignments.sql
    with open(f"{sql_dir}/employee_shift_assignments.sql", "w", encoding="utf-8") as f:
        f.write("-- Employee Shift Assignments SQL data\n")
        f.write("INSERT INTO employee_shift_assignments (id, employee_id, shift_template_id, date) VALUES\n")
        assignments = []
        for i in range(1, 21):
            emp_id = (i % 5) + 1
            shift_id = (i % 3) + 1
            date = f"2026-05-{10+i}"
            assignments.append(f"({i}, {emp_id}, {shift_id}, '{date}')")
        f.write(",\n".join(assignments) + "\nON CONFLICT (id) DO NOTHING;\n")

    # 17. employee_attendances.sql
    with open(f"{sql_dir}/employee_attendances.sql", "w", encoding="utf-8") as f:
        f.write("-- Employee Attendances SQL data\n")
        f.write("INSERT INTO employee_attendances (id, employee_id, date, clock_in, clock_out, hours_worked, is_early_leave, is_late) VALUES\n")
        attendances = []
        for i in range(1, 21):
            emp_id = (i % 5) + 1
            date = f"2026-05-{10+i}"
            cin = f"2026-05-{10+i} 08:05:00"
            cout = f"2026-05-{10+i} 16:02:00"
            attendances.append(f"({i}, {emp_id}, '{date}', '{cin}', '{cout}', 8.0, false, false)")
        f.write(",\n".join(attendances) + "\nON CONFLICT (id) DO NOTHING;\n")

    # 18. leave_requests.sql
    with open(f"{sql_dir}/leave_requests.sql", "w", encoding="utf-8") as f:
        f.write("-- Leave Requests SQL data\n")
        f.write("INSERT INTO leave_requests (id, employee_id, start_date, end_date, reason, status, leave_type) VALUES\n")
        requests = []
        for i in range(1, 21):
            emp_id = (i % 5) + 1
            sdate = f"2026-06-{(10+i)%28 + 1}"
            edate = f"2026-06-{(11+i)%28 + 1}"
            reason = f"Lý do xin nghỉ phép {i}"
            status = "APPROVED" if i % 2 == 0 else "PENDING"
            ltype = "ANNUAL" if i % 2 == 0 else "SICK"
            requests.append(f"({i}, {emp_id}, '{sdate}', '{edate}', '{reason}', '{status}', '{ltype}')")
        f.write(",\n".join(requests) + "\nON CONFLICT (id) DO NOTHING;\n")

    # 19. forgot_clock_requests.sql
    with open(f"{sql_dir}/forgot_clock_requests.sql", "w", encoding="utf-8") as f:
        f.write("-- Forgot Clock Requests SQL data\n")
        f.write("INSERT INTO forgot_clock_requests (id, employee_id, date, clock_type, reason, status, time_proposed) VALUES\n")
        requests = []
        for i in range(1, 21):
            emp_id = (i % 5) + 1
            date = f"2026-05-{10+i}"
            reason = f"Quên chấm công {i}"
            status = "APPROVED" if i % 2 == 0 else "PENDING"
            requests.append(f"({i}, {emp_id}, '{date}', 'IN', '{reason}', '{status}', '08:00')")
        f.write(",\n".join(requests) + "\nON CONFLICT (id) DO NOTHING;\n")

    # 20. suppliers.sql
    with open(f"{sql_dir}/suppliers.sql", "w", encoding="utf-8") as f:
        f.write("-- Suppliers SQL data\n")
        f.write("INSERT INTO suppliers (id, code, name, contact_email, phone, address) VALUES\n")
        suppliers = []
        names = ["Thực phẩm sạch Metro", "Nông sản sạch Đà Lạt", "Hải sản tươi Nha Trang", "Thịt gia súc CP", "Sữa Ba Vì", "Hương liệu Việt", "Thiết bị nhà hàng Horeca", "Gas công nghiệp Hà Nội", "Rau quả tươi VinEco", "Hải sản tươi Phan Thiết", "Nước giải khát Coca", "Bia Sài Gòn", "Gạo ngon ST25", "Gia vị Cholimex", "Bánh kẹo Kinh Đô", "Khăn lạnh miền Bắc", "Đồng phục nhà hàng", "Túi bóng tự hủy", "Văn phòng phẩm Hồng Hà", "Hóa chất tẩy rửa Unilever"]
        for i in range(1, 21):
            code = f"SUP-{i:03d}"
            email = f"contact{i}@supplier.com"
            phone = f"09012345{i:02d}"
            addr = f"Địa chỉ nhà cung cấp {i}"
            suppliers.append(f"({i}, '{code}', '{names[i-1]}', '{email}', '{phone}', '{addr}')")
        f.write(",\n".join(suppliers) + "\nON CONFLICT (id) DO NOTHING;\n")

    # 21. inventory_items.sql
    with open(f"{sql_dir}/inventory_items.sql", "w", encoding="utf-8") as f:
        f.write("-- Inventory Items SQL data\n")
        f.write("INSERT INTO inventory_items (id, sku, name, unit, minimum_threshold) VALUES\n")
        items = ["Bột mì", "Thịt bò nạm", "Thịt bò thăn", "Hạt cà phê Robusta", "Hạt cà phê Arabica", "Rau thơm các loại", "Giá đỗ sạch", "Đào ngâm đóng hộp", "Sả tươi", "Cam vàng Úc", "Quả bơ sáp", "Dưa hấu Long An", "Bột matcha Nhật", "Trân châu đen", "Bắp bò Mỹ", "Sườn heo non", "Cua biển Cà Mau", "Sườn sụn non", "Trứng cút sạch", "Bánh tráng phơi sương"]
        values = []
        for i, item in enumerate(items, 1):
            sku = f"MAT-{i:03d}"
            unit = "kg" if i != 8 and i != 14 and i != 19 else "hộp" if i == 8 else "túi" if i == 14 else "quả"
            values.append(f"({i}, '{sku}', '{item}', '{unit}', 10.0)")
        f.write(",\n".join(values) + "\nON CONFLICT (id) DO NOTHING;\n")

    # 22. branch_inventory.sql
    with open(f"{sql_dir}/branch_inventory.sql", "w", encoding="utf-8") as f:
        f.write("-- Branch Inventory SQL data\n")
        f.write("INSERT INTO branch_inventory (id, branch_id, item_id, quantity, reorder_point) VALUES\n")
        inventory = []
        for i in range(1, 21):
            branch = f"branch-{(i % 3) + 1}"
            item_id = i
            qty = i * 20.0
            reorder = 10.0
            inventory.append(f"({i}, '{branch}', {item_id}, {qty}, {reorder})")
        f.write(",\n".join(inventory) + "\nON CONFLICT (id) DO NOTHING;\n")

    # 23. product_stocks.sql (recipes)
    with open(f"{sql_dir}/product_stocks.sql", "w", encoding="utf-8") as f:
        f.write("-- Product Stocks (Recipes) SQL data\n")
        f.write("INSERT INTO product_stocks (id, variant_id, item_id, quantity_needed) VALUES\n")
        stocks = []
        for i in range(1, 21):
            variant_id = i
            item_id = (i % 10) + 1
            qty = 0.15
            stocks.append(f"({i}, {variant_id}, {item_id}, {qty})")
        f.write(",\n".join(stocks) + "\nON CONFLICT (id) DO NOTHING;\n")

    # 24. purchase_orders.sql
    with open(f"{sql_dir}/purchase_orders.sql", "w", encoding="utf-8") as f:
        f.write("-- Purchase Orders SQL data\n")
        f.write("INSERT INTO purchase_orders (id, po_code, supplier_id, branch_id, order_date, delivery_deadline, status, total_amount) VALUES\n")
        pos = []
        statuses = ["DRAFT", "SENT", "RECEIVED", "CANCELLED"]
        for i in range(1, 21):
            code = f"PO-2026-{i:03d}"
            sup_id = (i % 5) + 1
            branch = f"branch-{(i % 3) + 1}"
            odate = f"2026-05-{10+i} 10:00:00"
            deadline = f"2026-05-{10+i}"
            status = statuses[i % 4]
            amt = i * 1500000.0
            pos.append(f"({i}, '{code}', {sup_id}, '{branch}', '{odate}', '{deadline}', '{status}', {amt})")
        f.write(",\n".join(pos) + "\nON CONFLICT (id) DO NOTHING;\n")

    # 25. purchase_order_items.sql
    with open(f"{sql_dir}/purchase_order_items.sql", "w", encoding="utf-8") as f:
        f.write("-- Purchase Order Items SQL data\n")
        f.write("INSERT INTO purchase_order_items (id, purchase_order_id, item_id, quantity, unit_price) VALUES\n")
        items = []
        for i in range(1, 21):
            po_id = (i % 10) + 1
            item_id = (i % 10) + 1
            qty = i * 5.0
            price = 25000.0
            items.append(f"({i}, {po_id}, {item_id}, {qty}, {price})")
        f.write(",\n".join(items) + "\nON CONFLICT (id) DO NOTHING;\n")

    # 26. goods_receipts.sql
    with open(f"{sql_dir}/goods_receipts.sql", "w", encoding="utf-8") as f:
        f.write("-- Goods Receipts SQL data\n")
        f.write("INSERT INTO goods_receipts (id, purchase_order_id, received_date, received_by) VALUES\n")
        grns = []
        for i in range(1, 21):
            po_id = i
            rdate = f"2026-05-{10+i} 14:00:00"
            user_id = 3 # cashier
            grns.append(f"({i}, {po_id}, '{rdate}', 'Nhân viên {user_id}')")
        f.write(",\n".join(grns) + "\nON CONFLICT (id) DO NOTHING;\n")

    # 27. goods_receipt_items.sql
    with open(f"{sql_dir}/goods_receipt_items.sql", "w", encoding="utf-8") as f:
        f.write("-- Goods Receipt Items SQL data\n")
        f.write("INSERT INTO goods_receipt_items (id, goods_receipt_id, item_id, quantity_received, quantity_rejected, notes) VALUES\n")
        items = []
        for i in range(1, 21):
            grn_id = (i % 10) + 1
            item_id = (i % 10) + 1
            qty_rec = i * 5.0
            items.append(f"({i}, {grn_id}, {item_id}, {qty_rec}, 0.0, 'Nhận đủ')")
        f.write(",\n".join(items) + "\nON CONFLICT (id) DO NOTHING;\n")

    # 28. promotions.sql
    with open(f"{sql_dir}/promotions.sql", "w", encoding="utf-8") as f:
        f.write("-- Promotions SQL data\n")
        f.write("INSERT INTO promotions (id, name, promo_code, type, discount_value, min_order_value, max_usage_count, current_usage_count, start_date, end_date, is_active) VALUES\n")
        promos = []
        types = ["PercentDiscount", "FlatDiscount", "Buy1Get1"]
        for i in range(1, 21):
            code = f"CODE{i:02d}"
            name = f"Chương trình khuyến mãi {i}"
            ptype = types[i % 3]
            val = 10.0 if ptype != "FlatDiscount" else 20000.0
            promos.append(f"({i}, '{name}', '{code}', '{ptype}', {val}, 100000.0, 100, 0, '2026-05-01', '2026-08-30', true)")
        f.write(",\n".join(promos) + "\nON CONFLICT (id) DO NOTHING;\n")

    # 29. promotion_usage.sql
    with open(f"{sql_dir}/promotion_usage.sql", "w", encoding="utf-8") as f:
        f.write("-- Promotion Usage SQL data\n")
        f.write("INSERT INTO promotion_usage (id, promotion_id, order_id, customer_id, used_date, discount_applied) VALUES\n")
        usages = []
        for i in range(1, 21):
            promo_id = (i % 5) + 1
            order_id = i
            cust_id = (i % 10) + 1
            used_at = f"2026-05-29 12:{i:02d}:00"
            usages.append(f"({i}, {promo_id}, {order_id}, {cust_id}, '{used_at}', 15000.0)")
        f.write(",\n".join(usages) + "\nON CONFLICT (id) DO NOTHING;\n")

    # 30. user_sessions.sql
    with open(f"{sql_dir}/user_sessions.sql", "w", encoding="utf-8") as f:
        f.write("-- User Sessions SQL data\n")
        f.write("INSERT INTO user_sessions (id, user_id, token, created_at, expires_at, ip_address) VALUES\n")
        sessions = []
        for i in range(1, 21):
            user_id = (i % 10) + 1
            token = f"token-2026-{i:03d}"
            cat = f"2026-05-29 08:00:00"
            eat = f"2026-05-29 18:00:00"
            sessions.append(f"({i}, {user_id}, '{token}', '{cat}', '{eat}', '192.168.1.1')")
        f.write(",\n".join(sessions) + "\nON CONFLICT (id) DO NOTHING;\n")

    # 31. audit_logs.sql
    with open(f"{sql_dir}/audit_logs.sql", "w", encoding="utf-8") as f:
        f.write("-- Audit Logs SQL data\n")
        f.write("INSERT INTO audit_logs (id, user_id, action, description, created_at, ip_address, user_name) VALUES\n")
        logs = []
        for i in range(1, 21):
            user_id = (i % 10) + 1
            action = "LOGIN" if i % 2 == 0 else "UPDATE_STOCK"
            details = f"Chi tiết hành động audit {i}"
            ts = f"2026-05-29 12:{i:02d}:00"
            ip = f"192.168.1.{10+i}"
            logs.append(f"({i}, {user_id}, '{action}', '{details}', '{ts}', '{ip}', 'Nhân viên {user_id}')")
        f.write(",\n".join(logs) + "\nON CONFLICT (id) DO NOTHING;\n")

    # 32. branch_transfers.sql
    with open(f"{sql_dir}/branch_transfers.sql", "w", encoding="utf-8") as f:
        f.write("-- Branch Transfers SQL data\n")
        f.write("INSERT INTO branch_transfers (id, source_branch_id, target_branch_id, request_date, approve_date, status) VALUES\n")
        transfers = []
        for i in range(1, 21):
            from_b = f"branch-{(i % 2) + 1}"
            to_b = "branch-3"
            rdate = f"2026-05-{10+i} 10:00:00"
            tdate = f"2026-05-{10+i} 14:00:00"
            status = "COMPLETED"
            transfers.append(f"({i}, '{from_b}', '{to_b}', '{rdate}', '{tdate}', '{status}')")
        f.write(",\n".join(transfers) + "\nON CONFLICT (id) DO NOTHING;\n")

    # 33. branch_transfer_items.sql
    with open(f"{sql_dir}/branch_transfer_items.sql", "w", encoding="utf-8") as f:
        f.write("-- Branch Transfer Items SQL data\n")
        f.write("INSERT INTO branch_transfer_items (id, transfer_id, item_id, quantity) VALUES\n")
        items = []
        for i in range(1, 21):
            transfer_id = (i % 10) + 1
            item_id = (i % 10) + 1
            qty = i * 2.0
            items.append(f"({i}, {transfer_id}, {item_id}, {qty})")
        f.write(",\n".join(items) + "\nON CONFLICT (id) DO NOTHING;\n")

    # 34. inventory_logs.sql
    with open(f"{sql_dir}/inventory_logs.sql", "w", encoding="utf-8") as f:
        f.write("-- Inventory Logs SQL data\n")
        f.write("INSERT INTO inventory_logs (id, branch_id, item_id, type, change_quantity, reason, log_date) VALUES\n")
        logs = []
        for i in range(1, 21):
            branch = f"branch-{(i % 3) + 1}"
            item_id = (i % 10) + 1
            type_val = "STOCKIN" if i % 2 == 0 else "STOCKOUT"
            qty = i * 1.5
            ref_id = f"REF-{i:03d}"
            ts = f"2026-05-29 12:{i:02d}:00"
            logs.append(f"({i}, '{branch}', {item_id}, '{type_val}', {qty}, '{ref_id}', '{ts}')")
        f.write(",\n".join(logs) + "\nON CONFLICT (id) DO NOTHING;\n")

    # 35. loyalty_transactions.sql
    with open(f"{sql_dir}/loyalty_transactions.sql", "w", encoding="utf-8") as f:
        f.write("-- Loyalty Transactions SQL data\n")
        f.write("INSERT INTO loyalty_transactions (id, customer_id, points, transaction_date, type, order_id) VALUES\n")
        transactions = []
        for i in range(1, 21):
            cust_id = (i % 10) + 1
            earned = i * 10
            redeemed = 0 if i % 4 != 0 else 50
            points = earned if redeemed == 0 else -redeemed
            ttype = "EARNED" if points > 0 else "REDEEMED"
            tdate = f"2026-05-29 12:{i:02d}:00"
            transactions.append(f"({i}, {cust_id}, {points}, '{tdate}', '{ttype}', {i})")
        f.write(",\n".join(transactions) + "\nON CONFLICT (id) DO NOTHING;\n")

    # 36. payroll_runs.sql
    with open(f"{sql_dir}/payroll_runs.sql", "w", encoding="utf-8") as f:
        f.write("-- Payroll Runs SQL data\n")
        f.write("INSERT INTO payroll_runs (id, period, run_by, run_date) VALUES\n")
        runs = []
        for i in range(1, 21):
            rdate = f"2026-05-{10+i}"
            runs.append(f"({i}, '2026-05', 'Admin', '{rdate}')")
        f.write(",\n".join(runs) + "\nON CONFLICT (id) DO NOTHING;\n")

    # 37. payroll_entries.sql
    with open(f"{sql_dir}/payroll_entries.sql", "w", encoding="utf-8") as f:
        f.write("-- Payroll Entries SQL data\n")
        f.write("INSERT INTO payroll_entries (id, payroll_run_id, employee_id, base_pay, allowances, deductions, net_pay) VALUES\n")
        entries = []
        for i in range(1, 21):
            run_id = (i % 5) + 1
            emp_id = (i % 10) + 1
            base = 7000000.0
            allow = 500000.0
            deduct = 100000.0
            final = base + allow - deduct
            entries.append(f"({i}, {run_id}, {emp_id}, {base}, {allow}, {deduct}, {final})")
        f.write(",\n".join(entries) + "\nON CONFLICT (id) DO NOTHING;\n")

    print("Successfully generated all 37 SQL files inside d:/swp/sql!")

if __name__ == "__main__":
    generate_all_sql()
