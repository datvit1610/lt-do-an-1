package com.codec.system.application.command.request.user;

import lombok.Data;

@Data
public class ResetPasswordRequest {
  String oldPassword;
  String newPassword;
}
