package web.restaurant.swp.config;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import web.restaurant.swp.modules.auth.model.User;
import web.restaurant.swp.modules.auth.repository.UserRepository;
import web.restaurant.swp.modules.branch.repository.BranchRepository;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAdvice {

    private final UserRepository userRepository;
    private final BranchRepository branchRepository;

    @ModelAttribute
    public void addGlobalAttributes(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            userRepository.findByEmail(auth.getName()).ifPresent(loggedInUser -> {
                boolean isSuperAdmin = loggedInUser.getBranch() == null && 
                        loggedInUser.getRoles().stream().anyMatch(r -> "ADMIN".equalsIgnoreCase(r.getName()));
                model.addAttribute("isSuperAdmin", isSuperAdmin);

                boolean isAdmin = loggedInUser.getRoles().stream().anyMatch(r -> "ADMIN".equalsIgnoreCase(r.getName()));
                model.addAttribute("isAdmin", isAdmin);

                boolean canSwitchBranch = BranchContext.canSwitchBranch(loggedInUser);
                model.addAttribute("canSwitchBranch", canSwitchBranch);
                model.addAttribute("currentUser", loggedInUser);

                String activeBranchId = BranchContext.getActiveBranchId(loggedInUser);
                model.addAttribute("activeBranchId", activeBranchId);

                branchRepository.findById(activeBranchId).ifPresent(activeBranch -> {
                    model.addAttribute("activeBranchName", activeBranch.getName());
                });

                if (canSwitchBranch) {
                    model.addAttribute("branches", branchRepository.findAll());
                }
            });
        }
    }
}
