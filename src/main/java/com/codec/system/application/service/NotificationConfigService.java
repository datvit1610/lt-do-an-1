package com.codec.system.application.service;

import codec.common.Response;
import com.codec.system.application.command.request.notificationConfig.NotificationConfigRequest;
import com.codec.system.application.command.response.notificationConfig.NotificationConfigResponse;
import com.codec.system.application.command.response.notificationConfig.NotificationLogResponse;
import com.codec.system.pagination.infrastructure.primary.RestCodecSystemApplicationPage;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface NotificationConfigService {
  Response<NotificationConfigResponse> getConfig();
  void saveConfig(NotificationConfigRequest request, String userId);
  RestCodecSystemApplicationPage<NotificationLogResponse> search(LocalDate from, LocalDate to, Pageable pageable);
}
