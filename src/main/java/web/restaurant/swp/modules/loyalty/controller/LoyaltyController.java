package web.restaurant.swp.modules.loyalty.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

import java.time.LocalDate;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class LoyaltyController {

    private final CustomerRepository customerRepository;
    private final LoyaltyTransactionRepository loyaltyTransactionRepository;
    private final LoyaltyService loyaltyService;

    @GetMapping("/customer-portal/{phone}")
    public String customerPortal(@PathVariable String phone, Model model) {
        Optional<Customer> custOpt = customerRepository.findByPhone(phone);
        if (custOpt.isPresent()) {
            Customer customer = custOpt.get();
            model.addAttribute("customer", customer);
            model.addAttribute("transactions", loyaltyTransactionRepository.findByCustomerIdOrderByTransactionDateDesc(customer.getId()));
            return "customer_portal";
        }
        return "redirect:/login";
    }

    @PostMapping("/api/pos/customer/register")
    @ResponseBody
    public ResponseEntity<?> registerCust(@RequestParam String name, @RequestParam String phone, @RequestParam String birthDate) {
        try {
            Customer cust = loyaltyService.registerCustomer(name, phone, LocalDate.parse(birthDate));
            return ResponseEntity.ok(cust);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
