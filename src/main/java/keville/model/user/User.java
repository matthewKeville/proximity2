package keville.model.user;

import java.util.Collection;
import java.util.HashSet;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Table("USER")
public class User implements UserDetails {

    @Id
    public Integer id;

    public String username;
    public String password;
    public String email;

    public User() {};

    public User(String username, String email, String password) {
      this.email = email;
      this.username = username;
      this.password = password;
    }

    @Override
    public String getPassword() {
      return this.password;
    }
    @Override
    public String getUsername() {
      return this.username;
    }

    // Stub
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
      return new HashSet<GrantedAuthority>();
    }

    // Stub
    @Override
    public boolean isAccountNonExpired() {
      return true;
    }

    // Stub
    @Override
    public boolean isAccountNonLocked() {
      return true;
    }

    // Stub
    @Override
    public boolean isCredentialsNonExpired() {
      return true;
    }

    // Stub
    @Override
    public boolean isEnabled() {
      return true;
    }

}
