package com.tangchao.shop.service;

public interface TongjiBaiduService {

    String getAccessToken();

    Object getTimeTrendRpt(String startDate, String endDate);

    Object getSourceAll(String startDate, String endDate);

    Object getVisitToppage(String startDate, String endDate);

    Object getVisitDistrict(String startDate, String endDate);
}
