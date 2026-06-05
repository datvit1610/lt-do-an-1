package com.codec.system.application.service.impl;

import codec.common.Response;
import com.codec.system.application.command.request.classperiod.CreateClassPeriodRequest;
import com.codec.system.application.command.request.classperiod.UpdateClassPeriodRequest;
import com.codec.system.application.command.response.classperiod.ClassPeriodResponse;
import com.codec.system.application.service.ClassPeriodService;
import com.codec.system.domain.entity.ClassPeriodEntity;
import com.codec.system.domain.repository.ClassPeriodRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ClassPeriodServiceImpl implements ClassPeriodService {
  ClassPeriodRepository classPeriodRepository;

  private ClassPeriodResponse toResponse(ClassPeriodEntity e) {
    ClassPeriodResponse r = new ClassPeriodResponse();
    r.setId(e.getId());
    r.setPeriodNumber(e.getPeriodNumber());
    r.setShift(e.getShift());
    r.setStartTime(e.getStartTime());
    r.setEndTime(e.getEndTime());
    r.setCreatedDate(e.getCreatedDate());
    r.setModifiedDate(e.getModifiedDate());
    return r;
  }

  @Override
  public Response<List<ClassPeriodResponse>> getAllClassPeriod() {
    List<ClassPeriodResponse> responses = classPeriodRepository.findAllByOrderByPeriodNumberAsc()
      .stream()
      .filter(e -> !Boolean.TRUE.equals(e.getDeleted()))
      .map(this::toResponse)
      .toList();
    return Response.of(responses).success("Thành công", 200);
  }

  @Override
  public Response<ClassPeriodResponse> getClassPeriodById(String id) {
    Optional<ClassPeriodEntity> entity = classPeriodRepository.findById(id);
    if (entity.isEmpty() || Boolean.TRUE.equals(entity.get().getDeleted())) {
      throw new RuntimeException("Tiết học không tồn tại");
    }
    return Response.of(toResponse(entity.get())).success("Thành công", 200);
  }

  @Override
  @Transactional
  public void createClassPeriod(CreateClassPeriodRequest request, String userId) {
    try {
      ClassPeriodEntity entity = new ClassPeriodEntity();
      entity.setPeriodNumber(request.getPeriodNumber());
      entity.setShift(request.getShift());
      entity.setStartTime(request.getStartTime());
      entity.setEndTime(request.getEndTime());
      entity.setCreatedBy(userId);
      classPeriodRepository.save(entity);
    } catch (Exception e) {
      throw new RuntimeException("Gặp lỗi: " + e.getMessage());
    }
  }

  @Override
  @Transactional
  public void updateClassPeriod(String id, UpdateClassPeriodRequest request, String userId) {
    try {
      Optional<ClassPeriodEntity> entity = classPeriodRepository.findById(id);
      if (entity.isEmpty()) {
        throw new RuntimeException("Tiết học không tồn tại");
      }
      ClassPeriodEntity e = entity.get();
      e.setPeriodNumber(request.getPeriodNumber());
      e.setShift(request.getShift());
      e.setStartTime(request.getStartTime());
      e.setEndTime(request.getEndTime());
      e.setModifiedDate(new Date());
      e.setModifiedBy(userId);
      classPeriodRepository.save(e);
    } catch (Exception e) {
      throw new RuntimeException("Gặp lỗi: " + e.getMessage());
    }
  }

  @Override
  @Transactional
  public void deleteClassPeriod(String id, String userId) {
    try {
      Optional<ClassPeriodEntity> entity = classPeriodRepository.findById(id);
      if (entity.isEmpty()) {
        throw new RuntimeException("Tiết học không tồn tại");
      }
      ClassPeriodEntity e = entity.get();
      e.setDeleted(true);
      e.setModifiedDate(new Date());
      e.setModifiedBy(userId);
      classPeriodRepository.save(e);
    } catch (Exception e) {
      throw new RuntimeException("Gặp lỗi: " + e.getMessage());
    }
  }
}
