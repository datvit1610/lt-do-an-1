package com.codec.system.domain.repository;

import com.codec.system.domain.entity.LoanConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanConfigRepository extends JpaRepository<LoanConfigEntity, String> {
  LoanConfigEntity findFirstByOrderByIdAsc();
}
