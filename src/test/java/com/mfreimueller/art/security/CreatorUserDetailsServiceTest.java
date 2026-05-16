package com.mfreimueller.art.security;

import com.mfreimueller.art.domain.Creator;
import com.mfreimueller.art.persistence.CreatorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreatorUserDetailsServiceTest {

    @Mock
    private CreatorRepository creatorRepository;

    @InjectMocks
    private CreatorUserDetailsService service;

    @Test
    void returns_user_details_when_creator_found() {
        var creator = Creator.builder()
                .id(new Creator.CreatorId(1L))
                .username("testuser")
                .password("secret")
                .role(Creator.Role.EDITOR)
                .build();

        when(creatorRepository.findByUsername("testuser")).thenReturn(Optional.of(creator));

        var details = service.loadUserByUsername("testuser");

        assertThat(details).isInstanceOf(CreatorDetails.class);
        assertThat(details.getUsername()).isEqualTo("testuser");
        assertThat(details.getPassword()).isEqualTo("secret");
        assertThat(details.getAuthorities())
                .anyMatch(a -> a.getAuthority().equals("ROLE_EDITOR"));
    }

    @Test
    void throws_exception_when_creator_not_found() {
        when(creatorRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.loadUserByUsername("nonexistent"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("nonexistent");
    }
}
