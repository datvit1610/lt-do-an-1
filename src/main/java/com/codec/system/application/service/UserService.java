package com.codec.system.application.service;

import com.codec.system.application.command.request.user.CreateUserRequest;
import com.codec.system.application.command.request.user.UpdateUserRequest;
import com.codec.system.application.command.response.user.ListUserResponse;
import com.codec.system.pagination.infrastructure.primary.RestCodecSystemApplicationPage;
import org.springframework.data.domain.Pageable;

public interface UserService {
  RestCodecSystemApplicationPage<ListUserResponse> getAllUser(String userName, Integer status, String phone, String email, Pageable pageable);

  void createUser(CreateUserRequest request, String userId);
  void updateUser(String userId, UpdateUserRequest request, String user);
  void deleteUser(String userId, String user);
}

