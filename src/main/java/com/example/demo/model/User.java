package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import jakarta.validation.constraints.NotBlank;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "users")
@JsonIgnoreProperties({
        "enabled",
        "accountNonExpired",
        "credentialsNonExpired",
        "authorities",
        "accountNonLocked"
})
public class User implements UserDetails {

    @Id
    @Column(nullable = false, unique = true)
    @NotBlank(message = "Username cannot be blank")
    @Schema(example = "vamsi", description = "Username must be alphabetic and 3–20 characters")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    @Pattern(regexp = "^[A-Za-z]+$", message = "Username must contain only alphabets")
    private String username;

    @Column(nullable = false)
    @NotBlank(message = "Password cannot be blank")
    @Schema(example = "Pass@123", description = "Password must be at least 6 characters with a special character")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    @Pattern(regexp = "^(?=.*[!@#$%^&*()_+\\-={}|\\[\\]:\";'<>?,./]).+$",
    message = "Password must contain at least one special character")
    private String password;

    // Constructors
    public User() {
    }


    public User( String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters and Setters
    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // UserDetails interface methods
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList(); // No roles for now
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Always active
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Not locked
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Password never expires
    }

    @Override
    public boolean isEnabled() {
        return true; // Always enabled
    }
}
