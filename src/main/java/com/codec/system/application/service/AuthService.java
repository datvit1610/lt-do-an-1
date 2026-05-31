package com.codec.system.application.service;

import codec.common.Response;
import com.codec.system.application.command.request.user.AuthRequest;
import com.codec.system.application.command.request.user.RefreshTokenRequest;
import com.codec.system.application.command.request.user.ResetPasswordRequest;
import com.codec.system.application.command.response.user.AuthResponse;
import com.codec.system.application.command.response.user.ProfileResponse;

public interface AuthService {
  Response<AuthResponse> login(AuthRequest loginRequest) throws Exception;
  Response<AuthResponse> refresh(RefreshTokenRequest tokenRequest);
  Response<ProfileResponse> getProfileUser(String token);
  void resetPassword(ResetPasswordRequest resetPasswordRequest, String userId) throws Exception;
}
