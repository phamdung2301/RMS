package web.restaurant.swp.config;

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


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserRepository userRepository;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return email -> {
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isEmpty()) {
                throw new UsernameNotFoundException("User not found");
            }
            User user = userOpt.get();
            if (!user.isActive()) {
                throw new RuntimeException("Tài khoản đang bị khoá.");
            }

            Collection<GrantedAuthority> authorities = new ArrayList<>();
            user.getRoles().forEach(r -> authorities.add(() -> "ROLE_" + r.getName().toUpperCase()));

            return org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
                    .password(user.getPassword())
                    .authorities(authorities)
                    .build();
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/h2-console/**", "/api/**", "/ws/**") // Disable for console, api, ws
            )
            .headers(headers -> headers
                .frameOptions(frame -> frame.disable()) // Enable frames for H2 Console
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/h2-console/**", "/login", "/css/**", "/js/**", "/images/**", "/customer-portal/**").permitAll()
                .requestMatchers("/dashboard/**", "/analytics/**").hasAnyRole("ADMIN", "MANAGER")
                .requestMatchers("/pos/**").hasAnyRole("ADMIN", "MANAGER", "CASHIER", "EMPLOYEE", "KITCHEN", "CHEF")
                .requestMatchers("/kds/**").hasAnyRole("ADMIN", "MANAGER", "KITCHEN", "CHEF")
                .requestMatchers("/schedule/**").hasAnyRole("ADMIN", "MANAGER", "HR", "EMPLOYEE", "CASHIER", "CHEF")
                .requestMatchers("/procurement/**").hasAnyRole("ADMIN", "MANAGER", "PROCUREMENT")
                .requestMatchers("/inventory/**").hasAnyRole("ADMIN", "MANAGER", "WAREHOUSE", "CHEF")
                .requestMatchers("/employees/**").authenticated()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .successHandler(roleBasedSuccessHandler())
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler roleBasedSuccessHandler() {
        return new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
                
                String targetUrl = "/login";
                for (GrantedAuthority auth : authorities) {
                    String role = auth.getAuthority();
                    if ("ROLE_ADMIN".equals(role) || "ROLE_MANAGER".equals(role)) {
                        targetUrl = "/dashboard";
                        break;
                    } else if ("ROLE_CASHIER".equals(role)) {
                        targetUrl = "/pos";
                        break;
                    } else if ("ROLE_KITCHEN".equals(role)) {
                        targetUrl = "/kds";
                        break;
                    } else if ("ROLE_CHEF".equals(role)) {
                        targetUrl = "/inventory";
                        break;
                    } else if ("ROLE_EMPLOYEE".equals(role) || "ROLE_HR".equals(role) || "ROLE_PROCUREMENT".equals(role) || "ROLE_WAREHOUSE".equals(role)) {
                        targetUrl = "/employees";
                        break;
                    }
                }
                response.sendRedirect(targetUrl);
            }
        };
    }
}
