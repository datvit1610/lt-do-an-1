package com.codec.system.api;

import codec.common.Response;
import com.codec.system.application.command.response.notification.NotificationListResponse;
import com.codec.system.application.service.NotificationService;
import com.codec.system.application.service.authen.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RequestMapping("/api/v1")
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class NotificationController {

  NotificationService notificationService;
  JwtUtil jwtUtil;

  @Operation(summary = "Danh sách thông báo của người dùng + tổng số chưa đọc")
  @GetMapping("/notification/get-all")
  public Mono<Response<NotificationListResponse>> getNotifications(
    @Parameter(description = "Trang (bắt đầu từ 0)")
    @RequestParam(defaultValue = "0") int page,
    @Parameter(description = "Số bản ghi mỗi trang")
    @RequestParam(defaultValue = "10") int size,
    @RequestHeader("Authorization") String authHeader
  ) {
    String userId = resolveUserId(authHeader);
    if (userId == null) {
      return Mono.just(Response.fail("Token không hợp lệ", 403));
    }
    Pageable pageable = PageRequest.of(page, size);
    NotificationListResponse data = notificationService.getNotifications(userId, pageable);
    return Mono.just(Response.of(data).success("Thành công", 200));
  }

  @Operation(summary = "Đánh dấu 1 thông báo đã đọc")
  @PutMapping("/notification/{id}/read")
  public Mono<Response<Object>> markOneRead(
    @PathVariable("id") String id,
    @RequestHeader("Authorization") String authHeader
  ) {
    String userId = resolveUserId(authHeader);
    if (userId == null) {
      return Mono.just(Response.fail("Token không hợp lệ", 403));
    }
    notificationService.markOneRead(id);
    return Mono.just(Response.ok().success("Đã đánh dấu đã đọc", 200));
  }

  @Operation(summary = "Đánh dấu tất cả thông báo đã đọc")
  @PutMapping("/notification/read-all")
  public Mono<Response<Object>> markAllRead(
    @RequestHeader("Authorization") String authHeader
  ) {
    String userId = resolveUserId(authHeader);
    if (userId == null) {
      return Mono.just(Response.fail("Token không hợp lệ", 403));
    }
    notificationService.markAllRead(userId);
    return Mono.just(Response.ok().success("Đã đánh dấu tất cả đã đọc", 200));
  }

  /**
   * Lấy userId từ Authorization header (Bearer token).
   * Trả về null nếu token không hợp lệ.
   */
  private String resolveUserId(String authHeader) {
    try {
      return jwtUtil.getUserId(authHeader);
    } catch (Exception e) {
      return null;
    }
  }
}
