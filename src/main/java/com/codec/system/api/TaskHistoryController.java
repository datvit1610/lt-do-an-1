package com.codec.system.api;

import codec.common.Response;
import com.codec.system.application.command.request.task_history.FilterTaskHistoryRequest;
import com.codec.system.application.command.response.task_history.ListTaskResponse;
import com.codec.system.application.command.response.task_history.TaskHistoryResponse;
import com.codec.system.application.service.TaskHistoryService;
import com.codec.system.application.service.authen.JwtUtil;
import com.codec.system.pagination.infrastructure.primary.RestCodecSystemApplicationPage;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RequestMapping("/api/v1")
@RestController
@RequiredArgsConstructor
public class TaskHistoryController {
  private final TaskHistoryService taskHistoryService;
  private final JwtUtil jwtUtil;


  @Operation(summary = "Tìm kiếm lịch sử tác vụ")
//  @PreAuthorize(Permission.HIS_V)
  @GetMapping("/task-history/search")
  public Mono<Response<RestCodecSystemApplicationPage<TaskHistoryResponse>>> search(
    @RequestParam(required = false) String createdBy,
    @RequestParam(required = false) String task,
    @RequestParam(required = false) String startDate,
    @RequestParam(required = false) String endDate,
    @RequestParam(required = false) String content,
    @RequestHeader("Authorization") String authHeader,
    @ParameterObject Pageable pageable
  ) {
    String userId = jwtUtil.checkPermission(authHeader, "his-v");
    if (userId.equals("Api không có quyền truy cập") || userId.equals("Token không hợp lệ")) {
      return Mono.just(Response.fail(userId, 403));
    }
    FilterTaskHistoryRequest filterTaskHistoryRequest = new FilterTaskHistoryRequest();
    filterTaskHistoryRequest.setCreatedBy(createdBy);
    filterTaskHistoryRequest.setTask(task);
    filterTaskHistoryRequest.setStartDate(startDate);
    filterTaskHistoryRequest.setEndDate(endDate);
    filterTaskHistoryRequest.setContent(content);
    RestCodecSystemApplicationPage<TaskHistoryResponse> list = taskHistoryService.search(filterTaskHistoryRequest, pageable);
    return Mono.just(Response.of(list).success("Thành công", 200));
  }

  @Operation(summary = "select tác vụ")
  @GetMapping("/task-history/select")
  public Mono<Response<List<ListTaskResponse>>> listTask(
  ) {
    List<ListTaskResponse> list = taskHistoryService.listTask();
    return Mono.just(Response.of(list).success("Thành công", 200));
  }
}
