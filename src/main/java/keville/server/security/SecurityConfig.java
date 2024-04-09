package keville.server.security;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

  @Autowired
  private AuthenticationSuccessHandler authenticationSuccessHandler;

  @Bean
  public SecurityFilterChain securityFilterChain(@Autowired HttpSecurity http,
      @Autowired HandlerMappingIntrospector introspector) 
        throws Exception 
  {
    MvcRequestMatcher.Builder mvcMatcherBuilder = new MvcRequestMatcher.Builder(introspector);

    return http

      .csrf(csrf -> csrf.disable())
      .formLogin((form) -> form
          .permitAll()
          .loginPage("/login")
          .successHandler( authenticationSuccessHandler )
      )

      .logout((logout) -> logout
          .permitAll()
          .logoutSuccessUrl("/")
      )
      .authorizeHttpRequests( request -> request

        //static resources
        .requestMatchers(mvcMatcherBuilder.pattern("/built/bundle.js")).permitAll()
        .requestMatchers(mvcMatcherBuilder.pattern("/css/style.css")).permitAll()

        //web pages (non-auth)
        .requestMatchers(mvcMatcherBuilder.pattern("/")).permitAll()
        .requestMatchers(mvcMatcherBuilder.pattern("/error")).permitAll()

        .requestMatchers(mvcMatcherBuilder.pattern("/signup")).permitAll()
        .requestMatchers(mvcMatcherBuilder.pattern("/login")).permitAll()
        .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.POST,"/login")).permitAll()
        .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.POST, "/register")).permitAll()

        //api exposure
        .requestMatchers(mvcMatcherBuilder.pattern("/api/userinfo")).permitAll()

        .anyRequest().authenticated()

      )
      //.httpBasic(withDefaults())
      .build();
  }

  @Bean PasswordEncoder passwordEncoder() {
    Map<String,PasswordEncoder> encoders = new HashMap<>();
    encoders.put("noop", NoOpPasswordEncoder.getInstance());
    encoders.put("bcrypt",new BCryptPasswordEncoder(8));
    String defaultEncoder = "bcrypt";
    DelegatingPasswordEncoder delegatingPasswordEncoder = new DelegatingPasswordEncoder(defaultEncoder,encoders);
    return delegatingPasswordEncoder;
  }

}
