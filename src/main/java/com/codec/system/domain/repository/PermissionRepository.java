package com.codec.system.domain.repository;

import com.codec.system.domain.entity.PermissionEntity;
import jakarta.persistence.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<PermissionEntity, String> {

  boolean existsByName(String name);

  @Query(value = """
      select p.id as permissionId, p.name as permissionName, p.description as description, p.groupId as groupId
      from PermissionEntity p
    """)
  List<Tuple> getAll();

  @Query(value = """
      select p.id as permissionId, p.name as permissionName, p.description as description
      from PermissionEntity p
      join RolePermissionEntity rp on rp.permissionId = p.id
      where rp.roleId = :roleId and rp.deleted = false
    """)
  List<Tuple> getByRoleId(String roleId);
}
