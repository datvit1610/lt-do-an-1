package com.codec.system.application.service.impl;

import com.codec.system.application.command.request.task_history.FilterTaskHistoryRequest;
import com.codec.system.application.command.request.task_history.TaskHistoryRequest;
import com.codec.system.application.command.response.task_history.ListTaskResponse;
import com.codec.system.application.command.response.task_history.TaskHistoryResponse;
import com.codec.system.application.service.TaskHistoryService;
import com.codec.system.common.utils.Utils;
import com.codec.system.domain.entity.TaskHistoryEntity;
import com.codec.system.domain.repository.TaskHistoryRepository;
import com.codec.system.error.exception.NotFoundException;
import com.codec.system.pagination.domain.CodecSystemApplicationPage;
import com.codec.system.pagination.domain.CodecSystemApplicationPageable;
import com.codec.system.pagination.infrastructure.primary.RestCodecSystemApplicationPage;
import jakarta.persistence.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TaskHistoryServiceImpl implements TaskHistoryService {
  private final TaskHistoryRepository taskHistoryRepository;

  public TaskHistoryServiceImpl(TaskHistoryRepository taskHistoryRepository
  ) {
    this.taskHistoryRepository = taskHistoryRepository;
  }



  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void createTaskHistory (TaskHistoryRequest taskHistoryRequest) {
    TaskHistoryEntity taskHistoryEntity = new TaskHistoryEntity();
//    Object principal = authentication.getPrincipal();
//    Object claims = ((Jwt) principal).getClaims().get("name");
    taskHistoryEntity.setCreatedBy(taskHistoryRequest.getUserId());
    taskHistoryEntity.setTask(taskHistoryRequest.getTask());
    taskHistoryEntity.setContent(taskHistoryRequest.getContent());
    taskHistoryRepository.save(taskHistoryEntity);
    // Phát ra sự kiện khi lịch sử được tạo thành công
//    taskHistoryEventPublisher.publishTaskHistoryCreatedEvent(taskHistoryEntity);
  }

  @Override
  public RestCodecSystemApplicationPage<TaskHistoryResponse> search(FilterTaskHistoryRequest filterTaskHistoryRequest, Pageable pageable) {
    try {
      if (filterTaskHistoryRequest.getStartDate().isEmpty() && filterTaskHistoryRequest.getEndDate().isEmpty()) {
        throw new NotFoundException("Vui lòng chọn ngày để tìm kiếm");
      } else {
        String startDated = Utils.stringToString(filterTaskHistoryRequest.getStartDate());
        String endDated = Utils.stringToString(filterTaskHistoryRequest.getEndDate());
        Page<Tuple> tuples = taskHistoryRepository.findAll(filterTaskHistoryRequest.getCreatedBy(), filterTaskHistoryRequest.getTask(),
          filterTaskHistoryRequest.getContent(), startDated, endDated, pageable);
        List<TaskHistoryResponse> list = tuples.getContent()
          .stream()
          .map(TaskHistoryResponse::new)
          .toList();

        long currentCount = tuples.getTotalElements();
        CodecSystemApplicationPageable codecPageable = new CodecSystemApplicationPageable(pageable.getPageNumber(), pageable.getPageSize());
        return RestCodecSystemApplicationPage
          .from(CodecSystemApplicationPage
            .of(list, codecPageable, currentCount), taskHistory-> taskHistory);
      }
    } catch (RuntimeException e) {
      return null;
    }
  }

  @Override
  public List<ListTaskResponse> listTask() {
    try {
        List<Tuple> tuples = taskHistoryRepository.listTask();
      return tuples
        .stream()
        .map(ListTaskResponse::new)
        .toList();
    } catch (RuntimeException e) {
      return null;
    }
  }




}
