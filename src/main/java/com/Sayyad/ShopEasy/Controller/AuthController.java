package com.Sayyad.ShopEasy.Controller;

import com.Sayyad.ShopEasy.Entity.User;
import com.Sayyad.ShopEasy.Security.JwtTokenProvider;
import com.Sayyad.ShopEasy.Service.UserService;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User user) {
        try {
            logger.info("Registering new user with mobile number: {}", user.getMobileNumber());
            User registeredUser = userService.registerUser(
                user.getName(), 
                user.getMobileNumber(), 
                user.getPassword(), 
                user.getPassword(), 
                user.getEmail()
            );
            return ResponseEntity.ok(new ApiResponse("User registered successfully"));
        } catch (Exception e) {
            logger.error("Error registering user: {}", e.getMessage());
            throw e;
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            logger.info("Login attempt for mobile number: {}", loginRequest.getMobileNumber());
            
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getMobileNumber(), 
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.generateToken(authentication);
            
            logger.info("Login successful for mobile number: {}", loginRequest.getMobileNumber());
            return ResponseEntity.ok(new JwtResponse(jwt));
            
        } catch (BadCredentialsException e) {
            logger.warn("Invalid credentials for mobile number: {}", loginRequest.getMobileNumber());
            throw e;
        }
    }

    public static class LoginRequest {
        private String mobileNumber;
        private String password;

        public String getMobileNumber() {
            return mobileNumber;
        }

        public void setMobileNumber(String mobileNumber) {
            this.mobileNumber = mobileNumber;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class JwtResponse {
        private String token;
        private String type = "Bearer";

        public JwtResponse(String token) {
            this.token = token;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    public static class ApiResponse {
        private String message;

        public ApiResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
