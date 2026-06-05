package com.codec.system.application.service.impl;

import codec.common.Response;
import com.codec.system.application.command.request.device.CreateDeviceRequest;
import com.codec.system.application.command.request.device.UpdateDeviceRequest;
import com.codec.system.application.command.response.device.DeviceResponse;
import com.codec.system.application.service.DeviceService;
import com.codec.system.domain.entity.DeviceEntity;
import com.codec.system.domain.repository.DeviceRepository;
import com.codec.system.pagination.domain.CodecSystemApplicationPage;
import com.codec.system.pagination.domain.CodecSystemApplicationPageable;
import com.codec.system.pagination.infrastructure.primary.RestCodecSystemApplicationPage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class DeviceServiceImpl implements DeviceService {
  DeviceRepository deviceRepository;

  @Override
  public Response<RestCodecSystemApplicationPage<DeviceResponse>> getAllDevice(
    Pageable pageable, String deviceCode, String name, String deviceType, Integer status
  ) {
    Page<Object[]> page = deviceRepository.findAllDevicesWithUserNames(
      pageable, deviceCode, name, deviceType, status
    );
    List<DeviceResponse> responses = page.stream().map(row -> {
      DeviceResponse r = new DeviceResponse();
      r.setId((String) row[0]);
      r.setName((String) row[1]);
      r.setDeviceCode((String) row[2]);
      r.setDeviceType((String) row[3]);
      r.setStatus((Integer) row[4]);
      r.setLocation((String) row[5]);
      r.setQuantity((Integer) row[6]);
      r.setDescription((String) row[7]);
      r.setCreatedDate((java.util.Date) row[8]);
      r.setCreatedBy((String) row[9]); // full_name từ users table
      return r;
    }).toList();

    long currentCount = page.getTotalElements();
    CodecSystemApplicationPageable codecPageable = new CodecSystemApplicationPageable(pageable.getPageNumber(), pageable.getPageSize());

    RestCodecSystemApplicationPage<DeviceResponse> rest = RestCodecSystemApplicationPage.from(
      CodecSystemApplicationPage.of(responses, codecPageable, currentCount), d -> d);

    return Response.of(rest).success("Thành công", 200);
  }

  @Override
  @Transactional
  public void createDevice(CreateDeviceRequest request, String userId) {
    try {
      DeviceEntity device = new DeviceEntity();
      device.setName(request.getName());
      device.setDeviceCode(request.getDeviceCode());
      device.setDeviceType(request.getDeviceType());
      device.setStatus(request.getStatus());
      device.setLocation(request.getLocation());
      device.setQuantity(request.getQuantity());
      device.setDescription(request.getDescription());
      device.setCreatedBy(userId);
      deviceRepository.save(device);
      Response.ok();
    } catch (Exception e) {
      throw new RuntimeException("Gặp lỗi: " + e.getMessage());
    }
  }

  @Override
  @Transactional
  public void updateDevice(String id, UpdateDeviceRequest request, String userId) {
    try {
      Optional<DeviceEntity> entity = deviceRepository.findById(id);
      if (entity.isEmpty()) {
        throw new RuntimeException("Thiết bị không tồn tại");
      }
      DeviceEntity d = entity.get();
      d.setName(request.getName());
      d.setDeviceCode(request.getDeviceCode());
      d.setDeviceType(request.getDeviceType());
      d.setStatus(request.getStatus());
      d.setLocation(request.getLocation());
      d.setQuantity(request.getQuantity());
      d.setDescription(request.getDescription());
      d.setModifiedDate(new Date());
      d.setModifiedBy(userId);
      deviceRepository.save(d);
      Response.ok();
    } catch (Exception e) {
      throw new RuntimeException("Gặp lỗi: " + e.getMessage());
    }
  }

  @Override
  @Transactional
  public void deleteDevice(String id, String userId) {
    try {
      Optional<DeviceEntity> entity = deviceRepository.findById(id);
      if (entity.isEmpty()) {
        throw new RuntimeException("Thiết bị không tồn tại");
      }
      DeviceEntity d = entity.get();
      d.setDeleted(true);
      d.setModifiedDate(new Date());
      d.setModifiedBy(userId);
      deviceRepository.save(d);
      Response.ok();
    } catch (Exception e) {
      throw new RuntimeException("Gặp lỗi: " + e.getMessage());
    }
  }
}
