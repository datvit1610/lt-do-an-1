package com.codec.system.application.service;

import codec.common.Response;
import com.codec.system.application.command.request.devicetype.CreateDeviceTypeRequest;
import com.codec.system.application.command.request.devicetype.UpdateDeviceTypeRequest;
import com.codec.system.application.command.response.devicetype.DeviceTypeOptionResponse;
import com.codec.system.application.command.response.devicetype.DeviceTypeResponse;
import com.codec.system.pagination.infrastructure.primary.RestCodecSystemApplicationPage;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DeviceTypeService {
  Response<RestCodecSystemApplicationPage<DeviceTypeResponse>> getAllDeviceType(Pageable pageable, String name);
  Response<List<DeviceTypeOptionResponse>> getDeviceTypeOptions(String name);
  void createDeviceType(CreateDeviceTypeRequest request, String userId);
  void updateDeviceType(String id, UpdateDeviceTypeRequest request, String userId);
  void deleteDeviceType(String id, String userId);
}
