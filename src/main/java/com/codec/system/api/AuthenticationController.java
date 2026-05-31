package com.codec.system.api;

import codec.common.Response;
import com.codec.system.application.command.request.user.AuthRequest;
import com.codec.system.application.command.request.user.RefreshTokenRequest;
import com.codec.system.application.command.request.user.ResetPasswordRequest;
import com.codec.system.application.command.response.user.AuthResponse;
import com.codec.system.application.command.response.user.ProfileResponse;
import com.codec.system.application.service.AuthService;
import com.codec.system.application.service.authen.JwtUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static com.codec.system.common.utils.EncryptorPassword.decrypt;

@RequestMapping("/api/v1")
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AuthenticationController {
  @Autowired
  private JwtUtil jwtUtil;

  private final AuthService authService;

  @PostMapping("/auth/login")
  public Mono<Response<AuthResponse>> login(@RequestBody AuthRequest request) throws Exception {
    return Mono.just(Response.of(authService.login(request).getData()).success("Đăng nhập thành công", 200));
  }

  @PostMapping("/auth/refresh")
  public Mono<Response<AuthResponse>> refresh(@RequestBody RefreshTokenRequest refreshTokenRequest) {
    return Mono.just(Response.of(authService.refresh(refreshTokenRequest).getData()).success("Thành công", 200));
  }

  @PostMapping("/auth/logout")
  public Mono<Response<Object>> logout() {
    // Với JWT, logout có thể được xử lý client-side hoặc lưu token blacklist
    return Mono.just(Response.ok().success("Thành công", 200));
  }

  @GetMapping("/profile")
  public Mono<Response<ProfileResponse>> getProfile(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      Response<ProfileResponse> res = new Response<>();
      res.setSuccess(false);
      res.setCode(401);
      res.setMessage("Thiếu token");
      return Mono.just(res);
    }

    String token = authHeader.substring(7);
    ProfileResponse profileResponse = authService.getProfileUser(token).getData();

    return Mono.just(Response.of(profileResponse).success("Thành công", 200));
  }

  @PostMapping("/reset-password")
  public Mono<Response<Object>> resetPassword(
    @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
    @RequestBody ResetPasswordRequest resetPasswordRequest) throws Exception {
    String userId = jwtUtil.getUserId(authHeader);
    authService.resetPassword(resetPasswordRequest, userId);
    return Mono.just(Response.ok().success("Thành công", 201));
  }

  @GetMapping("/auth/get-pass")
  public Mono<String> getPass(@RequestBody AuthRequest request) throws Exception {
    return Mono.just(decrypt(request.getPassword()));
  }
}
