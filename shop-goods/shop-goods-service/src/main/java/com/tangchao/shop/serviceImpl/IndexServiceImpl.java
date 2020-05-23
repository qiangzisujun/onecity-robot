package com.tangchao.shop.serviceImpl;

import com.github.pagehelper.PageHelper;
import com.tangchao.common.constant.AdvertGroupCodeConstant;
import com.tangchao.common.constant.ConfigkeyConstant;
import com.tangchao.shop.advice.DataSourceNames;
import com.tangchao.shop.annotation.DataSource;
import com.tangchao.shop.mapper.CmsAdvertMapper;
import com.tangchao.shop.mapper.UserConfMapper;
import com.tangchao.shop.pojo.CmsAdvert;
import com.tangchao.shop.pojo.UserConf;
import com.tangchao.shop.service.ConfService;
import com.tangchao.shop.service.IndexService;
import com.tangchao.shop.service.ShopBannerService;
import com.tangchao.shop.service.ShopCategoryService;
import com.tangchao.shop.vo.BannerAndCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class IndexServiceImpl implements IndexService {

    @Autowired
    private ShopBannerService bannerService;

    @Autowired
    private ShopCategoryService shopCategoryService;

    @Autowired
    private CmsAdvertMapper cmsAdvertMapper;

    @Autowired
    private ConfService confService;


    @Override
    public BannerAndCategory getBannerAndCategoryInfo() {
        BannerAndCategory bannerAndCategory = new BannerAndCategory();
        bannerAndCategory.setBanners(bannerService.getShopBanners().getItems());
        bannerAndCategory.setCategories(shopCategoryService.getShopCategoryListByPage().getItems());
        return bannerAndCategory;
    }

    @Override
    @DataSource(name = DataSourceNames.SECOND)
    public List<Map<String, Object>> getBannerList() {
        PageHelper.orderBy("ad_sort asc");
        CmsAdvert cms = new CmsAdvert();
        cms.setFlag(0);
        cms.setGroupCode(AdvertGroupCodeConstant.H5_INDEX_BANNER);
        List<CmsAdvert> list = cmsAdvertMapper.select(cms);
        List<Map<String, Object>> result = new ArrayList<>();
        for (CmsAdvert conf : list) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", conf.getId());
            map.put("image", conf.getAdImg());
            map.put("url", conf.getAdHref());
            result.add(map);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getNavigationList() {
        CmsAdvert cms = new CmsAdvert();
        cms.setFlag(0);
        cms.setGroupCode(AdvertGroupCodeConstant.H5_NAV_ICON);
        List<CmsAdvert> list = cmsAdvertMapper.select(cms);
        List<Map<String, Object>> result = new ArrayList<>();
        for (CmsAdvert conf : list) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", conf.getId());//导航id
            map.put("image", conf.getAdImg());//图片
            map.put("url", conf.getAdHref());//跳转链接
            map.put("iconDesc", conf.getAdDescribe());//跳转链接
            result.add(map);
        }
        return result;
    }

    @Override
    public Map<String, Object> getWebSiteStatus() {
        Map<String, Object> map=new HashMap<>();

        UserConf conf=confService.selectCmsValue("mobile.site.status");
        if (conf!=null){
            map.put("status",conf.getConfValue());
        }
        UserConf conf1=confService.selectCmsValue("mobile.site.status.img");
        if (conf1!=null){
            map.put("img",conf1.getConfValue());
        }
        return map;
    }

    @Override
    public Map<String, Object> getDownloadAPPURL() {
        Map<String,Object> resultMap=new HashMap<>();

        //ios下载地址
        UserConf iosDownloadURL=confService.selectCmsValue(ConfigkeyConstant.MOBILE_APP_IOS_DOWNLOAD_URL);
        if (null!=iosDownloadURL.getConfValue()){
            resultMap.put("iosDownloadURL",iosDownloadURL.getConfValue());
        }else {
            resultMap.put("iosDownloadURL","1");
        }

        //Android下载地址
        UserConf androidDownloadURL=confService.selectCmsValue(ConfigkeyConstant.MOBILE_APP_ANDROID_DOWNLOAD_URL);
        if (null!=androidDownloadURL.getConfValue()){
            resultMap.put("androidDownloadURL",androidDownloadURL.getConfValue());
        }else {
            resultMap.put("androidDownloadURL","1");
        }
        return resultMap;
    }


    @Override
    @DataSource(name = DataSourceNames.SECOND)
    public List<Map<String, Object>> getZSBannerList() {

        PageHelper.orderBy("ad_sort asc");
        CmsAdvert cms = new CmsAdvert();
        cms.setFlag(0);
        cms.setGroupCode(AdvertGroupCodeConstant.ZS_H5_INDEX_BANNER);
        List<CmsAdvert> list = cmsAdvertMapper.select(cms);
        List<Map<String, Object>> result = new ArrayList<>();
        for (CmsAdvert conf : list) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", conf.getId());
            map.put("image", conf.getAdImg());
            map.put("url", conf.getAdHref());
            result.add(map);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> showZSBannerList(String key) {
        CmsAdvert cms = new CmsAdvert();
        cms.setFlag(0);
        cms.setGroupCode(key);
        List<CmsAdvert> list = cmsAdvertMapper.select(cms);
        List<Map<String, Object>> result = new ArrayList<>();
        for (CmsAdvert conf : list) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", conf.getId());
            map.put("image", conf.getAdImg());
            map.put("url", conf.getAdHref());
            result.add(map);
        }
        return result;
    }

}
