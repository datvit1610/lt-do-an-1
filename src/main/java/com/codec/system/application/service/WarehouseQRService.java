package com.codec.system.application.service;

import codec.common.Response;
import com.codec.system.application.command.request.qrcode.GenerateQRRequest;
import com.codec.system.application.command.response.qrcode.DetailWarehouseResponse;
import com.codec.system.application.command.response.qrcode.QRGenerateResponse;

import java.util.List;

public interface WarehouseQRService {
  Response<List<QRGenerateResponse>> generateQR(GenerateQRRequest request);
  Response<DetailWarehouseResponse> getDetailByQR(String id);
}
