package com.codec.system.application.service.impl;

import codec.common.Response;
import com.codec.system.application.command.request.notificationConfig.NotificationConfigRequest;
import com.codec.system.application.command.request.task_history.TaskHistoryRequest;
import com.codec.system.application.command.response.notificationConfig.NotificationConfigResponse;
import com.codec.system.application.command.response.notificationConfig.NotificationLogResponse;
import com.codec.system.application.service.NotificationConfigService;
import com.codec.system.application.service.TaskHistoryService;
import com.codec.system.config.NotificationScheduler;
import com.codec.system.domain.entity.NotificationConfigEntity;
import com.codec.system.domain.repository.NotificationConfigRepository;
import com.codec.system.domain.repository.NotificationLogRepository;
import com.codec.system.pagination.domain.CodecSystemApplicationPage;
import com.codec.system.pagination.domain.CodecSystemApplicationPageable;
import com.codec.system.pagination.infrastructure.primary.RestCodecSystemApplicationPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationConfigServiceImpl implements NotificationConfigService {

  private final TaskHistoryService taskHistoryService;
  private final NotificationConfigRepository notificationConfigRepository;
  private final NotificationLogRepository notificationLogRepository;
  private final NotificationScheduler notificationScheduler;

  @Override
  public Response<NotificationConfigResponse> getConfig() {
      NotificationConfigEntity entity = notificationConfigRepository.findFirstByOrderByIdAsc();
      if (entity == null) {
        return Response.<NotificationConfigResponse>of(null).success("Chưa có cấu hình", 200);
      }
      NotificationConfigResponse response = new NotificationConfigResponse();
      response.setExpiredDayNotify(entity.getExpiredDayNotify());
      return Response.of(response).success("Lấy cấu hình thành công", 200);
    }


  @Override
  @Transactional
  public void saveConfig(NotificationConfigRequest request, String userId) {
    // Đã có thì update, chưa có thì insert
    NotificationConfigEntity entity = notificationConfigRepository.findFirstByOrderByIdAsc();
    if (entity == null) {
      entity = new NotificationConfigEntity();
      entity.setCreatedBy(userId);
    }
    entity.setExpiredDayNotify(request.getExpiredDayNotify());
    entity.setModifiedBy(userId);
    entity.setModifiedDate(new Date());
    notificationConfigRepository.save(entity);

    // Đặt lại lịch ngay lập tức
    notificationScheduler.reschedule();

    NotificationConfigResponse response = new NotificationConfigResponse();
    response.setExpiredDayNotify(entity.getExpiredDayNotify());
    response.setNotifyHour(entity.getNotifyHour());
    response.setNotifyMinute(entity.getNotifyMinute());
    try {
      taskHistoryService.createTaskHistory(new TaskHistoryRequest(
        "Cấu hình ngưỡng thông báo", "Cấu hình thông báo: " + request.getExpiredDayNotify() + " ngày", userId)
      );
    } catch (Exception e) {
      log.error("Lỗi lưu lịch sử tác vụ");
    }
  }


  @Override
  public RestCodecSystemApplicationPage<NotificationLogResponse> search(LocalDate from, LocalDate to, Pageable pageable) {
    Page<NotificationLogResponse> result = notificationLogRepository
      .searchByDateRange(from, to, pageable)
      .map(e -> {
        NotificationLogResponse res = new NotificationLogResponse();
        res.setId(e.getId());
        res.setContent(e.getContent());
        res.setNearExpiryCount(e.getNearExpiryCount());
        res.setSentAt(e.getSentAt());
        res.setSuccessCount(e.getSuccessCount());
        res.setFailCount(e.getFailCount());
        return res;
      });
    List<NotificationLogResponse> list = result.getContent();
    long currentCount = result.getTotalElements();
    CodecSystemApplicationPageable codecPageable = new CodecSystemApplicationPageable(pageable.getPageNumber(), pageable.getPageSize());
    return RestCodecSystemApplicationPage
      .from(CodecSystemApplicationPage
        .of(list, codecPageable, currentCount), taskHistory-> taskHistory);
  }


}
