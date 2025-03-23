package org.garagesale.controller;

import jakarta.validation.Valid;
import org.garagesale.model.AppUser;
import org.garagesale.payload.ApiResponse;
import org.garagesale.repository.AppUserRepository;
import org.garagesale.repository.AuthUserRepository;
import org.garagesale.repository.RoleRepository;
import org.garagesale.security.AuthRole;
import org.garagesale.security.AuthUser;
import org.garagesale.security.AuthUserDetails;
import org.garagesale.security.LoginRequest;
import org.garagesale.security.RoleName;
import org.garagesale.security.SignupRequest;
import org.garagesale.security.UserInfoResponse;
import org.garagesale.security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AuthUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AppUserRepository appUserRepository;


    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<?>> register(@Valid @RequestBody SignupRequest signupRequest) {

        if (userRepository.existsByUserName(signupRequest.getUsername())) {
            return ResponseEntity.badRequest().body(ApiResponse.withError("Error: Username is already taken"));
        }

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity.badRequest().body(ApiResponse.withError("Error: Email is already taken"));
        }

        AuthUser user = new AuthUser(signupRequest.getUsername(), signupRequest.getEmail(), passwordEncoder.encode(signupRequest.getPassword()));

        AuthRole authRole = roleRepository.findByRoleName(RoleName.ROLE_USER).orElseThrow(() -> new RuntimeException("Error: Role nor found"));
        Set<AuthRole> authRoles = new HashSet<>();
        authRoles.add(authRole);
        user.setRoles(authRoles);

        userRepository.save(user);

        AppUser appUser = new AppUser();
        appUser.setAppUserId(user.getUserId());
        appUser.setUserName(user.getUserName());
        appUser.setEmail(user.getEmail());

        appUserRepository.save(appUser);

        return ResponseEntity.ok(ApiResponse.withPayload("User registered successfully"));

    }

    @PostMapping("/signin")
    public ResponseEntity<ApiResponse<?>> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication;

        try {

            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        } catch (AuthenticationException e) {
            return new ResponseEntity<>(ApiResponse.withError("Bad credentials"), HttpStatus.NOT_FOUND);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        AuthUserDetails userDetails = (AuthUserDetails) authentication.getPrincipal();

        String jwtToken = jwtUtils.generateTokenFromUsername(userDetails);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        UserInfoResponse userInfoResponse = new UserInfoResponse(userDetails.getId(), jwtToken, userDetails.getUsername(), roles);

        return new ResponseEntity<>(ApiResponse.withPayload(userInfoResponse), HttpStatus.OK);

    }
}
