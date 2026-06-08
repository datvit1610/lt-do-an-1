package com.codec.system.api;

import codec.common.Response;
import com.codec.system.application.command.request.loan.CreateLoanRequest;
import com.codec.system.application.command.request.loan.LoanConfigRequest;
import com.codec.system.application.command.request.loan.ReturnLoanRequest;
import com.codec.system.application.command.request.loan.UpdateLoanRequest;
import com.codec.system.application.command.response.loan.LoanConfigResponse;
import com.codec.system.application.command.response.loan.LoanResponse;
import com.codec.system.application.service.LoanConfigService;
import com.codec.system.application.service.LoanService;
import com.codec.system.application.service.authen.JwtUtil;
import jakarta.validation.Valid;
import com.codec.system.pagination.infrastructure.primary.RestCodecSystemApplicationPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@RequestMapping("/api/v1")
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class LoanController {
  LoanService loanService;
  LoanConfigService loanConfigService;
  JwtUtil jwtUtil;

  @Operation(summary = "Danh sách phiếu mượn")
  @GetMapping("/loan/get-all")
  public Mono<Response<RestCodecSystemApplicationPage<LoanResponse>>> getAllLoan(
    @Parameter(description = "Mã phiếu mượn (tìm gần đúng, không phân biệt hoa thường)")
    @RequestParam(required = false) String loanCode,
    @Parameter(description = "Tên người mượn (tìm gần đúng, không phân biệt hoa thường)")
    @RequestParam(required = false) String borrowerName,
    @Parameter(description = "Trạng thái: 1 - đang mượn, 2 - đã trả, 3 - Trả chậm, 4 - Mất thiết bị")
    @RequestParam(required = false) Integer status,
    @Parameter(description = "Mượn từ ngày (yyyy-MM-dd)")
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
    @Parameter(description = "Mượn đến ngày (yyyy-MM-dd)")
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
    @ParameterObject Pageable pageable,
    @RequestHeader("Authorization") String authHeader
  ) {
    String userId = jwtUtil.checkPermission(authHeader, "loan-v-admin");
    if (userId.equals("Api không có quyền truy cập") || userId.equals("Token không hợp lệ")) {
      return Mono.just(Response.fail(userId, 403));
    }
    Response<RestCodecSystemApplicationPage<LoanResponse>> data =
      loanService.getAllLoan(loanCode, borrowerName, status, fromDate, toDate, pageable);
    return Mono.just(Response.of(data.getData()).success("Thành công", 200));
  }

  @Operation(summary = "Danh sách phiếu mượn tự xem của user")
  @GetMapping("/loan/get-all-for-user")
  public Mono<Response<RestCodecSystemApplicationPage<LoanResponse>>> getAllLoanForUser(
    @Parameter(description = "Mã phiếu mượn (tìm gần đúng, không phân biệt hoa thường)")
    @RequestParam(required = false) String loanCode,
    @Parameter(description = "Trạng thái: 1 - đang mượn, 2 - đã trả, 3 - Trả chậm, 4 - Mất thiết bị")
    @RequestParam(required = false) Integer status,
    @Parameter(description = "Mượn từ ngày (yyyy-MM-dd)")
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
    @Parameter(description = "Mượn đến ngày (yyyy-MM-dd)")
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
    @ParameterObject Pageable pageable,
    @RequestHeader("Authorization") String authHeader
  ) {
    String userId = jwtUtil.checkPermission(authHeader, "loan-v-user");
    if (userId.equals("Api không có quyền truy cập") || userId.equals("Token không hợp lệ")) {
      return Mono.just(Response.fail(userId, 403));
    }
    Response<RestCodecSystemApplicationPage<LoanResponse>> data =
      loanService.getAllLoanForUser(loanCode, status, fromDate, toDate, pageable, userId);
    return Mono.just(Response.of(data.getData()).success("Thành công", 200));
  }

  @Operation(summary = "Tạo phiếu mượn")
  @PostMapping("/loan/create")
  public Mono<Response<Object>> createLoan(
    @RequestBody CreateLoanRequest request,
    @RequestHeader("Authorization") String authHeader
  ) {
    String userId = jwtUtil.checkPermission(authHeader, "loan-c");
    if (userId.equals("Api không có quyền truy cập") || userId.equals("Token không hợp lệ")) {
      return Mono.just(Response.fail(userId, 403));
    }
    loanService.createLoan(request, userId);
    return Mono.just(Response.ok().success("Tạo phiếu mượn thành công", 201));
  }

  @Operation(summary = "Cập nhật phiếu mượn")
  @PostMapping("/loan/update/{id}")
  public Mono<Response<Object>> updateLoan(
    @PathVariable("id") String id,
    @RequestBody UpdateLoanRequest request,
    @RequestHeader("Authorization") String authHeader
  ) {
    String userId = jwtUtil.checkPermission(authHeader, "loan-u");
    if (userId.equals("Api không có quyền truy cập") || userId.equals("Token không hợp lệ")) {
      return Mono.just(Response.fail(userId, 403));
    }
    loanService.updateLoan(id, request, userId);
    return Mono.just(Response.ok().success("Cập nhật thành công", 201));
  }

  @Operation(summary = "Ghi nhận trả phiếu mượn")
  @PostMapping("/loan/return/{id}")
  public Mono<Response<Object>> returnLoan(
    @PathVariable("id") String id,
    @RequestBody ReturnLoanRequest request,
    @RequestHeader("Authorization") String authHeader
  ) {
    String userId = jwtUtil.checkPermission(authHeader, "loan-u");
    if (userId.equals("Api không có quyền truy cập") || userId.equals("Token không hợp lệ")) {
      return Mono.just(Response.fail(userId, 403));
    }
    loanService.returnLoan(id, request, userId);
    return Mono.just(Response.ok().success("Ghi nhận trả thành công", 201));
  }

  @Operation(summary = "Xóa phiếu mượn")
  @PostMapping("/loan/delete/{id}")
  public Mono<Response<Object>> deleteLoan(
    @PathVariable("id") String id,
    @RequestHeader("Authorization") String authHeader
  ) {
    String userId = jwtUtil.checkPermission(authHeader, "loan-d");
    if (userId.equals("Api không có quyền truy cập") || userId.equals("Token không hợp lệ")) {
      return Mono.just(Response.fail(userId, 403));
    }
    loanService.deleteLoan(id, userId);
    return Mono.just(Response.ok().success("Xóa thành công", 201));
  }

  @Operation(summary = "Lấy cấu hình mượn trả (ngưỡng phút chậm trả)")
  @GetMapping("/loan-config/get")
  public Mono<Response<LoanConfigResponse>> getLoanConfig(
    @RequestHeader("Authorization") String authHeader
  ) {
    String userId = jwtUtil.checkPermission(authHeader, "loan-config-v");
    if (userId.equals("Api không có quyền truy cập") || userId.equals("Token không hợp lệ")) {
      return Mono.just(Response.fail(userId, 403));
    }
    LoanConfigResponse data = loanConfigService.getConfig().getData();
    return Mono.just(Response.of(data).success("Thành công", 200));
  }

  @Operation(summary = "Lưu cấu hình mượn trả (ngưỡng phút chậm trả)")
  @PostMapping("/loan-config/set")
  public Mono<Response<Object>> setLoanConfig(
    @RequestBody @Valid LoanConfigRequest request,
    @RequestHeader("Authorization") String authHeader
  ) {
    String userId = jwtUtil.checkPermission(authHeader, "loan-config-c");
    if (userId.equals("Api không có quyền truy cập") || userId.equals("Token không hợp lệ")) {
      return Mono.just(Response.fail(userId, 403));
    }
    loanConfigService.saveConfig(request, userId);
    return Mono.just(Response.ok().success("Thành công", 201));
  }
}
