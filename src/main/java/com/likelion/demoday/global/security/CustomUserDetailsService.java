package com.likelion.demoday.global.security;

import com.likelion.demoday.domain.entity.User;
import com.likelion.demoday.domain.enums.UserStatus;
import com.likelion.demoday.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// Spring Security UserDetailsService 구현

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    
    private final UserRepository userRepository;
    
    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user = userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userId));

        if (user.getStatus() == UserStatus.DELETED) {
            throw new UsernameNotFoundException("탈퇴한 회원입니다.");
        }

        return org.springframework.security.core.userdetails.User.builder()
                .username(String.valueOf(user.getId()))
                .password(user.getPasswordHash())
                .authorities("ROLE_USER")
                .build();
    }
}

