package com.codec.system.application.command.response.notification;

import com.codec.system.domain.entity.NotificationEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class NotificationResponse {
  private String id;
  private String title;
  private String message;
  private String type; // LATE_RETURN / LOST
  private String refId; // loan_id
  private Boolean isRead;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
  private Date createdDate;

  public NotificationResponse(NotificationEntity entity) {
    this.id = entity.getId();
    this.title = entity.getTitle();
    this.message = entity.getMessage();
    this.type = entity.getType();
    this.refId = entity.getRefId();
    this.isRead = entity.getIsRead();
    this.createdDate = entity.getCreatedDate();
  }
}
