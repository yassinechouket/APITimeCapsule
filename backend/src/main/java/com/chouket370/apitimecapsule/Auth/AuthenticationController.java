package com.chouket370.apitimecapsule.Auth;



import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService service;



    @PostMapping("/register")
    public AuthenticationResponse register(@RequestBody RegisterRequest request) {
        try {
            return service.register(request);
        } catch (Exception e) {
            System.err.println("Registration error: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @PostMapping("/authenticate")
    public AuthenticationResponse authenticate(@RequestBody AuthenticationRequest request) {
        return service.authenticate(request);
    }
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        System.out.println("TEST ENDPOINT HIT");
        return ResponseEntity.ok("Test works!");
    }


}
