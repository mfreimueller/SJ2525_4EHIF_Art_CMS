package com.mfreimueller.art.security;

import com.mfreimueller.art.domain.Creator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class CreatorDetails implements UserDetails {

    private final Creator creator;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + creator.getRole().name()));
    }

    @Override
    public String getPassword() {
        return creator.getPassword();
    }

    @Override
    public String getUsername() {
        return creator.getUsername();
    }

}
