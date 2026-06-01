package web.restaurant.swp.modules.auth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import web.restaurant.swp.modules.auth.service.AuthService;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/dashboard";
    }

    @PostMapping("/api/auth/forgot-password/request")
    @ResponseBody
    public ResponseEntity<?> requestReset(@RequestBody Map<String, String> payload) {
        try {
            String email = payload.get("email");
            authService.sendForgotPasswordOtp(email);
            return ResponseEntity.ok(Map.of("message", "Mã OTP đã được gửi về email của bạn."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/api/auth/forgot-password/verify")
    @ResponseBody
    public ResponseEntity<?> verifyResetOtp(@RequestBody Map<String, String> payload) {
        try {
            String email = payload.get("email");
            String otp = payload.get("otp");
            boolean isValid = authService.verifyForgotPasswordOtp(email, otp);
            if (isValid) {
                return ResponseEntity.ok(Map.of("message", "Mã OTP hợp lệ."));
            } else {
                return ResponseEntity.badRequest().body("Mã OTP không đúng hoặc đã hết hạn.");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/api/auth/forgot-password/reset")
    @ResponseBody
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> payload) {
        try {
            String email = payload.get("email");
            String otp = payload.get("otp");
            String newPassword = payload.get("newPassword");
            authService.resetPasswordWithOtp(email, otp, newPassword);
            return ResponseEntity.ok(Map.of("message", "Đặt lại mật khẩu thành công."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
