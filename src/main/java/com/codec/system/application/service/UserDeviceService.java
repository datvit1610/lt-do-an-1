package com.codec.system.application.service;

import com.codec.system.application.command.request.notificationConfig.DeviceRegisterRequest;
import org.springframework.security.core.Authentication;

public interface UserDeviceService {
  void registerDevice(DeviceRegisterRequest request, String authHeader);
}
