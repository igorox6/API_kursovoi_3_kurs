package com.example.api_1.service;

import com.example.api_1.entity.User;
import com.example.api_1.repo.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public MyUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseGet(() -> userRepository.findByPhone(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found with email or phone: " + username)));

        String roleName = user.getRole() != null ? user.getRole().getName() : "пользователь";
        String mappedRoleName;
        switch (roleName.toLowerCase()) {
            case "пользователь":
                mappedRoleName = "USER";
                break;
            case "администратор":
                mappedRoleName = "ADMIN";
                break;
            case "продавец":
                mappedRoleName = "SALESMAN";
                break;
            default:
                mappedRoleName = "USER";
        }

        return new org.springframework.security.core.userdetails.User(
                user.getEmail() != null ? user.getEmail() : user.getPhone(),
                user.getPassword(),
                java.util.Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + mappedRoleName))
        );
    }
}