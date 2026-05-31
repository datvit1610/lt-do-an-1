package com.codec.system.domain.repository;

import com.codec.system.domain.entity.RoleEntity;
import jakarta.persistence.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, String> {

  Optional<RoleEntity> findByNameAndDeletedIsFalse(String name);
  Optional<RoleEntity> findByNameAndDeletedIsFalseAndIdNot(String name, String  id);

  @Query(value = """
  select r.id as roleId, r.name as roleName from RoleEntity r
  where r.deleted = false
""")
  List<Tuple> selectRole();
  @Query(value = """
  select r.id as roleId, r.name as roleName from RoleEntity r
  where (:roleName IS NULL OR :roleName ILIKE '' OR r.name ILIKE CONCAT('%', :roleName, '%'))
  and r.deleted = false
""")
  Page<Tuple> getAll(String roleName, Pageable pageable);
}
