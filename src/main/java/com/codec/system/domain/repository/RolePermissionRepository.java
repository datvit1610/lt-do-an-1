package com.codec.system.domain.repository;

import com.codec.system.domain.entity.RolePermissionEntity;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermissionEntity, String> {

  @Query("""
    SELECT rp FROM RolePermissionEntity rp
    JOIN PermissionEntity p ON p.id = rp.permissionId
    WHERE p.name = :permissionName
    """)
  List<RolePermissionEntity> findByPermissionName(@Param("permissionName") String permissionName);

  @Query(value = """
      select rp.roleId as roleId, rp.permissionId as permissionId, p.name as permissionName, p.description as description
      from RolePermissionEntity rp
      join PermissionEntity p on p.id = rp.permissionId
      where rp.roleId in (:roleIds) and rp.deleted = false
    """)

  List<Tuple> findByRoleIds(List<String> roleIds);

  @Query(value = """
      select rp from RolePermissionEntity rp
      where rp.permissionId in (:permissionIds)
      and rp.roleId = :roleId
      and rp.deleted = false
    """)

  List<RolePermissionEntity> findByPermissionIds(List<String> permissionIds, String roleId);

}
