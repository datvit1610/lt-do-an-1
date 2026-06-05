package com.codec.system.api;

import codec.common.Response;
import com.codec.system.application.command.request.classperiod.CreateClassPeriodRequest;
import com.codec.system.application.command.request.classperiod.UpdateClassPeriodRequest;
import com.codec.system.application.command.response.classperiod.ClassPeriodResponse;
import com.codec.system.application.service.ClassPeriodService;
import com.codec.system.application.service.authen.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RequestMapping("/api/v1")
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ClassPeriodController {
  ClassPeriodService classPeriodService;
  JwtUtil jwtUtil;

  @Operation(summary = "Danh sách tiết học (masterdata ca học)")
  @GetMapping("/class-period/get-all")
  public Mono<Response<List<ClassPeriodResponse>>> getAllClassPeriod() {
    Response<List<ClassPeriodResponse>> data = classPeriodService.getAllClassPeriod();
    return Mono.just(Response.of(data.getData()).success("Thành công", 200));
  }

  @Operation(summary = "Lấy tiết học theo id")
  @GetMapping("/class-period/{id}")
  public Mono<Response<ClassPeriodResponse>> getClassPeriodById(@PathVariable("id") String id) {
    Response<ClassPeriodResponse> data = classPeriodService.getClassPeriodById(id);
    return Mono.just(Response.of(data.getData()).success("Thành công", 200));
  }

  @Operation(summary = "Thêm mới tiết học")
  @PostMapping("/class-period/create")
  public Mono<Response<Object>> createClassPeriod(
    @RequestBody CreateClassPeriodRequest request,
    @RequestHeader("Authorization") String authHeader
  ) {
    String userId = jwtUtil.checkPermission(authHeader, "class-period-c");
    if (userId.equals("Api không có quyền truy cập") || userId.equals("Token không hợp lệ")) {
      return Mono.just(Response.fail(userId, 403));
    }
    classPeriodService.createClassPeriod(request, userId);
    return Mono.just(Response.ok().success("Thêm mới thành công", 201));
  }

  @Operation(summary = "Cập nhật tiết học")
  @PostMapping("/class-period/update/{id}")
  public Mono<Response<Object>> updateClassPeriod(
    @PathVariable("id") String id,
    @RequestBody UpdateClassPeriodRequest request,
    @RequestHeader("Authorization") String authHeader
  ) {
    String userId = jwtUtil.checkPermission(authHeader, "class-period-u");
    if (userId.equals("Api không có quyền truy cập") || userId.equals("Token không hợp lệ")) {
      return Mono.just(Response.fail(userId, 403));
    }
    classPeriodService.updateClassPeriod(id, request, userId);
    return Mono.just(Response.ok().success("Cập nhật thành công", 201));
  }

  @Operation(summary = "Xóa tiết học")
  @PostMapping("/class-period/delete/{id}")
  public Mono<Response<Object>> deleteClassPeriod(
    @PathVariable("id") String id,
    @RequestHeader("Authorization") String authHeader
  ) {
    String userId = jwtUtil.checkPermission(authHeader, "class-period-d");
    if (userId.equals("Api không có quyền truy cập") || userId.equals("Token không hợp lệ")) {
      return Mono.just(Response.fail(userId, 403));
    }
    classPeriodService.deleteClassPeriod(id, userId);
    return Mono.just(Response.ok().success("Xóa thành công", 201));
  }
}
