package com.codec.system.application.service.impl;

import com.codec.system.application.command.request.notificationConfig.DeviceRegisterRequest;
import com.codec.system.application.service.UserDeviceService;
import com.codec.system.application.service.authen.JwtUtil;
import com.codec.system.domain.entity.UserDeviceEntity;
import com.codec.system.domain.repository.UserDeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserDeviceServiceImpl implements UserDeviceService {
  private final UserDeviceRepository userDeviceRepository;
  private final JwtUtil jwtUtil;

  @Override
  @Transactional
  public void registerDevice(DeviceRegisterRequest request, String authHeader) {
    String userId = jwtUtil.getUserId(authHeader);

    // Nếu đã có token theo platform thì update, chưa có thì tạo mới
    UserDeviceEntity device = userDeviceRepository
      .findByUserIdAndPlatform(userId, request.getPlatform())
      .orElse(new UserDeviceEntity());

    device.setUserId(userId);
    device.setFcmToken(request.getFcmToken());
    device.setPlatform(request.getPlatform());
    device.setIsActive(true);
    userDeviceRepository.save(device);
  }
}
