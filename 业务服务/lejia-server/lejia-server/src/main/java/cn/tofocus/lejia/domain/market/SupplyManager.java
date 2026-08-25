package cn.tofocus.lejia.domain.market;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.core.data.TreeModel;
import cn.tofocus.core.exception.SysErrCode;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.page.PageParameter;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.dto.DtoEnhance;
import cn.tofocus.lejia.bean.dto.market.MktSupplyDetailInfo;
import cn.tofocus.lejia.bean.dto.market.MktSupplyGoodsInfo;
import cn.tofocus.lejia.bean.dto.market.MktSupplyInfo;
import cn.tofocus.lejia.bean.dto.market.MktSupplyOnList;
import cn.tofocus.lejia.bean.dto.market.MktSupplyPageDetail;
import cn.tofocus.lejia.bean.dto.market.MktSupplyParamDTO;
import cn.tofocus.lejia.bean.dto.market.MktSupplySpaceInfo;
import cn.tofocus.lejia.bean.dto.market.MktSupplyVendorInfo;
import cn.tofocus.lejia.bean.dto.market.SupplySendConfDTO;
import cn.tofocus.lejia.bean.entity.goods.MktGoods;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsSpace;
import cn.tofocus.lejia.bean.entity.market.MktGtype;
import cn.tofocus.lejia.bean.entity.market.MktSupply;
import cn.tofocus.lejia.bean.entity.sys.SysConfigEntity;
import cn.tofocus.lejia.bean.entity.sys.SysFarmer;
import cn.tofocus.lejia.bean.entity.sys.SysFarmerConfig;
import cn.tofocus.lejia.bean.entity.vendor.MktVendor;
import cn.tofocus.lejia.bean.enums.MType;
import cn.tofocus.lejia.bean.enums.PointType;
import cn.tofocus.lejia.bean.enums.SettlementMethodType;
import cn.tofocus.lejia.bean.enums.v5.FarmerType;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.goods.MktGoodsDao;
import cn.tofocus.lejia.dao.goods.MktGoodsSpaceDao;
import cn.tofocus.lejia.dao.market.MktGtypeDao;
import cn.tofocus.lejia.dao.market.MktSupplyDao;
import cn.tofocus.lejia.dao.sys.SysConfigDao;
import cn.tofocus.lejia.dao.sys.SysFarmerConfigDao;
import cn.tofocus.lejia.dao.sys.SysFarmerDao;
import cn.tofocus.lejia.dao.vendor.MktVendorDao;
import cn.tofocus.lejia.exception.LejiaErrCode;
import lombok.extern.slf4j.Slf4j;

/**
 * 商品供应库Manager类
 */
@Slf4j
@Component
public class SupplyManager
{
    /**
     * mkt_supply 商品供应库表
     */
    @Resource
    private MktSupplyDao mktSupplyDao;
    
    /**
     * mkt_goods 商品表
     */
    @Resource
    private MktGoodsDao mktGoodsDao;
    
    /**
     * mkt_vendor 商品供应商表
     */
    @Resource
    private MktVendorDao mktVendorDao;
    
    /**
     * mkt_goods_space 商品规格表
     */
    @Resource
    private MktGoodsSpaceDao mktGoodsSpaceDao;
    
    /**
     * sys_config 配置表
     */
    @Resource
    private SysConfigDao sysConfigDao;
    
    /**
     * sys_config的manager类
     */
    @Resource
    private SysConfigManager sysConfigManager;
    
    /**
     * sys_farmer 市场表
     */
    @Resource
    private SysFarmerDao sysFarmerDao;
    
    /**
     * mkt_gtype
     */
    @Resource
    private MktGtypeDao mktGtypeDao;
    
    /**
     * sys_farmer_config 市场配置表
     */
    @Resource
    private SysFarmerConfigDao sysFarmerConfigDao;
    
    /**
     * dto增强
     */
    @Resource
    private DtoEnhance dtoEnhance;
    
    /**
     * 商品供应详情列表按照sort排序
     * @param o1 对象1
     * @param o2 对象2
     * @return   结果
     */
    private Integer compareSort(Object o1, Object o2)
    {
        Integer sort1 = 0;
        Integer sort2 = 0;
        try
        {
            sort1 = (Integer)PropertyUtils.getProperty(o1, "sort");
            sort2 = (Integer)PropertyUtils.getProperty(o2, "sort");
        }
        catch (Throwable e)
        {
            throw TofocusException.of(LejiaErrCode.GET_BEAN_VALUE_FAILURE);
        }
        
        boolean bothInValid = Objects.nonNull(sort1) && Objects.nonNull(sort2);
        // sort1和sort2都为true
        if (bothInValid)
        {
            return sort1.compareTo(sort2);
        }
        // 仅仅sort1不为空，则往前排
        if (Objects.nonNull(sort1))
        {
            return -1;
        }
        // 仅仅sort2不为空，则往后排
        if (Objects.nonNull(sort2))
        {
            return 1;
        }
        // 其他情况，不移动顺序
        return 0;
    }
    
    /**
     * 商品供应详情排序规则
     * @return         结果
     */
    private final Comparator<Object> comparator = (o1, o2) -> {
        String space1 = "";
        String space2 = "";
        try
        {
            space1 = (String)PropertyUtils.getProperty(o1, "space");
            space2 = (String)PropertyUtils.getProperty(o2, "space");
        }
        catch (Throwable e)
        {
            throw TofocusException.of(LejiaErrCode.GET_BEAN_VALUE_FAILURE);
        }
        
        //首先比较规格，如果规格相同，则比较排序
        int flag = space1.compareTo(space2);
        if (flag == 0)
        {
            return compareSort(o1, o2);
        }
        else
        {
            return flag;
        }
    };
    
    /**
     * 商品供应库列表查询
     * @param param 		请求参数
     * @return				结果
     */
    public PageResult<MktSupplyOnList> pageList(MktSupplyParamDTO param)
    {
        Integer page = Objects.nonNull(param.getPage()) ? param.getPage() : 0;
        Integer pageSize = Objects.nonNull(param.getPagesize()) ? param.getPagesize() : 10;
        // 初始化结果
        PageResult<MktSupplyOnList> result = new PageResult<>();
        result.setPageable(new PageParameter(page, pageSize));
        
        // 供应库数据
        PointType pointType = sysConfigManager.judgePoint();
        log.info("pageList-pointType: " + pointType);
        List<MktSupply> mktSupplies = new ArrayList<>();
        switch (pointType)
        {
            // 运营端
            case OPERATION:
            {
                // 供应库数据
                mktSupplies = mktSupplyDao
                    .listSupply(param.getEnabled(), param.getFarmer(), param.getGoodPkeys(), param.getMType());
                break;
            }
            // 市场端
            case MARKET:
            {
                mktSupplies = mktSupplyDao.listSupply(param.getEnabled(),
                    CurrentSession.marketPkey(),
                    param.getGoodPkeys(),
                    param.getMType());
                break;
            }
            default:
            {
                return result;
            }
        }
        
        if (CollectionUtils.isNotEmpty(mktSupplies))
        {
            List<Integer> goodsIds = mktSupplies.stream().map(MktSupply::getGood).collect(Collectors.toList());
            // 商品数据
            PageResult<MktGoods> pageResult = mktGoodsDao.selectPage()
                .page(page)
                .pagesize(pageSize)
                .eq("gtype", param.getGtype())
                .like("title", param.getGoodsName())
                .in("pkey", goodsIds)
                .sort("sort", false)
                .eq("mType", param.getMType())
                .exec();
            // 转换DTO
            PageResult<MktSupplyOnList> result2 = BeanUtil.beanPageFrom(MktSupplyOnList.class, pageResult);
            dtoEnhance.deal(MktSupplyOnList.class, result2);
            List<MktSupplyOnList> content = result2.getContent();
            // 原先的content的类型是Collection$UnmodifiableRandomAccessList
            content = new ArrayList<>(content);
            // 过滤商品供应库启用状态数据
            if (Objects.nonNull(param.getEnabled()))
            {
                // 是否启用
                Boolean enabled = param.getEnabled();
                for (MktSupplyOnList item : content)
                {
                    // 数据源于@JoinDTO
                    // sorted排序，enabled匹配明细状态
                    List<MktSupplyPageDetail> newDetailList = item.getDetails()
                        .stream()
                        .filter(detail -> enabled.equals(detail.getEnabled()))
                        .sorted(comparator)
                        .collect(Collectors.toList());
//                    newDetailList.forEach(e -> {
//                        MktVendor mktVendor = mktVendorDao.get(e.getVendor());
//                        e.setVendorName(mktVendor.getDisplayName());
//                    });
                    item.setDetails(newDetailList);
                }
            }
            
            // 比较每个商品的detail（供应信息）的sort的第一项，从小到大排序
            Comparator<MktSupplyOnList> c = (o1, o2) -> {
                // detail最少存在一条
                Integer sort1 = o1.getDetails().get(0).getSort();
                Integer sort2 = o2.getDetails().get(0).getSort();
                return sort1.compareTo(sort2);
            };
            content.sort(c);
            
            // 处理商品供应详情
            // 当前公司-市场的供应商
            List<MktVendor> mktVendors = mktVendorDao.select().eq("ascription", CurrentSession.ascriptionPkey()).exec();
            // 规格pkey和名称
            Map<Integer, String> spacePkeyName = mktGoodsSpaceDao.select()
                .exec()
                .stream()
                .collect(Collectors.toMap(MktGoodsSpace::getPkey, MktGoodsSpace::getSpace));
            
            content.forEach(item -> {
                // 显示商品pkey
                item.setGoodsPkey(item.getPkey());
                // 商品供应详情
                List<MktSupplyPageDetail> details = item.getDetails();
                
                details.forEach(d -> {
                    try
                    {
                        int i = Integer.parseInt(d.getSpace());
                        d.setSpaceName(spacePkeyName.get(i));
                    }
                    catch (NumberFormatException e)
                    {
                        d.setSpaceName("（当前规格不存在）");
                    }
                    
                    // 设置供应商是否存在
                    d.setIsExist(true);
                    mktVendors.forEach(mktVendor -> {
                        if (mktVendor.getPkey().equals(d.getVendor()))
                        {
                            // 停用或者删除的供应商
                            if (!mktVendor.getEnabled() || mktVendor.getIdDel())
                            {
                                d.setIsExist(false);
                            }
                            d.setVendorName(mktVendor.getDisplayName());
                        }
                    });
                });
                item.setVendorShopping(true);
                if(StringUtils.isNotBlank(item.getFarmer()))
                {
                    SysFarmer sysFarmer = sysFarmerDao.get(item.getFarmer());
                    if(sysFarmer != null && FarmerType.VENDOR_SHOPPING_MALL.equals(sysFarmer.getType()))
                    {
                        item.setVendorShopping(false);
                    }
                }
                
            });
            result2.setContent(content);
            return result2;
        }
        return result;
    }
    
    /**
     * 获取运营端-派单配置
     * @return 结果
     */
    public SupplySendConfDTO getConf()
    {
        SupplySendConfDTO result = new SupplySendConfDTO();
        PointType pointType = sysConfigManager.judgePoint();
        Integer ascription = CurrentSession.ascriptionPkey();
        // 运营端
        if (PointType.OPERATION.equals(pointType))
        {
            result.setIsOperation(sysConfigDao.getValue(Constant.SysConfig.GOODS_SUPPLY_DEPLOY, ascription));
            // 统一配置是人工还是自动
            result.setAutomaticPurchase(sysConfigDao.getValue(Constant.SysConfig.GOODS_PURCHASE_DEPLOY, ascription));
            return result;
        }
        else
        {
            throw TofocusException.of(LejiaErrCode.NOT_RIGHT);
        }
    }
    
    /**
     * 修改运营端-派单配置
     * @param upd 参数
     * @return 结果
     */
    @Transactional(rollbackFor = Throwable.class)
    public Boolean updSendConf(SupplySendConfDTO upd)
    {
        PointType pointType = sysConfigManager.judgePoint();
        Integer ascription = CurrentSession.ascriptionPkey();
        // 运营端
        if (PointType.OPERATION.equals(pointType))
        {
            // 商品供应库配置（0-市场自定义，1-统一配置）
            SysConfigEntity config = sysConfigDao.getBean(Constant.SysConfig.GOODS_SUPPLY_DEPLOY, ascription);
            Boolean sendConfig = upd.getIsOperation();
            config.setValue(sendConfig ? "1" : "0");
            
            // 是否自动采购配置 （0-人工指派，1-自动指派）
            SysConfigEntity config2 = sysConfigDao.getBean(Constant.SysConfig.GOODS_PURCHASE_DEPLOY, ascription);
            Boolean automaticPurchase = upd.getAutomaticPurchase();
            // 统一配置时才可自动指派
            if (Objects.nonNull(automaticPurchase) && automaticPurchase && sendConfig)
            {
                config2.setValue("1");
            }
            else
            {
                config2.setValue("0");
            }
            
            List<SysConfigEntity> configs = new ArrayList<>();
            configs.add(config);
            configs.add(config2);
            
            return sysConfigDao.updateBatch(configs);
        }
        else
        {
            throw TofocusException.of(LejiaErrCode.NOT_RIGHT);
        }
    }
    
    /**
     * 当前市场能否增删改商品供应库
     * @return 结果
     */
    public Boolean isManipulation()
    {
        Boolean result = false;
        PointType pointType = sysConfigManager.judgePoint();
        Integer ascription = CurrentSession.ascriptionPkey();
        switch (pointType)
        {
            case OPERATION:
            {
                result = true;
                break;
            }
            case MARKET:
            {
                Boolean flag = sysConfigDao.getValue(Constant.SysConfig.GOODS_SUPPLY_DEPLOY, ascription);
                // 市场自定义
                if (!flag)
                {
                    result = true;
                }
                break;
            }
            default:
            {
                break;
            }
        }
        return result;
    }
    
    /**
     * 商品供应库数据删除（不支持删除整条商品信息）
     * @param pkeys		主键列表
     * @return			结果
     */
    @Transactional(rollbackFor = Throwable.class)
    public Boolean del(List<Integer> pkeys)
    {
        Boolean flag = isManipulation();
        if (flag)
        {
            List<MktSupply> list = mktSupplyDao.select().in("pkey", pkeys).exec();
            Integer size = list.size();
            if (!size.equals(pkeys.size()))
            {
                throw TofocusException.of(LejiaErrCode.SOME_DATA_INEXISTENCE);
            }
            
            // 修改flag
            Set<String> spaces = list.stream().map(MktSupply::getSpace).collect(Collectors.toSet());
            // 删除数据
            mktSupplyDao.removeAllById(pkeys);
            spaces.forEach(space -> {
                // 每种规格重新设置flag
                List<MktSupply> sameSpaces = mktSupplyDao.select().eq("space", space).eq("enabled", true).exec();
                setFlagTrue(sameSpaces);
            });
            return true;
        }
        else
        {
            throw TofocusException.of(LejiaErrCode.NOT_RIGHT);
        }
    }
    
    /**
     * 根据商品删除商品供应库信息
     * @param goodPkeys		商品主键
     * @return			    结果
     */
    @Transactional(rollbackFor = Throwable.class)
    public Boolean delByGoods(List<Integer> goodPkeys)
    {
        Boolean flag = isManipulation();
        if (flag)
        {
            List<MktSupply> list = mktSupplyDao.select().in("good", goodPkeys).exec();
            mktSupplyDao.removeAll(list);
            return true;
        }
        else
        {
            throw TofocusException.of(LejiaErrCode.NOT_RIGHT);
        }
    }
    
    /**
     * 商品供应库——商品树形列表
     * @param marketPkey  市场pkey
     * @return            结果
     */
    public List<TreeModel<Integer, MktSupplyGoodsInfo>> goodsList(MType mType, String marketPkey)
    {
        List<TreeModel<Integer, MktSupplyGoodsInfo>> result = new ArrayList<>();
        // 能否有权限
        boolean manipulation = false;
        Integer ascription = CurrentSession.ascriptionPkey();
        PointType pointType = sysConfigManager.judgePoint();
        switch (pointType)
        {
            // 运营端，必填市场pkey
            case OPERATION:
            {
                if (Objects.isNull(marketPkey))
                {
//                    throw TofocusException.of(SysErrCode.REQUIRED_PRARAM_NULL, "运营端请传递选择的市场marketPkey");
                    return null;
                }
                SysFarmer sysFarmer = sysFarmerDao.get(marketPkey);
                if (Objects.nonNull(sysFarmer))
                {
                    manipulation = true;
                    result = goodsListLogic(marketPkey, sysFarmer.getOrg(), mType);
                }
                break;
            }
            // 市场端，不用设置市场pkey
            case MARKET:
            {
                Boolean flag = sysConfigDao.getValue(Constant.SysConfig.GOODS_SUPPLY_DEPLOY, ascription);
                // 市场自定义
                if (!flag)
                {
                    manipulation = true;
                    result = goodsListLogic(CurrentSession.marketPkey(), CurrentSession.companyPkey(), mType);
                }
                break;
            }
            default:
            {
                break;
            }
        }
        
        if (!manipulation)
        {
            throw TofocusException.of(LejiaErrCode.NOT_RIGHT);
        }
        
        return result;
    }
    
    /**
     * 商品供应库——商品列表具体逻辑
     * @param marketPkey    市场pkey
     * @param companyPkey   公司pkey
     * @return              返回结果
     */
    private List<TreeModel<Integer, MktSupplyGoodsInfo>> goodsListLogic(String marketPkey, String companyPkey, MType mType)
    {
        List<MktSupplyGoodsInfo> list = new ArrayList<>();
        
        // 当前公司-市场的商品
        List<MktGoods> mktGoodsList = mktGoodsDao.select()
            .eq("farmer", marketPkey)
            .eq("company", companyPkey)
            .eq("mType", mType)
            .eq("idDel", false)
            .exec();
        
        // 已经添加的商品id
        List<Integer> goodsList = mktSupplyDao.select()
            .eq("farmer", marketPkey)
            .eq("company", companyPkey)
            .eq("mType", mType)
            .exec()
            .stream()
            .map(MktSupply::getGood)
            .collect(Collectors.toList());
        
        mktGoodsList.forEach(mktGoods -> {
            MktSupplyGoodsInfo info = BeanUtil.beanFrom(MktSupplyGoodsInfo.class, mktGoods);
            // 设置能否选择
            info.setEnabled(true);
            // 停用的商品不能选择
            if (!mktGoods.getEnabled())
            {
                info.setEnabled(false);
            }
            // 已经设置过的商品不能选择
            if (goodsList.contains(info.getPkey()))
            {
                info.setEnabled(false);
            }
            list.add(info);
        });
        
        // 商品数据的一级分类
        Set<Integer> gtypeList = list.stream().map(MktSupplyGoodsInfo::getGtype).collect(Collectors.toSet());
        
        // 商品分类pkey -> name 映射
        Map<Integer, String> gtypePkeyNameMap = mktGtypeDao.select()
            .in("pkey", gtypeList)
            .exec()
            .stream()
            .collect(Collectors.toMap(MktGtype::getPkey, MktGtype::getName));
        
        // 初始化结果
        List<TreeModel<Integer, MktSupplyGoodsInfo>> result = new ArrayList<>();
        
        gtypeList.forEach(gtype -> {
            TreeModel<Integer, MktSupplyGoodsInfo> first = new TreeModel<>();
            first.setPkey(gtype);
            first.setName(gtypePkeyNameMap.get(gtype));
            first.setDisabled(false);
            // 设置第二级，商品名称
            List<TreeModel<Integer, MktSupplyGoodsInfo>> seconds = new ArrayList<>();
            list.forEach(info -> {
                if (gtype.equals(info.getGtype()))
                {
                    TreeModel<Integer, MktSupplyGoodsInfo> second = new TreeModel<>();
                    second.setPkey(info.getPkey());
                    second.setName(info.getTitle());
                    second.setDisabled(!info.getEnabled());
                    second.setLeaf(false);
                    
                    seconds.add(second);
                }
            });
            first.setSub(seconds);
            first.setLeaf(CollectionUtils.isNotEmpty(seconds));
            // 添加result
            result.add(first);
        });
        return result;
    }
    
    /**
     * 商品供应库——规格列表
     * @return         结果
     */
    public List<MktSupplySpaceInfo> spaceList(Integer goodsPkey)
    {
        List<MktSupplySpaceInfo> result = new ArrayList<>();
        
        List<MktGoodsSpace> spaces = mktGoodsSpaceDao.select().eq("goods", goodsPkey).exec();
        spaces.forEach(space -> {
            MktSupplySpaceInfo res = new MktSupplySpaceInfo();
            res.setPkey(space.getPkey());
            res.setName(space.getSpace());
            result.add(res);
        });
        
        return result;
    }
    
    /**
     * 商品供应库——供应商列表
     * @param marketPkey 市场pkey
     * @return         结果
     */
    public List<MktSupplyVendorInfo> vendorList(String marketPkey)
    {
        List<MktSupplyVendorInfo> result = new ArrayList<>();
        String realMarketPkey = null;
        String realCompanyPkey = null;
        Integer ascription = CurrentSession.ascriptionPkey();
        // 能否有权限
        boolean manipulation = false;
        PointType pointType = sysConfigManager.judgePoint();
        switch (pointType)
        {
            // 运营端，必填市场pkey
            case OPERATION:
            {
                if (Objects.isNull(marketPkey))
                {
//                    throw TofocusException.of(SysErrCode.REQUIRED_PRARAM_NULL, "运营端请传递选择的市场marketPkey");
                    return null;
                }
                SysFarmer sysFarmer = sysFarmerDao.get(marketPkey);
                if (Objects.nonNull(sysFarmer))
                {
                    manipulation = true;
                    realMarketPkey = marketPkey;
                    realCompanyPkey = sysFarmer.getOrg();
                }
                break;
            }
            // 市场端，不用设置市场pkey
            case MARKET:
            {
                Boolean flag = sysConfigDao.getValue(Constant.SysConfig.GOODS_SUPPLY_DEPLOY, ascription);
                // 市场自定义
                if (!flag)
                {
                    manipulation = true;
                    realMarketPkey = CurrentSession.marketPkey();
                    realCompanyPkey = CurrentSession.companyPkey();
                }
                break;
            }
            default:
            {
                break;
            }
        }
        
        if (!manipulation)
        {
            throw TofocusException.of(LejiaErrCode.NOT_RIGHT);
        }
        
        // 当前公司-市场的供应商
        List<MktVendor> mktVendors =
            mktVendorDao.select().eq("farmer", realMarketPkey).eq("company", realCompanyPkey)
            .eq("idDel", false).exec();
        
        mktVendors.forEach(mktVendor -> {
            // 来源为合作商户中已经启用的商户
            // 当合作商户中商户被关闭启用和删除时，此处列表在供应商名字后面显示“（供应商不存在）”
            // BeanUtil.beanFrom有问题
            MktSupplyVendorInfo info = new MktSupplyVendorInfo();
            info.setPkey(mktVendor.getPkey());
            info.setName(mktVendor.getDisplayName());
            // 设置能否选择
            info.setIsExist(true);
            // 停用或者删除的供应商
            if (!mktVendor.getEnabled() || mktVendor.getIdDel())
            {
                info.setIsExist(false);
                info.setName(info.getName() + "（供应商不存在）");
            }
            result.add(info);
        });
        return result;
    }
    
    /**
     * 商品供应库——商品明细
     * @param marketPkey    市场pkey
     * @param goodsPkey		商品pkey
     * @return         结果
     */
    public MktSupplyInfo detail(String marketPkey, Integer goodsPkey)
    {
        // 能否新增/修改
        Boolean manipulation = false;
        Integer ascription = CurrentSession.ascriptionPkey();
        PointType pointType = sysConfigManager.judgePoint();
        MktSupplyInfo resultInfo = null;
        switch (pointType)
        {
            // 运营端，必填市场pkey
            case OPERATION:
            {
                manipulation = true;
                if (Objects.isNull(marketPkey))
                {
                    throw TofocusException.of(SysErrCode.REQUIRED_PRARAM_NULL, "运营端请传递选择的市场marketPkey");
                }
                resultInfo = getDetail(marketPkey, goodsPkey);
                break;
            }
            // 市场端，不用设置市场pkey
            case MARKET:
            {
                Boolean flag = sysConfigDao.getValue(Constant.SysConfig.GOODS_SUPPLY_DEPLOY, ascription);
                // 市场自定义
                if (!flag)
                {
                    manipulation = true;
                    resultInfo = getDetail(CurrentSession.marketPkey(), goodsPkey);
                }
                break;
            }
            default:
            {
                break;
            }
        }
        
        if (!manipulation)
        {
            throw TofocusException.of(LejiaErrCode.NOT_RIGHT);
        }
        
        return resultInfo;
    }
    
    private MktSupplyInfo getDetail(String marketPkey, Integer goodsPkey)
    {
        // 初始化结果
        MktSupplyInfo result = new MktSupplyInfo();
        result.setList(Collections.emptyList());
        List<MktSupply> queryList = mktSupplyDao.select().eq("farmer", marketPkey).eq("good", goodsPkey).exec();
        if (CollectionUtils.isNotEmpty(queryList))
        {
            // 设置市场key和名称
            SysFarmer sysFarmer = sysFarmerDao.get(marketPkey);
            result.setMarketPkey(marketPkey);
            result.setMarketName(Objects.nonNull(sysFarmer) ? sysFarmer.getName() : "市场已经被删除");
            
            // 设置商品pkey和名称
            MktGoods mktGoods = mktGoodsDao.get(goodsPkey);
            result.setCommissionRate2(queryList.get(0).getCommissionRate2());
            result.setGoodsPkey(goodsPkey);
            result.setMType(mktGoods.getMType());
            result.setGoodsName(Objects.nonNull(mktGoods) ? mktGoods.getTitle() : "商品已经被删除");
            
            // 规格pkey和名称
            Map<Integer, String> spacePkeyName = mktGoodsSpaceDao.select()
                .exec()
                .stream()
                .collect(Collectors.toMap(MktGoodsSpace::getPkey, MktGoodsSpace::getSpace));
            
            List<MktSupplyDetailInfo> mktSupplyDetailInfos = new ArrayList<>();
            queryList.forEach(item -> {
                MktSupplyDetailInfo mktSupplyDetailInfo = BeanUtil.beanFrom(MktSupplyDetailInfo.class, item);
                // 设置规格
                try
                {
                    int i = Integer.parseInt(item.getSpace());
                    mktSupplyDetailInfo.setSpace(i);
                    mktSupplyDetailInfo.setSpaceName(spacePkeyName.get(i));
                }
                catch (NumberFormatException e)
                {
                    mktSupplyDetailInfo.setSpaceName("（当前规格不存在）");
                }
                MktVendor mktVendor = mktVendorDao.get(mktSupplyDetailInfo.getVendor());
                if(mktVendor != null)
                    mktSupplyDetailInfo.setVendorName(mktVendor.getDisplayName());
                mktSupplyDetailInfos.add(mktSupplyDetailInfo);
            });
            // 设置供应商名称
            Comparator<MktSupplyDetailInfo> comparator2 = (o1, o2) -> {
                //首先比较规格，如果规格相同，则比较排序
                int flag = o1.getSpace().compareTo(o2.getSpace());
                if (flag == 0)
                {
                    return compareSort(o1, o2);
                }
                else
                {
                    return flag;
                }
            };
            // 排序
            mktSupplyDetailInfos.sort(comparator2);
            
            result.setList(mktSupplyDetailInfos);
        }
        return result;
    }
    
    /**
     * 商品供应库——新增
     * @param mktSupplyInfo	商品供应库——单项信息
     * @return				结果
     */
    @Transactional(rollbackFor = Throwable.class)
    public Boolean insert(MktSupplyInfo mktSupplyInfo, Boolean flag)
    {
        return insertOrUpdate(mktSupplyInfo, flag);
    }
    
    /**
     * 商品供应库——更新
     * @param mktSupplyInfo	商品供应库——单项信息
     * @return				结果
     */
    @Transactional(rollbackFor = Throwable.class)
    public Boolean update(MktSupplyInfo mktSupplyInfo, Boolean flag)
    {
        return insertOrUpdate(mktSupplyInfo, flag);
    }
    
    /**
     * 新增、修改
     * @param mktSupplyInfo 商品供应库——单项信息
     * @return              结果
     */
    private Boolean insertOrUpdate(MktSupplyInfo mktSupplyInfo, Boolean flag)
    {
        // 是否成功
        Boolean result = false;
        // 能否新增/修改
        Boolean manipulation = false;
        Integer ascription = CurrentSession.ascriptionPkey();
        PointType pointType = sysConfigManager.judgePoint();
        Integer goodsPkey = mktSupplyInfo.getGoodsPkey();
        switch (pointType)
        {
            // 运营端，必填市场
            case OPERATION:
            {
                if (Objects.isNull(mktSupplyInfo.getMarketPkey()))
                {
                    throw TofocusException.of(SysErrCode.REQUIRED_PRARAM_NULL, "运营端请传递选择的市场marketPkey");
                }
                SysFarmer sysFarmer = sysFarmerDao.get(mktSupplyInfo.getMarketPkey());
                if (Objects.nonNull(sysFarmer))
                {
                    checkFarmerTypeIns(goodsPkey, mktSupplyInfo.getMarketPkey(), flag);
                    manipulation = true;
                    insertOrUpdateLogic(mktSupplyInfo, sysFarmer.getOrg(), mktSupplyInfo.getMarketPkey(), ascription);
                }
                break;
            }
            // 市场端，不用设置市场
            case MARKET:
            {
                String companyPkey = CurrentSession.companyPkey();
                String marketPkey = CurrentSession.marketPkey();
                Boolean goodsSupplyDeploy = sysConfigDao.getValue(Constant.SysConfig.GOODS_SUPPLY_DEPLOY, ascription);
                // 市场自定义
                if (!goodsSupplyDeploy)
                {
                    checkFarmerTypeIns(goodsPkey, marketPkey, flag);
                    manipulation = true;
                    insertOrUpdateLogic(mktSupplyInfo, companyPkey, marketPkey, ascription);
                }
                break;
            }
            default:
            {
                break;
            }
        }
        
        if (!manipulation)
        {
            throw TofocusException.of(LejiaErrCode.NOT_RIGHT);
        }
        
        result = true;
        return result;
    }
    
    
    public Boolean insertOrUpdateBoxGoods(MktSupplyInfo mktSupplyInfo, String marketPkey, String companyPkey, Integer ascription)
    {
        // 是否成功
        Boolean result = false;
        // 能否新增/修改
//        Boolean manipulation = false;
        PointType pointType = sysConfigManager.judgePoint();
        Integer goodsPkey = mktSupplyInfo.getGoodsPkey();
        switch (pointType)
        {
            // 运营端，必填市场
            case OPERATION:
            {
                if (Objects.isNull(mktSupplyInfo.getMarketPkey()))
                {
                    throw TofocusException.of(SysErrCode.REQUIRED_PRARAM_NULL, "运营端请传递选择的市场marketPkey");
                }
                SysFarmer sysFarmer = sysFarmerDao.get(mktSupplyInfo.getMarketPkey());
                if (Objects.nonNull(sysFarmer))
                {
                    checkFarmerTypeIns(goodsPkey, mktSupplyInfo.getMarketPkey(), false);
//                    manipulation = true;
                    insertOrUpdateLogic(mktSupplyInfo, sysFarmer.getOrg(), mktSupplyInfo.getMarketPkey(), ascription);
                }
                break;
            }
            // 市场端，不用设置市场
            case MARKET:
            {
                Boolean goodsSupplyDeploy = sysConfigDao.getValue(Constant.SysConfig.GOODS_SUPPLY_DEPLOY, ascription);
                // 市场自定义
                if (!goodsSupplyDeploy)
                {
                    checkFarmerTypeIns(goodsPkey, marketPkey, false);
//                    manipulation = true;
                    insertOrUpdateLogic(mktSupplyInfo, companyPkey, marketPkey, ascription);
                }
                break;
            }
            default:
            {
                break;
            }
        }
        
//        if (!manipulation)
//        {
//            throw TofocusException.of(LejiaErrCode.NOT_RIGHT);
//        }
        
        result = true;
        return result;
    }
    
    public Boolean insertOrUpdateBoxGoodsRun(MktSupplyInfo mktSupplyInfo, String marketPkey, String companyPkey, Integer ascription)
    {
        // 是否成功
        Boolean result = false;
        // 能否新增/修改
        Boolean manipulation = false;
        PointType pointType = sysConfigManager.judgePoint();
        Integer goodsPkey = mktSupplyInfo.getGoodsPkey();
        switch (pointType)
        {
            // 运营端，必填市场
            case OPERATION:
            {
                if (Objects.isNull(mktSupplyInfo.getMarketPkey()))
                {
                    throw TofocusException.of(SysErrCode.REQUIRED_PRARAM_NULL, "运营端请传递选择的市场marketPkey");
                }
                SysFarmer sysFarmer = sysFarmerDao.get(mktSupplyInfo.getMarketPkey());
                if (Objects.nonNull(sysFarmer))
                {
                    checkFarmerTypeIns(goodsPkey, mktSupplyInfo.getMarketPkey(), false);
                    manipulation = true;
                    insertOrUpdateLogic(mktSupplyInfo, sysFarmer.getOrg(), mktSupplyInfo.getMarketPkey(), ascription);
                }
                break;
            }
            // 市场端，不用设置市场
            case MARKET:
            {
                Boolean goodsSupplyDeploy = sysConfigDao.getValue(Constant.SysConfig.GOODS_SUPPLY_DEPLOY, ascription);
                // 市场自定义
                if (!goodsSupplyDeploy)
                {
                    checkFarmerTypeIns(goodsPkey, marketPkey, false);
                    manipulation = true;
                    insertOrUpdateLogic(mktSupplyInfo, companyPkey, marketPkey, ascription);
                }
                break;
            }
            default:
            {
                break;
            }
        }
        
        result = true;
        return result;
    }
    
    private void checkFarmerTypeIns(Integer goodsPkey, String farmer, Boolean flag)
    {
        if(!flag)
            return;
        List<MktSupply> list = mktSupplyDao.select().eq("good", goodsPkey).exec();
        SysFarmer sysFarmer = sysFarmerDao.get(farmer);
        if(FarmerType.VENDOR_SHOPPING_MALL.equals(sysFarmer.getType()) &&
            list != null && !list.isEmpty())
        {
            throw TofocusException.of(LejiaErrCode.VENDOR_SHOPPING_MALL_SUPPLY_ERROR);
        }
    }
    
    /**
     * 新增、修改具体逻辑
     * @param mktSupplyInfo 请求入参
     * @param companyPkey   公司pkey
     * @param marketPkey    市场pkey
     */
    private void insertOrUpdateLogic(MktSupplyInfo mktSupplyInfo, String companyPkey, String marketPkey, Integer ascription)
    {
        List<MktSupplyDetailInfo> list = mktSupplyInfo.getList();
        SettlementMethodType smType = sysFarmerConfigDao.get(marketPkey).getSettlementMethod();
        BigDecimal commissionRate2 = mktSupplyInfo.getCommissionRate2();
        if(commissionRate2 == null && SettlementMethodType.COMMISSION_SETTLEMENT.equals(smType))
            throw TofocusException.of(LejiaErrCode.SETTLEMENTMETHODTYPE_COMMISSION_ERROR);
        // 采购派单顺序校验
        Set<Integer> sortList = list.stream().map(MktSupplyDetailInfo::getSort).collect(Collectors.toSet());
        sortList.forEach(sort -> {
            if (sort <= 0)
            {
                throw TofocusException.of(LejiaErrCode.SORT_NOT_GREATER_THAN_ZERO);
            }
        });
        
        List<MktSupplyDetailInfo> newList = new ArrayList<>();
        
        // 填写的所有规格
        Set<Integer> spaces = list.stream().map(MktSupplyDetailInfo::getSpace).collect(Collectors.toSet());
        for (Integer space : spaces)
        {
            // 当前规格的列表数据
            List<MktSupplyDetailInfo> spaceLists =
                list.stream().filter(i -> space.equals(i.getSpace())).collect(Collectors.toList());
            
            if (CollectionUtils.isNotEmpty(spaceLists))
            {
                // 同一规格下的供应商
                Set<Integer> vendors =
                    spaceLists.stream().map(MktSupplyDetailInfo::getVendor).collect(Collectors.toSet());
                if (vendors.size() < spaceLists.size())
                {
                    throw TofocusException.of(LejiaErrCode.EXCEED_THE_LIMIT, "同一规格下，同一供应商只能设置一次");
                }
                // 同一规格下的排序
                Set<Integer> sortList2 =
                    spaceLists.stream().map(MktSupplyDetailInfo::getSort).collect(Collectors.toSet());
                if (sortList2.size() < spaceLists.size())
                {
                    throw TofocusException.of(LejiaErrCode.SORT_REPEAT);
                }
                
                Comparator<MktSupplyDetailInfo> compare = this::compareSort;
                // 排序
                spaceLists.sort(compare);
                
                // 设置sort值
                for (int i = 0; i < spaceLists.size(); i++)
                {
                    MktSupplyDetailInfo first = spaceLists.get(0);
                    if (Objects.isNull(first.getSort()))
                    {
                        first.setSort(1);
                    }
                    if (i + 1 < spaceLists.size())
                    {
                        MktSupplyDetailInfo before = spaceLists.get(i);
                        MktSupplyDetailInfo after = spaceLists.get(i + 1);
                        if (Objects.nonNull(before.getSort()) && Objects.isNull(after.getSort()))
                        {
                            after.setSort(before.getSort() + 1);
                        }
                    }
                }
                
                newList.addAll(spaceLists);
            }
        }
        
        // 先删除老的
        List<MktSupply> oldEntityList = mktSupplyDao.select().eq("good", mktSupplyInfo.getGoodsPkey()).exec();
        Map<Integer,MktSupply> sMap = new HashMap<>();
        Boolean sortFlag = true;
        for(MktSupplyDetailInfo s : list)
        {
            if(s.getPkey() == null)
            {
                sortFlag = false;
                break;
            }
        }
        if (CollectionUtils.isNotEmpty(oldEntityList))
        {
            mktSupplyDao.removeAll(oldEntityList);

            if(Boolean.TRUE.equals(sortFlag))
            {
                oldEntityList.forEach(e -> sMap.put(e.getPkey(), e));
                for(MktSupplyDetailInfo s : list)
                {
                    if(!sMap.containsKey(s.getPkey()))
                    {
                        sortFlag = false;
                        break;
                    }
                    MktSupply supply = sMap.get(s.getPkey());
                    if(!s.getSpace().equals(Integer.valueOf(supply.getSpace())) 
                        || !s.getVendor().equals(supply.getVendor())
                        || !s.getEnabled().equals(supply.getEnabled())
                        || !s.getSort().equals(supply.getSort())
                        )
                    {
                        sortFlag = false;
                        break;
                    }
                }
            }
        }
        System.out.println("sortFlag: " + sortFlag);
        // dto -> 实体
        if (CollectionUtils.isNotEmpty(newList))
        {
            // 2021-12-29 新加个判断  采购价是否超过商品单价
            Map<Integer, BigDecimal> map = mktGoodsSpaceDao.findSpacePrice(spaces);
            Integer goodsPkey = mktSupplyInfo.getGoodsPkey();
            MType mType = mktSupplyInfo.getMType();
            List<MktSupply> entityList = new ArrayList<>();
            for (int i = 0; i < newList.size(); i++)
            {
                MktSupplyDetailInfo mktSupplyDetailInfo = newList.get(i);
                // 复制属性
                MktSupply mktSupply = BeanUtil.beanFrom(MktSupply.class, mktSupplyDetailInfo);
                BigDecimal price = map.get(mktSupplyDetailInfo.getSpace());
                if (SettlementMethodType.PURCHASE_SETTLEMENT.equals(smType) && mktSupply.getPurchasingPrice() != null 
                    && mktSupply.getPurchasingPrice().compareTo(price) > 0)
                    throw TofocusException.of(LejiaErrCode.PURCHASE_PRICE_ERROR);
                
                mktSupply.setCommissionRate2(commissionRate2);
                mktSupply.setSpace(mktSupplyDetailInfo.getSpace().toString());
                mktSupply.setGood(goodsPkey);
                mktSupply.setMType(mType);
                // 不传递enabled，默认设置为true
                if (Objects.isNull(mktSupply.getEnabled()))
                {
                    mktSupply.setEnabled(true);
                }
                mktSupply.setCompany(companyPkey);
                mktSupply.setFarmer(marketPkey);
                mktSupply.setAscription(ascription);
                mktSupply.setFlag(false);
                if(Boolean.TRUE.equals(sortFlag) && mktSupply.getPkey() != null && sMap.containsKey(mktSupply.getPkey()))
                {
                    mktSupply.setFlag(sMap.get(mktSupply.getPkey()).getFlag());
                }
                mktSupply.setSettlementMethod(smType);
                entityList.add(mktSupply);
            }
            mktSupplyDao.putAll(entityList);
        }
        
        // 更新完需要重新设置flag数据
        if(Boolean.FALSE.equals(sortFlag))
        {
            for (Integer space : spaces)
            {
                List<MktSupply> sameSpaces = mktSupplyDao.select()
                    .eq("space", space)
                    .eq("enabled", true)
                    .sort("sort")
                    .exec();
                setFlagTrue(sameSpaces);
            }
        }
    }
    
    /**
     * 商品供应库启用/停用
     * @param pkey	商品供应库单项数据pkey
     * @return		是否成功
     */
    @Transactional(rollbackFor = Throwable.class)
    public Boolean enable(Integer pkey)
    {
        boolean manipulation = isManipulation();
        if (manipulation)
        {
            boolean result = false;
            MktSupply mktSupply = mktSupplyDao.get(pkey);
            if (Objects.nonNull(mktSupply))
            {
                Boolean enabled = !mktSupply.getEnabled();
                mktSupply.setEnabled(enabled);
                // 修改派单顺序的flag逻辑
                List<MktSupply> sameSpaces =
                    mktSupplyDao.select().eq("space", mktSupply.getSpace()).eq("enabled", true).exec();
                // 如果修改后enabled是false
                if (!enabled)
                {
                    mktSupply.setFlag(false);
                    mktSupplyDao.update(mktSupply);
                    // 更新完需要重新查询数据
                    List<MktSupply> sameSpaces2 =
                        mktSupplyDao.select().eq("space", mktSupply.getSpace()).eq("enabled", true).exec();
                    setFlagTrue(sameSpaces2);
                }
                else
                {
                    mktSupplyDao.update(mktSupply);
                    setFlagTrue(sameSpaces);
                }
                result = true;
            }
            else
            {
                throw TofocusException.of(LejiaErrCode.DATA_INEXISTENCE);
            }
            return result;
        }
        else
        {
            throw TofocusException.of(LejiaErrCode.NOT_RIGHT);
        }
    }
    
    /**
     * 设置相同space的数据的派送顺序flag
     * @param list  商品供应库列表
     */
    private void setFlagTrue(List<MktSupply> list)
    {
        // 找出sort最小的项设置为true
        if (CollectionUtils.isNotEmpty(list))
        {
            for (int i = 0; i < list.size(); i++)
            {
                // 其余项设为false
                list.get(i).setFlag(i == 0);
            }
            mktSupplyDao.updateAll(list);
        }
    }
    
    /**
     * 商品供应库-运营端是否开启统一配置
     * @return 结果
     */
    public Boolean isGoodSupply()
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        return sysConfigDao.getValue(Constant.SysConfig.GOODS_SUPPLY_DEPLOY, ascription);
    }
    
    /**
     * 商品供应库-是否系统自动派单
     * @return 结果
     */
    public Boolean isGoodPurchaseDeploy()
    {
        PointType pointType = sysConfigManager.judgePoint();
        Integer ascription = CurrentSession.ascriptionPkey();
        switch (pointType)
        {
            // 运营端
            case OPERATION:
                return sysConfigDao.getValue(Constant.SysConfig.GOODS_PURCHASE_DEPLOY, ascription);
            // 市场端
            case MARKET:
                // 全局商品供应库配置-为市场自定义时，查询市场自己的派单配置
                Boolean global = sysConfigDao.getValue(Constant.SysConfig.GOODS_SUPPLY_DEPLOY, ascription);
                if (!global)
                {
                    String marketPkey = CurrentSession.marketPkey();
                    SysFarmerConfig sysFarmerConfig = sysFarmerConfigDao.get(marketPkey);
                    if (Objects.nonNull(sysFarmerConfig) && sysFarmerConfig.getAutomaticPurchase())
                    {
                        return true;
                    }
                }
                return false;
            default:
                throw TofocusException.of(LejiaErrCode.NOT_RIGHT);
        }
    }
    
}
