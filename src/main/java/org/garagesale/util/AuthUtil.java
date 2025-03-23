package org.garagesale.util;

import org.garagesale.repository.AuthUserRepository;
import org.garagesale.security.AuthUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class AuthUtil {

    @Autowired
    AuthUserRepository userRepository;

    public String loggedInEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        AuthUser authUser = userRepository
                .findByUserName(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with name: " + authentication.getName()));

        return authUser.getEmail();
    }

    public AuthUser loggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        AuthUser authUser = userRepository
                .findByUserName(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with name: " + authentication.getName()));

        return authUser;
    }
}
