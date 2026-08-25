package cn.tofocus.lejia.domain.market;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import cn.tofocus.common.util.StringUtil;
import cn.tofocus.lejia.bean.entity.zx.ZxUserInfo;
import cn.tofocus.lejia.bean.enums.*;
import cn.tofocus.lejia.bean.enums.v2.ZxCardStatus;
import cn.tofocus.lejia.dao.zx.ZxUserInfoDao;
import cn.tofocus.lejia.domain.GoodListQueryer;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.core.Result;
import cn.tofocus.core.exception.SysErrCode;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.SelectBuilder;
import cn.tofocus.db.dto.DtoEnhance;
import cn.tofocus.file.api.v3.FileApiV3;
import cn.tofocus.file.bean.FileInfoV3;
import cn.tofocus.file.bean.MemoryMultipartFile;
import cn.tofocus.lejia.bean.dto.market.MarketPkeyNameDTO;
import cn.tofocus.lejia.bean.dto.market.MktVendorDTO;
import cn.tofocus.lejia.bean.dto.market.MktVendorFileDTO;
import cn.tofocus.lejia.bean.dto.market.MktVendorOnList;
import cn.tofocus.lejia.bean.dto.market.MktVendorPkeyNameDTO;
import cn.tofocus.lejia.bean.dto.market.MktVendorPointLineOnList;
import cn.tofocus.lejia.bean.dto.market.MktVendorQueryParamDTO;
import cn.tofocus.lejia.bean.dto.market.XaszVendorInfo;
import cn.tofocus.lejia.bean.entity.applet.XaszAssociationEntity;
import cn.tofocus.lejia.bean.entity.goods.MktGoods;
import cn.tofocus.lejia.bean.entity.market.MktGtype;
import cn.tofocus.lejia.bean.entity.market.MktSupply;
import cn.tofocus.lejia.bean.entity.member.MktMember;
import cn.tofocus.lejia.bean.entity.sys.SysFarmer;
import cn.tofocus.lejia.bean.entity.sys.SysFarmerConfig;
import cn.tofocus.lejia.bean.entity.vendor.MktVendor;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorBigData;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorBoutique;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorFile;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorPoint;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorPointLine;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorStaff;
import cn.tofocus.lejia.bean.enums.v2.VendorZxStatus;
import cn.tofocus.lejia.bean.enums.v5.FarmerType;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.applet.XaszAssociationDao;
import cn.tofocus.lejia.dao.goods.MktGoodsDao;
import cn.tofocus.lejia.dao.market.MktGtypeDao;
import cn.tofocus.lejia.dao.market.MktMemberDao;
import cn.tofocus.lejia.dao.market.MktSupplierDao;
import cn.tofocus.lejia.dao.market.MktSupplyDao;
import cn.tofocus.lejia.dao.sys.SysConfigDao;
import cn.tofocus.lejia.dao.sys.SysFarmerConfigDao;
import cn.tofocus.lejia.dao.sys.SysFarmerDao;
import cn.tofocus.lejia.dao.vendor.MktVendorBigdataDao;
import cn.tofocus.lejia.dao.vendor.MktVendorBoutiqueDao;
import cn.tofocus.lejia.dao.vendor.MktVendorDao;
import cn.tofocus.lejia.dao.vendor.MktVendorFileDao;
import cn.tofocus.lejia.dao.vendor.MktVendorPointDao;
import cn.tofocus.lejia.dao.vendor.MktVendorPointLineDao;
import cn.tofocus.lejia.dao.vendor.MktVendorStaffDao;
import cn.tofocus.lejia.exception.LejiaErrCode;
import cn.tofocus.lejia.exception.WsaleErrCode;
import cn.tofocus.lejia.file.define.BaseState;
import cn.tofocus.lejia.file.define.State;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class VendorManager
{
    @Autowired
    private MktVendorDao vendorDao;
    
    @Autowired
    private MktGoodsDao goodsDao;
    
    @Autowired
    private MktVendorBoutiqueDao vendorBoutiqueDao;
    
    @Autowired
    private MktVendorStaffDao vendorStaffDao;
    
    @Autowired
    private MktVendorPointDao vendorPointDao;
    
    @Autowired
    private MktVendorPointLineDao vendorPointLineDao;

    @Autowired
    private MktSupplierDao supplierDao;
    
    @Autowired
    private MktMemberDao memberDao;
    
    /**
     * sys_farmer_config 市场配置表
     */
    @Resource
    private SysFarmerConfigDao sysFarmerConfigDao;
    
    /**
     * sys_farmer 市场表
     */
    @Autowired
    private SysFarmerDao sysFarmerDao;
    
    /**
     * sys_config dao层
     */
    @Resource
    private SysConfigDao sysConfigDao;
    
    /**
     * sys_config 管理类
     */
    @Resource
    private SysConfigManager sysConfigManager;
    
    /**
     * mkt_gtype 一级分类
     */
    @Resource
    private MktGtypeDao mktGtypeDao;
    
    /**
     * mkt_vendor_file 商户文件表
     */
    @Resource
    private MktVendorFileDao mktVendorFileDao;
    
    /**
     * mkt_vendor_bigdata 商户风采展示详情内容表
     */
    @Resource
    private MktVendorBigdataDao mktVendorBigdataDao;
    
    @Autowired
    private XaszAssociationDao xaszAssociationDao;
    
    @Autowired
    private MktSupplyDao mktSupplyDao;

    @Autowired
    private ZxUserInfoDao zxUserInfoDao;

    @Value("${zx.qingfen.ascription:13}")
    private Integer qfAscription;
    
    @Autowired
    private GoodListQueryer goodListQueryer;
    
    /**
     * 增强类
     */
    @Resource
    private DtoEnhance dtoEnhance;
    
    /**
     * 获取合作商户
     * @param pkey 合作商户主键
     * @return     结果
     */
    public MktVendorDTO getVendor(Integer pkey)
    {
        MktVendor vendor = vendorDao.getVendor(pkey);
        if (vendor == null)
        {
            throw TofocusException.of(WsaleErrCode.NOT_INQUIRE);
        }
        else
        {
            MktVendorDTO mktVendorDTO = BeanUtil.beanFrom(MktVendorDTO.class, vendor);
            String businessScope = vendor.getBusinessScope();
            // 经营范围
            List<Integer> realBusinessScope = new ArrayList<>();
            // 经营范围中文
            StringBuilder builder = new StringBuilder();
            Map<Integer, String> typePkeyNames =
                mktGtypeDao.listGtype(vendor.getFarmer(), vendor.getAscription()).stream().collect(Collectors.toMap(MktGtype::getPkey, MktGtype::getName));
            
            if (StringUtils.isNotBlank(businessScope))
            {
                String[] scopes = businessScope.split(",");
                for (int i = 0; i < scopes.length; i++)
                {
                    int scopePkey = 0;
                    String name = "";
                    
                    if (i > 0)
                    {
                        name += ",";
                    }
                    
                    try
                    {
                        scopePkey = Integer.parseInt(scopes[i]);
                        realBusinessScope.add(scopePkey);
                    }
                    // 转换失败处理
                    catch (NumberFormatException e)
                    {
                        name += "未知";
                    }
                    // 一级范围名称
                    String v = typePkeyNames.get(scopePkey);
                    if (Objects.nonNull(v))
                    {
                        name += v;
                    }
                    else
                    {
                        name += "未知";
                    }
                    builder.append(name);
                }
            }
            mktVendorDTO.setBusinessScopes(realBusinessScope);
            mktVendorDTO.setBusinessScopesName(builder.toString());
            
            // 头像、视频、个性宣传
            List<MktVendorFile> files = mktVendorFileDao.select().eq("vendorPkey", pkey).exec();
            List<MktVendorFileDTO> fileDtos = BeanUtil.beanListFrom(MktVendorFileDTO.class, files);
            mktVendorDTO.setFiles(fileDtos);
            // 风采展示详情内容
            MktVendorBigData mktVendorBigData = mktVendorBigdataDao.get(pkey);
            mktVendorDTO
                .setMktVendorBigData(Objects.isNull(mktVendorBigData) ? new MktVendorBigData() : mktVendorBigData);
            return mktVendorDTO;
        }
    }
    
    /**
     * 查询合作商户
     * @return         结果
     */
    public PageResult<MktVendorOnList> queryVendor(MktVendorQueryParamDTO paramDTO)
    {
        // 运营端/市场端/公司端判断
        PointType pointType = sysConfigManager.judgePoint();
        List<String> marketPkeys = new ArrayList<>();
        switch (pointType)
        {
            // 运营端
            case OPERATION:
            {
                // 市场商城，判断标记
                if ("market".equals(paramDTO.getFlag()))
                {
                    List<String> paramMarketPkeys = paramDTO.getMarketPkeys();
                    if (CollectionUtils.isNotEmpty(paramMarketPkeys))
                    {
                        marketPkeys.addAll(paramMarketPkeys);
                    }
                }
                else
                {
                    // 积分商城（原有的）
                    marketPkeys.add(CurrentSession.marketPkey());
                }
                break;
            }
            // 市场端
            case MARKET:
            {
                marketPkeys.add(CurrentSession.marketPkey());
                break;
            }
            case COMPANY:
            default:
            {
                throw TofocusException.of(LejiaErrCode.NOT_RIGHT);
            }
        }
        
        paramDTO.setMarketPkeys(marketPkeys);
        // 查询获得结果
        PageResult<MktVendor> pageResult = vendorDao.queryVendor(paramDTO, CurrentSession.ascriptionPkey());
        
        PageResult<MktVendorOnList> result = BeanUtil.beanPageFrom(MktVendorOnList.class, pageResult);
        dtoEnhance.deal(MktVendorOnList.class, result);
        
        Map<Integer, String> gtypePkeyNameList =
            mktGtypeDao.listGtype(CurrentSession.marketPkey(), CurrentSession.ascriptionPkey()).stream().collect(Collectors.toMap(MktGtype::getPkey, MktGtype::getName));
        
        for (MktVendorOnList bean : result.getContent())
        {
            // 设置经营范围
            // 13,14 -> 蔬菜豆类,新鲜水果
            String businessScope = bean.getBusinessScope();
            if (StringUtils.isNotBlank(businessScope))
            {
                StringBuilder builder = new StringBuilder();
                
                String[] scopes = businessScope.split(",");
                for (int i = 0; i < scopes.length; i++)
                {
                    int scopePkey = Integer.parseInt(scopes[i]);
                    // 经营范围名称
                    String gtypeName = gtypePkeyNameList.get(scopePkey);
                    if (StringUtils.isBlank(gtypeName))
                    {
                        gtypeName = "未知";
                    }
                    if (i == 0)
                    {
                        builder.append(gtypeName);
                    }
                    else
                    {
                        builder.append("；").append(gtypeName);
                    }
                }
                bean.setBusinessScope(builder.toString());
            }
            
            // 设置商户积分
            MktVendorPoint vendorPoint = vendorPointDao.get(bean.getPkey());
            if (vendorPoint == null)
            {
                bean.setPoints(0);
            }
            else
            {
                bean.setPoints(vendorPoint.getPoints());
            }
            
            if (qfAscription.equals(CurrentSession.ascriptionPkey()))
            {
                ZxUserInfo zxUserInfo = zxUserInfoDao.get(ZxUserType.VENDOR,
                    bean.getPkey().toString(),
                    CurrentSession.ascriptionPkey(),
                    ZxUserInfo.class);
                if (zxUserInfo != null && StringUtil.isNotBlank(zxUserInfo.getZxUserId()))
                    bean.setZxRegistered(true);
            }
        }
        return result;
    }
    
    /**
     * 新增逻辑
     * @param dto           dto
     * @return              结果
     */
    @Transactional(rollbackFor = Throwable.class)
    public Integer insertVendorV2(MktVendorDTO dto)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        PointType point = sysConfigManager.judgePoint();
        String farmer = dto.getFarmer();
        
        String marketPkey = null;
        String companyPkey = null;
        switch (point)
        {
            case OPERATION:
            {
                SysFarmer sysFarmer = sysFarmerDao.get(dto.getFarmer());
                if (Objects.isNull(sysFarmer))
                {
                    throw TofocusException.of(LejiaErrCode.MARKET_INEXISTENCE);
                }
                else
                {
                    marketPkey = farmer;
                    companyPkey = sysFarmer.getOrg();
                }
                break;
            }
            case MARKET:
            {
                // 运营端不开启统一配置，市场端可以操作
                Boolean unified = isUnified();
                if (!unified)
                {
                    marketPkey = CurrentSession.marketPkey();
                    companyPkey = CurrentSession.companyPkey();
                }
                else
                {
                    throw TofocusException.of(LejiaErrCode.NOT_RIGHT);
                }
                break;
            }
            default:
                break;
        }
        
        // 校验市场pkey
        if (PointType.OPERATION.equals(point) && StringUtils.isBlank(farmer))
        {
            throw TofocusException.of(SysErrCode.REQUIRED_PRARAM_NULL, "运营端需要设置市场pkey");
        }
        
        if (!PointType.OPERATION.equals(point) && StringUtils.isNotBlank(farmer))
        {
            throw TofocusException.of(LejiaErrCode.PARAM_NOT_ALLOWED, "非运营端不允许设置市场pkey");
        }
        SysFarmer sysFarmer = sysFarmerDao.get(marketPkey);
        // 2024-09-06 产品临时需求 
        // 2025-10-20 去掉限制
//        if(FarmerType.VENDOR_SHOPPING_MALL.equals(sysFarmer.getType()) && xaszAssociationDao.checkFarmerExist(marketPkey))
//            throw TofocusException.of(LejiaErrCode.VENDOR_SHOPPING_MALL_VENDOR_INS_ERROR);
        // 校验手机号
        long count = vendorDao.aggregation().eq("mobile", dto.getMobile()).eq("ascription", ascription).execCount();
        if (count > 0) throw TofocusException.of(WsaleErrCode.MOBILE_REPEAT);
        // 校验手机号是不是被供应商占用
        if (supplierDao.existMobile(dto.getMobile(), null, ascription))
            throw TofocusException.of(WsaleErrCode.MOBILE_REPEAT, "手机号已被占用");
        dto.setName(dto.getName().trim());
        // 校验名称
        if (vendorDao.checkRepeatName(null, dto.getName(), marketPkey))
            throw TofocusException.of(WsaleErrCode.NAME_REPEAT);
        if(FarmerType.VENDOR_SHOPPING_MALL.equals(sysFarmer.getType()) && vendorDao.checkDisplayRepeatName(null, dto.getDisplayName(), marketPkey))
        {
            throw TofocusException.of(WsaleErrCode.NAME_REPEAT, "展示名称不能重复");
        }
        
        
        
        MktVendor vendor = BeanUtil.beanFrom(MktVendor.class, dto);
        // 处理地址
        if (StringUtils.isBlank(vendor.getAddr()))
        {
            vendor.setAddr("");
        }
        vendor.setFarmer(marketPkey);
        vendor.setCompany(companyPkey);
        vendor.setAscription(ascription);
        vendor.setZxStatus(VendorZxStatus.NOT_AUDIT);
        if (StringUtils.isBlank(vendor.getManager()))
        {
            vendor.setManager("");
        }
        vendor.setDisplayFlag(false);
        vendor.setEnabled(true);
        vendor.setIdDel(false);
        vendor.setRowVension(1);
        vendor.setVisitCount(0);
        vendor.setRateUpdateTime(new Date());
        vendor.setBusinessScope(StringUtils.join(dto.getBusinessScopes(), ","));
        SysFarmerConfig config = sysFarmerConfigDao.get(marketPkey);
        if (config != null)
        {
            vendor.setSettlementMethod(config.getSettlementMethod());
        }
        else
        {
            // 原先的商户设置"按采购价采购"
            vendor.setSettlementMethod(SettlementMethodType.PURCHASE_SETTLEMENT);
        }
        if(vendor.getCommissionRate() == null)
            vendor.setCommissionRate(BigDecimal.ZERO);
        if(qfAscription.equals(vendor.getAscription()))
        {
            SysFarmerConfig sysFarmerConfig = sysFarmerConfigDao.get(marketPkey);
            if(!CommissionType.MERCHANT.equals(sysFarmerConfig.getCommissionType()))
            {
//                BigDecimal cr = BigDecimal.ZERO;
//                    Constant.ZxConfig.TJ_COMMISSION_RATE.multiply(new BigDecimal("100"));
//                if(sysFarmerConfig.getCommissionRate() != null)
//                    cr = cr.add(sysFarmerConfig.getCommissionRate());
                if(sysFarmerConfig.getCommissionRate() != null && vendor.getCommissionRate().compareTo(sysFarmerConfig.getCommissionRate()) < 0)
                    throw TofocusException.of(LejiaErrCode.COMMISSIONRATE_ERROR);
            }
        }
        
        // 新增/修改商户
        MktVendor put = vendorDao.add(vendor);
        // 新增时新增商户积分记录
        MktVendorPoint vp = new MktVendorPoint();
        vp.setPkey(put.getPkey());
        vp.setPoints(0);
        vendorPointDao.add(vp);
        String content = "";
        if (dto.getMktVendorBigData() != null) content = dto.getMktVendorBigData().getContent();
        putVendorConfig(marketPkey, put.getPkey(), dto.getFiles(), content);

        // 清分的运营商，要新增账户数据
        if (qfAscription.equals(ascription))
        {
            ZxUserInfo zxUserInfo = new ZxUserInfo();
            zxUserInfo.setType(ZxUserType.VENDOR);
            zxUserInfo.setValue(put.getPkey().toString());
            zxUserInfo.setName(put.getName());
            zxUserInfo.setComms(BigDecimal.ZERO);
            zxUserInfo.setCardStatus(ZxCardStatus.NOT_BINDING);
            zxUserInfo.setDelFlag(Boolean.FALSE);
            zxUserInfo.setAscription(ascription);
            zxUserInfoDao.add(zxUserInfo);
        }

        return put.getPkey();
    }
    
    /**
     * 更新逻辑
     * @param dto           dto
     * @return              结果
     */
    @Transactional(rollbackFor = Throwable.class)
    public Integer updateVendorV2(MktVendorDTO dto)
    {
        PointType point = sysConfigManager.judgePoint();
        String farmer = dto.getFarmer();
        String marketPkey = null;
        String companyPkey = null;
        switch (point)
        {
            case OPERATION:
            {
                SysFarmer sysFarmer = sysFarmerDao.get(dto.getFarmer());
                if (Objects.isNull(sysFarmer))
                {
                    throw TofocusException.of(LejiaErrCode.MARKET_INEXISTENCE);
                }
                else
                {
                    marketPkey = farmer;
                    companyPkey = sysFarmer.getOrg();
                }
                break;
            }
            case MARKET:
            {
                // 运营端不开启统一配置，市场端可以操作
                Boolean unified = isUnified();
                if (!unified)
                {
                    marketPkey = CurrentSession.marketPkey();
                    companyPkey = CurrentSession.companyPkey();
                }
                else
                {
                    throw TofocusException.of(LejiaErrCode.NOT_RIGHT);
                }
                break;
            }
            default:
                break;
        }
        
        Integer dtoPkey = dto.getPkey();
        MktVendor vendor = vendorDao.get(dtoPkey);
        if (vendor == null)
        {
            throw TofocusException.of(WsaleErrCode.NOT_INQUIRE);
        }
        dto.setName(dto.getName().trim());
        // 校验手机号
        long count = vendorDao.aggregation().notEq("pkey", dto.getPkey()).eq("mobile", dto.getMobile()).execCount();
        if (count > 0) throw TofocusException.of(WsaleErrCode.MOBILE_REPEAT);
        
        // 原先的佣金费用
        BigDecimal oldRate = vendor.getCommissionRate();
        String mobile = vendor.getMobile();
        SysFarmer sysFarmer = sysFarmerDao.get(marketPkey);
        String oldDisplayName = vendor.getDisplayName();
        boolean updName = !dto.getName().equals(vendor.getName());
//        if(FarmerType.VENDOR_SHOPPING_MALL.equals(sysFarmer.getType()))
//        {
//            BeanUtils.copyProperties(dto, vendor, "zxStatus", "booth", "zxIdentity", "bankname", "bankuser", 
//                "bankcard", "bankBranchName", "bankNo");
//        }
//        else
            BeanUtils.copyProperties(dto, vendor, "zxStatus");
        if(!dto.getDisplayName().equals(oldDisplayName))
        {
            vendor.setDisplayFlag(true);
        }
        // 手机号码修改以后,openid制空 需要重新登录
        if (!mobile.equals(dto.getMobile())) vendor.setOpenid1(null);
        // 处理地址
        if (StringUtils.isBlank(vendor.getAddr()))
        {
            vendor.setAddr("");
        }
        if (StringUtils.isBlank(vendor.getManager()))
        {
            vendor.setManager("");
        }
        
        // 修改后的佣金费用
        BigDecimal newRate = dto.getCommissionRate();
        if(qfAscription.equals(vendor.getAscription()))
        {
            SysFarmerConfig sysFarmerConfig = sysFarmerConfigDao.get(dto.getFarmer());
            if(!CommissionType.MERCHANT.equals(sysFarmerConfig.getCommissionType()))
            {
//                BigDecimal cr = Constant.ZxConfig.TJ_COMMISSION_RATE.multiply(new BigDecimal("100"));
//                if(sysFarmerConfig.getCommissionRate() != null)
//                    cr = cr.add(sysFarmerConfig.getCommissionRate());
                if(sysFarmerConfig.getCommissionRate() != null && newRate.compareTo(sysFarmerConfig.getCommissionRate()) < 0)
                    throw TofocusException.of(LejiaErrCode.COMMISSIONRATE_ERROR);
            }
        }
        // 设置佣金更新时间
        if (Objects.nonNull(oldRate) && Objects.isNull(newRate))
        {
            vendor.setRateUpdateTime(new Date());
        }
        if (Objects.nonNull(newRate) && Objects.isNull(oldRate))
        {
            vendor.setRateUpdateTime(new Date());
        }
        if (Objects.nonNull(newRate) && Objects.nonNull(oldRate) && !newRate.equals(oldRate))
        {
            vendor.setRateUpdateTime(new Date());
        }
        // 校验名称 - 和其他人的名称比
        if (vendorDao.checkRepeatName(dtoPkey, dto.getName(), marketPkey))
            throw TofocusException.of(WsaleErrCode.NAME_REPEAT);
        if(FarmerType.VENDOR_SHOPPING_MALL.equals(sysFarmer.getType()) && vendorDao.checkDisplayRepeatName(dtoPkey, dto.getDisplayName(), marketPkey))
        {
            throw TofocusException.of(WsaleErrCode.NAME_REPEAT);
        }
            
        vendor.setBusinessScope(StringUtils.join(dto.getBusinessScopes(), ","));
        SysFarmerConfig config = sysFarmerConfigDao.get(marketPkey);
        if (config != null)
        {
            vendor.setSettlementMethod(config.getSettlementMethod());
        }
        else
        {
            // 原先的商户设置"按采购价采购"
            vendor.setSettlementMethod(SettlementMethodType.PURCHASE_SETTLEMENT);
        }
        // 修改商户
        vendor.setCompany(companyPkey);
        MktVendor put = vendorDao.update(vendor);
        String content = "";
        if (dto.getMktVendorBigData() != null) content = dto.getMktVendorBigData().getContent();
        putVendorConfig(marketPkey, dto.getPkey(), dto.getFiles(), content);
        // 商户商城,所有商品的费率跟商户的费率走
        if(FarmerType.VENDOR_SHOPPING_MALL.equals(sysFarmer.getType()))
        {
            BigDecimal commissionRate = vendor.getCommissionRate();
            if(commissionRate == null)
                commissionRate = BigDecimal.ZERO;
            List<MktSupply> supplyList = mktSupplyDao.select().eq("vendor", put.getPkey()).exec();
            for(MktSupply s : supplyList)
            {
                s.setCommissionRate2(commissionRate);
                s.setSettlementMethod(SettlementMethodType.COMMISSION_SETTLEMENT);
            }
            mktSupplyDao.updateAll(supplyList);
        }
        // 清分的运营商，如果修改了名称，同步更新
        if (qfAscription.equals(put.getAscription()) && updName)
        {
            ZxUserInfo zxUserInfo =
                zxUserInfoDao.get(ZxUserType.VENDOR, put.getPkey().toString(), put.getAscription(), ZxUserInfo.class);
            if (zxUserInfo != null)
            {
                zxUserInfo.setName(put.getName());
                zxUserInfoDao.put(zxUserInfo);
            }
        }
        return put.getPkey();
    }
    
    
    /**
     * 新增逻辑
     * @param dto           dto
     * @return              结果
     */
    @Transactional(rollbackFor = Throwable.class)
    public Integer insertOrUpdateVendorV3(XaszVendorInfo dto)
    {
        if(dto.getMarket() == null)
            return null;
        XaszAssociationEntity xaszAssociationEntity = xaszAssociationDao.getMarket(dto.getMarket());
        if(xaszAssociationEntity == null)
            return null;
        String farmer = xaszAssociationEntity.getFarmer();
        SysFarmer sysFarmer = sysFarmerDao.get(farmer);
        if(!FarmerType.VENDOR_SHOPPING_MALL.equals(sysFarmer.getType()))
            return null;
        MktVendor mktVendor = vendorDao.selectOne().eq("merchant", dto.getMerchant()).eq("idDel", false).exec();
        Integer checkPkey = null;
        MktVendor vendor = new MktVendor();
        if(mktVendor != null)
        {
            checkPkey = mktVendor.getPkey();
            BeanUtils.copyProperties(mktVendor, vendor);
        }
        // 校验手机号
        long count = vendorDao.aggregation().notEq("pkey", checkPkey).eq("mobile", dto.getMobile()).execCount();
        if (count > 0) throw TofocusException.of(WsaleErrCode.MOBILE_REPEAT);
        // 校验名称
        if (vendorDao.checkRepeatName(checkPkey, dto.getName(), farmer))
            throw TofocusException.of(WsaleErrCode.NAME_REPEAT);
       
        vendor.setPkey(checkPkey);
        vendor.setName(dto.getName());
        vendor.setBooth(dto.getBooth());
        // 手机号码修改以后,openid制空 需要重新登录
        if (mktVendor != null && !mktVendor.getMobile().equals(dto.getMobile())) vendor.setOpenid1(null);
        vendor.setZxIdentity(dto.getZxIdentity());
        
        // 银行相关信息 只更新一次
        if(StringUtils.isBlank(vendor.getBankname()) && StringUtils.isBlank(vendor.getBankuser())
            && StringUtils.isBlank(vendor.getBankcard()) && StringUtils.isBlank(vendor.getBankBranchName())
            && StringUtils.isBlank(vendor.getBankNo()))
        {
            vendor.setBankname(dto.getBankname());
            vendor.setBankuser(dto.getBankuser());
            vendor.setBankcard(dto.getBankcard());
            vendor.setBankBranchName(dto.getBankBranchName());
            vendor.setBankNo(dto.getBankNo());
        }
        
        if(vendor.getCommissionRate() == null)
            vendor.setCommissionRate(BigDecimal.ZERO);
        // 修改商户
        vendor.setMobile(dto.getMobile());
        vendor.setFarmer(farmer);
        vendor.setCompany(sysFarmer.getOrg());
        vendor.setMerchant(dto.getMerchant());
        // 处理地址
        vendor.setAddr("");
        // 新增
        if(mktVendor == null)
        {
            vendor.setDisplayFlag(false);
            if(StringUtils.isNotBlank(vendor.getBooth()))
            {
                if(StringUtils.isNotBlank(dto.getAreaTypeName()))
                {
                    vendor.setDisplayName(dto.getAreaTypeName() + "/" + vendor.getBooth());
                    vendor.setBooth(vendor.getDisplayName());
                }
                else
                    vendor.setDisplayName(vendor.getBooth());
            }
            else
            {
                vendor.setDisplayName(vendor.getName());
            }
            vendor.setEnabled(false);
            vendor.setIdDel(false);
            vendor.setRowVension(1);
            vendor.setVisitCount(0);
            vendor.setRateUpdateTime(new Date());
            vendor.setSettlementMethod(SettlementMethodType.COMMISSION_SETTLEMENT);
            vendor.setManager("");
            vendor.setAscription(xaszAssociationEntity.getAscription());
            vendor.setZxStatus(VendorZxStatus.NOT_AUDIT);
        }
        // 编辑
        else
        {
            if(!Boolean.TRUE.equals(mktVendor.getDisplayFlag()))
            {
                if(StringUtils.isNotBlank(vendor.getBooth()))
                {
                    if(StringUtils.isNotBlank(dto.getAreaTypeName()))
                    {
                        vendor.setDisplayName(dto.getAreaTypeName() + "/" + vendor.getBooth());
                        vendor.setBooth(vendor.getDisplayName());
                    }
                    else
                        vendor.setDisplayName(vendor.getBooth());
                }
                else
                {
                    vendor.setDisplayName(vendor.getName());
                }
            }
            else
            {
                vendor.setBooth(dto.getAreaTypeName() + "/" + vendor.getBooth());
            }
        }
        // 新增/修改商户
        MktVendor put = vendorDao.put(vendor);
        // 新增
        if(mktVendor == null)
        {
            // 新增时新增商户积分记录
            MktVendorPoint vp = new MktVendorPoint();
            vp.setPkey(put.getPkey());
            vp.setPoints(0);
            vendorPointDao.add(vp);
            // 清分的运营商，要新增账户数据
            if (qfAscription.equals(put.getAscription()))
            {
                ZxUserInfo zxUserInfo = new ZxUserInfo();
                zxUserInfo.setType(ZxUserType.VENDOR);
                zxUserInfo.setValue(put.getPkey().toString());
                zxUserInfo.setName(put.getName());
                zxUserInfo.setComms(BigDecimal.ZERO);
                zxUserInfo.setCardStatus(ZxCardStatus.NOT_BINDING);
                zxUserInfo.setDelFlag(Boolean.FALSE);
                zxUserInfo.setAscription(put.getAscription());
                zxUserInfoDao.add(zxUserInfo);
            }
        }
        // 清分的运营商，如果修改了名称，同步更新
        else if (qfAscription.equals(put.getAscription()) && !put.getName().equals(mktVendor.getName()))
        {
            ZxUserInfo zxUserInfo = zxUserInfoDao
                .get(ZxUserType.VENDOR, put.getPkey().toString(), put.getAscription(), ZxUserInfo.class);
            if (zxUserInfo != null)
            {
                zxUserInfo.setName(put.getName());
                zxUserInfoDao.put(zxUserInfo);
            }
        }
        return put.getPkey();
    }
    
    public void putVendorConfig(String marketPkey, Integer pkey, List<MktVendorFileDTO> newFiles, String content)
    {
        // 商户结算方式
        
        // 设置头像、视频、个性宣传
        List<MktVendorFile> oldFiles = mktVendorFileDao.select().eq("vendorPkey", pkey).exec();
        if (CollectionUtils.isNotEmpty(oldFiles))
        {
            mktVendorFileDao.removeAll(oldFiles);
        }
        if (CollectionUtils.isNotEmpty(newFiles))
        {
            List<MktVendorFile> mktVendorFiles = BeanUtil.beanListFrom(MktVendorFile.class, newFiles);
            long headIcon = newFiles.stream().filter(f -> VendorFileType.HEAD_ICON.equals(f.getType())).count();
            long video = newFiles.stream().filter(f -> VendorFileType.VIDEO.equals(f.getType())).count();
            long propaganda = newFiles.stream().filter(f -> VendorFileType.PROPAGANDA.equals(f.getType())).count();
            if (headIcon > 1)
            {
                throw TofocusException.of(LejiaErrCode.PARAM_OVERSIZE, "头像只能上传一张");
            }
            if (video > 1)
            {
                throw TofocusException.of(LejiaErrCode.PARAM_OVERSIZE, "宣传视频只能上传一个");
            }
            if (propaganda > 5)
            {
                throw TofocusException.of(LejiaErrCode.PARAM_OVERSIZE, "个性宣传最多5张");
            }
            
            mktVendorFiles.forEach(file -> {
                file.setVendorPkey(pkey);
                file.setEnabled(true);
                file.setAscription(CurrentSession.ascriptionPkey());
            });
            mktVendorFileDao.addAll(mktVendorFiles);
        }
        
        // 设置风采展示详情内容
        if (Objects.nonNull(content))
        {
            if (content.length() > 65535)
            {
                throw TofocusException.of(LejiaErrCode.PARAM_OVERSIZE, "风采展示详情内容过长，请删除一部分内容");
            }
        }
        MktVendorBigData mktVendorBigData = mktVendorBigdataDao.get(pkey);
        if (Objects.nonNull(mktVendorBigData))
        {
            mktVendorBigData.setContent(content);
            mktVendorBigdataDao.update(mktVendorBigData);
        }
        else
        {
            mktVendorBigData = new MktVendorBigData();
            mktVendorBigData.setPkey(pkey);
            mktVendorBigData.setContent(content);
            mktVendorBigData.setAscription(CurrentSession.ascriptionPkey());
            mktVendorBigdataDao.add(mktVendorBigData);
        }
    }
    
    /**
     * 新增合作商户
     * @param dto           dto
     * @return              结果
     */
    @Transactional(rollbackFor = Throwable.class)
    public Integer insVendor(MktVendorDTO dto)
    {
        return insertVendorV2(dto);
    }
    
    /**
     * 修改合作商户
     * @param dto           dto
     * @return              结果
     */
    @Transactional(rollbackFor = Throwable.class)
    public Boolean updVendor(MktVendorDTO dto)
    {
        updateVendorV2(dto);
        return true;
    }
    
    @Transactional(rollbackFor = Throwable.class)
    public Boolean delVendor(Integer pkey)
    {
        // 运营端/市场端/公司端判断判断
        hasRight();
        MktVendor vendor = dataIsValid(pkey);
        
        String mobile = vendor.getMobile();
        String PrefixM = "0000";
        
        // 修改之前已经删除的，电话号码相似的商户的电话？？
        List<MktVendor> delExec = vendorDao.select().like("mobile", mobile).eq("idDel", true).exec();
        if (delExec != null && !delExec.isEmpty())
        {
            // 电话号码前4位列表
            List<Integer> prefixMList = new ArrayList<>();
            for (MktVendor c : delExec)
            {
                prefixMList.add(Integer.valueOf(c.getMobile().substring(0, 4)));
            }
            Collections.sort(prefixMList);
            // 列表最后一项的电话号码+1，再进行格式化：将1显示为0001，相当于补零操作
            PrefixM = String.format("%04d", prefixMList.get(prefixMList.size() - 1) + 1);
        }
        vendor.setMobile(PrefixM + mobile);
        
        if (vendor.getEnabled())
        {
            throw TofocusException.of(WsaleErrCode.NOT_DELETED);
        }
        vendor.setIdDel(true);
        vendorDao.update(vendor);
        // 清分的运营商，需要解绑中信银行卡，注销中信商户，逻辑删除账户数据
        if (qfAscription.equals(vendor.getAscription()))
        {
            ZxUserInfo zxUserInfo = zxUserInfoDao
                .get(ZxUserType.VENDOR, vendor.getPkey().toString(), vendor.getAscription(), ZxUserInfo.class);
            if (zxUserInfo != null)
            {
                zxUserInfo.setDelFlag(Boolean.TRUE);
                zxUserInfoDao.put(zxUserInfo);
            }
        }
        return true;
    }
    
    @Transactional(rollbackFor = Throwable.class)
    public Boolean enabledVendor(Integer pkey, Boolean flag)
    {
        // 运营端/市场端/公司端判断判断
        hasRight();
        MktVendor vendor = dataIsValid(pkey);
        if(flag && StringUtils.isBlank(vendor.getBusinessScope()))
        {
            throw TofocusException.of(WsaleErrCode.VENDOR_BUSINESSSCOPE_ERROR);
        }
        if(!flag)
        {
            MktVendorBoutique boutique = vendorBoutiqueDao.byVendorAndFarmer(vendor.getPkey(), vendor.getFarmer());
            if(boutique != null)
            {
                boutique.setEnabled(false);
                vendorBoutiqueDao.update(boutique);
            }
        }
        // 设置数据
        vendor.setEnabled(flag);
        MktVendor update = vendorDao.update(vendor);
        if(Boolean.FALSE.equals(flag))
        {
            List<MktGoods> list = goodsDao.listVendor(update.getPkey(), update.getAscription());
            if(list != null && !list.isEmpty())
            {
                for(MktGoods g : list)
                {
                    g.setEnabled(false);
                }
                goodsDao.updateAll(list);
                if(!update.getFarmer().startsWith(Constant.Operation))
                    goodListQueryer.resetAll(update.getFarmer(), null);
            }
        }
        // 更新完后比较结果
        return flag.equals(update.getEnabled());
    }
    
    /**
     * 合作商户数据是否有效
     * @param pkey     主键
     */
    private MktVendor dataIsValid(Integer pkey)
    {
        MktVendor vendor = vendorDao.getVendor(pkey);
        if (vendor == null)
        {
            throw TofocusException.of(WsaleErrCode.NOT_INQUIRE);
        }
        return vendor;
    }
    
    /**
     * 当前用户是否有权限
     */
    private void hasRight()
    {
        PointType pointType = sysConfigManager.judgePoint();
        switch (pointType)
        {
            case OPERATION:
                break;
            case MARKET:
                Boolean unified = isUnified();
                if (unified)
                {
                    throw TofocusException.of(LejiaErrCode.NOT_RIGHT);
                }
                break;
            default:
                throw TofocusException.of(LejiaErrCode.NOT_RIGHT);
        }
    }
    
    public PageResult<MktVendorPointLineOnList> queryVendorPointLine(int page, int pagesize, Integer vendor,
        SourceType source, String mobile, String name, String startDate, String endDate)
    {
        PageResult<MktVendorPointLineOnList> result = null;
        List<Integer> pkeys = new ArrayList<>();
        String marketPkey = CurrentSession.marketPkey();
        SelectBuilder<Integer, MktVendor> builder = vendorDao.select().eq("farmer", marketPkey);
        if (StringUtils.isNotBlank(name) || StringUtils.isNotBlank(mobile))
        {
            builder.like("name", name).like("mobile", mobile);
        }
        List<MktVendor> exec = builder.exec();
        if (exec == null || exec.size() <= 0) return result;
        for (MktVendor v : exec)
        {
            pkeys.add(v.getPkey());
        }
        PageResult<MktVendorPointLine> pageResult =
            vendorPointLineDao.queryVendorPointLine(page, pagesize, vendor, source, startDate, endDate, pkeys);
        result = BeanUtil.beanPageFrom(MktVendorPointLineOnList.class, pageResult);
        
        for (MktVendorPointLineOnList bean : result.getContent())
        {
            MktMember entity = memberDao.get(bean.getMember());
            if (entity != null) bean.setMemberName(entity.getName());
            MktVendor mktVendor = vendorDao.get(bean.getVendor());
            if (mktVendor != null)
            {
                bean.setVendorName(mktVendor.getDisplayName());
                bean.setVendorMobile(mktVendor.getMobile());
            }
        }
        
        List<MktVendorPointLineOnList> content = new ArrayList<>();
        content.addAll(result.getContent());
        Iterator<MktVendorPointLineOnList> iterator = content.iterator();
        while (iterator.hasNext())
        {
            MktVendorPointLineOnList next = iterator.next();
            if (StringUtils.isNotBlank(name)) if (!next.getVendorName().contains(name)) iterator.remove();
            if (StringUtils.isNotBlank(mobile)) if (!next.getVendorMobile().contains(mobile)) iterator.remove();
        }
        result.setContent(content);
        return result;
    }
    
    /**
     * 运营端-市场商城-市场列表
     * @return             结果
     */
    public List<MarketPkeyNameDTO> marketList()
    {
        PointType pointType = sysConfigManager.judgePoint();
        if (PointType.OPERATION.equals(pointType))
        {
            List<SysFarmer> sysFarmers = sysFarmerDao.queryValidMarketList(CurrentSession.ascriptionPkey());
            return BeanUtil.beanListFrom(MarketPkeyNameDTO.class, sysFarmers);
        }
        else
        {
            throw TofocusException.of(LejiaErrCode.NOT_RIGHT);
        }
    }
    
    /**
     * 经营范围
     * @return   结果
     */
    public List<MktVendorPkeyNameDTO> gtypeList(String farmer)
    {
        PointType pointType = sysConfigManager.judgePoint();
        if (PointType.OPERATION.equals(pointType) || PointType.MARKET.equals(pointType))
        {
            // 初始化结果
            List<MktVendorPkeyNameDTO> res = new ArrayList<>();
            if(StringUtils.isBlank(farmer))
                farmer = CurrentSession.marketPkey();
            List<MktGtype> mktGtypes = mktGtypeDao.listGtype(farmer, CurrentSession.ascriptionPkey());
            mktGtypes.forEach(mktGtype -> {
                MktVendorPkeyNameDTO re = new MktVendorPkeyNameDTO();
                re.setPkey(mktGtype.getPkey());
                re.setName(mktGtype.getName());
                re.setDisabled(!mktGtype.getEnabled());
                
                res.add(re);
            });
            return res;
        }
        else
        {
            throw TofocusException.of(LejiaErrCode.NOT_RIGHT);
        }
    }
    
    /**
     * 运营端是否开启统一配置
     * @return   结果
     */
    public Boolean isUnified()
    {
        PointType pointType = sysConfigManager.judgePoint();
        if (PointType.OPERATION.equals(pointType) || PointType.MARKET.equals(pointType))
        {
            return sysConfigDao.getValue(Constant.SysConfig.VENDOR_MANAGER_DEPLOY, CurrentSession.ascriptionPkey());
        }
        else
        {
            throw TofocusException.of(LejiaErrCode.NOT_RIGHT);
        }
    }
    
    public State uploadImage(HttpServletRequest request, Map<String, Object> conf, FileApiV3 fileApiV3)
    {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
        MultipartFile multipartFile = multipartRequest.getFile(conf.get("fieldName").toString());
        String contentType = multipartFile.getContentType();
        log.info("contentType: " + contentType);
        try
        {
            byte[] fileContent = multipartFile.getBytes();
            MultipartFile tmpfile = new MemoryMultipartFile("file", multipartFile.getName(), contentType, fileContent);
            Result<FileInfoV3> uploadImage = fileApiV3.uploadImage(tmpfile, multipartFile.getName(),"富文本编辑框");
            log.info("uploadImage: " + JsonUtil.toString(uploadImage, true));
            FileInfoV3 info = uploadImage.getResult();
            State state = new BaseState(true);
            if (info == null) return new BaseState(false);
            state.putInfo("size", info.getSize());
            state.putInfo("title", info.getFileName());
            state.putInfo("name", info.getFileName());
            state.putInfo("url", info.getUrl());
            state.putInfo("type", info.getExtName());
            state.putInfo("original", info.getFileName());
            return state;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return new BaseState(false);
        
    }
    
    @Transactional
    public Integer insVendorPoint(String name, String addr, String mobile)
    {
        MktVendor exec = vendorDao.selectOne().eq("mobile", mobile).exec();
        if (exec != null) throw TofocusException.of();
        MktVendor vendor = new MktVendor();
        vendor.setName(name);
        vendor.setAddr(addr);
        vendor.setMobile(mobile);
        vendor.setManager("");
        vendor.setRowVension(1);
        vendor.setIdDel(false);
        vendor.setEnabled(true);
        vendor.setVisitCount(0);
        vendor.setBusinessScope("");
        vendor.setFarmer(CurrentSession.marketPkey());
        vendor.setCompany(CurrentSession.companyPkey());
        vendor.setAscription(CurrentSession.ascriptionPkey());
        MktVendor add = vendorDao.add(vendor);
        MktVendorPoint vp = new MktVendorPoint();
        vp.setPkey(add.getPkey());
        vp.setPoints(0);
        vendorPointDao.add(vp);
        // 清分的运营商，要新增账户数据
        if (qfAscription.equals(add.getAscription()))
        {
            ZxUserInfo zxUserInfo = new ZxUserInfo();
            zxUserInfo.setType(ZxUserType.VENDOR);
            zxUserInfo.setValue(add.getPkey().toString());
            zxUserInfo.setName(add.getName());
            zxUserInfo.setComms(BigDecimal.ZERO);
            zxUserInfo.setCardStatus(ZxCardStatus.NOT_BINDING);
            zxUserInfo.setDelFlag(Boolean.FALSE);
            zxUserInfo.setAscription(add.getAscription());
            zxUserInfoDao.add(zxUserInfo);
        }
        return add.getPkey();
    }
    
    @Transactional
    public Boolean updVendorPoint(Integer pkey, String name, String addr, String mobile)
    {
        MktVendor vendor = vendorDao.get(pkey);
        boolean updName = !name.equals(vendor.getName());
        if (StringUtils.isNotBlank(name))
        {
            vendor.setName(name);
            List<MktVendorStaff> exec = vendorStaffDao.select().eq("vendor", vendor.getPkey()).exec();
            for (MktVendorStaff s : exec)
            {
                s.setName(name);
            }
            vendorStaffDao.updateAll(exec);
        }
        if (StringUtils.isNotBlank(addr)) vendor.setAddr(addr);
        if (StringUtils.isNotBlank(mobile))
        {
            List<MktVendor> exec = vendorDao.select().eq("mobile", mobile).exec();
            if (!exec.isEmpty() && !mobile.equals(vendor.getMobile()))
                throw TofocusException.of(WsaleErrCode.MOBILE_REPEAT);
            vendor.setMobile(mobile);
        }
        MktVendor update = vendorDao.update(vendor);
        if (update == null) return false;
        // 清分的运营商，如果修改了名称，同步更新
        if (qfAscription.equals(update.getAscription()) && updName)
        {
            ZxUserInfo zxUserInfo = zxUserInfoDao
                .get(ZxUserType.VENDOR, update.getPkey().toString(), update.getAscription(), ZxUserInfo.class);
            if (zxUserInfo != null)
            {
                zxUserInfo.setName(update.getName());
                zxUserInfoDao.put(zxUserInfo);
            }
        }
        return true;
    }
    
    public Boolean bindVendor(String openid1, String openid2)
    {
        MktVendor vendor = vendorDao.selectOne().eq("openid1", openid1).exec();
        if (vendor == null) throw TofocusException.of(LejiaErrCode.MERCHANT_INEXISTENCE);
        vendor.setOpenid2(openid2);
        vendorDao.update(vendor);
        return true;
    }
    
}
