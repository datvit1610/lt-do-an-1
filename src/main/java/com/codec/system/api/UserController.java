package com.codec.system.api;

import codec.common.Response;
import com.codec.system.application.command.request.user.CreateUserRequest;
import com.codec.system.application.command.request.user.UpdateUserRequest;
import com.codec.system.application.command.response.user.ListUserResponse;
import com.codec.system.application.service.UserService;
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

@RequestMapping("/api/v1")
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class UserController {
  UserService userService;
  JwtUtil jwtUtil;


  @Operation(summary = "Danh sách tài khoản")
  @GetMapping("/user/get-all")
  public Mono<Response<RestCodecSystemApplicationPage<ListUserResponse>>> findAllUser(
    @RequestParam(value = "userName", required = false) String userName,
    @RequestParam(value = "status", required = false) Integer status,
    @RequestParam(value = "phone", required = false) String phone,
    @RequestParam(value = "email", required = false) String email,
    @RequestHeader("Authorization") String authHeader,
    @ParameterObject Pageable pageable
  ) {
    String userId = jwtUtil.checkPermission(authHeader, "acc-v");
    if (userId.equals("Api không có quyền truy cập") || userId.equals("Token không hợp lệ")) {
      return Mono.just(Response.fail(userId, 403));
    }
    RestCodecSystemApplicationPage<ListUserResponse> data = userService.getAllUser(userName, status, phone, email, pageable);
    return Mono.just(Response.of(data).success("Thành công", 200));
  }

  @Operation(summary = "thêm mới user")
  @PostMapping("/user/create")
  public Mono<Response<Object>> createUser(
    @RequestBody CreateUserRequest createUserRequest,
    @RequestHeader("Authorization") String authHeader
  ) {
    String userId = jwtUtil.checkPermission(authHeader, "acc-c");
    if (userId.equals("Api không có quyền truy cập") || userId.equals("Token không hợp lệ")) {
      return Mono.just(Response.fail(userId, 403));
    }
    userService.createUser(createUserRequest, userId);
    return Mono.just(Response.ok().success("Thêm mới thành công", 201));
  }

  @Operation(summary = "cập nhật user")
  @PostMapping("/user/update/{userId}")
  public Mono<Response<Object>> createRole(
    @PathVariable("userId") String userId,
    @RequestBody UpdateUserRequest updateUserRequest,
    @RequestHeader("Authorization") String authHeader
  ) {
    String user = jwtUtil.checkPermission(authHeader, "acc-u");
    if (user.equals("Api không có quyền truy cập") || user.equals("Token không hợp lệ")) {
      return Mono.just(Response.fail(user, 403));
    }
    userService.updateUser(userId, updateUserRequest, user);
    return Mono.just(Response.ok().success("Cập nhật thành công", 201));
  }

  @Operation(summary = "Xóa user")
  @PostMapping("/user/delete/{userId}")
  public Mono<Response<Object>> deleteRole(
    @PathVariable("userId") String userId,
    @RequestHeader("Authorization") String authHeader
  ) {
    String user = jwtUtil.checkPermission(authHeader, "acc-d");
    if (user.equals("Api không có quyền truy cập") || user.equals("Token không hợp lệ")) {
      return Mono.just(Response.fail(user, 403));
    }
    userService.deleteUser(userId, user);
    return Mono.just(Response.ok().success("Xóa thành công", 201));
  }
}
