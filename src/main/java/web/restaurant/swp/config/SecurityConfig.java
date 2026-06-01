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
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

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
                .requestMatchers("/h2-console/**", "/login", "/css/**", "/js/**", "/images/**", "/customer-portal/**", "/api/auth/forgot-password/**").permitAll()
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
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(oauth2UserService())
                )
                .successHandler(roleBasedSuccessHandler())
                .failureHandler((request, response, exception) -> {
                    if (exception.getMessage() != null && exception.getMessage().contains("chờ kích hoạt")) {
                        response.sendRedirect("/login?pending=true");
                    } else {
                        response.sendRedirect("/login?error");
                    }
                })
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        return request -> {
            OAuth2User oAuth2User = delegate.loadUser(request);
            String email = oAuth2User.getAttribute("email");
            if (email == null) {
                throw new OAuth2AuthenticationException(new OAuth2Error("invalid_token"), "Không tìm thấy email từ tài khoản Google.");
            }

            Optional<User> userOpt = userRepository.findByEmail(email);
            User user;
            if (userOpt.isEmpty()) {
                // Tự động đăng ký tài khoản mới với vai trò mặc định EMPLOYEE
                String name = oAuth2User.getAttribute("name");
                if (name == null || name.trim().isEmpty()) {
                    name = email.split("@")[0];
                }

                Role defaultRole = roleRepository.findByName("EMPLOYEE")
                        .orElseThrow(() -> new OAuth2AuthenticationException(new OAuth2Error("role_not_found"), "Vai trò mặc định không tồn tại."));

                java.util.Set<Role> roles = new java.util.HashSet<>();
                roles.add(defaultRole);

                user = User.builder()
                        .email(email)
                        .password(passwordEncoder().encode(java.util.UUID.randomUUID().toString()))
                        .name(name)
                        .isActive(false)
                        .roles(roles)
                        .failedLoginAttempts(0)
                        .isTwoFactorEnabled(false)
                        .build();

                user = userRepository.save(user);
            } else {
                user = userOpt.get();
            }

            if (!user.isActive()) {
                throw new OAuth2AuthenticationException(new OAuth2Error("unauthorized_client"), "Tài khoản đang bị khoá.");
            }

            Collection<GrantedAuthority> authorities = new ArrayList<>();
            user.getRoles().forEach(r -> authorities.add(() -> "ROLE_" + r.getName().toUpperCase()));

            String userNameAttributeName = request.getClientRegistration()
                    .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

            return new DefaultOAuth2User(authorities, oAuth2User.getAttributes(), userNameAttributeName);
        };
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
