package com.example.manager;

import com.example.manager.model.User;
import com.example.manager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
@Service
public class AuthController {

    @Autowired
    private UserRepository userRepository;

@PostMapping("/signup")
public ResponseEntity<String> signup(@RequestBody Map<String, String> userData) {
    String username = userData.get("username");
    String email = userData.get("email");
    String password = userData.get("password");

    User user = new User(username, email, password);
    userRepository.save(user);

    return ResponseEntity.ok("✅ تم إنشاء الحساب بنجاح");
}

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> userData) {
        String email = userData.get("email");
        String password = userData.get("password");

        System.out.println("🚀 البريد الإلكتروني وكلمة المرور: " + email + ", " + password);

        if (validateUser(email, password)) {
            return ResponseEntity.ok("✅ تسجيل الدخول بنجاح");
        } else {
            return ResponseEntity.status(400).body("❌ البريد الإلكتروني أو كلمة المرور غير صحيحة");
        }
    }

    private boolean validateUser(String email, String password) {
        return userRepository.findByEmailAndPassword(email, password).isPresent();
    }
}
