package com.codec.system.api;

import codec.common.Response;
import com.codec.system.application.command.request.notificationConfig.DeviceRegisterRequest;
import com.codec.system.application.command.request.notificationConfig.NotificationConfigRequest;
import com.codec.system.application.command.request.notificationConfig.NotificationReadRequest;
import com.codec.system.application.command.request.notificationConfig.TestNotificationRequest;
import com.codec.system.application.command.response.notificationConfig.NotificationConfigResponse;
import com.codec.system.application.command.response.notificationConfig.NotificationLogResponse;
import com.codec.system.application.command.response.notificationConfig.NotificationUserResponse;
import com.codec.system.application.service.NotificationConfigService;
import com.codec.system.application.service.NotificationUserService;
import com.codec.system.application.service.UserDeviceService;
import com.codec.system.application.service.authen.JwtUtil;
import com.codec.system.application.service.impl.NotificationSendService;
import com.codec.system.pagination.infrastructure.primary.RestCodecSystemApplicationPage;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Comment;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@RequestMapping("/api/v1")
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class NotificationConfigController {
  NotificationConfigService notificationConfigService;
  UserDeviceService userDeviceService;
  NotificationSendService notificationSendService;
  JwtUtil jwtUtil;
  NotificationUserService notificationUserService;

  @Comment("Lấy cấu hình thông báo")
  @GetMapping("/notification-config/get")
  public Mono<Response<NotificationConfigResponse>> getConfig(
    @RequestHeader("Authorization") String authHeader
  ) {
    String userId = jwtUtil.checkPermission(authHeader, "notify-v");
    if (userId.equals("Api không có quyền truy cập") || userId.equals("Token không hợp lệ")) {
      return Mono.just(Response.fail(userId, 403));
    }
    NotificationConfigResponse response =  notificationConfigService.getConfig().getData();
    return Mono.just(Response.of(response).success("Thành công", 200));
  }

  @Comment("Lưu cấu hình thông báo")
  @PostMapping("/notification-config/set")
  public Mono<Response<Object>> saveConfig(
    @RequestBody @Valid NotificationConfigRequest request,
    @RequestHeader("Authorization") String authHeader
    ) {
    String userId = jwtUtil.checkPermission(authHeader, "notify-c");
    if (userId.equals("Api không có quyền truy cập") || userId.equals("Token không hợp lệ")) {
      return Mono.just(Response.fail(userId, 403));
    }
    notificationConfigService.saveConfig(request, userId);
    return Mono.just(Response.ok().success("Thành công", 201));
  }


  @PostMapping("/device/register")
  public Response<Void> registerDevice(
    @RequestBody DeviceRegisterRequest request,
    @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
    userDeviceService.registerDevice(request, authHeader);
    return Response.<Void>of(null).success("Đăng ký thiết bị thành công", 200);
  }

  @Comment("Lấy lịch sử thông báo ( bỏ chưa dùng )")
  @GetMapping("/notification-log/search")
  public Response<RestCodecSystemApplicationPage<NotificationLogResponse>> getNotificationLogs(
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
    @RequestHeader("Authorization") String authHeader,
    @ParameterObject Pageable pageable) {
    String userId = jwtUtil.checkPermission(authHeader, "notify-v");
    if (userId.equals("Api không có quyền truy cập") || userId.equals("Token không hợp lệ")) {
      return Response.fail(userId, 403);
    }
    RestCodecSystemApplicationPage<NotificationLogResponse> logs = notificationConfigService.search(from, to, pageable);
    return Response.of(logs).success("Lấy lịch sử thông báo thành công", 200);
  }

  @PostMapping("/notification/test")
  public Response<String> testNotification(@RequestBody TestNotificationRequest request) {
    return notificationSendService.testSend(request.getFcmToken(), request.getTitle(), request.getBody());
  }

  @Comment("Lấy thông báo của user đăng nhập")
  @GetMapping("/notification/get-me")
  public Response<RestCodecSystemApplicationPage<NotificationUserResponse>> getMyNotifications(
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toTo,
    @ParameterObject Pageable pageable,
    @RequestHeader("Authorization") String authHeader) {
    String userId = jwtUtil.getUserId(authHeader);
    return Response.of(notificationUserService.getMyNotifications(userId, fromDate, toTo, pageable));
  }

  @Comment("Đánh dấu đã đọc thông báo")
  @PostMapping("/notification/read")
  public Response<Void> markAsRead(
    @RequestBody NotificationReadRequest request,
    @RequestHeader("Authorization") String authHeader) {

    String userId = jwtUtil.getUserId(authHeader);
    return notificationUserService.markAsRead(request, userId);
  }

  @GetMapping("/notification/unread-count")
  public Response<Long> countUnread(@RequestHeader("Authorization") String authHeader) {

    String userId = jwtUtil.getUserId(authHeader);
    return notificationUserService.countUnread(userId);
  }

}
