package com.codec.system.api;

import codec.common.Response;
import com.codec.system.application.command.request.loan.CreateLoanRequest;
import com.codec.system.application.command.request.loan.UpdateLoanRequest;
import com.codec.system.application.command.response.loan.LoanResponse;
import com.codec.system.application.service.LoanService;
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
public class LoanController {
  LoanService loanService;
  JwtUtil jwtUtil;

  @Operation(summary = "Danh sách phiếu mượn")
  @GetMapping("/loan/get-all")
  public Mono<Response<RestCodecSystemApplicationPage<LoanResponse>>> getAllLoan(
    @ParameterObject Pageable pageable
  ) {
    Response<RestCodecSystemApplicationPage<LoanResponse>> data = loanService.getAllLoan(pageable);
    return Mono.just(Response.of(data.getData()).success("Thành công", 200));
  }

  @Operation(summary = "Lấy phiếu mượn theo id")
  @GetMapping("/loan/{id}")
  public Mono<Response<LoanResponse>> getLoanById(@PathVariable("id") String id) {
    Response<LoanResponse> data = loanService.getLoanById(id);
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
}
