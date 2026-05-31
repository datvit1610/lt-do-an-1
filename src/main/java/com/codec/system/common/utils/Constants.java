package com.codec.system.common.utils;

public class Constants {
  public static final String ADMIN_APPROVE_INVENTORY  = "/api/v1/adapter/campaign/approve-campaign-to-dsp";
  public static final String UPDATE_STATUS_CAMPAIGN_OTT  = "/api/v1/adapter/campaign/ott/update-approval-status";
  public static final String PUSH_CAMPAIGN_TO_AD  = "/service/v1/ad-server/create-creative";
  public static final String PUSH_BROAD_CAST_STATUS_TO_DSP = "/api/v1/adapter/campaign/update-broadcast-status-to-dsp";
  public static final String UPDATE_CERTIFICATION = "/api/v1/adapter/campaign/upload-certification";
  public static final String RECEIVE_FCM_TOKEN_AND_SUB_USER = "/api/v1/adapter/approval-fcm-token-and-sub-user";
  public static final String SAVE_NOTIFICATION_TO_DSP = "/api/v1/adapter/notification/save-notification-to-dsp";
  public static final String GENERATE_CODE = "/api/v1/ad-sever/gen-code/inventory/outstream/generate";


  public static final String UPDATE_STATUS_SCHE = "/api/v1/process/tvc-broadcasts/log";
  public static final String GET_CONTRACT_CAMPAIGN_INFO = "/service/v1/sap/to-ssp/get-contract-campaign-info";

  public static class BillingUrl {
    public static final String REVENUE_TOTAL = "/api/v1/bill/report/ssp/ott/direct/revenue/total";
    public static final String REPORT_REVENUE_AND_IMPRESSION = "/api/v1/bill/report/ssp/ott/direct/daily-revenue-impressions";
    public static final String CURRENT_PREVIOUS_MONTH = "/api/v1/bill/report/ssp/ott/direct/revenue/outstream-instream/current-previous-month";
    public static final String CURRENT_PREVIOUS_MONTH_BY_DOMAIN = "/api/v1/bill/report/ssp/ott/direct/revenue/outstream-instream/current-previous-month-by-domain";
    public static final String CURRENT_PREVIOUS_MONTH_BY_CHARGE_REASON = "/api/v1/bill/report/ssp/ott/direct/revenue/outstream-instream/current-previous-month-by-charge-reason";
    public static final String REVENUE_OUTSTREAM_SLOT_PERFORMANCE = "/api/v1/bill/report/ssp/ott/direct/revenue/outstream/slot/performance";
    public static final String REVENUE_INSTREAM_SLOT_PERFORMANCE = "/api/v1/bill/report/ssp/ott/direct/revenue/instream/slot/performance";
    public static final String OUTSTREAM_TOTAL_CTR_CPM = "/api/v1/bill/report/ssp/ott/direct/revenue/outstream/total-ctr-cpm";
    public static final String OUTSTREAM_COMPLETED_VIEW_AND_RATE = "/api/v1/bill/report/ssp/ott/direct/revenue/outstream/completed-view-and-completion-rate";
    public static final String CAMPAIGN_TOP_10 = "/api/v1/bill/report/ssp/ott/direct/revenue/campaign-top-10";


  }
}
