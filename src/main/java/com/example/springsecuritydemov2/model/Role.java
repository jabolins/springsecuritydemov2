package com.example.springsecuritydemov2.model;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

public enum Role {
    ADMIN(Set.of(Permission.DEVELOPERS_READ,Permission.DEVELOPERS_WRITE)),
    USER (Set.of(Permission.DEVELOPERS_READ));

    private final Set<Permission> permissionSet;

    Role(Set<Permission> permissionSet) {
        this.permissionSet = permissionSet;
    }

    public Set<Permission> getPermissionSet() {
        return permissionSet;
    }

    //SpringSecurity izmanto SimpleGrantedAuthority lai noteiktu kam kādas pilnvaras ir
    // . Mēs pārrakstām loģiku lai tur dabūtu iekšā mūsu pilnvaras kas atrakstītas ieprieks
    public Set<SimpleGrantedAuthority> getAuthorities(){
        return getPermissionSet().stream()
                .map(permissionSet ->new SimpleGrantedAuthority(permissionSet.getPermission()))
                .collect(Collectors.toSet());
   }
}
