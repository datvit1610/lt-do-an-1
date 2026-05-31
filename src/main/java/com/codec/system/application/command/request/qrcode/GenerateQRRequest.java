package com.codec.system.application.command.request.qrcode;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class GenerateQRRequest {
  @NotEmpty
  private List<String> ids; // 1 hoặc nhiều sản phẩm
}
