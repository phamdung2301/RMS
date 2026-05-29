package web.restaurant.swp.modules.inventory.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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

import java.util.Optional;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class InventoryController {

    private final BranchInventoryRepository branchInventoryRepository;
    private final UserRepository userRepository;
    private final InventoryService inventoryService;
    private final ProductStockRepository productStockRepository;
    private final ProductVariantRepository productVariantRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

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

    @GetMapping("/inventory")
    public String inventory(Model model) {
        String branchId = getActiveBranchId();
        model.addAttribute("inventoryStocks", branchInventoryRepository.findByBranchBranchId(branchId));
        model.addAttribute("lowStockItems", inventoryService.getLowStockItems(branchId));
        model.addAttribute("productStocks", productStockRepository.findAll());
        model.addAttribute("variants", productVariantRepository.findAll());
        model.addAttribute("inventoryItems", inventoryItemRepository.findAll());
        model.addAttribute("products", productRepository.findAll());
        model.addAttribute("categories", categoryRepository.findAll());
        return "inventory";
    }


    @PostMapping("/api/inventory/adjust")
    @ResponseBody
    public ResponseEntity<?> adjustStock(@RequestBody AdjustStockRequest request) {
        try {
            inventoryService.executeStocktake(
                getActiveBranchId(),
                request.getItemId(),
                request.getActualQuantity()
            );
            return ResponseEntity.ok("Successfully adjusted stock");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/api/inventory/recipes")
    @ResponseBody
    public ResponseEntity<?> saveRecipe(@RequestBody RecipeRequest request) {
        try {
            ProductVariant variant = productVariantRepository.findById(request.getVariantId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy món ăn"));
            InventoryItem item = inventoryItemRepository.findById(request.getItemId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy nguyên liệu"));
            
            // Check if mapping already exists
            Optional<ProductStock> existingOpt = productStockRepository.findByVariantId(request.getVariantId()).stream()
                    .filter(ps -> ps.getItem().getId().equals(request.getItemId()))
                    .findFirst();
            
            ProductStock stock;
            if (existingOpt.isPresent()) {
                stock = existingOpt.get();
                stock.setQuantityNeeded(request.getQuantityNeeded());
            } else {
                stock = ProductStock.builder()
                        .variant(variant)
                        .item(item)
                        .quantityNeeded(request.getQuantityNeeded())
                        .build();
            }
            
            productStockRepository.save(stock);
            return ResponseEntity.ok("Successfully saved recipe portion");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/api/inventory/recipes/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteRecipe(@PathVariable Long id) {
        try {
            productStockRepository.deleteById(id);
            return ResponseEntity.ok("Successfully deleted recipe portion");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/api/inventory/categories")
    @ResponseBody
    public ResponseEntity<?> saveCategory(@RequestBody CategoryRequest request) {
        try {
            if (request.getName() == null || request.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Tên danh mục không được trống");
            }
            Category cat = Category.builder().name(request.getName().trim()).build();
            categoryRepository.save(cat);
            return ResponseEntity.ok("Successfully saved category");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/api/inventory/categories/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        try {
            categoryRepository.deleteById(id);
            return ResponseEntity.ok("Successfully deleted category");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Không thể xóa danh mục này (có thể có món ăn đang thuộc danh mục này)");
        }
    }

    @PostMapping("/api/inventory/menu")
    @ResponseBody
    public ResponseEntity<?> saveMenu(@RequestBody MenuRequest request) {
        try {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục"));

            Product product;
            ProductVariant variant;

            if (request.getId() != null) {
                // Update existing variant
                variant = productVariantRepository.findById(request.getId())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy biến thể"));
                product = variant.getProduct();
                
                product.setName(request.getProductName().trim());
                product.setCategory(category);
                product.setDescription(request.getDescription());
                if (request.getImagePath() != null && !request.getImagePath().trim().isEmpty()) {
                    product.setImagePath(request.getImagePath().trim());
                }
                product.setActive(request.isActive());
                productRepository.save(product);

                variant.setName(request.getVariantName().trim());
                variant.setPrice(request.getPrice());
                variant.setOriginalPrice(request.getOriginalPrice());
                variant.setSku(request.getSku().trim());
                productVariantRepository.save(variant);
            } else {
                // Check if SKU already exists
                if (productVariantRepository.findBySku(request.getSku().trim()).isPresent()) {
                    throw new RuntimeException("Mã SKU đã tồn tại trên hệ thống!");
                }

                // Create new product
                product = Product.builder()
                        .name(request.getProductName().trim())
                        .category(category)
                        .description(request.getDescription())
                        .imagePath(request.getImagePath() != null && !request.getImagePath().trim().isEmpty() ? request.getImagePath().trim() : "default.png")
                        .isActive(request.isActive())
                        .build();
                product = productRepository.save(product);

                // Create product variant
                variant = ProductVariant.builder()
                        .product(product)
                        .name(request.getVariantName().trim())
                        .price(request.getPrice())
                        .originalPrice(request.getOriginalPrice())
                        .sku(request.getSku().trim())
                        .isTopping(false)
                        .build();
                productVariantRepository.save(variant);
            }

            return ResponseEntity.ok("Successfully saved menu item");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/api/inventory/menu/{variantId}")
    @ResponseBody
    public ResponseEntity<?> deleteMenu(@PathVariable Long variantId) {
        try {
            ProductVariant variant = productVariantRepository.findById(variantId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy biến thể"));
            Product product = variant.getProduct();

            // Check if there are other variants of the product
            List<ProductVariant> otherVariants = productVariantRepository.findByProductId(product.getId());
            
            // Delete variant
            productVariantRepository.delete(variant);

            // If this was the only variant, delete the product too
            if (otherVariants.size() <= 1) {
                productRepository.delete(product);
            }

            return ResponseEntity.ok("Successfully deleted menu item");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Không thể xóa món ăn này: " + e.getMessage());
        }
    }

    @lombok.Data
    public static class CategoryRequest {
        private String name;
    }

    @lombok.Data
    public static class MenuRequest {
        private Long id; // Variant ID
        private String productName;
        private Long categoryId;
        private String description;
        private String imagePath;
        private boolean active = true;
        
        private String variantName;
        private Double price;
        private Double originalPrice;
        private String sku;
    }

    @lombok.Data
    public static class AdjustStockRequest {
        private Long itemId;
        private Double actualQuantity;
    }

    @lombok.Data
    public static class RecipeRequest {
        private Long variantId;
        private Long itemId;
        private Double quantityNeeded;
    }
}
