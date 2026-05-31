//package com.codec.system.security;
//
//
//import codec.common.DateUtils;
//import com.codec.security.security.AuthoritiesConstants;
//import com.codec.security.security.SecurityUtils;
//import com.codec.security.security.execption.CustomAccessDeniedHandler;
//import com.codec.security.security.execption.ServerAuthenticationEntryPoint;
//import com.codec.security.security.oauth2.AudienceValidator;
//import com.codec.security.security.oauth2.JwtGrantedAuthorityConverter;
//import com.github.benmanes.caffeine.cache.Cache;
//import com.github.benmanes.caffeine.cache.Caffeine;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.ParameterizedTypeReference;
//import org.springframework.core.convert.converter.Converter;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.authentication.AbstractAuthenticationToken;
//import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
//import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
//import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
//import org.springframework.security.config.web.server.ServerHttpSecurity;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.oauth2.client.oidc.userinfo.OidcReactiveOAuth2UserService;
//import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
//import org.springframework.security.oauth2.client.registration.ClientRegistration;
//import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
//import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService;
//import org.springframework.security.oauth2.client.web.server.DefaultServerOAuth2AuthorizationRequestResolver;
//import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizationRequestResolver;
//import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
//import org.springframework.security.oauth2.core.OAuth2TokenValidator;
//import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
//import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
//import org.springframework.security.oauth2.core.oidc.user.OidcUser;
//import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
//import org.springframework.security.oauth2.jwt.*;
//import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
//import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
//import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
//import org.springframework.security.web.server.SecurityWebFilterChain;
//import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;
//import org.springframework.security.web.server.csrf.ServerCsrfTokenRequestAttributeHandler;
//import org.springframework.security.web.server.header.ReferrerPolicyServerHttpHeadersWriter;
//import org.springframework.security.web.server.header.XFrameOptionsServerHttpHeadersWriter.Mode;
//import org.springframework.security.web.server.util.matcher.NegatedServerWebExchangeMatcher;
//import org.springframework.security.web.server.util.matcher.OrServerWebExchangeMatcher;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.reactive.CorsConfigurationSource;
//import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.core.publisher.Mono;
//import reactor.netty.http.client.HttpClient;
//import reactor.netty.http.client.HttpClientRequest;
//import reactor.netty.resources.ConnectionProvider;
//import tech.jhipster.config.JHipsterProperties;
//import tech.jhipster.web.filter.reactive.CookieCsrfFilter;
//
//import java.time.Duration;
//import java.util.*;
//import java.util.function.Consumer;
//
//import static org.springframework.security.config.Customizer.withDefaults;
//import static org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers.pathMatchers;
//
//@Configuration
//@EnableWebFluxSecurity
//@EnableReactiveMethodSecurity(useAuthorizationManager = true)
//public class SecurityConfiguration {
//
//  private static final Logger log = LoggerFactory.getLogger(SecurityConfiguration.class);
//
//  private final JHipsterProperties jHipsterProperties;
//  @Autowired
//  private CustomAccessDeniedHandler handler;
//
//  @Autowired
//  private ServerAuthenticationEntryPoint entryPoint;
//
//  @Value("${spring.security.oauth2.client.provider.oidc.issuer-uri}")
//  private String issuerUri;
//  private final ReactiveClientRegistrationRepository clientRegistrationRepository;
//
//  // See https://github.com/jhipster/generator-jhipster/issues/18868
//  // We don't use a distributed cache or the user selected cache implementation here on purpose
//  private final Cache<String, Mono<Jwt>> users = Caffeine
//    .newBuilder()
//    .maximumSize(10_000)
//    .expireAfterWrite(Duration.ofMinutes(1))
//    .recordStats()
//    .build();
//
//  public SecurityConfiguration(JHipsterProperties jHipsterProperties,
//                               ReactiveClientRegistrationRepository clientRegistrationRepository) {
//    this.jHipsterProperties = jHipsterProperties;
//    this.clientRegistrationRepository = clientRegistrationRepository;
//  }
//
//  @Bean
//  public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
//
//    http
//      .csrf().disable().exceptionHandling()
//      .accessDeniedHandler(handler)
//      .authenticationEntryPoint(entryPoint).and()
//      .securityMatcher(
//        new NegatedServerWebExchangeMatcher(
//          new OrServerWebExchangeMatcher(pathMatchers("/app/**", "/i18n/**", "/content/**", "/swagger-ui/**"))
//        )
//      )
//      .csrf(csrf ->
//        csrf
//          .csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse())
//          // See https://stackoverflow.com/q/74447118/65681
//          .csrfTokenRequestHandler(new ServerCsrfTokenRequestAttributeHandler())
//      )
//      // See https://github.com/spring-projects/spring-security/issues/5766
//      .addFilterAt(new CookieCsrfFilter(), SecurityWebFiltersOrder.REACTOR_CONTEXT)
//      .headers(headers ->
//        headers
//          .contentSecurityPolicy(csp -> csp.policyDirectives(jHipsterProperties.getSecurity().getContentSecurityPolicy()))
//          .frameOptions(frameOptions -> frameOptions.mode(Mode.DENY))
//          .referrerPolicy(referrer ->
//            referrer.policy(ReferrerPolicyServerHttpHeadersWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
//          )
//          .permissionsPolicy(permissions ->
//            permissions.policy(
//              "camera=(), fullscreen=(self), geolocation=(), gyroscope=(), magnetometer=(), microphone=(), midi=(), payment=(), sync-xhr=()"
//            )
//          )
//      )
//      .authorizeExchange(authz ->
//        // prettier-ignore
//        authz
//          .pathMatchers("/api/authenticate").permitAll()
//          .pathMatchers("/api/auth-info").permitAll()
//          .pathMatchers("/check/**").permitAll()
//          .pathMatchers("/actuator/**").permitAll()
//          .pathMatchers("/api/admin/**").hasAuthority(AuthoritiesConstants.ADMIN)
//          .pathMatchers("/api/v1/**").authenticated()
//          .pathMatchers("/v3/api-docs/**").hasAuthority(AuthoritiesConstants.ADMIN)
//          .pathMatchers("/management/health").permitAll()
//          .pathMatchers("/management/health/**").permitAll()
//          .pathMatchers("/management/info").permitAll()
//          .pathMatchers("/management/prometheus").permitAll()
//          .pathMatchers("/service/v1/**").permitAll()
//          .pathMatchers("/management/**").hasAuthority(AuthoritiesConstants.ADMIN)
//          .and().csrf().disable()
//      )
//      .oauth2Login(oauth2 -> oauth2.authorizationRequestResolver(authorizationRequestResolver(this.clientRegistrationRepository)))
//      .oauth2Client(withDefaults())
//      .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));
//
//    return http.build();
//  }
//  @Bean
//  CorsConfigurationSource corsConfiguration() {
//    CorsConfiguration corsConfig = new CorsConfiguration();
//    corsConfig.applyPermitDefaultValues();
//    corsConfig.addAllowedMethod(HttpMethod.PUT);
//    corsConfig.addAllowedMethod(HttpMethod.DELETE);
//    corsConfig.setAllowedOrigins(List.of("*"));
//
//    UrlBasedCorsConfigurationSource source =
//      new UrlBasedCorsConfigurationSource();
//    source.registerCorsConfiguration("/**", corsConfig);
//    return source;
//  }
//
//  private ServerOAuth2AuthorizationRequestResolver authorizationRequestResolver(
//    ReactiveClientRegistrationRepository clientRegistrationRepository
//  ) {
//    DefaultServerOAuth2AuthorizationRequestResolver authorizationRequestResolver = new DefaultServerOAuth2AuthorizationRequestResolver(
//      clientRegistrationRepository
//    );
//    if (this.issuerUri.contains("auth0.com")) {
//      authorizationRequestResolver.setAuthorizationRequestCustomizer(authorizationRequestCustomizer());
//    }
//    return authorizationRequestResolver;
//  }
//
//  private Consumer<OAuth2AuthorizationRequest.Builder> authorizationRequestCustomizer() {
//    return customizer ->
//      customizer.authorizationRequestUri(uriBuilder ->
//        uriBuilder.queryParam("audience", jHipsterProperties.getSecurity().getOauth2().getAudience()).build()
//      );
//  }
//
//  Converter<Jwt, Mono<AbstractAuthenticationToken>> jwtAuthenticationConverter() {
//    JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
//    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new JwtGrantedAuthorityConverter());
//    return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
//  }
//
//  /**
//   * Map authorities from "groups" or "roles" claim in ID Token.
//   *
//   * @return a {@link ReactiveOAuth2UserService} that has the groups from the IdP.
//   */
//  @Bean
//  public ReactiveOAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
//    final OidcReactiveOAuth2UserService delegate = new OidcReactiveOAuth2UserService();
//
//    return userRequest -> {
//      // Delegate to the default implementation for loading a user
//      return delegate
//        .loadUser(userRequest)
//        .map(user -> {
//          Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
//
//          user
//            .getAuthorities()
//            .forEach(authority -> {
//              if (authority instanceof OidcUserAuthority) {
//                OidcUserAuthority oidcUserAuthority = (OidcUserAuthority) authority;
//                mappedAuthorities.addAll(
//                  SecurityUtils.extractAuthorityFromClaims(oidcUserAuthority.getUserInfo().getClaims())
//                );
//              }
//            });
//
//          return new DefaultOidcUser(mappedAuthorities, user.getIdToken(), user.getUserInfo());
//        });
//    };
//  }
//
//  @Bean
//  ReactiveJwtDecoder jwtDecoder(ReactiveClientRegistrationRepository registrations) {
//    Mono<ClientRegistration> clientRegistration = registrations.findByRegistrationId("oidc");
//
//    return clientRegistration
//      .map(oidc ->
//        createJwtDecoder(
//          oidc.getProviderDetails().getIssuerUri(),
//          oidc.getProviderDetails().getJwkSetUri(),
//          oidc.getProviderDetails().getUserInfoEndpoint().getUri()
//        )
//      )
//      .block();
//  }
//
//  private ReactiveJwtDecoder createJwtDecoder(String issuerUri, String jwkSetUri, String userInfoUri) {
//    NimbusReactiveJwtDecoder jwtDecoder = new NimbusReactiveJwtDecoder(jwkSetUri);
//    OAuth2TokenValidator<Jwt> audienceValidator = new AudienceValidator(jHipsterProperties.getSecurity().getOauth2().getAudience());
//    OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuerUri);
//    OAuth2TokenValidator<Jwt> withAudience = new DelegatingOAuth2TokenValidator<>(withIssuer, audienceValidator);
//    jwtDecoder.setJwtValidator(withAudience);
//    return new ReactiveJwtDecoder() {
//      @Override
//      public Mono<Jwt> decode(String token) throws JwtException {
//        return jwtDecoder.decode(token).flatMap(jwt -> enrich(token, jwt));
//      }
//      private Mono<Jwt> enrich(String token, Jwt jwt) {
//        // Only look up user information if identity claims are missing
//        if (jwt.hasClaim("given_name") && jwt.hasClaim("family_name")) {
//          return Mono.just(jwt);
//        }
//        // Retrieve user info from OAuth provider if not already loaded
//        return Mono.just(jwt);
////        return users.get(
////          jwt.getSubject(),
////          s -> {
////            WebClient webClient = WebClient.create();
//////            log.info(token);
////            return webClient
////              .get()
////              .uri(userInfoUri)
//////              .httpRequest(httpRequest -> {
//////                HttpClientRequest reactorRequest = httpRequest.getNativeRequest();
//////                reactorRequest.responseTimeout(Duration.ofSeconds(5));
//////              })
////              .headers(headers -> headers.setBearerAuth(token))
////              .retrieve()
////              .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
////              })
////              .map(userInfo ->
////                  Jwt
////                    .withTokenValue(jwt.getTokenValue())
////                    .subject(jwt.getSubject())
////                    .audience(jwt.getAudience())
////                    .headers(headers -> headers.putAll(jwt.getHeaders()))
////                    .claims(claims -> {
//////                      log.info(token);
//////                    String username = userInfo.get("preferred_username").toString();
//////                    // special handling for Auth0
//////                    if (userInfo.get("sub").toString().contains("|") && username.contains("@")) {
//////                      userInfo.put("email", username);
//////                    }
//////                    // Allow full name in a name claim - happens with Auth0
//////                    if (userInfo.get("name") != null) {
//////                      String[] name = userInfo.get("name").toString().split("\\s+");
//////                      if (name.length > 0) {
//////                        userInfo.put("given_name", name[0]);
//////                        userInfo.put("family_name", String.join(" ", Arrays.copyOfRange(name, 1, name.length)));
//////                      }
//////                    }
////                      claims.putAll(userInfo);
////                    })
////                    .claims(claims -> claims.putAll(jwt.getClaims()))
////                    .build()
////              );
////          }
////        );
//      }
//    };
//  }
//
//  public static List<String> getCurrentMerchantId(Authentication authentication) {
//    List<String> map = new ArrayList<>();
//    if (authentication instanceof JwtAuthenticationToken) {
//      Object principal = authentication.getPrincipal();
//      List<Object> claint = (List<Object>) ((Jwt) principal).getClaims().get("data");
//      for (Object domain : claint) {
//        map.add(((LinkedHashMap) domain).get("guid").toString());
//      }
//      return map;
////      Object domain = ((LinkedHashMap) claint).get("domainDtos");
////      Object merchant = ((LinkedHashMap) ((ArrayList) domain).get(0)).get("merchant");
////      return ((LinkedHashMap) merchant).get("guuId").toString();
//    }
//    return map;
//  }
//
//
//}
