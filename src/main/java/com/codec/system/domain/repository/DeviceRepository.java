package com.codec.system.domain.repository;

import com.codec.system.domain.entity.DeviceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<DeviceEntity, String> {
  Optional<DeviceEntity> findByDeviceCodeAndDeletedIsFalse(String deviceCode);
}
