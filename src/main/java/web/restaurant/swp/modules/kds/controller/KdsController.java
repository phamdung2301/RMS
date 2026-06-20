package web.restaurant.swp.modules.kds.controller;

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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class KdsController {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final UserRepository userRepository;
    private final InventoryService inventoryService;

    private User getLoggedInUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return null;
        return userRepository.findByEmail(auth.getName()).orElse(null);
    }

    private String getActiveBranchId() {
        return web.restaurant.swp.config.BranchContext.getActiveBranchId(getLoggedInUser());
    }

    @GetMapping("/kds")
    public String kds(Model model) {
        String branchId = getActiveBranchId();
        List<Order> activeOrders = orderRepository.findByBranchIdAndStatusNot(branchId, "Served");
        List<OrderDetail> allDetails = activeOrders.stream()
                .flatMap(o -> orderDetailRepository.findByOrderId(o.getId()).stream())
                .filter(d -> !"Served".equalsIgnoreCase(d.getStatus()) && !"Cancelled".equalsIgnoreCase(d.getStatus()))
                .collect(Collectors.toList());

        model.addAttribute("orderDetails", allDetails);
        return "kds";
    }

    @PostMapping("/api/kds/status")
    @ResponseBody
    public ResponseEntity<?> updateKdsStatus(@RequestParam Long detailId, @RequestParam String status) {
        try {
            OrderDetail detail = orderDetailRepository.findById(detailId)
                    .orElseThrow(() -> new RuntimeException("Detail not found"));
            detail.setStatus(status);
            orderDetailRepository.save(detail);

            if ("SERVED".equalsIgnoreCase(status)) {
                inventoryService.deductStockForOrderDetail(detail);
            }

            if ("READY".equalsIgnoreCase(status)) {
                KdsWebSocketHandler.broadcast("KDS_READY_ALERT:" + detail.getOrder().getSession().getTable().getName());
            } else {
                KdsWebSocketHandler.broadcast("ORDER_STATE_CHANGED");
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
