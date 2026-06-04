package com.codec.system.application.service;

import codec.common.Response;
import com.codec.system.application.command.request.device.CreateDeviceRequest;
import com.codec.system.application.command.request.device.UpdateDeviceRequest;
import com.codec.system.application.command.response.device.DeviceResponse;
import com.codec.system.pagination.infrastructure.primary.RestCodecSystemApplicationPage;
import org.springframework.data.domain.Pageable;

public interface DeviceService {
  Response<RestCodecSystemApplicationPage<DeviceResponse>> getAllDevice(Pageable pageable);
  Response<DeviceResponse> getDeviceById(String id);
  void createDevice(CreateDeviceRequest request, String userId);
  void updateDevice(String id, UpdateDeviceRequest request, String userId);
  void deleteDevice(String id, String userId);
}
