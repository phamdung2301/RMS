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
    private final OrderDetailRepository orderDetailRepository;
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
        return web.restaurant.swp.config.BranchContext.getActiveBranchId(getLoggedInUser());
    }

    private String formatShortAmount(double amount) {
        if (amount >= 1_000_000) {
            double val = amount / 1_000_000.0;
            if (val == (long) val) {
                return String.format(Locale.US, "%dM", (long) val);
            } else {
                return String.format(Locale.US, "%.1fM", val);
            }
        } else if (amount >= 1_000) {
            double val = amount / 1_000.0;
            if (val == (long) val) {
                return String.format(Locale.US, "%dk", (long) val);
            } else {
                return String.format(Locale.US, "%.1fk", val);
            }
        } else {
            return String.format(Locale.US, "%d", (long) amount);
        }
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
        
        List<Branch> allBranches = branchRepository.findAll();
        model.addAttribute("branches", allBranches);

        // Fetch actual branch revenues
        Map<String, Double> branchRevenueMap = new HashMap<>();
        List<Object[]> revenueData = orderRepository.findRevenueByBranch();
        for (Object[] row : revenueData) {
            if (row[0] != null && row[1] != null) {
                branchRevenueMap.put((String) row[0], ((Number) row[1]).doubleValue());
            }
        }

        double maxRevenue = 0.0;
        for (Branch b : allBranches) {
            double rev = branchRevenueMap.getOrDefault(b.getBranchId(), 0.0);
            if (rev > maxRevenue) maxRevenue = rev;
        }

        List<Map<String, Object>> branchRevenues = new ArrayList<>();
        for (Branch b : allBranches) {
            double rev = branchRevenueMap.getOrDefault(b.getBranchId(), 0.0);
            
            int percentage = 10;
            if (maxRevenue > 0) {
                percentage = (int) ((rev / maxRevenue) * 80) + 10;
            }
            
            String formattedRevenue = formatShortAmount(rev);
            
            Map<String, Object> item = new HashMap<>();
            item.put("branchId", b.getBranchId());
            item.put("branchName", b.getName());
            item.put("formattedRevenue", formattedRevenue);
            item.put("percentage", percentage);
            item.put("isCurrent", b.getBranchId().equals(branchId));
            
            branchRevenues.add(item);
        }
        
        branchRevenues.sort((m1, m2) -> {
            Double r1 = branchRevenueMap.getOrDefault(m1.get("branchId"), 0.0);
            Double r2 = branchRevenueMap.getOrDefault(m2.get("branchId"), 0.0);
            return r2.compareTo(r1);
        });
        model.addAttribute("branchRevenues", branchRevenues);

        // Fetch actual best sellers
        List<Map<String, Object>> bestSellers = new ArrayList<>();
        List<Object[]> bestSellersData = (branchId != null) 
                ? orderDetailRepository.findBestSellersByBranch(branchId)
                : orderDetailRepository.findBestSellersAll();
                
        int count = 0;
        for (Object[] row : bestSellersData) {
            if (count >= 5) break;
            if (row[0] != null && row[1] != null) {
                Map<String, Object> item = new HashMap<>();
                item.put("name", row[0]);
                item.put("value", row[1] + " phần");
                bestSellers.add(item);
                count++;
            }
        }
        if (bestSellers.isEmpty()) {
            bestSellers = Arrays.asList(
                Map.of("name", "Chưa có món ăn được phục vụ", "value", "-")
            );
        }
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
