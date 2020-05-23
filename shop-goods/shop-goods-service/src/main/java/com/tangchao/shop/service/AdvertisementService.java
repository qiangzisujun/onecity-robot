package com.tangchao.shop.service;

import com.github.pagehelper.PageInfo;
import com.tangchao.shop.pojo.AdvertGroup;
import com.tangchao.shop.pojo.Annc;
import com.tangchao.shop.pojo.CmsAdvert;
import com.tangchao.shop.pojo.MailConf;

import java.util.List;
import java.util.Map;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2019/11/9 19:46
 */
public interface AdvertisementService {

    PageInfo selectList(Long userId,Integer pageNo,Integer pageSize);

    void updateBannerInfo(Long userId, AdvertGroup advertGroup);

    void deleteBannerInfo(Long userId, Map<String, Object> data);

    void insertBannerInfo(Long userId, AdvertGroup advertGroup);

    PageInfo getCmsAdvertList(Long userId, Integer pageNo, Integer pageSize);

    void updateCmsAdvertList(Long userId, CmsAdvert advert);

    void deleteCmsAdvertInfo(Long userId, Map<String, Object> data);

    void insertCmsAdvertInfo(Long userId, CmsAdvert advert);

    PageInfo getAnnouncementList(Long userId, Integer pageNo, Integer pageSize);

    void updateAnnouncement(Long userId, Annc annc);

    void deleteAnnouncement(Long userId, Map<String, Object> data);

    void insertAnnouncement(Long userId, Annc annc);

    PageInfo getMailboxList(Long userId,Integer pageNo, Integer pageSize);

    void updateMailboxInfo(Long userId, MailConf mail);

    void deleteMailboxInfo(Long userId, Map<String, Object> data);

    void insertMailboxInfo(Long userId, MailConf mail);
}
