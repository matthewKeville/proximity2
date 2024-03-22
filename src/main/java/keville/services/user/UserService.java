package keville.services.user;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import keville.model.user.User;

//formerly UserDetailsManager (but the contract was superfluous)
public interface UserService extends UserDetailsService {
  public User getUserByUsername(String username) throws UsernameNotFoundException;
}
