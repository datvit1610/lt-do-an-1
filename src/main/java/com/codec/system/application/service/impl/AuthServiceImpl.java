package com.codec.system.application.service.impl;

import codec.common.Response;
import com.codec.system.application.command.request.user.AuthRequest;
import com.codec.system.application.command.request.user.RefreshTokenRequest;
import com.codec.system.application.command.request.user.ResetPasswordRequest;
import com.codec.system.application.command.response.user.AuthResponse;
import com.codec.system.application.command.response.user.ProfileResponse;
import com.codec.system.application.service.AuthService;
import com.codec.system.application.service.authen.JwtUtil;
import com.codec.system.domain.entity.RoleEntity;
import com.codec.system.domain.entity.UserEntity;
import com.codec.system.domain.repository.PermissionRepository;
import com.codec.system.domain.repository.RoleRepository;
import com.codec.system.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.codec.system.common.utils.EncryptorPassword.decrypt;
import static com.codec.system.common.utils.EncryptorPassword.encrypt;


@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
  @Autowired
  private JwtUtil jwtUtil;

  @Autowired
  private UserDetailsService userDetailsService;

  private final UserRepository userRepository;

  private final RoleRepository roleRepository;

  private final PermissionRepository permissionRepository;


  @Autowired
  private PasswordEncoder passwordEncoder;

  @Override
  public Response<AuthResponse> login(AuthRequest loginRequest) throws Exception {
//    UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
//    if (userDetails == null) {
//      throw new RuntimeException("Tài khoản không tồn tại");
//    }
    Optional<UserEntity> userEntity = userRepository.findByUsernameAndDeletedIsFalse(loginRequest.getUsername());
    if (userEntity.isEmpty()) {
      throw new RuntimeException("Tài khoản không tồn tại");
    }
//    log.info(decrypt(userEntity.get().getPassword()));
    if (userEntity.get().getDeleted()) {
      throw new RuntimeException("Tài khoản đã bị xóa");
    }
    if (userEntity.get().getStatus() == 0) {
      throw new RuntimeException("Tài khoản đang bị khóa. Vui lòng liên hệ Admin hệ thống để xử lý.");
    }
    String deCodePassword = decrypt(userEntity.get().getPassword());
//    if (!passwordEncoder.matches(loginRequest.getPassword(), userDetails.getPassword())) {
//      throw new RuntimeException("Sai mật khẩu, vui lòng nhập lại!");
//    }
    if (!loginRequest.getPassword().equals(deCodePassword)) {
      throw new RuntimeException("Sai mật khẩu, vui lòng nhập lại!");
    }
    //lấy danh sách nhóm quyền để add vào token
    List<ProfileResponse.RoleInfo.Permission> permissionList = permissionRepository.getByRoleId(userEntity.get().getRoleId())
      .stream().map(ProfileResponse.RoleInfo.Permission::new).toList();
    List<String> permissions = permissionList.stream().map(ProfileResponse.RoleInfo.Permission::getName).toList();
    String token = jwtUtil.generateToken(loginRequest.getUsername(), permissions, userEntity.get().getId(), userEntity.get().getAccountType());
    String refreshToken = jwtUtil.generateRefreshToken(loginRequest.getUsername());

    AuthResponse response = new AuthResponse();
    response.setAccessToken(token);
    response.setRefreshToken(refreshToken);

    return Response.of(response);
  }

  @Override
  public Response<AuthResponse> refresh(RefreshTokenRequest tokenRequest) {
    String refreshToken = tokenRequest.getRefreshToken();

    if (!jwtUtil.validateToken(refreshToken)) {
      throw new RuntimeException("Refresh token không hợp lệ");
    }
    String username = jwtUtil.getUsernameFromToken(refreshToken);
    Optional<UserEntity> userEntity = userRepository.findByUsernameAndDeletedIsFalse(username);
    if (userEntity.isEmpty()) {
      throw new RuntimeException("Tài khoản không tồn tại");
    }
    //lấy danh sách nhóm quyền để add vào token
    List<ProfileResponse.RoleInfo.Permission> permissionList = permissionRepository.getByRoleId(userEntity.get().getRoleId())
      .stream().map(ProfileResponse.RoleInfo.Permission::new).toList();
    List<String> permissions = permissionList.stream().map(ProfileResponse.RoleInfo.Permission::getName).toList();
    String newAccessToken = jwtUtil.generateToken(username, permissions, userEntity.get().getId(), userEntity.get().getAccountType());
    String newRefreshToken = jwtUtil.generateRefreshToken(username); // Optional: làm mới luôn

    AuthResponse response = new AuthResponse();
    response.setAccessToken(newAccessToken);
    response.setRefreshToken(newRefreshToken);

    return Response.of(response);

  }

  @Override
  public Response<ProfileResponse> getProfileUser(String token) {
    if (!jwtUtil.validateToken(token)) {
      throw new RuntimeException("Token không hợp lệ");
    }

    String username = jwtUtil.getUsernameFromToken(token);
    Optional<UserEntity> userEntity = userRepository.findByUsernameAndDeletedIsFalse(username);
    if (userEntity.isEmpty()) {
      throw new RuntimeException("Người dùng không tồn tại");
    }
    ProfileResponse profileResponse = new ProfileResponse(
      userEntity.get().getId(),
      userEntity.get().getPhoneNumber(),
      userEntity.get().getFullName(),
      userEntity.get().getStatus(),
      userEntity.get().getAccountType()
    );


    //set nhóm quyền
    Optional<RoleEntity> roleEntity = roleRepository.findById(userEntity.get().getRoleId());
    if (roleEntity.isEmpty()) {
      throw new RuntimeException("Người dùng không có nhóm quyền");
    }
    ProfileResponse.RoleInfo roleInfo = new ProfileResponse.RoleInfo();
    roleInfo.setId(roleEntity.get().getId());
    roleInfo.setName(roleEntity.get().getName());

    //set danh sách quyền
    List<ProfileResponse.RoleInfo.Permission> permissionList = permissionRepository.getByRoleId(roleEntity.get().getId())
      .stream().map(ProfileResponse.RoleInfo.Permission::new).toList();
    roleInfo.setPermissions(permissionList);
    profileResponse.setRoleInfo(roleInfo);

    return Response.of(profileResponse);
  }

  @Override
  @Transactional
  public void resetPassword(ResetPasswordRequest resetPasswordRequest, String userId) throws Exception {
    Optional<UserEntity> userEntity = userRepository.findById(userId);
    if (userEntity.isEmpty()) {
      throw new RuntimeException("Người dùng không tồn tại");
    }
    //check password cũ có khớp không
    if (!decrypt(userEntity.get().getPassword()).equals(resetPasswordRequest.getOldPassword())) {
      throw new RuntimeException("Mật khẩu cũ không trùng khớp");
    }
    //update password mới
    userEntity.get().setPassword(encrypt(resetPasswordRequest.getNewPassword()));
    userRepository.save(userEntity.get());
    log.info("Đổi mật khẩu thành công");
  }

}
