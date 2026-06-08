package com.codec.system.application.service;

import codec.common.Response;
import com.codec.system.application.command.request.device.CreateDeviceRequest;
import com.codec.system.application.command.request.device.UpdateDeviceRequest;
import com.codec.system.application.command.response.device.DeviceOptionResponse;
import com.codec.system.application.command.response.device.DeviceResponse;
import com.codec.system.pagination.infrastructure.primary.RestCodecSystemApplicationPage;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DeviceService {
  Response<RestCodecSystemApplicationPage<DeviceResponse>> getAllDevice(
    Pageable pageable, String deviceCode, String name, String deviceType, Integer status
  );
  Response<List<DeviceOptionResponse>> getDeviceOptions(String name);
  void createDevice(CreateDeviceRequest request, String userId);
  void updateDevice(String id, UpdateDeviceRequest request, String userId);
  void deleteDevice(String id, String userId);
}
