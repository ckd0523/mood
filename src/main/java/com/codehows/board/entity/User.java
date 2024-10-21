package com.codehows.board.entity;

import com.codehows.board.constant.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;


@Entity
@NoArgsConstructor(access = AccessLevel.PUBLIC)
//@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "User")
@Getter
@Setter
@ToString
@EntityListeners(AuditingEntityListener.class)
public class User implements UserDetails {

    @Id
    @Column(name = "uid", updatable = false)
    private String uid;

    @Column(name = "password")
    private String password;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "email", nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;


    @Builder
    public User(String uid, String password, String email, String nickname, Role role) {
        this.uid = uid;
        this.password = password;
        this.email = email;
        this.nickname = nickname;
        this.role = Role.USER;
    }

    //카카오 DB 저장
    public User(String uid, String email, String nickname){
        this.uid = uid;
        this.email = email;
        this.nickname = nickname;
        this.role = Role.USER;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.getAuthority()));
    }

    @Override
    public String getUsername() {
        return uid;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
