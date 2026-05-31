package com.codec.system.domain.repository;

import com.codec.system.domain.entity.UserEntity;
import jakarta.persistence.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {

  List<UserEntity> findByRoleIdAndDeletedIsFalse(String roleId);

  Optional<UserEntity> findByUsername(String username);
  Optional<UserEntity> findByUsernameAndDeletedIsFalse(String username);

  @Query(value = """
  select u.id as userId, u.roleId as roleId, u.username as userName, u.fullName as fullName,
  u.phoneNumber as phoneNumber, u.status as status, u.createdDate as createdDate, u.email as email, u.position as position,
  u.modifiedDate as modifiedDate, creator.fullName as createdBy
  from UserEntity u
  LEFT JOIN UserEntity creator ON creator.id = u.createdBy
  where (:userName IS NULL OR :userName ILIKE '' OR u.username ILIKE CONCAT('%', :userName, '%'))
  and (:status is null or u.status = :status)
  and (:phone is null or u.phoneNumber ILIKE CONCAT('%', :phone, '%'))
  and (:email is null or u.email ILIKE CONCAT('%', :email, '%'))
  and u.deleted = false and u.accountType = 2
  order by u.createdDate desc
""")
  Page<Tuple> getAllUser(String userName, Integer status, String phone, String email, Pageable pageable);

  @Query("""
        SELECT u.id FROM UserEntity u
        WHERE u.roleId IN :roleIds
          AND u.status = 1
        """)
  List<String> findActiveUserIdsByRoleIds(@Param("roleIds") List<String> roleIds);

}
