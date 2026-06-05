package com.codec.system.application.service.impl;

import codec.common.Response;
import com.codec.system.application.command.request.loan.LoanConfigRequest;
import com.codec.system.application.command.response.loan.LoanConfigResponse;
import com.codec.system.application.service.LoanConfigService;
import com.codec.system.domain.entity.LoanConfigEntity;
import com.codec.system.domain.repository.LoanConfigRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class LoanConfigServiceImpl implements LoanConfigService {
  LoanConfigRepository loanConfigRepository;

  @Override
  public Response<LoanConfigResponse> getConfig() {
    LoanConfigEntity entity = loanConfigRepository.findFirstByOrderByIdAsc();
    LoanConfigResponse response = new LoanConfigResponse();
    response.setLateThresholdMinutes(
      entity != null && entity.getLateThresholdMinutes() != null
        ? entity.getLateThresholdMinutes()
        : DEFAULT_LATE_THRESHOLD_MINUTES);
    return Response.of(response).success("Thành công", 200);
  }

  @Override
  @Transactional
  public void saveConfig(LoanConfigRequest request, String userId) {
    LoanConfigEntity entity = loanConfigRepository.findFirstByOrderByIdAsc();
    if (entity == null) {
      entity = new LoanConfigEntity();
      entity.setCreatedBy(userId);
    }
    entity.setLateThresholdMinutes(request.getLateThresholdMinutes());
    entity.setModifiedBy(userId);
    entity.setModifiedDate(new Date());
    loanConfigRepository.save(entity);
  }

  @Override
  public int getLateThresholdMinutes() {
    LoanConfigEntity entity = loanConfigRepository.findFirstByOrderByIdAsc();
    if (entity != null && entity.getLateThresholdMinutes() != null) {
      return entity.getLateThresholdMinutes();
    }
    return DEFAULT_LATE_THRESHOLD_MINUTES;
  }
}
