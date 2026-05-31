package com.codec.system.domain.repository;

import com.codec.system.domain.entity.UserDeviceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserDeviceRepository extends JpaRepository<UserDeviceEntity, Long> {

  @Query("""
        SELECT d.fcmToken FROM UserDeviceEntity d
        WHERE d.userId IN :userIds
          AND d.isActive = true
        """)
  List<String> findActiveTokensByUserIds(@Param("userIds") List<String> userIds);

  Optional<UserDeviceEntity> findByUserIdAndPlatform(String userId, String platform);
}
