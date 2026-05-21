package org.peepol.domain.service;

import lombok.RequiredArgsConstructor;
import org.peepol.domain.enums.Status;
import org.peepol.domain.repo.UserRepo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByUsernameAndStatus(username, Status.ENABLED)
                .orElseThrow(() -> new UsernameNotFoundException("Unable to find user: " + username));
    }
}
