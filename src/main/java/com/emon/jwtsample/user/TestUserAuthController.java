package com.emon.jwtsample.user;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*",maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class TestUserAuthController {

    @GetMapping("/all")
    public String allHasAccess(){
        return "public";
    }

    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('USER','ADMIN','MODERATOR')")
    public String userHasAccess(){
        return "public";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminHasAccess(){
        return "admin";
    }

    @GetMapping("/moderator")
    @PreAuthorize("hasRole('MODERATOR')")
    public String moderatorHasAccess(){
        return "moderator";
    }
}
