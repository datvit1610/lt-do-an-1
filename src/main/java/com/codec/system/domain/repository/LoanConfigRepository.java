package com.codec.system.domain.repository;

import com.codec.system.domain.entity.LoanConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface LoanConfigRepository extends JpaRepository<LoanConfigEntity, String> {
  LoanConfigEntity findFirstByOrderByIdAsc();
}
