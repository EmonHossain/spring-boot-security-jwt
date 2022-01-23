package com.emon.jwtsample.user;

import com.emon.jwtsample.payload.AuthenticationResponse;
import com.emon.jwtsample.payload.LoginRequest;
import com.emon.jwtsample.payload.SignupRequest;
import com.emon.jwtsample.role.EnumeratedRole;
import com.emon.jwtsample.role.Role;
import com.emon.jwtsample.role.RoleRepository;
import com.emon.jwtsample.security.jwt.Utils;
import com.emon.jwtsample.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private Utils utils;

    public AuthenticationResponse doAuthenticateUser(LoginRequest lr){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(lr.getUsername(),lr.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = utils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return new AuthenticationResponse(
                token,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles
        );
    }

    public HttpStatus doRegisterNewUser(SignupRequest signupRequest) {
        if(userRepository.existsByUsername(signupRequest.getUsername())){
            return HttpStatus.BAD_REQUEST;
        }
        else if(userRepository.existsByEmail(signupRequest.getEmail())){
            return HttpStatus.BAD_REQUEST;
        }else {
            User user = new User(signupRequest.getUsername(), signupRequest.getEmail(), passwordEncoder.encode(signupRequest.getPassword()));
            Set<String> strRoles = signupRequest.getRoles();
            Set<Role> roles = new HashSet<>();
            if(strRoles == null){
                Role userRole = roleRepository.findByName(EnumeratedRole.ROLE_USER).orElseThrow(()->new RuntimeException("Error : Role is not found."));
                roles.add(userRole);
            }else {
                strRoles.forEach(role ->{
                    switch (role){
                        case "admin":
                            Role adminRole = roleRepository.findByName(EnumeratedRole.ROLE_ADMIN).orElseThrow(()->new RuntimeException("Error : Role is not found."));
                            roles.add(adminRole);
                            break;
                        case "moderator":
                            Role moderatorRole = roleRepository.findByName(EnumeratedRole.ROLE_MODERATOR).orElseThrow(()->new RuntimeException("Error : Role is not found."));
                            roles.add(moderatorRole);
                            break;
                        default:
                            Role userRole = roleRepository.findByName(EnumeratedRole.ROLE_USER).orElseThrow(()->new RuntimeException("Error : Role is not found."));
                            roles.add(userRole);
                            break;
                    }
                });
            }


            user.setRoles(roles);
            userRepository.save(user);
            return HttpStatus.OK;
        }
    }
}
