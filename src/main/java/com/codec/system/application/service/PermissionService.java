package com.codec.system.application.service;

import codec.common.Response;
import com.codec.system.application.command.request.user.CreateRoleRequest;
import com.codec.system.application.command.response.user.ListPermissionResponse;
import com.codec.system.application.command.response.user.ListRolePermissionResponse;
import com.codec.system.pagination.infrastructure.primary.RestCodecSystemApplicationPage;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PermissionService {
  Response<List<ListPermissionResponse>> getAllPermission();
  Response<List<ListRolePermissionResponse>> selectRole();
  RestCodecSystemApplicationPage<ListRolePermissionResponse> getAllRole(String roleName, Pageable pageable);
  void createRole(CreateRoleRequest createRoleRequest, String userId);
  void updateRole(String roleId, CreateRoleRequest createRoleRequest, String userId);
  void deleteRole(String roleId, String userId);
}
