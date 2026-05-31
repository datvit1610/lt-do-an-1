package com.codec.system.application.service;

import com.codec.system.application.command.request.task_history.FilterTaskHistoryRequest;
import com.codec.system.application.command.request.task_history.TaskHistoryRequest;
import com.codec.system.application.command.response.task_history.ListTaskResponse;
import com.codec.system.application.command.response.task_history.TaskHistoryResponse;
import com.codec.system.pagination.infrastructure.primary.RestCodecSystemApplicationPage;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TaskHistoryService {

  void createTaskHistory (TaskHistoryRequest taskHistoryRequest);

  RestCodecSystemApplicationPage<TaskHistoryResponse> search(FilterTaskHistoryRequest filterTaskHistoryRequest, Pageable pageable);

  List<ListTaskResponse> listTask();

}
