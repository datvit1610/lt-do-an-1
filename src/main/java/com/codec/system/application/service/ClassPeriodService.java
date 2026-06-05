package com.codec.system.application.service;

import codec.common.Response;
import com.codec.system.application.command.request.classperiod.CreateClassPeriodRequest;
import com.codec.system.application.command.request.classperiod.UpdateClassPeriodRequest;
import com.codec.system.application.command.response.classperiod.ClassPeriodResponse;

import java.util.List;

public interface ClassPeriodService {
  Response<List<ClassPeriodResponse>> getAllClassPeriod();
  Response<ClassPeriodResponse> getClassPeriodById(String id);
  void createClassPeriod(CreateClassPeriodRequest request, String userId);
  void updateClassPeriod(String id, UpdateClassPeriodRequest request, String userId);
  void deleteClassPeriod(String id, String userId);
}
