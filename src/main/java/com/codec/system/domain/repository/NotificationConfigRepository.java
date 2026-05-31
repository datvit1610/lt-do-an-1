package com.codec.system.domain.repository;

import com.codec.system.domain.entity.NotificationConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationConfigRepository extends JpaRepository<NotificationConfigEntity, String> {
  NotificationConfigEntity findFirstByOrderByIdAsc();
}
