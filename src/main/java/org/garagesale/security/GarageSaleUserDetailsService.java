package org.garagesale.security;

import org.garagesale.repository.GarageSaleUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class GarageSaleUserDetailsService implements UserDetailsService {

    @Autowired
    private GarageSaleUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        GarageSaleUser garageSaleUser = userRepository.findByUserName(username).orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        return GarageSaleUserDetails.build(garageSaleUser);
    }
}
