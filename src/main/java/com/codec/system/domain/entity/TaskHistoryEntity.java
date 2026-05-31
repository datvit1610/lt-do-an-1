package com.codec.system.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Entity
@Table(name = "task_history")
@Getter
@Setter
@NoArgsConstructor
public class TaskHistoryEntity extends BaseEntity {

  @Column(name = "task", nullable = false)
  private String task;

  @Column(name = "content", nullable = false, columnDefinition = "TEXT")
  private String content;


}
