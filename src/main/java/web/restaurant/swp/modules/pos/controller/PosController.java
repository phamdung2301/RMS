package web.restaurant.swp.modules.pos.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import web.restaurant.swp.config.KdsWebSocketHandler;
import web.restaurant.swp.modules.auth.model.*;
import web.restaurant.swp.modules.auth.repository.*;
import web.restaurant.swp.modules.auth.service.*;
import web.restaurant.swp.modules.pos.model.*;
import web.restaurant.swp.modules.pos.repository.*;
import web.restaurant.swp.modules.pos.service.*;
import web.restaurant.swp.modules.inventory.model.*;
import web.restaurant.swp.modules.inventory.repository.*;
import web.restaurant.swp.modules.inventory.service.*;
import web.restaurant.swp.modules.procurement.model.*;
import web.restaurant.swp.modules.procurement.repository.*;
import web.restaurant.swp.modules.procurement.service.*;
import web.restaurant.swp.modules.hr.model.*;
import web.restaurant.swp.modules.hr.repository.*;
import web.restaurant.swp.modules.hr.service.*;
import web.restaurant.swp.modules.loyalty.model.*;
import web.restaurant.swp.modules.loyalty.repository.*;
import web.restaurant.swp.modules.loyalty.service.*;
import web.restaurant.swp.modules.promotion.model.*;
import web.restaurant.swp.modules.promotion.repository.*;
import web.restaurant.swp.modules.promotion.service.*;
import web.restaurant.swp.modules.analytics.service.*;
import web.restaurant.swp.modules.branch.model.*;
import web.restaurant.swp.modules.branch.repository.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PosController {

    private final TableRepository tableRepository;
    private final TableSessionRepository tableSessionRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductVariantRepository productVariantRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final BranchRepository branchRepository;
    private final RoleRepository roleRepository;
    private final UserSessionRepository userSessionRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final OrderService orderService;

    private User getLoggedInUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return null;
        return userRepository.findByEmail(auth.getName()).orElse(null);
    }

    private String getActiveBranchId() {
        User user = getLoggedInUser();
        if (user != null && user.getBranch() != null) {
            return user.getBranch().getBranchId();
        }
        return "branch-1";
    }

    @GetMapping("/pos")
    public String pos(Model model) {
        String branchId = getActiveBranchId();
        model.addAttribute("tables", tableRepository.findByRoomBranchBranchId(branchId));
        model.addAttribute("products", productRepository.findByIsActiveTrue());
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("variants", productVariantRepository.findAll());
        model.addAttribute("customers", customerRepository.findAll());
        model.addAttribute("rooms", roomRepository.findByBranchBranchId(branchId));
        model.addAttribute("activeBranchId", branchId);
        return "pos";
    }

    @GetMapping("/api/pos/session/active")
    @ResponseBody
    public ResponseEntity<?> getActiveSession(@RequestParam Long tableId) {
        Optional<TableSession> sessionOpt = tableSessionRepository.findByTableIdAndStatus(tableId, "ACTIVE");
        if (sessionOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        TableSession session = sessionOpt.get();
        List<Order> orders = orderRepository.findBySessionId(session.getId());
        
        Map<String, Object> response = new HashMap<>();
        response.put("sessionId", session.getId());
        response.put("tableId", session.getTable().getId());
        response.put("tableName", session.getTable().getName());
        response.put("status", session.getStatus());
        
        List<Map<String, Object>> cartItems = new ArrayList<>();
        double total = 0.0;
        
        for (Order order : orders) {
            if ("PENDING".equalsIgnoreCase(order.getStatus()) || "SENT".equalsIgnoreCase(order.getStatus())) {
                List<OrderDetail> details = orderDetailRepository.findByOrderId(order.getId());
                for (OrderDetail detail : details) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("detailId", detail.getId());
                    item.put("productName", detail.getVariant().getProduct().getName());
                    item.put("variantName", detail.getVariant().getName());
                    item.put("price", detail.getPrice());
                    item.put("quantity", detail.getQuantity());
                    item.put("status", detail.getStatus());
                    item.put("notes", detail.getNotes());
                    cartItems.add(item);
                    total += detail.getPrice() * detail.getQuantity();
                }
            }
        }
        response.put("items", cartItems);
        response.put("total", total);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/pos/session/open")
    @ResponseBody
    public ResponseEntity<?> openSession(@RequestParam Long tableId, @RequestParam(required = false) Long customerId) {
        try {
            TableSession session = orderService.openTableSession(tableId, customerId);
            return ResponseEntity.ok(session);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/api/pos/order/add")
    @ResponseBody
    public ResponseEntity<?> addToCart(@RequestParam Long sessionId, @RequestParam Long variantId, @RequestParam int quantity, @RequestParam(required = false, defaultValue = "") String notes) {
        try {
            OrderDetail detail = orderService.addItemToSession(sessionId, variantId, quantity, notes);
            return ResponseEntity.ok(detail);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/api/pos/order/send")
    @ResponseBody
    public ResponseEntity<?> sendToKds(@RequestParam Long sessionId) {
        try {
            orderService.sendToKitchen(sessionId);
            KdsWebSocketHandler.broadcast("NEW_ORDER_SUBMITTED");
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/api/pos/bill/merge")
    @ResponseBody
    public ResponseEntity<?> mergeBill(@RequestParam Long sourceSessionId, @RequestParam Long targetSessionId) {
        try {
            orderService.mergeBill(sourceSessionId, targetSessionId);
            KdsWebSocketHandler.broadcast("ORDER_STATE_CHANGED");
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/api/pos/bill/split")
    @ResponseBody
    public ResponseEntity<?> splitBill(@RequestParam Long sessionId, @RequestParam String detailIds) {
        try {
            List<Long> ids = Arrays.stream(detailIds.split(","))
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
            List<Long> sessions = orderService.splitBill(sessionId, ids);
            KdsWebSocketHandler.broadcast("ORDER_STATE_CHANGED");
            return ResponseEntity.ok(sessions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/api/pos/checkout/vnpay")
    @ResponseBody
    public ResponseEntity<?> requestVNPayQR(@RequestParam Long sessionId) {
        try {
            String payData = orderService.generateVNPayQR(sessionId);
            return ResponseEntity.ok(Map.of("qrData", payData));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/api/pos/checkout/confirm")
    @ResponseBody
    public ResponseEntity<?> finalizePayment(@RequestParam Long sessionId, @RequestParam double amount) {
        try {
            orderService.confirmPayment(sessionId, amount);
            KdsWebSocketHandler.broadcast("ORDER_STATE_CHANGED");
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/api/pos/rooms")
    @ResponseBody
    public ResponseEntity<?> getRooms() {
        try {
            String branchId = getActiveBranchId();
            List<Room> rooms = roomRepository.findByBranchBranchId(branchId);
            return ResponseEntity.ok(rooms);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/api/pos/tables")
    @ResponseBody
    public ResponseEntity<?> getTables() {
        try {
            String branchId = getActiveBranchId();
            List<TableEntity> tables = tableRepository.findByRoomBranchBranchId(branchId);
            return ResponseEntity.ok(tables);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/api/pos/rooms/add")
    @ResponseBody
    public ResponseEntity<?> addRoom(@RequestParam String name) {
        try {
            User loggedInUser = getLoggedInUser();
            if (loggedInUser == null || loggedInUser.getRoles().stream().noneMatch(r -> "ADMIN".equalsIgnoreCase(r.getName()) || "MANAGER".equalsIgnoreCase(r.getName()))) {
                return ResponseEntity.status(403).body("Không có quyền thực hiện thao tác này.");
            }
            
            String branchId = getActiveBranchId();
            Branch branch = branchRepository.findById(branchId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy chi nhánh"));
            Room room = Room.builder()
                    .name(name)
                    .branch(branch)
                    .build();
            room = roomRepository.save(room);
            return ResponseEntity.ok(room);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/api/pos/rooms/delete")
    @ResponseBody
    public ResponseEntity<?> deleteRoom(@RequestParam Long roomId) {
        try {
            User loggedInUser = getLoggedInUser();
            if (loggedInUser == null || loggedInUser.getRoles().stream().noneMatch(r -> "ADMIN".equalsIgnoreCase(r.getName()) || "MANAGER".equalsIgnoreCase(r.getName()))) {
                return ResponseEntity.status(403).body("Không có quyền thực hiện thao tác này.");
            }
            
            Room room = roomRepository.findById(roomId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng/khu vực"));
            
            List<TableEntity> tables = tableRepository.findByRoomId(roomId);
            if (!tables.isEmpty()) {
                return ResponseEntity.badRequest().body("Không thể xóa khu vực này vì vẫn còn bàn thuộc khu vực.");
            }
            
            roomRepository.delete(room);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/api/pos/tables/add")
    @ResponseBody
    public ResponseEntity<?> addTable(@RequestParam String name, @RequestParam Long roomId, @RequestParam Integer capacity) {
        try {
            User loggedInUser = getLoggedInUser();
            if (loggedInUser == null || loggedInUser.getRoles().stream().noneMatch(r -> "ADMIN".equalsIgnoreCase(r.getName()) || "MANAGER".equalsIgnoreCase(r.getName()))) {
                return ResponseEntity.status(403).body("Không có quyền thực hiện thao tác này.");
            }
            
            Room room = roomRepository.findById(roomId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng/khu vực"));
            TableEntity table = TableEntity.builder()
                    .name(name)
                    .room(room)
                    .capacity(capacity)
                    .status("EMPTY")
                    .guestCount(0)
                    .build();
            table = tableRepository.save(table);
            return ResponseEntity.ok(table);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/api/pos/tables/update")
    @ResponseBody
    public ResponseEntity<?> updateTable(@RequestParam Long tableId, @RequestParam String name, @RequestParam Long roomId, @RequestParam Integer capacity) {
        try {
            User loggedInUser = getLoggedInUser();
            if (loggedInUser == null || loggedInUser.getRoles().stream().noneMatch(r -> "ADMIN".equalsIgnoreCase(r.getName()) || "MANAGER".equalsIgnoreCase(r.getName()))) {
                return ResponseEntity.status(403).body("Không có quyền thực hiện thao tác này.");
            }
            
            TableEntity table = tableRepository.findById(tableId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy bàn"));
            Room room = roomRepository.findById(roomId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng/khu vực"));
            
            table.setName(name);
            table.setRoom(room);
            table.setCapacity(capacity);
            table = tableRepository.save(table);
            return ResponseEntity.ok(table);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/api/pos/tables/delete")
    @ResponseBody
    public ResponseEntity<?> deleteTable(@RequestParam Long tableId) {
        try {
            User loggedInUser = getLoggedInUser();
            if (loggedInUser == null || loggedInUser.getRoles().stream().noneMatch(r -> "ADMIN".equalsIgnoreCase(r.getName()) || "MANAGER".equalsIgnoreCase(r.getName()))) {
                return ResponseEntity.status(403).body("Không có quyền thực hiện thao tác này.");
            }
            
            TableEntity table = tableRepository.findById(tableId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy bàn"));
            
            if (!"EMPTY".equalsIgnoreCase(table.getStatus())) {
                return ResponseEntity.badRequest().body("Không thể xóa bàn đang có khách hoặc đã đặt.");
            }
            
            Optional<TableSession> sessionOpt = tableSessionRepository.findByTableIdAndStatus(tableId, "ACTIVE");
            if (sessionOpt.isPresent()) {
                return ResponseEntity.badRequest().body("Không thể xóa bàn đang có phiên hoạt động.");
            }
            
            boolean hasHistory = tableSessionRepository.existsByTableId(tableId);
            if (hasHistory) {
                return ResponseEntity.badRequest().body("Không thể xóa bàn này vì đã có lịch sử hoạt động/hóa đơn. Bạn có thể đổi tên bàn hoặc chuyển nó sang phòng khác.");
            }
            
            tableRepository.delete(table);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/api/pos/branch-admins")
    @ResponseBody
    public ResponseEntity<?> getBranchAdmins() {
        try {
            User loggedInUser = getLoggedInUser();
            if (loggedInUser == null || loggedInUser.getBranch() != null || loggedInUser.getRoles().stream().noneMatch(r -> "ADMIN".equalsIgnoreCase(r.getName()))) {
                return ResponseEntity.status(403).body("Không có quyền truy cập.");
            }

            List<User> users = userRepository.findAll().stream()
                    .filter(u -> !u.getEmail().equalsIgnoreCase(loggedInUser.getEmail()))
                    .filter(u -> u.getRoles().stream().anyMatch(r -> "ADMIN".equalsIgnoreCase(r.getName()) || "MANAGER".equalsIgnoreCase(r.getName())))
                    .collect(Collectors.toList());

            List<Map<String, Object>> result = new ArrayList<>();
            for (User u : users) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", u.getId());
                map.put("name", u.getName());
                map.put("email", u.getEmail());
                map.put("branchId", u.getBranch() != null ? u.getBranch().getBranchId() : "");
                map.put("branchName", u.getBranch() != null ? u.getBranch().getName() : "Hệ Thống (Không chi nhánh)");
                
                String roleName = u.getRoles().stream()
                        .map(Role::getName)
                        .filter(r -> "ADMIN".equalsIgnoreCase(r) || "MANAGER".equalsIgnoreCase(r))
                        .findFirst().orElse("MANAGER");
                map.put("roleName", roleName);
                map.put("isActive", u.isActive());
                result.add(map);
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/api/pos/branch-admins/add")
    @ResponseBody
    public ResponseEntity<?> addBranchAdmin(@RequestParam String email, @RequestParam String name, @RequestParam String password, @RequestParam(required = false) String branchId, @RequestParam String roleName) {
        try {
            User loggedInUser = getLoggedInUser();
            if (loggedInUser == null || loggedInUser.getBranch() != null || loggedInUser.getRoles().stream().noneMatch(r -> "ADMIN".equalsIgnoreCase(r.getName()))) {
                return ResponseEntity.status(403).body("Không có quyền thực hiện.");
            }

            if (userRepository.findByEmail(email).isPresent()) {
                return ResponseEntity.badRequest().body("Email đã tồn tại trong hệ thống.");
            }

            Branch branch = null;
            if (branchId != null && !branchId.trim().isEmpty()) {
                branch = branchRepository.findById(branchId).orElse(null);
            }

            Role role = roleRepository.findByName(roleName.toUpperCase())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy vai trò " + roleName));

            User user = User.builder()
                    .email(email)
                    .name(name)
                    .password(passwordEncoder.encode(password))
                    .branch(branch)
                    .roles(new HashSet<>(Arrays.asList(role)))
                    .isActive(true)
                    .build();

            user = userRepository.save(user);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/api/pos/branch-admins/update")
    @ResponseBody
    public ResponseEntity<?> updateBranchAdmin(@RequestParam Long id, @RequestParam String email, @RequestParam String name, @RequestParam(required = false) String password, @RequestParam(required = false) String branchId, @RequestParam String roleName, @RequestParam boolean isActive) {
        try {
            User loggedInUser = getLoggedInUser();
            if (loggedInUser == null || loggedInUser.getBranch() != null || loggedInUser.getRoles().stream().noneMatch(r -> "ADMIN".equalsIgnoreCase(r.getName()))) {
                return ResponseEntity.status(403).body("Không có quyền thực hiện.");
            }

            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản."));

            if (!user.getEmail().equalsIgnoreCase(email)) {
                if (userRepository.findByEmail(email).isPresent()) {
                    return ResponseEntity.badRequest().body("Email đã tồn tại.");
                }
                user.setEmail(email);
            }

            user.setName(name);
            user.setActive(isActive);

            if (password != null && !password.trim().isEmpty()) {
                user.setPassword(passwordEncoder.encode(password));
            }

            Branch branch = null;
            if (branchId != null && !branchId.trim().isEmpty()) {
                branch = branchRepository.findById(branchId).orElse(null);
            }
            user.setBranch(branch);

            Role role = roleRepository.findByName(roleName.toUpperCase())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy vai trò."));
            user.setRoles(new HashSet<>(Arrays.asList(role)));

            user = userRepository.save(user);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/api/pos/branch-admins/delete")
    @ResponseBody
    @org.springframework.transaction.annotation.Transactional
    public ResponseEntity<?> deleteBranchAdmin(@RequestParam Long id) {
        try {
            User loggedInUser = getLoggedInUser();
            if (loggedInUser == null || loggedInUser.getBranch() != null || loggedInUser.getRoles().stream().noneMatch(r -> "ADMIN".equalsIgnoreCase(r.getName()))) {
                return ResponseEntity.status(403).body("Không có quyền thực hiện.");
            }

            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản."));

            Optional<Employee> empOpt = employeeRepository.findByUserId(id);
            if (empOpt.isPresent()) {
                user.setActive(false);
                userRepository.save(user);
                return ResponseEntity.ok(Map.of("message", "Tài khoản có hồ sơ nhân sự liên kết. Đã vô hiệu hóa (khóa) tài khoản thay vì xóa vật lý.", "softDeleted", true));
            }

            userSessionRepository.deleteByUserId(id);
            userRepository.delete(user);
            return ResponseEntity.ok(Map.of("message", "Đã xóa tài khoản thành công.", "softDeleted", false));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
