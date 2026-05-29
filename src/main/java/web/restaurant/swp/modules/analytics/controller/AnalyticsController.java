package web.restaurant.swp.modules.analytics.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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

import java.util.*;

@Controller
@RequiredArgsConstructor
public class AnalyticsController {

    private final OrderRepository orderRepository;
    private final TableRepository tableRepository;
    private final BranchRepository branchRepository;
    private final UserRepository userRepository;
    private final AIService aiService;

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

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        String branchId = getActiveBranchId();
        
        List<Order> orders = orderRepository.findByBranchId(branchId);
        double totalRevenue = orders.stream()
                .filter(o -> "SERVED".equalsIgnoreCase(o.getStatus()))
                .mapToDouble(Order::getTotalAmount)
                .sum();
        long activeTablesCount = tableRepository.findByRoomBranchBranchId(branchId).stream()
                .filter(t -> "OCCUPIED".equalsIgnoreCase(t.getStatus()))
                .count();
        long pendingOrdersCount = orders.stream()
                .filter(o -> "PENDING".equalsIgnoreCase(o.getStatus()))
                .count();

        model.addAttribute("branchId", branchId);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("activeTables", activeTablesCount);
        model.addAttribute("pendingOrders", pendingOrdersCount);
        model.addAttribute("orders", orders);

        User loggedInUser = getLoggedInUser();
        boolean isSuperAdmin = loggedInUser != null && loggedInUser.getBranch() == null && loggedInUser.getRoles().stream().anyMatch(r -> "ADMIN".equalsIgnoreCase(r.getName()));
        model.addAttribute("isSuperAdmin", isSuperAdmin);
        model.addAttribute("branches", branchRepository.findAll());

        // Seed data for best selling chart
        List<Map<String, Object>> bestSellers = Arrays.asList(
            Map.of("name", "Gỏi Cuốn Tôm", "value", "1.6M"),
            Map.of("name", "Phở Bò", "value", "1.7M"),
            Map.of("name", "Cơm Tấm Sườn", "value", "1.9M"),
            Map.of("name", "Bánh Mì Thịt", "value", "0.8M"),
            Map.of("name", "Bún Chả Hà Nội", "value", "1.2M")
        );
        model.addAttribute("bestSellers", bestSellers);

        return "dashboard";
    }

    @PostMapping("/api/analytics/ai-chat")
    @ResponseBody
    public ResponseEntity<?> chatAI(@RequestParam String query) {
        try {
            String branchId = getActiveBranchId();
            String response = aiService.analyzeDailyReport(branchId, query);
            return ResponseEntity.ok(Map.of("reply", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
