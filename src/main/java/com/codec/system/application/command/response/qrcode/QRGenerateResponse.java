package com.codec.system.application.command.response.qrcode;

import lombok.Data;

@Data
public class QRGenerateResponse {
  private String id;
  private String productName;
  private String qrCode;     // base64 PNG
  private String qrUrl;      // URL được nhúng trong QR
}
