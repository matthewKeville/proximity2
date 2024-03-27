package keville.security;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import static org.springframework.security.config.Customizer.withDefaults;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(@Autowired HttpSecurity http,
      @Autowired HandlerMappingIntrospector introspector) 
        throws Exception 
  {
    MvcRequestMatcher.Builder mvcMatcherBuilder = new MvcRequestMatcher.Builder(introspector);

    return http

      .csrf(csrf -> csrf.disable())
      .formLogin(withDefaults())
      .logout((logout) -> logout.permitAll())
      .authorizeHttpRequests( request -> request

        .requestMatchers(mvcMatcherBuilder.pattern("/built/bundle.js")).permitAll()
        .requestMatchers(mvcMatcherBuilder.pattern("/css/style.css")).permitAll()

        .requestMatchers(mvcMatcherBuilder.pattern("/")).permitAll()
        .requestMatchers(mvcMatcherBuilder.pattern("/api/userinfo")).permitAll()
        .requestMatchers(mvcMatcherBuilder.pattern("/error")).permitAll()

        .anyRequest().authenticated()

      )
      .httpBasic(withDefaults())
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
