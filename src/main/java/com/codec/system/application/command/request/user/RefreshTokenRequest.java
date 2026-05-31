package com.codec.system.application.command.request.user;


import lombok.Data;

@Data
public class RefreshTokenRequest {
  private String refreshToken;
}
