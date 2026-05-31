package com.codec.system.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.util.Date;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public abstract class BaseEntity implements Serializable {
  @Id
  @GeneratedValue(generator = "uuid2")
  @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
  @Column(name = "id", updatable = false, nullable = false)
  private String id;

  @Column(name = "created_date", updatable = false)
  @CreationTimestamp
  private Date createdDate = new Date();

  @Column(name = "modified_date")
  @UpdateTimestamp
  private Date modifiedDate = new Date();

//  @Column(name = "created_by", insertable = false, updatable = false)
  private String createdBy;

//  @Column(name = "modified_by", insertable = false, updatable = false)
  private String modifiedBy;

  @Column(name = "deleted", columnDefinition = "BOOLEAN DEFAULT false")
  Boolean deleted = false;
}
