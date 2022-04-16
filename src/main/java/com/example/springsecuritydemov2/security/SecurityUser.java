package com.example.springsecuritydemov2.security;

import com.example.springsecuritydemov2.model.Status;
import com.example.springsecuritydemov2.model.User;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
public class SecurityUser implements UserDetails {

    private final String username;
    private final String password;
    private final List<SimpleGrantedAuthority> authorities;
    private final boolean isActive;

    public SecurityUser(String username, String password, List<SimpleGrantedAuthority> authorities, boolean isActive) {
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.isActive = isActive;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isActive;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isActive;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isActive;
    }

    @Override
    public boolean isEnabled() {
        return isActive;
    }

    public static UserDetails fromUser(User user){
        return new org.springframework.security.core.userdetails.User(// šo izvēlējos no saraksta sākot rakstīt User
                user.getUsername()
                , user.getPassword()
                , user.getStatus().equals(Status.ACTIVE) // četri gabali jo augstāk ir 4 pozīcijas
                , user.getStatus().equals(Status.ACTIVE)
                , user.getStatus().equals(Status.ACTIVE)
                , user.getStatus().equals(Status.ACTIVE)
                , user.getRole().getAuthorities()
        );
    }
}
