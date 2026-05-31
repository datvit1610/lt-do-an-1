package com.codec.system.error_generator.infrastructure.primary;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.codec.system.error.domain.CodecSystemApplicationException;
import codec.error.domain.StandardErrorKey;

@RestController
@RequestMapping("/api/errors")
class CodecSystemApplicationErrorsResource {

  @GetMapping("bad-request")
  void getBadRequest() {
    throw CodecSystemApplicationException.badRequest(StandardErrorKey.BAD_REQUEST).addParameter("code", "400").build();
  }
}
