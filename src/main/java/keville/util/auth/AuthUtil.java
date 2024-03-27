package keville.util.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import keville.model.user.User;

public class AuthUtil {

  //this method depends on the asssumption that UserDetails is a keville.model.User
  //changes to this assumption break this method
  public static Integer getPrincipalUserId(Authentication authentication) {
    User user = (User) authentication.getPrincipal();
    return user.id;
  }

  public static String getPrincipalUsername(Authentication authentication) {
    UserDetails userInfo = (UserDetails) authentication.getPrincipal();
    return userInfo.getUsername();
  }

}
