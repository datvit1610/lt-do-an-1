package com.codec.system.domain.repository;

import com.codec.system.domain.entity.ClassPeriodEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassPeriodRepository extends JpaRepository<ClassPeriodEntity, String> {
  List<ClassPeriodEntity> findAllByOrderByPeriodNumberAsc();

  Optional<ClassPeriodEntity> findByPeriodNumber(Integer periodNumber);
}
