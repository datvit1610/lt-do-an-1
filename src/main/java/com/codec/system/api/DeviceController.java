package com.codec.system.api;

import codec.common.Response;
import com.codec.system.application.command.request.device.CreateDeviceRequest;
import com.codec.system.application.command.request.device.UpdateDeviceRequest;
import com.codec.system.application.command.response.device.DeviceResponse;
import com.codec.system.application.service.DeviceService;
import com.codec.system.application.service.authen.JwtUtil;
import com.codec.system.pagination.infrastructure.primary.RestCodecSystemApplicationPage;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RequestMapping("/api/v1")
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class DeviceController {
  DeviceService deviceService;
  JwtUtil jwtUtil;

  @Operation(summary = "Danh sách thiết bị")
  @GetMapping("/device/get-all")
  public Mono<Response<RestCodecSystemApplicationPage<DeviceResponse>>> getAllDevice(
    @ParameterObject Pageable pageable
  ) {
    Response<RestCodecSystemApplicationPage<DeviceResponse>> data = deviceService.getAllDevice(pageable);
    return Mono.just(Response.of(data.getData()).success("Thành công", 200));
  }

  @Operation(summary = "Lấy thiết bị theo id")
  @GetMapping("/device/{id}")
  public Mono<Response<DeviceResponse>> getDeviceById(@PathVariable("id") String id) {
    Response<DeviceResponse> data = deviceService.getDeviceById(id);
    return Mono.just(Response.of(data.getData()).success("Thành công", 200));
  }

  @Operation(summary = "Thêm mới thiết bị")
  @PostMapping("/device/create")
  public Mono<Response<Object>> createDevice(
    @RequestBody CreateDeviceRequest request,
    @RequestHeader("Authorization") String authHeader
  ) {
    String userId = jwtUtil.checkPermission(authHeader, "device-c");
    if (userId.equals("Api không có quyền truy cập") || userId.equals("Token không hợp lệ")) {
      return Mono.just(Response.fail(userId, 403));
    }
    deviceService.createDevice(request, userId);
    return Mono.just(Response.ok().success("Thêm mới thành công", 201));
  }

  @Operation(summary = "Cập nhật thiết bị")
  @PostMapping("/device/update/{id}")
  public Mono<Response<Object>> updateDevice(
    @PathVariable("id") String id,
    @RequestBody UpdateDeviceRequest request,
    @RequestHeader("Authorization") String authHeader
  ) {
    String userId = jwtUtil.checkPermission(authHeader, "device-u");
    if (userId.equals("Api không có quyền truy cập") || userId.equals("Token không hợp lệ")) {
      return Mono.just(Response.fail(userId, 403));
    }
    deviceService.updateDevice(id, request, userId);
    return Mono.just(Response.ok().success("Cập nhật thành công", 201));
  }

  @Operation(summary = "Xóa thiết bị")
  @PostMapping("/device/delete/{id}")
  public Mono<Response<Object>> deleteDevice(
    @PathVariable("id") String id,
    @RequestHeader("Authorization") String authHeader
  ) {
    String userId = jwtUtil.checkPermission(authHeader, "device-d");
    if (userId.equals("Api không có quyền truy cập") || userId.equals("Token không hợp lệ")) {
      return Mono.just(Response.fail(userId, 403));
    }
    deviceService.deleteDevice(id, userId);
    return Mono.just(Response.ok().success("Xóa thành công", 201));
  }

}
