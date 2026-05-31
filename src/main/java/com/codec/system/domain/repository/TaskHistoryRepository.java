package com.codec.system.domain.repository;

import com.codec.system.domain.entity.TaskHistoryEntity;
import jakarta.persistence.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskHistoryRepository extends JpaRepository<TaskHistoryEntity, String> {

  @Query("select th.createdDate as createdDate, u.fullName as createdBy, th.task as task, th.content as content " +
    "from TaskHistoryEntity th " +
    "join UserEntity u on u.id = th.createdBy " +
    "where (:createdBy IS NULL OR :createdBy ILIKE '' OR u.fullName ILIKE CONCAT('%', :createdBy, '%')) " +
    "and (:task IS NULL OR :task ILIKE '' OR th.task ILIKE CONCAT('%', :task, '%')) " +
    "and (:content IS NULL OR :content ILIKE '' OR th.content ILIKE CONCAT('%', :content, '%')) " +
    "and (date(th.createdDate) >= TO_DATE(:startDate, 'DD/MM/YYYY') and date(th.createdDate) <= TO_DATE(:endDate, 'DD/MM/YYYY')) " +
    " order by th.createdDate desc")
  Page<Tuple> findAll(@Param("createdBy") String createdBy,
                      @Param("task") String task,
                      @Param("content") String content,
                      @Param("startDate") String startDate,
                      @Param("endDate") String endDate,
                      Pageable pageable);

  @Query("select distinct th.task as task " +
    "from TaskHistoryEntity th " +
    "order by th.task asc")
  List<Tuple> listTask();
}
