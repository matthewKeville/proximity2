package keville.controller.api;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import keville.dto.UserInfoDTO;
import keville.util.auth.AuthUtil;

@RestController
@RequestMapping("/api/userinfo")
public class UserInfoController {

  private static final Logger LOG = LoggerFactory.getLogger(UserInfoController.class);

  public UserInfoController(){}

  @GetMapping("")
  public UserInfoDTO getUserInfo(
      @Autowired Authentication authentication) {

      return authentication != null && authentication.isAuthenticated() ? 
        new UserInfoDTO(true,AuthUtil.getPrincipalUsername(authentication),AuthUtil.getPrincipalUserId(authentication))
        : 
        new UserInfoDTO(false,"Guest",null);

    }

}
