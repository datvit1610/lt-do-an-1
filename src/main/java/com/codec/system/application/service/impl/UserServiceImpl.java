package com.codec.system.application.service.impl;

import codec.common.Response;
import com.codec.system.application.command.request.user.CreateUserRequest;
import com.codec.system.application.command.request.task_history.TaskHistoryRequest;
import com.codec.system.application.command.request.user.UpdateUserRequest;
import com.codec.system.application.command.response.user.ListUserResponse;
import com.codec.system.application.service.TaskHistoryService;
import com.codec.system.application.service.UserService;
import com.codec.system.domain.entity.UserEntity;
import com.codec.system.domain.repository.UserRepository;
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

import static com.codec.system.common.utils.EncryptorPassword.encrypt;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
  UserRepository userRepository;
//  @Autowired
//  private PasswordEncoder passwordEncoder;
  TaskHistoryService taskHistoryService;


  /*
  danh sách user
   */
  @Override
  public RestCodecSystemApplicationPage<ListUserResponse> getAllUser(String userName, Integer status, String phone, String email, Pageable pageable) {
    Page<Tuple> page = userRepository.getAllUser(userName, status, phone, email, pageable);
    List<ListUserResponse> responses = page.stream().map(ListUserResponse::new).toList();

    long currentCount = page.getTotalElements();
    CodecSystemApplicationPageable codecPageable = new CodecSystemApplicationPageable(pageable.getPageNumber(), pageable.getPageSize());
    return RestCodecSystemApplicationPage
      .from(CodecSystemApplicationPage
        .of(responses, codecPageable, currentCount), user -> user);
  }

  /*
  thêm mới user
   */
  @Override
  @Transactional
  public void createUser(CreateUserRequest request, String userId) {
    try {

      Optional<UserEntity> checkUserName = userRepository.findByUsernameAndDeletedIsFalse(request.getUserName());
      if (checkUserName.isPresent()) {
        throw new RuntimeException("Tên tài khoản đã tồn tại");
      }
      UserEntity userEntity = new UserEntity();
      userEntity.setUsername(request.getUserName());
      userEntity.setPassword(encrypt(request.getPassword()));
      userEntity.setFullName(request.getFullName());
      userEntity.setPhoneNumber(request.getPhoneNumber());
      userEntity.setEmail(request.getEmail());
      userEntity.setPosition(request.getPosition());
      userEntity.setStatus(request.getStatus());
      userEntity.setRoleId(request.getRoleId());
      userEntity.setCreatedBy(userId);
      userRepository.save(userEntity);
      try {
        taskHistoryService.createTaskHistory(new TaskHistoryRequest("Thêm tài khoản", "Thêm tài khoản thành công, tên: " + userEntity.getFullName(), userId));
        log.info("Thêm tài khoản thành công, tên: " + userEntity.getFullName());
      } catch (Exception e) {
        log.error("Lỗi trong quá trình lưu log");
      }
      Response.ok();
    } catch (Exception e) {
      throw new RuntimeException("Gặp lỗi: " + e.getMessage());
    }
  }




  /*
  Cập nhật user
   */
  @Override
  @Transactional
  public void updateUser(String userId, UpdateUserRequest request, String user) {
    try {

      Optional<UserEntity> userEntity = userRepository.findById(userId);
      if (userEntity.isEmpty()) {
        throw new RuntimeException("Tài khoản không tồn tại");
      }
      if (request.getPassword() != null) {
        userEntity.get().setPassword(encrypt(request.getPassword()));
      }
      userEntity.get().setFullName(request.getFullName());
      userEntity.get().setPhoneNumber(request.getPhoneNumber());
      userEntity.get().setEmail(request.getEmail());
      userEntity.get().setPosition(request.getPosition());
      userEntity.get().setStatus(request.getStatus());
      userEntity.get().setRoleId(request.getRoleId());
      userEntity.get().setModifiedDate(new Date());
      userEntity.get().setModifiedBy(user);
      userRepository.save(userEntity.get());

      try {
        taskHistoryService.createTaskHistory(new TaskHistoryRequest("Hiệu chỉnh tài khoản", "Hiệu chỉnh khoản thành công, tên: " + userEntity.get().getFullName(), userId));
        log.info("Hiệu chỉnh khoản thành công, tên: " + userEntity.get().getFullName());
      } catch (Exception e) {
        log.error("Lỗi trong quá trình lưu log");
      }
      Response.ok();
    } catch (Exception e) {
      throw new RuntimeException("Gặp lỗi: " + e.getMessage());
    }
  }



  /*
  Xóa user
   */
  @Override
  @Transactional
  public void deleteUser(String userId, String user) {
    try {

      Optional<UserEntity> userEntity = userRepository.findById(userId);
      if (userEntity.isEmpty()) {
        throw new RuntimeException("Tài khoản không tồn tại");
      }
      userEntity.get().setDeleted(true);
      userEntity.get().setModifiedDate(new Date());
      userEntity.get().setModifiedBy(user);
      userRepository.save(userEntity.get());
      try {
        taskHistoryService.createTaskHistory(new TaskHistoryRequest("Xóa tài khoản", "Xóa tài khoản thành công, tên: " + userEntity.get().getFullName(), userId));
        log.info("Xóa tài khoản thành công, tên: " + userEntity.get().getFullName());
      } catch (Exception e) {
        log.error("Lỗi trong quá trình lưu log");
      }
      Response.ok();
    } catch (Exception e) {
      throw new RuntimeException("Gặp lỗi: " + e.getMessage());
    }
  }
}
