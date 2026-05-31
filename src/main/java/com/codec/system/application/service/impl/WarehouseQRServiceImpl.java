package com.codec.system.application.service.impl;

import codec.common.Response;
import com.codec.system.application.command.request.qrcode.GenerateQRRequest;
import com.codec.system.application.command.response.qrcode.DetailWarehouseResponse;
import com.codec.system.application.command.response.qrcode.QRGenerateResponse;
import com.codec.system.application.service.WarehouseQRService;
import com.codec.system.common.utils.QRCodeUtil;
import com.codec.system.domain.entity.WarehouseEntity;
import com.codec.system.domain.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WarehouseQRServiceImpl implements WarehouseQRService {
  private final WarehouseRepository warehouseRepository;
  private final QRCodeUtil qrCodeUtil;

  @Value("${app.base-url}")
  private String baseUrl;

  // Tạo và gán QR cho 1 hoặc nhiều sản phẩm
  @Override
  @Transactional
  public Response<List<QRGenerateResponse>> generateQR(GenerateQRRequest request) {
    List<WarehouseEntity> entities = warehouseRepository.findByIdInAndDeletedFalse(request.getIds());

    if (entities.isEmpty()) {
      return Response.fail("Không tìm thấy sản phẩm", 404);
    }

    List<QRGenerateResponse> result = new ArrayList<>();

    for (WarehouseEntity entity : entities) {
      try {
        // Nếu đã có QR, bỏ qua sản phẩm này
        if (entity.getQrCode() != null) {
          continue;
        }
        // URL nhúng vào QR — FE quét ra sẽ nhận được productId
        String qrUrl = baseUrl + "/api/v1/warehouse/detail/" + entity.getId();
        String qrBase64 = qrCodeUtil.generateQRBase64(entity.getId());

        entity.setQrCode(qrBase64);
        warehouseRepository.save(entity);

        QRGenerateResponse res = new QRGenerateResponse();
        res.setId(entity.getId());
        res.setProductName(entity.getProductName());
        res.setQrCode(qrBase64);
//        res.setQrUrl(qrUrl);
        result.add(res);

      } catch (Exception e) {
        // Bỏ qua sản phẩm lỗi, tiếp tục các sản phẩm còn lại
      }
    }

    return Response.of(result).success("Tạo QR thành công", 200);
  }

  // Chi tiết sản phẩm khi quét QR
  @Override
  public Response<DetailWarehouseResponse> getDetailByQR(String id) {
    WarehouseEntity entity = warehouseRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

    DetailWarehouseResponse response = mapToResponse(entity);
    return Response.of(response).success("Lấy chi tiết sản phẩm thành công", 200);
  }

  private DetailWarehouseResponse mapToResponse(WarehouseEntity entity) {
    DetailWarehouseResponse response = new DetailWarehouseResponse();
    response.setId(entity.getId());
    response.setProductGroup(entity.getProductGroup());
    response.setProductName(entity.getProductName());
    response.setOrigin(entity.getOrigin());
    response.setProductDetail(entity.getProductDetail());
    response.setMixingSpecification(entity.getMixingSpecification());
    response.setManufacturingDate(entity.getManufacturingDate());
    response.setExpiryDate(entity.getExpiryDate());
    response.setImportDate(entity.getImportDate());
    response.setUnit(entity.getUnit());
    response.setImportQuantity(entity.getImportQuantity());
    response.setImportedBy(entity.getImportedBy());
    response.setStorageLocation(entity.getStorageLocation());
    response.setNote(entity.getNote());
    response.setQrCode(entity.getQrCode());
    return response;
  }
}
