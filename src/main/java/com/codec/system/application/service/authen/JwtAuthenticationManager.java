package com.codec.system.application.service.authen;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Component
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

  @Autowired
  private JwtUtil jwtUtil;

  @Override
  public Mono<Authentication> authenticate(Authentication authentication) {
    String authToken = authentication.getCredentials().toString();
    if (jwtUtil.validateToken(authToken)) {
      String username = jwtUtil.getUsernameFromToken(authToken);
      return Mono.just(new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList()));
    }
    return Mono.empty();
  }
}

