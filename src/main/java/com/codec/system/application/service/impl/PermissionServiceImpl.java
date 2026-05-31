package com.codec.system.application.service.impl;

import codec.common.Response;
import com.codec.system.application.command.request.user.CreateRoleRequest;
import com.codec.system.application.command.request.task_history.TaskHistoryRequest;
import com.codec.system.application.command.response.user.ListPermissionResponse;
import com.codec.system.application.command.response.user.ListRolePermissionResponse;
import com.codec.system.application.service.PermissionService;
import com.codec.system.application.service.TaskHistoryService;
import com.codec.system.domain.entity.RoleEntity;
import com.codec.system.domain.entity.RolePermissionEntity;
import com.codec.system.domain.entity.UserEntity;
import com.codec.system.domain.repository.*;
import com.codec.system.pagination.domain.CodecSystemApplicationPage;
import com.codec.system.pagination.domain.CodecSystemApplicationPageable;
import com.codec.system.pagination.infrastructure.primary.RestCodecSystemApplicationPage;
import jakarta.persistence.Tuple;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {
  RoleRepository roleRepository;
  RolePermissionRepository rolePermissionRepository;
  PermissionRepository permissionRepository;
  UserRepository userRepository;
  TaskHistoryService taskHistoryService;


  /*
  Lấy danh sách quyền
   */
  @Override
  public Response<List<ListPermissionResponse>> getAllPermission() {
    List<ListPermissionResponse> responses = permissionRepository.getAll().stream().map(ListPermissionResponse::new).toList();
    return Response.of(responses);

  }

  /*
  danh sách nhóm quyền select
   */
  @Override
  public Response<List<ListRolePermissionResponse>> selectRole() {
    List<ListRolePermissionResponse> responses = roleRepository.selectRole().stream().map(ListRolePermissionResponse::new).toList();
    return Response.of(responses);
  }


  /*
  Lấy danh sách nhóm quyền
   */
  @Override
  public RestCodecSystemApplicationPage<ListRolePermissionResponse> getAllRole(String roleName, Pageable pageable) {
    Page<Tuple> page = roleRepository.getAll(roleName, pageable);
    List<ListRolePermissionResponse> responses = page.stream().map(ListRolePermissionResponse::new).toList();
    List<String> roleIds = responses.stream().map(ListRolePermissionResponse::getRoleId).toList();
    List<ListRolePermissionResponse.Permission> permissionList = rolePermissionRepository.findByRoleIds(roleIds)
      .stream().map(ListRolePermissionResponse.Permission::new).toList();
    //map danh sách quyền thêm nhóm quyền
    Map<String, List<ListRolePermissionResponse.Permission>> permissionMaps = permissionList.stream()
      .collect(Collectors.groupingBy(ListRolePermissionResponse.Permission::getRoleId));
    responses.forEach(r -> {
      List<ListRolePermissionResponse.Permission> permissions = permissionMaps.get(r.getRoleId());
      r.setPermissions(permissions);
    });
    long currentCount = page.getTotalElements();
    CodecSystemApplicationPageable codecPageable = new CodecSystemApplicationPageable(pageable.getPageNumber(), pageable.getPageSize());
    return RestCodecSystemApplicationPage
      .from(CodecSystemApplicationPage
        .of(responses, codecPageable, currentCount), role -> role);

  }

  /*
  thêm mới nhóm quyền
   */
  @Override
  @Transactional
  public void createRole(CreateRoleRequest createRoleRequest, String userId) {
    try {
      Optional<RoleEntity> checkRole = roleRepository.findByNameAndDeletedIsFalse(createRoleRequest.getRoleName());
      if (checkRole.isPresent()) {
        throw new RuntimeException("Tên nhóm quyền đã tồn tại");
      }
      RoleEntity roleEntity = new RoleEntity();
      roleEntity.setName(createRoleRequest.getRoleName());
      roleEntity.setCreatedBy(userId);
      roleRepository.save(roleEntity);

      //thêm quyền mới
      if (!createRoleRequest.getPermissionNews().isEmpty()) {
        List<RolePermissionEntity> rolePermissionEntityList = new ArrayList<>();
        createRoleRequest.getPermissionNews().forEach(per -> {
          RolePermissionEntity rolePermissionEntity = new RolePermissionEntity();
          rolePermissionEntity.setPermissionId(per);
          rolePermissionEntity.setRoleId(roleEntity.getId());
          rolePermissionEntity.setCreatedBy(userId);
          rolePermissionEntityList.add(rolePermissionEntity);
        });
        rolePermissionRepository.saveAll(rolePermissionEntityList);
      }
      try {
        taskHistoryService.createTaskHistory(new TaskHistoryRequest("Thêm nhóm quyền", "Thêm nhóm quyền thành công, tên: " + roleEntity.getName(), userId));
        log.info("Thêm nhóm quyền thành công, tên: " + roleEntity.getName());
      } catch (Exception e) {
        log.error("Lỗi trong quá trình lưu log");
      }
      Response.ok();

    } catch (Exception e) {
      throw new RuntimeException("Gặp lỗi: " + e.getMessage());
    }

  }


  /*
  update nhóm quyền, add thêm quyền vào nhóm
   */
  @Override
  @Transactional
  public void updateRole(String roleId, CreateRoleRequest createRoleRequest, String userId) {
    try {

      Optional<RoleEntity> roleEntity = roleRepository.findById(roleId);
      if (roleEntity.isEmpty()) {
        throw new RuntimeException("Nhóm quyền không tồn tại");
      }
      if (createRoleRequest.getRoleName() != null) {
        Optional<RoleEntity> checkRole = roleRepository.findByNameAndDeletedIsFalseAndIdNot(createRoleRequest.getRoleName(), roleEntity.get().getId());
        if (checkRole.isPresent()) {
          throw new RuntimeException("Tên nhóm quyền đã tồn tại");
        }
        roleEntity.get().setName(createRoleRequest.getRoleName());
        roleEntity.get().setModifiedBy(userId);
        roleRepository.save(roleEntity.get());
      }
      //check nhóm quyền muốn xóa
      if (!createRoleRequest.getPermissionOlds().isEmpty()) {
        List<RolePermissionEntity> rolePermissionEntityList = rolePermissionRepository.findByPermissionIds(createRoleRequest.getPermissionOlds(), roleEntity.get().getId());
        rolePermissionEntityList.forEach(r -> {
          r.setDeleted(true);
          r.setModifiedDate(new Date());
          r.setModifiedBy(userId);
        });
        rolePermissionRepository.saveAll(rolePermissionEntityList);
      }

      //thêm quyền mới
      if (!createRoleRequest.getPermissionNews().isEmpty()) {
        List<RolePermissionEntity> rolePermissionEntityList = new ArrayList<>();
        createRoleRequest.getPermissionNews().forEach(per -> {
          RolePermissionEntity rolePermissionEntity = new RolePermissionEntity();
          rolePermissionEntity.setPermissionId(per);
          rolePermissionEntity.setRoleId(roleId);
          rolePermissionEntity.setCreatedBy(userId);
          rolePermissionEntityList.add(rolePermissionEntity);
        });
        rolePermissionRepository.saveAll(rolePermissionEntityList);
      }
      try {
        taskHistoryService.createTaskHistory(new TaskHistoryRequest("Hiệu chỉnh nhóm quyền", "Hiệu chỉnh nhóm quyền thành công, tên: " + roleEntity.get().getName(), userId));
        log.info("Hiệu chỉnh nhóm quyền thành công, tên: " + roleEntity.get().getName());
      } catch (Exception e) {
        log.error("Lỗi trong quá trình lưu log");
      }
      Response.ok();
    } catch (Exception e) {
      throw new RuntimeException("Gặp lỗi: " + e.getMessage());
    }
  }

  @Override
  @Transactional
  public void deleteRole(String roleId, String userId) {
    try {
      Optional<RoleEntity> roleEntity = roleRepository.findById(roleId);
      if (roleEntity.isEmpty()) {
        throw new RuntimeException("Nhóm quyền không tồn tại");
      }
      List<UserEntity> userEntityList = userRepository.findByRoleIdAndDeletedIsFalse(roleId);
      if (!userEntityList.isEmpty()) {
        throw new RuntimeException("Nhóm quyền đã được sử dụng");
      }
      roleEntity.get().setDeleted(true);
      roleEntity.get().setModifiedDate(new Date());
      roleEntity.get().setModifiedBy(userId);
      roleRepository.save(roleEntity.get());
      try {
        taskHistoryService.createTaskHistory(new TaskHistoryRequest("Xóa nhóm quyền", "Xóa nhóm quyền thành công, tên: " + roleEntity.get().getName(), userId));
        log.info("Xóa nhóm quyền thành công, tên: " + roleEntity.get().getName());
      } catch (Exception e) {
        log.error("Lỗi trong quá trình lưu log");
      }
      Response.ok();

    } catch (Exception e) {
      throw new RuntimeException("Gặp lỗi: " + e.getMessage());
    }

  }


}
