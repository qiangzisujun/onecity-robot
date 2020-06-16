package com.tangchao.shop.serviceImpl;

import cn.hutool.json.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tangchao.common.enums.ExceptionEnum;
import com.tangchao.common.exception.CustomerException;
import com.tangchao.common.utils.ExportExcelUtil;
import com.tangchao.common.utils.IdWorker;
import com.tangchao.common.vo.PageResult;
import com.tangchao.shop.utils.ImportExcelUtils;
import com.tangchao.shop.utils.IsNullUtils;
import com.tangchao.shop.utils.UploadFile;
import com.tangchao.shop.dto.GoodsStageDTO;
import com.tangchao.shop.dto.ShopGoodsDTO;
import com.tangchao.shop.mapper.*;
import com.tangchao.shop.params.ShopGoodsParam;
import com.tangchao.shop.pojo.*;
import com.tangchao.shop.service.ShopGoodsService;
import com.tangchao.shop.vo.GoodsStageVO;
import com.tangchao.shop.vo.GoodsTypeVO;
import com.tangchao.shop.vo.ShopGoodsVO;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
        public class ShopGoodsServiceImpl implements ShopGoodsService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String KEY_PREFIX = "goodsType:uid";

    @Autowired
    private ShopGoodsMapper goodsMapper;

    @Autowired
    private ShopOrderDetailMapper shopOrderDetailMapper;

    @Autowired
    private ShopSpecParamMapper shopSpecParamMapper;

    @Autowired
    private GoodsStageMapper goodsStageMapper;

    @Autowired
    private GoodsFavouriteMapper goodsFavouriteMapper;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private ShopOrderMapper shopOrderMapper;

    @Autowired
    private ShopSpecificationMapper shopSpecificationMapper;

    @Autowired
    private ShopSpecGroupMapper shopSpecGroupMapper;

    @Autowired
    private ShopGroupValueMapper shopGroupValueMapper;

    @Autowired
    private CustomerPurchasesMapper customerPurchasesMapper;


    @Override
    public List<ShopGoodsVO> getShopGoodsByPage(Integer page, Integer rows, Long cid1) {
        //分页
        PageHelper.startPage(page, rows);
        ShopGoods goods = new ShopGoods();
        goods.setEnable(true);
        goods.setSaleable(true);
        if (cid1 != null) {
            goods.setCid(cid1);
        }
        List<ShopGoods> list = goodsMapper.select(goods);

        List<ShopGoodsVO> shopList = new ArrayList<>();
        for (ShopGoods g : list) {
            ShopGoodsVO shopGoodsVO = new ShopGoodsVO();
            shopGoodsVO.setId(g.getId());
            shopGoodsVO.setCid(g.getCid());
            shopGoodsVO.setImages(g.getImages().split(",")[0].toString());//获取第一个图片
            shopGoodsVO.setPackingList(g.getPackingList());
            shopGoodsVO.setStock(g.getStock());
            shopGoodsVO.setTitle(g.getTitle());
            shopGoodsVO.setSubTitle(g.getSubTitle());
            ShopOrderDetail shopOrder = new ShopOrderDetail();
            shopOrder.setGoodsId(g.getId());
            Integer num = shopOrderDetailMapper.selectCount(shopOrder);
            shopGoodsVO.setSalesVolume(num);
            shopList.add(shopGoodsVO);
        }
        return shopList;
    }

    @Override
    public ShopGoodsVO getShopGoodsByGid(Long goodsId) {
        ShopGoods goods = new ShopGoods();
        goods.setId(goodsId);
        goods = goodsMapper.selectOne(goods);
        ShopGoodsVO shopGoodsVO = new ShopGoodsVO();
        shopGoodsVO.setId(goods.getId());
        shopGoodsVO.setCid(goods.getCid());
        shopGoodsVO.setImages(goods.getImages());//需要处理成json
        shopGoodsVO.setPackingList(goods.getPackingList());
        shopGoodsVO.setStock(goods.getStock());
        shopGoodsVO.setTitle(goods.getTitle());
        shopGoodsVO.setPrice(goods.getPrice());
        shopGoodsVO.setSubTitle(goods.getSubTitle());
        shopGoodsVO.setIntegral(goods.getIntegral());
        //规格
        ShopSpecParam param = new ShopSpecParam();
        param.setGoodsId(goods.getId());
        shopGoodsVO.setSpecParams(shopSpecParamMapper.select(param));
        return shopGoodsVO;
    }

    @Override
    public PageResult<GoodsStageVO> selectGoodsList(GoodsStageDTO stageDTO,Long userCode) {

        //long start=System.currentTimeMillis();
        boolean flag=false;
        if (userCode == null && userCode == null) {
            flag=false;
        }else{
            flag=true;
        }
        // 设置分页
        PageHelper.startPage(stageDTO.getPageNo(), stageDTO.getPageSize());
        // 设置排序
        String orderBy = stageDTO.getOrderBy();
        if (StringUtils.isEmpty(orderBy) || "default".equals(orderBy)) {// 默认人气排序
            PageHelper.orderBy("goods_hot desc,id asc");
        } else if (orderBy.equals("priceDesc")) {
            PageHelper.orderBy("goods_price desc"); //(价值降序)
        } else if (orderBy.equals("priceAsc")) {
            PageHelper.orderBy("goods_price asc");//(价值升序)
        } else if (orderBy.equals("new")) {
            PageHelper.orderBy("sell_start_time desc");//(新品)
        } else if (orderBy.equals("full")) {
            PageHelper.orderBy("(buy_index/buy_size) desc");//(即将揭晓)
        }
        Example example = new Example(GoodsStage.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andNotEqualTo("flag", -1);
        criteria.andEqualTo("isAward", 0);
        criteria.andNotEqualTo("isActivity", 1);
        // 模糊搜索
        if (!StringUtils.isEmpty(stageDTO.getSearchKey())) {
            criteria.andLike("goodsName", "%" + stageDTO.getSearchKey() + "%");
        }
        if (!StringUtils.isEmpty(stageDTO.getTypeId())) {
            criteria.andEqualTo("typeId", stageDTO.getTypeId());
        }
        List<GoodsStage> list = goodsStageMapper.selectByExample(example);
        List<GoodsStageVO> resultList = new ArrayList<>();
        for (GoodsStage stage : list) {
            GoodsStageVO vo = new GoodsStageVO();
            vo.setBuyIndex(stage.getBuyIndex());
            vo.setBuyNum(stage.getBuyNum());
            vo.setBuyPrice(stage.getBuyPrice());
            vo.setBuySize(stage.getBuySize());
            vo.setGoodsName(stage.getGoodsName());
            vo.setGoodsNo(stage.getGoodsNo());
            vo.setGoodsPicture(stage.getGoodsPicture());
            vo.setId(stage.getId());
            vo.setGoodsPrice(stage.getGoodsPrice());
            if (flag){
                GoodsFavourite favourite=new GoodsFavourite();
                favourite.setCustomerCode(userCode);
                favourite.setGoodsNo(stage.getGoodsNo());
                List<GoodsFavourite> favouriteList=goodsFavouriteMapper.select(favourite);
                if (favouriteList.isEmpty()){
                    vo.setIsCollection(0);
                }else{
                    vo.setIsCollection(1);
                }
            }
            resultList.add(vo);
        }
        PageInfo<GoodsStageVO> pageInfo = new PageInfo<>(resultList);
        //long end=System.currentTimeMillis();
        //System.out.println(end-start);
        return new PageResult<>(pageInfo.getTotal(), resultList);
    }

    @Override
    public List<GoodsTypeVO> queryGoodsTypeList() {
        String key = KEY_PREFIX;
        String haskey = "haskey";
        List<GoodsTypeVO>  goodsType = goodsStageMapper.selectGoodsTypeList();
        /*BoundHashOperations<String, Object, Object> operation = redisTemplate.boundHashOps(key);
        if (!redisTemplate.hasKey(key)) {
            //查询数据库
            goodsType = goodsStageMapper.selectGoodsTypeList();
            //新增
            operation.put(haskey, JsonUtils.serialize(goodsType));
        } else {
            //获取redis中的分类
            goodsType = JsonUtils.parseList(operation.values().toString().substring(1, operation.values().toString().length() - 1), GoodsTypeVO.class);
        }*/
        return goodsType;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity add(Long userId, ShopGoodsParam shopGoodsParam) {
        if (userId == null) throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        ShopGoods shopGoods = new ShopGoods();
        BeanUtils.copyProperties(shopGoodsParam, shopGoods);
        Integer count = goodsMapper.insertSelective(shopGoods);

        if (null!=shopGoodsParam.getSpecificationList()&&shopGoodsParam.getSpecificationList().size()>0){
            //添加规格信息
            for (ShopSpecification spec:shopGoodsParam.getSpecificationList()){
                spec.setGoodsId(shopGoods.getId());
                count+=shopSpecificationMapper.insertSelective(spec);
            }
        }

        if (count>=1) {
            return ResponseEntity.ok(shopGoods);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @Override
    public PageResult<ShopGoods> list(Long userId, Integer pageNo, Integer pageSize, String title,String typeId,Integer type) {
        if (userId == null) throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        Example example = new Example(ShopGoods.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("datalevel", 1);
        if (StringUtils.isNotBlank(title)) {
            criteria.andLike("title", "%"+title+"%");
        }
        if (StringUtils.isNoneBlank(typeId)){
            criteria.andEqualTo("goodsTypeId", typeId);
        }
        if (type!=null&&type.equals(1)) {
            criteria.andEqualTo("isSpecialPrice", 1);
        }
        if (type!=null&&type.equals(2)) {
            criteria.andEqualTo("isStrict", 1);
        }
        PageHelper.startPage(pageNo,pageSize);
        PageHelper.orderBy("last_update_time desc");
        List<ShopGoods> shopGoods = goodsMapper.selectByExample(example);

        // TODO: 2020/3/16 Aquan：添加shopGoods判断，为空时不执行下面规格组查询
        if (shopGoods.size()>0) {
            List<Long> shopIds = shopGoods.stream().map(s -> s.getId()).collect(Collectors.toList());
            Example example_spec = new Example(ShopSpecification.class);
            Example.Criteria criteria_spec = example_spec.createCriteria();
            criteria_spec.andEqualTo("status", 1);
            if (shopIds.size() > 0) {
                criteria_spec.andIn("goodsId", shopIds);
            }
            List<ShopSpecification> specificationList = shopSpecificationMapper.selectByExample(example_spec);
            if (specificationList.size() > 0) {
                Map<Long, List<ShopSpecification>> specMap = specificationList.stream().collect(Collectors.groupingBy(ShopSpecification::getGoodsId));
                shopGoods.forEach(s -> s.setSpecificationList(specMap.get(s.getId())));
            }
        }

    /*    if (specificationList.size()>0){
            Map<Long,List<ShopSpecification>> groupMapList= specificationList.stream().collect(Collectors.groupingBy(ShopSpecification::getGoodsId));
            List<Long> groupId=groupMapList.keySet().stream().collect(Collectors.toList());
            Example example_group = new Example(ShopSpecGroup.class);
            Example.Criteria criteria_group = example_group.createCriteria();
            criteria_group.andEqualTo("status", 1);
            if (shopIds.size()>0) {
                criteria_group.andIn("goodsId", groupId);
            }
            List<ShopSpecGroup> groupList = specGroupMapper.selectByExample(example_group);
            Map<Long,List<ShopSpecGroup>> groupMap= groupList.stream().collect(Collectors.groupingBy(ShopSpecGroup::getId));
            shopGoods.forEach(s -> s.setSpecGroups(groupMap.get(s.getId())));
        }*/


        PageInfo<ShopGoods> goodsPageInfo = new PageInfo<>(shopGoods);
        return new PageResult<>(goodsPageInfo.getTotal(),shopGoods);
    }

    @Override
    public ResponseEntity standUpDown(Long userId, Long id, Boolean operate) {
        if (userId == null) throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        ShopGoods shopGoods = new ShopGoods();
        shopGoods.setId(id);
        shopGoods.setSaleable(operate);
        Integer count = goodsMapper.updateByPrimaryKeySelective(shopGoods);
        if (count.toString().equals("1")) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @Override
    public ResponseEntity isHome(Long userId, Long id, Integer operate) {
        if (userId == null) throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        ShopGoods shopGoods = new ShopGoods();
        shopGoods.setId(id);
        shopGoods.setIsHome(operate);
        Integer count = goodsMapper.updateByPrimaryKeySelective(shopGoods);
        if (count.toString().equals("1")) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }


    @Override
    public ResponseEntity getBy(Long userId, Long id) {
        ShopGoodsDTO shopGoodsDTO = new ShopGoodsDTO();
        ShopGoods shopGoods = goodsMapper.selectByPrimaryKey(id);
        BeanUtils.copyProperties(shopGoods, shopGoodsDTO);
        return ResponseEntity.ok(shopGoodsDTO);
    }

    @Override
    public ResponseEntity deleteBy(Long userId, Long id) {
        if (userId == null) throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        ShopGoods shopGoods = new ShopGoods();
        shopGoods.setId(id);
        shopGoods.setDatalevel(0);
        Integer count = goodsMapper.updateByPrimaryKeySelective(shopGoods);
        if (count.toString().equals("1")) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity updateBy(Long userId, ShopGoodsParam shopGoodsParam) {
        if (userId == null) throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        if (StringUtils.isBlank(shopGoodsParam.getId().toString())) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        ShopGoods shopGoods = new ShopGoods();
        BeanUtils.copyProperties(shopGoodsParam, shopGoods);
        Integer count = goodsMapper.updateByPrimaryKeySelective(shopGoods);


        Example example = new Example(ShopSpecification.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("status",1);
        criteria.andEqualTo("goodsId", shopGoods.getId());
        List<ShopSpecification>  oldList=shopSpecificationMapper.selectByExample(example);

        List<Long>  oldIds=oldList.stream().map(s->s.getId()).collect(Collectors.toList());

        if (null!=shopGoodsParam.getSpecificationList()&&shopGoodsParam.getSpecificationList().size()>0){


            //新增的(包含增删改)
            Map<Boolean,List<ShopSpecification>> distinguish=shopGoodsParam.getSpecificationList().stream().collect(Collectors.partitioningBy(t->t.getId()>0));

            List<ShopSpecification> addShopSpecificationList=distinguish.get(false);//新增

            List<ShopSpecification> updateShopSpecificationList=distinguish.get(true);//修改

            //新增
            if(CollectionUtils.isNotEmpty(addShopSpecificationList)){
                for (ShopSpecification add:addShopSpecificationList){
                    add.setGoodsId(shopGoods.getId());
                    count+=shopSpecificationMapper.insertSelective(add);
                }
            }

            //修改
            if (CollectionUtils.isNotEmpty(updateShopSpecificationList)){
                for (ShopSpecification spec:updateShopSpecificationList){
                    oldIds.remove(spec.getId());
                }

                for (ShopSpecification spec:updateShopSpecificationList){
                        Example example_update = new Example(ShopSpecification.class);
                        Example.Criteria criteria_update = example_update.createCriteria();
                        criteria_update.andEqualTo("id", spec.getId());
                        count+=shopSpecificationMapper.updateByExampleSelective(spec,example_update);
                }

            }

            //删除
            if (CollectionUtils.isNotEmpty(oldIds)){
                count+=shopSpecificationMapper.deleteShopSpecification(oldIds);
            }

        }else{
            if (CollectionUtils.isNotEmpty(oldIds)){
                count+=shopSpecificationMapper.deleteShopSpecification(oldIds);
            }
        }
        if (count>=1) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @Override
    public PageResult<ShopGoods> getList(Integer pageNo, Integer pageSize, String title) {
        Example example = new Example(ShopGoods.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("datalevel", 1);
        criteria.andEqualTo("saleable", 1);
        if (StringUtils.isNotBlank(title)) {
            criteria.andLike("title", "%" + title + "%");
        }
        PageHelper.startPage(pageNo,pageSize);
        PageHelper.orderBy("sort desc");
        List<ShopGoods> shopGoods = goodsMapper.selectByExample(example);
        for ( ShopGoods shopGood : shopGoods) {
            String images = shopGood.getImages();
            String[] split = images.split(",");
            shopGood.setImages(split[0]);
        }
        // TODO: 2020/3/16 Aquan：添加shopGoods判断，为空时不执行下面规格组查询
        if (shopGoods.size()>0) {
            List<Long> shopIds = shopGoods.stream().map(s -> s.getId()).collect(Collectors.toList());
            Example example_spec = new Example(ShopSpecification.class);
            Example.Criteria criteria_spec = example_spec.createCriteria();
            criteria_spec.andEqualTo("status", 1);
            if (shopIds.size() > 0) {
                criteria_spec.andIn("goodsId", shopIds);
            }
            List<ShopSpecification> specificationList = shopSpecificationMapper.selectByExample(example_spec);
            if (specificationList.size() > 0) {
                Map<Long, List<ShopSpecification>> specMap = specificationList.stream().collect(Collectors.groupingBy(ShopSpecification::getGoodsId));
                shopGoods.forEach(s -> s.setSpecificationList(specMap.get(s.getId())));
            }
        }
        PageInfo<ShopGoods> goodsPageInfo = new PageInfo<>(shopGoods);
        return new PageResult<>(goodsPageInfo.getTotal(),shopGoods);
    }

    @Override
    public ResponseEntity getInfo(Long id) {
        ShopGoodsDTO shopGoodsDTO = new ShopGoodsDTO();
        ShopGoods shopGoods = goodsMapper.selectByPrimaryKey(id);
        BeanUtils.copyProperties(shopGoods, shopGoodsDTO);

        ShopSpecification spec=new ShopSpecification();
        spec.setGoodsId(shopGoods.getId());
        spec.setStatus(1);
        List<ShopSpecification> list=shopSpecificationMapper.select(spec);
        shopGoodsDTO.setSpecificationList(list);

        // TODO: 2020/3/11 规格组
        // Integer goodsTypeId = shopGoods.getGoodsTypeId();//分类ID
        // Example example = new Example(ShopSpecGroup.class);
        // Example.Criteria criteria = example.createCriteria();
        // criteria.andEqualTo("status", 1);
        // criteria.andEqualTo("typeId", goodsTypeId);
        // List<ShopSpecGroup> shopSpecGroupList = shopSpecGroupMapper.selectByExample(example);
        // for ( ShopSpecGroup shopSpecGroup : shopSpecGroupList) {
        //     Long shopSpecGroupId = shopSpecGroup.getId();
        //     Example valueExample = new Example(ShopGroupValue.class);
        //     Example.Criteria valueCriteria = valueExample.createCriteria();
        //     valueCriteria.andEqualTo("status", 1);
        //     valueCriteria.andEqualTo("groupId", shopSpecGroupId);
        //     List<ShopGroupValue> shopGroupValues = shopGroupValueMapper.selectByExample(valueExample);
        //     shopSpecGroup.setShopGroupValues(shopGroupValues);
        // }
        // shopGoodsDTO.setSpecGroupList(shopSpecGroupList);

        // TODO: 2020/3/18 Aquan 判断是否有规格
        if (StringUtils.isNotBlank(shopGoods.getSpecList())) {
            String data = "{\"info\": " + shopGoods.getSpecList() + "}";
            JSONObject jsonObject = new JSONObject(data);
            shopGoodsDTO.setSpecListObject(jsonObject);
        }

        return ResponseEntity.ok(shopGoodsDTO);
    }

    @Override
    public PageResult<ShopGoods> getSpecialGoodsList(Integer pageNo, Integer pageSize, Integer type) {
        Example example = new Example(ShopGoods.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("datalevel", 1);
        criteria.andEqualTo("saleable", 1);
        // TODO: 2020/2/28 类别：1限时特价 2热销
        if (type == 1) {
            criteria.andEqualTo("isSpecialPrice", 1);
        }
        if (type == 2) {
            criteria.andEqualTo("isSellWell", 1);
        }
        if (type == 3) {
            criteria.andEqualTo("isStrict", 1);
        }
        if (type == 4) {
            criteria.andEqualTo("isHome", 1);
        }
        PageHelper.startPage(pageNo,pageSize);
        PageHelper.orderBy("sort desc");
        List<ShopGoods> shopGoods = goodsMapper.selectByExample(example);
        for ( ShopGoods shopGood : shopGoods) {
            String images = shopGood.getImages();
            String[] split = images.split(",");
            shopGood.setImages(split[0]);
        }
        // TODO: 2020/3/16 Aquan：添加shopGoods判断，为空时不执行下面规格组查询
        if (shopGoods.size()>0) {
            List<Long> shopIds = shopGoods.stream().map(s -> s.getId()).collect(Collectors.toList());
            Example example_spec = new Example(ShopSpecification.class);
            Example.Criteria criteria_spec = example_spec.createCriteria();
            criteria_spec.andEqualTo("status", 1);
            if (shopIds.size() > 0) {
                criteria_spec.andIn("goodsId", shopIds);
            }
            List<ShopSpecification> specificationList = shopSpecificationMapper.selectByExample(example_spec);
            if (specificationList.size() > 0) {
                Map<Long, List<ShopSpecification>> specMap = specificationList.stream().collect(Collectors.groupingBy(ShopSpecification::getGoodsId));
                shopGoods.forEach(s -> s.setSpecificationList(specMap.get(s.getId())));
            }
        }

        PageInfo<ShopGoods> goodsPageInfo = new PageInfo<>(shopGoods);
        return new PageResult<>(goodsPageInfo.getTotal(),shopGoods);
    }

    @Override
    public int getGoodsLimitNumByGoodsNo(Long userCode, String goodsNo) {
        if (userCode == null) throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        CustomerPurchases purchases=new CustomerPurchases();
        purchases.setUserCode(userCode);
        purchases.setGoodsNo(goodsNo);
        purchases=customerPurchasesMapper.selectOne(purchases);
        if (purchases!=null){
            return purchases.getNum();
        }
        return 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity batchImport(MultipartFile[] files, HttpServletRequest request) {
        /*上传路径*/
        String path = System.getProperty("user.dir")  + "\\fileExcle\\" + files[0].getOriginalFilename();
        try {
            boolean status = UploadFile.fileUpLoad(files, request, path);
            if (!status) {
                // 文件上传失败！
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("文件上传失败！");
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            log.error(e.getMessage());
        }
        Workbook workbook = null;  //工作簿
        Sheet sheet = null;         //工作表
        String[] headers = null;   //表头信息

        try {
            workbook = ImportExcelUtils.createWorkbook(path);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        sheet = ImportExcelUtils.getSheet(workbook, 0);
        List<Object[]> oList = ImportExcelUtils.listFromSheet(sheet);
        if (!IsNullUtils.isEmpty(oList)) {
            headers = Arrays.asList(oList.get(0)).toArray(new String[0]);
            // if (!headers[0].replaceAll(" ", "").equals("ID")) {
            //     return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("请导入正确的模板");
            // }
        }

        List<ShopGoods> shopGoodsList = new ArrayList<>();
        if (!IsNullUtils.isEmpty(oList.get(1))) {
            for (int s = 1; s < oList.size(); s++) {
                Object[] rows = null;
                rows = Arrays.asList(oList.get(s)).toArray(new Object[0]);
                ShopGoods shopGoods = new ShopGoods();
                shopGoods.setTitle(rows[0].toString());
                shopGoods.setSort(Integer.valueOf(rows[1].toString()));
                shopGoods.setSalesVolume(Integer.valueOf(rows[2].toString()));
                shopGoods.setGoodsTypeId(Integer.valueOf(rows[3].toString()));
                shopGoods.setStock(Integer.valueOf(rows[4].toString()));
                BigDecimal priceForme = new BigDecimal(rows[5].toString());
                BigDecimal priceFormeMultiply = priceForme.multiply(new BigDecimal("100"));
                shopGoods.setPriceForme(priceFormeMultiply.longValue());
                BigDecimal price = new BigDecimal(rows[6].toString());
                BigDecimal priceMultiply = price.multiply(new BigDecimal("100"));
                shopGoods.setPrice(priceMultiply.longValue());
                shopGoods.setIntegral(Long.valueOf(rows[7].toString()));
                shopGoods.setCommission(new BigDecimal(rows[8].toString()));
                shopGoods.setDiscount(new BigDecimal(rows[9].toString()));
                shopGoods.setIsSpecialPrice(Integer.valueOf(rows[10].toString()));
                shopGoods.setIsSellWell(Integer.valueOf(rows[11].toString()));
                shopGoods.setIsReturn(Integer.valueOf(rows[12].toString()));
                shopGoods.setIsSpeed(Integer.valueOf(rows[13].toString()));
                shopGoods.setIsStrict(Integer.valueOf(rows[14].toString()));
                shopGoodsList.add(shopGoods);
            }

            // log.warn(shopGoodsList.toString());
            if (shopGoodsList.size() > 0) {
                for (ShopGoods c : shopGoodsList) {
                    int i = goodsMapper.insertSelective(c);
                    if (i<=0){
                        throw new RuntimeException("导入异常请检查参数");
                    }
                }
            }

        }
        return ResponseEntity.status(HttpStatus.OK).body("导入成功");
    }


    @Override
    public void batchExport(HttpServletResponse response) {
        List<Map<String,Object>> shopGoods = goodsMapper.findAll();
        String[] headers = { "商品名称", "商品排序", "虚拟购买量","商品类型", "商品库存", "商品原价", "商品售价", "可获得抽奖数", "分销佣金", "可抵扣优惠券金额", "限时秒杀", "热销榜", "七天可退换", "急速发货", "严选产品"};
        String[] keys = { "title", "sort","sales_volume", "goods_type_id", "stock", "price_forme", "price", "integral", "commission", "discount", "is_special_price", "is_sell_well", "is_return", "is_speed", "is_strict"};
        Integer[] widths = { 100 };
        try {
            ExportExcelUtil.exportExcel("商品表", headers, keys, widths, shopGoods, response, null);
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void batchSetShop(Map<String, Object> data) {
        List<String> ids= (List<String>) data.get("ids");
        String status=data.get("type").toString();
        Integer typeVal=Integer.valueOf(data.get("typeVal").toString());
        ShopGoods goods=new ShopGoods();
        if (status.equals("1")){//严选
            goods.setIsStrict(typeVal);
        }else if(status.equals("2")){//秒杀
            goods.setIsSpecialPrice(typeVal);
        }
        Example example=new Example(ShopGoods.class);
        example.createCriteria().andIn("id",ids);
        goodsMapper.updateByExampleSelective(goods,example);
    }


}
