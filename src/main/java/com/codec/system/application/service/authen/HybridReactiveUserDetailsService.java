package com.codec.system.application.service.authen;


import com.codec.system.domain.repository.UserRepository;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class HybridReactiveUserDetailsService implements ReactiveUserDetailsService {

  private final UserRepository userRepository;

  public HybridReactiveUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public Mono<UserDetails> findByUsername(String username) {
    return Mono.fromCallable(() -> userRepository.findByUsernameAndDeletedIsFalse(username))
      .subscribeOn(Schedulers.boundedElastic())
      .flatMap(optional -> optional.map(user -> Mono.just(User.withUsername(user.getUsername())
        .password(user.getPassword())
        .roles("USER")
        .build())).orElse(Mono.empty()));
  }
}

