package com.emon.jwtsample.user;

import com.emon.jwtsample.payload.AuthenticationResponse;
import com.emon.jwtsample.payload.LoginRequest;
import com.emon.jwtsample.payload.Message;
import com.emon.jwtsample.payload.SignupRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.http.HttpResponse;

@RestController
@CrossOrigin(origins = "*",maxAge = 3600)
@RequestMapping("/api/auth/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody(required = true) LoginRequest loginRequest){
        AuthenticationResponse response = userService.doAuthenticateUser(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody(required = true) SignupRequest signupRequest){
        HttpStatus status = userService.doRegisterNewUser(signupRequest);
        switch (status){
            case OK -> {
                return ResponseEntity.ok(new Message("User registration successful!"));
            }
            case BAD_REQUEST -> {
                return ResponseEntity.badRequest().body(new Message("username or email already exists"));
            }
        }
        return ResponseEntity.internalServerError().body("Sorry registration failed");
    }
}
