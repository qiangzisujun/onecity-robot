package com.tangchao.shop.serviceImpl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tangchao.common.enums.ExceptionEnum;
import com.tangchao.common.exception.CustomerException;
import com.tangchao.shop.mapper.AdvertGroupMapper;
import com.tangchao.shop.mapper.AnncMapper;
import com.tangchao.shop.mapper.CmsAdvertMapper;
import com.tangchao.shop.mapper.MailConfMapper;
import com.tangchao.shop.pojo.AdvertGroup;
import com.tangchao.shop.pojo.Annc;
import com.tangchao.shop.pojo.CmsAdvert;
import com.tangchao.shop.pojo.MailConf;
import com.tangchao.shop.service.AdvertisementService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2019/11/9 19:53
 */
@Service
public class AdvertisementServiceImpl implements AdvertisementService {

    @Autowired
    private CmsAdvertMapper cmsAdvertMapper;

    @Autowired
    private AdvertGroupMapper groupMapper;

    @Autowired
    private AnncMapper anncMapper;

    @Autowired
    private MailConfMapper mailConfMapper;

    @Override
    public PageInfo selectList(Long userId,Integer pageNo,Integer pageSize) {
        if (userId == null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        PageHelper.startPage(pageNo,pageSize);;
        List<AdvertGroup> advertGroupList=cmsAdvertMapper.selectList();
        advertGroupList=advertGroupList.stream().sorted(Comparator.comparing(AdvertGroup::getCreateTime).reversed()).collect(Collectors.toList());
        return new PageInfo(advertGroupList);
    }

    @Override
    public void updateBannerInfo(Long userId, AdvertGroup advertGroup) {
        if (userId == null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        if (advertGroup==null){
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }
        advertGroup.setLastModifyId(userId);
        advertGroup.setLastModifyTime(new Date());
        int count=groupMapper.updateByPrimaryKeySelective(advertGroup);
        if (count!=1){
            throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
        }
    }

    @Override
    public void deleteBannerInfo(Long userId, Map<String, Object> data) {
        if (userId == null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }

        String id=data.get("id").toString();
        if (StringUtils.isBlank(id)){
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }

        AdvertGroup group=groupMapper.selectByPrimaryKey(id);
        if (group==null){
            throw new CustomerException(ExceptionEnum.CONFIG_NOT_FOND);
        }
        group.setLastModifyTime(new Date());
        group.setLastModifyId(userId);
        group.setFlag(-1);
        int count=groupMapper.updateByPrimaryKeySelective(group);
        if (count!=1){
            throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
        }
    }

    @Override
    public void insertBannerInfo(Long userId, AdvertGroup advertGroup) {
        if (userId == null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }

        if (StringUtils.isBlank(advertGroup.getGroupCode())||StringUtils.isBlank(advertGroup.getGroupName())){
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }
        advertGroup.setFlag(0);
        advertGroup.setLastModifyId(userId);
        advertGroup.setLastModifyTime(new Date());
        advertGroup.setCreateTime(new Date());
        advertGroup.setCreateId(userId);
        int count=groupMapper.insertSelective(advertGroup);
        if (count!=1){
            throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
        }
    }

    @Override
    public PageInfo getCmsAdvertList(Long userId, Integer pageNo, Integer pageSize) {
        if (userId == null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }

        PageHelper.startPage(pageNo,pageSize);
        List<CmsAdvert> advertGroupList=cmsAdvertMapper.selectAdvertList();
        advertGroupList=advertGroupList.stream().sorted(Comparator.comparing(CmsAdvert::getAdSort).reversed()).collect(Collectors.toList());
        return new PageInfo(advertGroupList);
    }

    @Override
    public void updateCmsAdvertList(Long userId, CmsAdvert advert) {
        if (userId == null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        CmsAdvert cmsAdvert=cmsAdvertMapper.selectByPrimaryKey(advert.getId());
        if (cmsAdvert==null){
            throw new CustomerException(ExceptionEnum.CONFIG_NOT_FOND);
        }
        advert.setLastModifyId(userId);
        advert.setLastModifyTime(new Date());
        int count=cmsAdvertMapper.updateByPrimaryKeySelective(advert);
        if (count!=1){
            throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
        }
    }

    @Override
    public void deleteCmsAdvertInfo(Long userId, Map<String, Object> data) {
        if (userId == null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }

        String id=data.get("id").toString();
        if (StringUtils.isBlank(id)){
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }

        CmsAdvert group=cmsAdvertMapper.selectByPrimaryKey(id);
        if (group==null){
            throw new CustomerException(ExceptionEnum.CONFIG_NOT_FOND);
        }
        group.setLastModifyTime(new Date());
        group.setLastModifyId(userId);
        group.setFlag(-1);
        int count=cmsAdvertMapper.updateByPrimaryKeySelective(group);
        if (count!=1){
            throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
        }
    }

    @Override
    public void insertCmsAdvertInfo(Long userId, CmsAdvert advert) {
        if (userId == null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        advert.setLastModifyTime(new Date());
        advert.setLastModifyId(userId);
        advert.setFlag(0);
        advert.setCreateId(userId);
        advert.setCreateTime(new Date());
        int count=cmsAdvertMapper.insertSelective(advert);
        if (count!=1){
            throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
        }
    }

    @Override
    public PageInfo getAnnouncementList(Long userId, Integer pageNo, Integer pageSize) {
        /*if (userId == null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }*/
        PageHelper.startPage(pageNo,pageSize);
        Annc annc=new Annc();
        annc.setFlag(0);
        List<Annc> list=anncMapper.select(annc);
        list=list.stream().sorted(Comparator.comparing(Annc::getCreateTime).reversed()).collect(Collectors.toList());
        return new PageInfo(list);
    }

    @Override
    public void updateAnnouncement(Long userId,Annc annc) {
        if (userId == null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        Annc announcement=anncMapper.selectByPrimaryKey(annc.getId());
        if (announcement==null){
            throw new CustomerException(ExceptionEnum.CONFIG_NOT_FOND);
        }
        annc.setLastModifyId(userId);
        annc.setLastModifyTime(new Date());
        int count=anncMapper.updateByPrimaryKeySelective(annc);
        if (count!=1){
            throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
        }
    }

    @Override
    public void deleteAnnouncement(Long userId, Map<String, Object> data) {
        if (userId == null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        String id=data.get("id").toString();
        Annc announcement=anncMapper.selectByPrimaryKey(id);
        if (announcement==null){
            throw new CustomerException(ExceptionEnum.CONFIG_NOT_FOND);
        }
        announcement.setFlag(-1);
        announcement.setLastModifyTime(new Date());
        announcement.setLastModifyId(userId);
        int count=anncMapper.updateByPrimaryKeySelective(announcement);
        if (count!=1){
            throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
        }
    }

    @Override
    public void insertAnnouncement(Long userId, Annc annc) {
        if (userId == null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        if (annc==null){
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }
        annc.setFlag(0);
        annc.setLastModifyId(userId);
        annc.setLastModifyTime(new Date());
        annc.setCreateId(userId);
        annc.setCreateTime(new Date());
        int count=anncMapper.insertSelective(annc);
        if (count!=1){
            throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
        }
    }

    @Override
    public PageInfo getMailboxList(Long userId,Integer pageNo, Integer pageSize) {
        if (userId == null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        PageHelper.startPage(pageNo,pageSize);
        MailConf mail=new MailConf();
        mail.setFlag(0);
        List<MailConf> list=mailConfMapper.select(mail);
        list=list.stream().sorted(Comparator.comparing(MailConf::getCreateTime).reversed()).collect(Collectors.toList());
        return new PageInfo(list);
    }

    @Override
    public void updateMailboxInfo(Long userId, MailConf mail) {
        if (userId == null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        MailConf mailConf=mailConfMapper.selectByPrimaryKey(mail.getId());
        if (mailConf==null){
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }
        mail.setLastModifyId(userId);
        mail.setLastModifyTime(new Date());
        int count=mailConfMapper.updateByPrimaryKeySelective(mail);
        if (count!=1){
            throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
        }
    }

    @Override
    public void deleteMailboxInfo(Long userId, Map<String, Object> data) {
        if (userId == null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        String id=data.get("id").toString();
        MailConf mailConf=mailConfMapper.selectByPrimaryKey(id);
        if (mailConf==null){
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }
        mailConf.setFlag(-1);
        mailConf.setLastModifyId(userId);
        mailConf.setLastModifyTime(new Date());
        int count=mailConfMapper.updateByPrimaryKeySelective(mailConf);
        if (count!=1){
            throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
        }
    }

    @Override
    public void insertMailboxInfo(Long userId, MailConf mail) {
        if (userId == null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        mail.setFlag(0);
        mail.setLastModifyId(userId);
        mail.setLastModifyTime(new Date());
        mail.setCreateId(userId);
        mail.setCreateTime(new Date());
        int count=mailConfMapper.insertSelective(mail);
        if (count!=1){
            throw new CustomerException(ExceptionEnum.OPERATING_FAIL);
        }
    }
}
