package com.codec.system.common.utils;

public class Permission {

  // QL kênh
  public static final String CN_C = "hasAuthority('cn-c')"; //Import danh sách kênh
  public static final String CN_V = "hasAuthority('cn-v')"; //Xem danh sách kênh

  //QL loại chương trình
  public static final String PG_TYPE_C = "hasAuthority('pg-type-c')"; //Import loại chương trình
  public static final String PG_TYPE_V = "hasAuthority('pg-type-v')"; //Xem danh sách loại chương trình

  //QL inventory
//  public static final String INV_V = "hasAuthority('inv-v')"; //Xem danh sách inventory
  public static final String INV_V = "inv-v"; //Xem danh sách inventory

  public static final String INV_C = "hasAuthority('inv-c')"; //Thêm mới inventory
  public static final String INV_U = "hasAuthority('inv-u')"; //Cập nhật mới inventory
  public static final String INV_D = "hasAuthority('inv-d')"; //Xóa inventory


  //QL campaign inventory
  public static final String CAM_INV_V = "hasAuthority('cam-inv-v')"; //Xem danh sách campaign
  public static final String CAM_INV_APPROVE = "hasAuthority('cam-inv-approve')"; //Duyệt/hủy campaign

}
