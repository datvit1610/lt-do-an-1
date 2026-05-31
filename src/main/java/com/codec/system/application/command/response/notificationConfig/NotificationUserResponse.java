package com.codec.system.application.command.response.notificationConfig;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Tuple;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

@Data
public class NotificationUserResponse {
  private String notificationLogId;
  private String content;

  @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
  private LocalDateTime sentAt;

  private Boolean isRead;

  @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
  private LocalDateTime readAt;
  private Integer status = 2;

  public NotificationUserResponse(Tuple tuple) {
    this.notificationLogId = tuple.get("id", String.class);
    this.content = tuple.get("content", String.class);
//    this.sentAt = tuple.get("sentAt", LocalDateTime.class);
    this.isRead = tuple.get("isRead", Boolean.class);
//    this.readAt = tuple.get("readAt", LocalDateTime.class);
    this.sentAt = Optional.ofNullable(tuple.get("sentAt", Timestamp.class))
      .map(Timestamp::toLocalDateTime)
      .orElse(null);

    this.readAt = Optional.ofNullable(tuple.get("readAt", Timestamp.class))
      .map(Timestamp::toLocalDateTime)
      .orElse(null);
  }
}
