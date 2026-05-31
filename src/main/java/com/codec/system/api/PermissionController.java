package com.codec.system.api;

import codec.common.Response;
import com.codec.system.application.command.request.user.CreateRoleRequest;
import com.codec.system.application.command.response.user.ListPermissionResponse;
import com.codec.system.application.command.response.user.ListRolePermissionResponse;
import com.codec.system.application.service.PermissionService;
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
public class PermissionController {
  PermissionService permissionService;
  JwtUtil jwtUtil;

  @Operation(summary = "Danh sách quyền")
  @GetMapping("/permission/get-all")
  public Mono<Response<List<ListPermissionResponse>>> findAllPermission(
  ) {

    List<ListPermissionResponse> data = permissionService.getAllPermission().getData();
    return Mono.just(Response.of(data).success("Thành công", 200));
  }

  @Operation(summary = "Danh sách nhóm quyền select")
  @GetMapping("/role/select")
  public Mono<Response<List<ListRolePermissionResponse>>> roleSelect(
  ) {
    List<ListRolePermissionResponse> data = permissionService.selectRole().getData();
    return Mono.just(Response.of(data).success("Thành công", 200));
  }

  @Operation(summary = "Danh sách nhóm quyền")
  @GetMapping("/role/get-all")
  public Mono<Response<RestCodecSystemApplicationPage<ListRolePermissionResponse>>> findAllRole(
    @RequestParam(value = "roleName", required = false) String roleName,
    @ParameterObject Pageable pageable,
    @RequestHeader("Authorization") String authHeader
  ) {
    String userId = jwtUtil.checkPermission(authHeader, "role-v");
    if (userId.equals("Api không có quyền truy cập") || userId.equals("Token không hợp lệ")) {
      return Mono.just(Response.fail(userId, 403));
    }
    RestCodecSystemApplicationPage<ListRolePermissionResponse> data = permissionService.getAllRole(roleName, pageable);
    return Mono.just(Response.of(data).success("Thành công", 200));
  }


  @Operation(summary = "thêm mới nhóm quyền")
  @PostMapping("/role/create")
  public Mono<Response<Object>> createRole(
    @RequestBody CreateRoleRequest createRoleRequest,
    @RequestHeader("Authorization") String authHeader
  ) {
    String userId = jwtUtil.checkPermission(authHeader, "role-c");
    if (userId.equals("Api không có quyền truy cập") || userId.equals("Token không hợp lệ")) {
      return Mono.just(Response.fail(userId, 403));
    }
    permissionService.createRole(createRoleRequest, userId);
    return Mono.just(Response.ok().success("Thêm mới thành công", 201));
  }

  @Operation(summary = "cập nhật nhóm quyền")
  @PostMapping("/role/update/{roleId}")
  public Mono<Response<Object>> createRole(
    @PathVariable("roleId") String roleId,
    @RequestBody CreateRoleRequest createRoleRequest,
    @RequestHeader("Authorization") String authHeader
  ) {
    String userId = jwtUtil.checkPermission(authHeader, "role-u");
    if (userId.equals("Api không có quyền truy cập") || userId.equals("Token không hợp lệ")) {
      return Mono.just(Response.fail(userId, 403));
    }
    permissionService.updateRole(roleId, createRoleRequest, userId);
    return Mono.just(Response.ok().success("Cập nhật thành công", 201));
  }

  @Operation(summary = "Xóa nhóm quyền")
  @PostMapping("/role/delete/{roleId}")
  public Mono<Response<Object>> deleteRole(
    @PathVariable("roleId") String roleId,
    @RequestHeader("Authorization") String authHeader
  ) {
    String userId = jwtUtil.checkPermission(authHeader, "role-d");
    if (userId.equals("Api không có quyền truy cập") || userId.equals("Token không hợp lệ")) {
      return Mono.just(Response.fail(userId, 403));
    }
    permissionService.deleteRole(roleId, userId);
    return Mono.just(Response.ok().success("Xóa thành công", 201));
  }

}
