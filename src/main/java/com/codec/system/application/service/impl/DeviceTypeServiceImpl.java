package com.codec.system.application.service.impl;

import codec.common.Response;
import com.codec.system.application.command.request.devicetype.CreateDeviceTypeRequest;
import com.codec.system.application.command.request.devicetype.UpdateDeviceTypeRequest;
import com.codec.system.application.command.response.devicetype.DeviceTypeOptionResponse;
import com.codec.system.application.command.response.devicetype.DeviceTypeResponse;
import com.codec.system.application.service.DeviceTypeService;
import com.codec.system.domain.entity.DeviceTypeEntity;
import com.codec.system.domain.repository.DeviceTypeRepository;
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
public class DeviceTypeServiceImpl implements DeviceTypeService {
  DeviceTypeRepository deviceTypeRepository;

  @Override
  public Response<RestCodecSystemApplicationPage<DeviceTypeResponse>> getAllDeviceType(Pageable pageable, String name) {
    Page<Object[]> page = deviceTypeRepository.findAllDeviceTypes(pageable, name);
    List<DeviceTypeResponse> responses = page.stream().map(row -> {
      DeviceTypeResponse r = new DeviceTypeResponse();
      r.setId((String) row[0]);
      r.setDeviceType((String) row[1]);
      return r;
    }).toList();

    long currentCount = page.getTotalElements();
    CodecSystemApplicationPageable codecPageable = new CodecSystemApplicationPageable(pageable.getPageNumber(), pageable.getPageSize());

    RestCodecSystemApplicationPage<DeviceTypeResponse> rest = RestCodecSystemApplicationPage.from(
      CodecSystemApplicationPage.of(responses, codecPageable, currentCount), d -> d);

    return Response.of(rest).success("Thành công", 200);
  }

  @Override
  public Response<List<DeviceTypeOptionResponse>> getDeviceTypeOptions(String name) {
    List<DeviceTypeOptionResponse> options = deviceTypeRepository.findDeviceTypeOptions(name)
      .stream().map(DeviceTypeOptionResponse::new).toList();
    return Response.of(options).success("Thành công", 200);
  }

  @Override
  @Transactional
  public void createDeviceType(CreateDeviceTypeRequest request, String userId) {
    try {
      if (request.getDeviceType() == null || request.getDeviceType().isBlank()) {
        throw new RuntimeException("Tên loại thiết bị không được để trống");
      }
      if (deviceTypeRepository.existsByDeviceTypeAndDeletedIsFalse(request.getDeviceType())) {
        throw new RuntimeException("Loại thiết bị đã tồn tại");
      }
      DeviceTypeEntity deviceType = new DeviceTypeEntity();
      deviceType.setDeviceType(request.getDeviceType());
      deviceType.setCreatedBy(userId);
      deviceTypeRepository.save(deviceType);
    } catch (Exception e) {
      throw new RuntimeException("Gặp lỗi: " + e.getMessage());
    }
  }

  @Override
  @Transactional
  public void updateDeviceType(String id, UpdateDeviceTypeRequest request, String userId) {
    try {
      Optional<DeviceTypeEntity> entity = deviceTypeRepository.findById(id);
      if (entity.isEmpty() || Boolean.TRUE.equals(entity.get().getDeleted())) {
        throw new RuntimeException("Loại thiết bị không tồn tại");
      }
      DeviceTypeEntity d = entity.get();
      d.setDeviceType(request.getDeviceType());
      d.setModifiedDate(new Date());
      d.setModifiedBy(userId);
      deviceTypeRepository.save(d);
    } catch (Exception e) {
      throw new RuntimeException("Gặp lỗi: " + e.getMessage());
    }
  }

  @Override
  @Transactional
  public void deleteDeviceType(String id, String userId) {
    try {
      Optional<DeviceTypeEntity> entity = deviceTypeRepository.findById(id);
      if (entity.isEmpty()) {
        throw new RuntimeException("Loại thiết bị không tồn tại");
      }
      DeviceTypeEntity d = entity.get();
      d.setDeleted(true);
      d.setModifiedDate(new Date());
      d.setModifiedBy(userId);
      deviceTypeRepository.save(d);
    } catch (Exception e) {
      throw new RuntimeException("Gặp lỗi: " + e.getMessage());
    }
  }
}
