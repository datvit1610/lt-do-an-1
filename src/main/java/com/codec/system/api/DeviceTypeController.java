package com.codec.system.api;

import codec.common.Response;
import com.codec.system.application.command.request.devicetype.CreateDeviceTypeRequest;
import com.codec.system.application.command.request.devicetype.UpdateDeviceTypeRequest;
import com.codec.system.application.command.response.devicetype.DeviceTypeOptionResponse;
import com.codec.system.application.command.response.devicetype.DeviceTypeResponse;
import com.codec.system.application.service.DeviceTypeService;
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
public class DeviceTypeController {
  DeviceTypeService deviceTypeService;
  JwtUtil jwtUtil;

  @Operation(summary = "Danh sách loại thiết bị")
  @GetMapping("/device-type/get-all")
  public Mono<Response<RestCodecSystemApplicationPage<DeviceTypeResponse>>> getAllDeviceType(
    @ParameterObject Pageable pageable,
    @RequestParam(required = false) String name,
    @RequestHeader("Authorization") String authHeader
  ) {
    String userId = jwtUtil.checkPermission(authHeader, "device-v");
    if (userId.equals("Api không có quyền truy cập") || userId.equals("Token không hợp lệ")) {
      return Mono.just(Response.fail(userId, 403));
    }
    Response<RestCodecSystemApplicationPage<DeviceTypeResponse>> data = deviceTypeService.getAllDeviceType(
      pageable, name
    );
    return Mono.just(Response.of(data.getData()).success("Thành công", 200));
  }

  @Operation(summary = "Danh sách loại thiết bị rút gọn (id, name) để chọn lúc tạo thiết bị")
  @GetMapping("/device-type/select")
  public Mono<Response<List<DeviceTypeOptionResponse>>> getDeviceTypeOptions(
    @RequestParam(required = false) String name,
    @RequestHeader("Authorization") String authHeader
  ) {
    String userId = jwtUtil.checkPermission(authHeader, "device-v");
    if (userId.equals("Api không có quyền truy cập") || userId.equals("Token không hợp lệ")) {
      return Mono.just(Response.fail(userId, 403));
    }
    Response<List<DeviceTypeOptionResponse>> data = deviceTypeService.getDeviceTypeOptions(name);
    return Mono.just(Response.of(data.getData()).success("Thành công", 200));
  }

  @Operation(summary = "Thêm mới loại thiết bị")
  @PostMapping("/device-type/create")
  public Mono<Response<Object>> createDeviceType(
    @RequestBody CreateDeviceTypeRequest request,
    @RequestHeader("Authorization") String authHeader
  ) {
    String userId = jwtUtil.checkPermission(authHeader, "device-c");
    if (userId.equals("Api không có quyền truy cập") || userId.equals("Token không hợp lệ")) {
      return Mono.just(Response.fail(userId, 403));
    }
    deviceTypeService.createDeviceType(request, userId);
    return Mono.just(Response.ok().success("Thêm mới thành công", 201));
  }

  @Operation(summary = "Cập nhật loại thiết bị")
  @PostMapping("/device-type/update/{id}")
  public Mono<Response<Object>> updateDeviceType(
    @PathVariable("id") String id,
    @RequestBody UpdateDeviceTypeRequest request,
    @RequestHeader("Authorization") String authHeader
  ) {
    String userId = jwtUtil.checkPermission(authHeader, "device-u");
    if (userId.equals("Api không có quyền truy cập") || userId.equals("Token không hợp lệ")) {
      return Mono.just(Response.fail(userId, 403));
    }
    deviceTypeService.updateDeviceType(id, request, userId);
    return Mono.just(Response.ok().success("Cập nhật thành công", 201));
  }

  @Operation(summary = "Xóa loại thiết bị")
  @PostMapping("/device-type/delete/{id}")
  public Mono<Response<Object>> deleteDeviceType(
    @PathVariable("id") String id,
    @RequestHeader("Authorization") String authHeader
  ) {
    String userId = jwtUtil.checkPermission(authHeader, "device-d");
    if (userId.equals("Api không có quyền truy cập") || userId.equals("Token không hợp lệ")) {
      return Mono.just(Response.fail(userId, 403));
    }
    deviceTypeService.deleteDeviceType(id, userId);
    return Mono.just(Response.ok().success("Xóa thành công", 201));
  }

}
