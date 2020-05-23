package com.tangchao.shop.service.Impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.tangchao.shop.dto.TongjiDTO;
import com.tangchao.shop.dto.TongjiSourceAllDTO;
import com.tangchao.shop.dto.TongjiVisitToppageDTO;
import com.tangchao.shop.mapper.SysVariablesMapper;
import com.tangchao.shop.pojo.SysVariables;
import com.tangchao.shop.service.TongjiBaiduService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Class TongjiBaiduServiceImpl
 * @Description TODO
 * @Author Aquan
 * @Date 2020/5/6 10:34
 * @Version 1.0
 **/
@Slf4j
@Service
public class TongjiBaiduServiceImpl implements TongjiBaiduService {

    private final SysVariablesMapper sysVariablesMapper;

    public TongjiBaiduServiceImpl(SysVariablesMapper sysVariablesMapper) {
        this.sysVariablesMapper = sysVariablesMapper;
    }

    @Override
    public String getAccessToken() {
        final String clientId = "4aLDLWGBAAA4j4cILAx9ZG8M";
        final String clientSecret = "3ynHsO83zIPpeneneioQNUHGxHnprGjz";
        Example refreshExample = new Example(SysVariables.class);
        Example.Criteria refreshCriteria = refreshExample.createCriteria();
        refreshCriteria.andEqualTo("sysKey", "baidu_refresh_token");
        SysVariables refreshSysVariables = sysVariablesMapper.selectOneByExample(refreshExample);
        String url = "http://openapi.baidu.com/oauth/2.0/token?grant_type=refresh_token&client_id=" + clientId + "&client_secret=" + clientSecret + "&refresh_token=" + refreshSysVariables.getSysValue();
        String result = HttpUtil.get(url);
        log.warn(result);
        JSONObject jsonObject = JSONUtil.parseObj(result);
        String accessToken = String.valueOf(jsonObject.get("access_token"));
        String refreshToken = String.valueOf(jsonObject.get("refresh_token"));
        refreshSysVariables.setSysValue(refreshToken);
        sysVariablesMapper.updateByPrimaryKeySelective(refreshSysVariables);
        SysVariables accessSysVariables = SysVariables.builder()
                .id(2l)
                .sysValue(accessToken)
                .build();
        sysVariablesMapper.updateByPrimaryKeySelective(accessSysVariables);
        return accessToken;
    }

    @Override
    public Object getTimeTrendRpt(String startDate, String endDate) {
        long startTime = System.currentTimeMillis();
        List<Object> itemList = getItemList("overview/getTimeTrendRpt", "pv_count,visitor_count,ip_count,bounce_ratio,avg_visit_time,trans_count", startDate, endDate);
        List<Object> dateList = (List<Object>) itemList.get(0);
        List<Object> dataList = (List<Object>) itemList.get(1);
        log.warn(String.valueOf(dateList.size()));
        List<TongjiDTO> data = new ArrayList<>();
        for (int i=0; i<dateList.size(); i++) {
            List<Object> element = (List<Object>) dataList.get(i);
            TongjiDTO pvCount = TongjiDTO.builder()
                    .name("浏览量(PV)")
                    .date(((List<Object>) dateList.get(i)).get(0).toString())
                    .value(element.get(0).equals("--") ? 0 : element.get(0))
                    .build();
            data.add(pvCount);

            TongjiDTO visitorCount = TongjiDTO.builder()
                    .name("访客数(UV)")
                    .date(((List<Object>) dateList.get(i)).get(0).toString())
                    .value(element.get(1).equals("--") ? 0 : element.get(1))
                    .build();
            data.add(visitorCount);

            TongjiDTO ipCount = TongjiDTO.builder()
                    .name("IP数")
                    .date(((List<Object>) dateList.get(i)).get(0).toString())
                    .value(element.get(2).equals("--") ? 0 : element.get(2))
                    .build();
            data.add(ipCount);

            TongjiDTO bounceRatio = TongjiDTO.builder()
                    .name("跳出率")
                    .date(((List<Object>) dateList.get(i)).get(0).toString())
                    .value(element.get(3).equals("--") ? 0 : element.get(3))
                    .build();
            data.add(bounceRatio);
            TongjiDTO avgVisitTime = TongjiDTO.builder()
                    .name("平均访问时长")
                    .date(((List<Object>) dateList.get(i)).get(0).toString())
                    .value(element.get(4).equals("--") ? 0 : ((Integer)element.get(4))/60)
                    .build();
            data.add(avgVisitTime);
        }
        List<TongjiDTO> collect = data.stream().sorted(Comparator.comparing(TongjiDTO::getName)).collect(Collectors.toList());
        long endTime = System.currentTimeMillis();
        log.info("该用户的请求已经处理完毕，请求花费的时间为：" + (endTime - startTime)/1000 + "s");
        return collect;
    }

    @Override
    public Object getSourceAll(String startDate, String endDate) {
        long startTime = System.currentTimeMillis();
        List<Object> itemList = getItemList("source/all/a", "pv_count,visit_count,visitor_count,new_visitor_count,ip_count,avg_visit_time,avg_visit_pages", startDate, endDate);
        List<Object> titleList = (List<Object>) itemList.get(0);
        List<Object> dataList = (List<Object>) itemList.get(1);
        List<TongjiSourceAllDTO> data = new ArrayList<>();
        for (int i=0; i<titleList.size(); i++) {
            List<Object> element = (List<Object>) dataList.get(i);
            TongjiSourceAllDTO pvCount = TongjiSourceAllDTO.builder()
                    .sourceName(((Map<String, Object>)(((List<Object>) titleList.get(i)).get(0))).get("name").toString())
                    .elementName("浏览量(PV)")
                    .value(element.get(0).equals("--") ? 0 : element.get(0))
                    .build();
            data.add(pvCount);

            TongjiSourceAllDTO visitCount = TongjiSourceAllDTO.builder()
                    .sourceName(((Map<String, Object>)(((List<Object>) titleList.get(i)).get(0))).get("name").toString())
                    .elementName("访问次数")
                    .value(element.get(1).equals("--") ? 0 : element.get(1))
                    .build();
            data.add(visitCount);

            TongjiSourceAllDTO visitorCount = TongjiSourceAllDTO.builder()
                    .sourceName(((Map<String, Object>)(((List<Object>) titleList.get(i)).get(0))).get("name").toString())
                    .elementName("访客数(UV)")
                    .value(element.get(2).equals("--") ? 0 : element.get(2))
                    .build();
            data.add(visitorCount);

            TongjiSourceAllDTO newVisitorCount = TongjiSourceAllDTO.builder()
                    .sourceName(((Map<String, Object>)(((List<Object>) titleList.get(i)).get(0))).get("name").toString())
                    .elementName("新访客数")
                    .value(element.get(3).equals("--") ? 0 : element.get(3))
                    .build();
            data.add(newVisitorCount);

            TongjiSourceAllDTO ipCount = TongjiSourceAllDTO.builder()
                    .sourceName(((Map<String, Object>)(((List<Object>) titleList.get(i)).get(0))).get("name").toString())
                    .elementName("IP数")
                    .value(element.get(4).equals("--") ? 0 : element.get(4))
                    .build();
            data.add(ipCount);

            TongjiSourceAllDTO avgVisitTime = TongjiSourceAllDTO.builder()
                    .sourceName(((Map<String, Object>)(((List<Object>) titleList.get(i)).get(0))).get("name").toString())
                    .elementName("平均访问时长")
                    .value(element.get(5).equals("--") ? 0 : element.get(5))
                    .build();
            data.add(avgVisitTime);

            TongjiSourceAllDTO avgVisitPages = TongjiSourceAllDTO.builder()
                    .sourceName(((Map<String, Object>)(((List<Object>) titleList.get(i)).get(0))).get("name").toString())
                    .elementName("平均访问页数")
                    .value(element.get(6).equals("--") ? 0 : element.get(6))
                    .build();
            data.add(avgVisitPages);
        }
        data = data.stream().sorted(Comparator.comparing(TongjiSourceAllDTO::getSourceName)).collect(Collectors.toList());
        long endTime = System.currentTimeMillis();
        log.info("该用户的请求已经处理完毕，请求花费的时间为：" + (endTime - startTime)/1000 + "s");
        return data;
    }

    @Override
    public Object getVisitToppage(String startDate, String endDate) {
        long startTime = System.currentTimeMillis();
        List<Object> itemList = getItemList("visit/toppage/a", "pv_count,visit_count,visitor_count,new_visitor_count,ip_count,avg_visit_time,avg_visit_pages", startDate, endDate);
        List<Object> urlList = (List<Object>) itemList.get(0);
        List<Object> metricsList = (List<Object>) itemList.get(1);
        List<TongjiVisitToppageDTO> data = new ArrayList<>();
        for (int i=0; i<urlList.size(); i++) {
            List<Object> element = (List<Object>) metricsList.get(i);
            TongjiVisitToppageDTO pvCount = TongjiVisitToppageDTO.builder()
                    .url(((Map<String, Object>)(((List<Object>) urlList.get(i)).get(0))).get("name").toString())
                    .metrics("浏览量(PV)")
                    .value(element.get(0).equals("--") ? 0 : element.get(0))
                    .build();
            data.add(pvCount);

            TongjiVisitToppageDTO visitorCount = TongjiVisitToppageDTO.builder()
                    .url(((Map<String, Object>)(((List<Object>) urlList.get(i)).get(0))).get("name").toString())
                    .metrics("访客数(UV)")
                    .value(element.get(1).equals("--") ? 0 : element.get(1))
                    .build();
            data.add(visitorCount);
        }
        data = data.stream().sorted(Comparator.comparing(TongjiVisitToppageDTO::getUrl)).collect(Collectors.toList());
        long endTime = System.currentTimeMillis();
        log.info("该用户的请求已经处理完毕，请求花费的时间为：" + (endTime - startTime)/1000 + "s");
        return data;
    }

    @Override
    public Object getVisitDistrict(String startDate, String endDate) {
        long startTime = System.currentTimeMillis();
        List<Object> itemList = getItemList("visit/district/a", "pv_count,visit_count,visitor_count", startDate, endDate);
        List<Object> urlList = (List<Object>) itemList.get(0);
        List<Object> metricsList = (List<Object>) itemList.get(1);
        List<TongjiVisitToppageDTO> data = new ArrayList<>();
        for (int i=0; i<urlList.size(); i++) {
            List<Object> element = (List<Object>) metricsList.get(i);
            TongjiVisitToppageDTO pvCount = TongjiVisitToppageDTO.builder()
                    .url(((Map<String, Object>)(((List<Object>) urlList.get(i)).get(0))).get("name").toString())
                    .metrics("浏览量(PV)")
                    .value(element.get(0).equals("--") ? 0 : element.get(0))
                    .build();
            data.add(pvCount);

            TongjiVisitToppageDTO visitCount = TongjiVisitToppageDTO.builder()
                    .url(((Map<String, Object>)(((List<Object>) urlList.get(i)).get(0))).get("name").toString())
                    .metrics("访问次数")
                    .value(element.get(1).equals("--") ? 0 : element.get(1))
                    .build();
            data.add(visitCount);

            TongjiVisitToppageDTO visitorCount = TongjiVisitToppageDTO.builder()
                    .url(((Map<String, Object>)(((List<Object>) urlList.get(i)).get(0))).get("name").toString())
                    .metrics("访客数(UV)")
                    .value(element.get(1).equals("--") ? 0 : element.get(1))
                    .build();
            data.add(visitorCount);
        }
        data = data.stream().sorted(Comparator.comparing(TongjiVisitToppageDTO::getUrl)).collect(Collectors.toList());
        long endTime = System.currentTimeMillis();
        log.info("该用户的请求已经处理完毕，请求花费的时间为：" + (endTime - startTime)/1000 + "s");
        return data;
    }

    public List<Object> getItemList(String method, String metrics, String startDate, String endDate) {
        Example refreshExample = new Example(SysVariables.class);
        Example.Criteria refreshCriteria = refreshExample.createCriteria();
        refreshCriteria.andEqualTo("sysKey", "baidu_access_token");
        SysVariables accessSysVariables = sysVariablesMapper.selectOneByExample(refreshExample);
        String url = "https://openapi.baidu.com/rest/2.0/tongji/report/getData?access_token=" + accessSysVariables.getSysValue() + "&site_id=15023739&method=" + method + "&start_date=" + startDate + "&end_date=" + endDate + "&metrics=" + metrics;
        String response = HttpUtil.get(url);
        JSONObject jsonObject = JSONUtil.parseObj(response);
        Object result = jsonObject.get("result");
        JSONObject resultObject = JSONUtil.parseObj(result);
        Object items = resultObject.get("items");
        List<Object> itemList = (List<Object>) items;
        return itemList;
    }
}
