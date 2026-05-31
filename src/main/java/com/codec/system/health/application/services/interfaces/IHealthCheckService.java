package com.codec.system.health.application.services.interfaces;

import codec.common.Response;
import com.codec.system.health.domain.dto.HealCheckDto;

public interface IHealthCheckService {
     Response<HealCheckDto> getProfile();
}
