package com.codec.system.application.service.authen;

import com.codec.system.domain.entity.UserEntity;
import com.codec.system.domain.repository.UserRepository;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.*;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
  @Autowired
  private UserRepository userRepository;
  private final JwtAuthenticationManager authenticationManager;
  private final JwtSecurityContextRepository securityContextRepository;

  public SecurityConfig(JwtAuthenticationManager authenticationManager, JwtSecurityContextRepository contextRepository) {
    this.authenticationManager = authenticationManager;
    this.securityContextRepository = contextRepository;
  }

  @Bean
  public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
    return http
      .csrf().disable()
      .httpBasic().disable()
      .formLogin().disable()
      .authenticationManager(authenticationManager)
      .securityContextRepository(securityContextRepository)
      .authorizeExchange()
      .pathMatchers("/api/v1/auth/**").permitAll()
      .pathMatchers("/api/v1/tvc/**").permitAll()
      .pathMatchers("/", "/index.html", "/style.css", "/firebase-messaging-sw.js", "/test-fcm.html", "/js/**", "/css/**", "/images/**").permitAll()
      .pathMatchers("/api/v1/notification").permitAll()
      .pathMatchers("/service/v1/**").permitAll()
      .pathMatchers("/api/v1/warehouse/detail/*").permitAll()
      .anyExchange().authenticated()
      .and().build();
  }

//  @Bean
//  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//    return http
//      .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
//      .authorizeRequests()
//      .anyRequest().authenticated()
//      .and().build();
//  }

  @Bean
  public UserDetailsService userDetailsService() {
    return username -> {
      Optional<UserEntity> user = userRepository.findByUsernameAndDeletedIsFalse(username);
      if (user.isEmpty()) {
        throw new UsernameNotFoundException("Tài khoản không tồn tại: " + username);
      }

      return new org.springframework.security.core.userdetails.User(
        user.get().getUsername(),
        user.get().getPassword(),
        new ArrayList<>() // bạn có thể set roles/authorities ở đây
      );
    };
  }

  @Bean
  public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
    http
      .authorizeExchange(exchanges -> exchanges
        .anyExchange().authenticated()
      )
      .oauth2ResourceServer(oauth2 -> oauth2.jwt());

    return http.build();
  }

  @Bean
  public ReactiveJwtDecoder jwtDecoder(@Value("${jwt.secret}") String secret) {
    return NimbusReactiveJwtDecoder.withSecretKey(Keys.hmacShaKeyFor(secret.getBytes())).build();
  }

  @Bean
  CorsConfigurationSource corsConfiguration() {
    CorsConfiguration corsConfig = new CorsConfiguration();
    corsConfig.applyPermitDefaultValues();
    corsConfig.addAllowedMethod(HttpMethod.PUT);
    corsConfig.addAllowedMethod(HttpMethod.DELETE);
    corsConfig.setAllowedOrigins(List.of("*"));

    UrlBasedCorsConfigurationSource source =
      new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", corsConfig);
    return source;
  }

}
