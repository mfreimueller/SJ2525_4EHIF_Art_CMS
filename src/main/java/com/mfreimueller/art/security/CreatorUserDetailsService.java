package com.mfreimueller.art.security;

import com.mfreimueller.art.persistence.CreatorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CreatorUserDetailsService implements UserDetailsService {

    private final CreatorRepository creatorRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var creator = creatorRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Creator not found: " + username));
        return new CreatorDetails(creator);
    }
}
