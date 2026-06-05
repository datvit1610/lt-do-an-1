package com.codec.system.config;

import com.codec.system.domain.entity.PermissionEntity;
import com.codec.system.domain.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Seed các mã quyền mới cho nghiệp vụ ca học & cấu hình mượn trả nếu chưa tồn tại trong bảng permission.
 * Quyền được cấp cho role qua màn quản lý phân quyền sau khi đã có mã.
 */
//@Component
@Order(2)
@Slf4j
@RequiredArgsConstructor
public class LoanPermissionSeeder implements CommandLineRunner {

  private final PermissionRepository permissionRepository;

  @Override
  @Transactional
  public void run(String... args) {
    Map<String, String> permissions = new LinkedHashMap<>();
    permissions.put("class-period-v", "Xem danh sách tiết học");
    permissions.put("class-period-c", "Thêm mới tiết học");
    permissions.put("class-period-u", "Cập nhật tiết học");
    permissions.put("class-period-d", "Xóa tiết học");
    permissions.put("loan-config-v", "Xem cấu hình mượn trả");
    permissions.put("loan-config-c", "Cập nhật cấu hình mượn trả");

    permissions.forEach((name, description) -> {
      if (!permissionRepository.existsByName(name)) {
        PermissionEntity entity = new PermissionEntity();
        entity.setName(name);
        entity.setDescription(description);
        entity.setCreatedBy("system");
        permissionRepository.save(entity);
        log.info("Đã seed quyền mới: {}", name);
      }
    });
  }
}
