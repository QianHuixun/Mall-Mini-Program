package cn.tofocus.lejia.domain.zx;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.common.util.PageUtil;
import cn.tofocus.common.util.StringUtil;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.page.PageParameter;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.zx.*;
import cn.tofocus.lejia.bean.entity.sys.SysFarmer;
import cn.tofocus.lejia.bean.entity.vendor.MktVendor;
import cn.tofocus.lejia.bean.entity.zx.ZxUserInfo;
import cn.tofocus.lejia.bean.entity.zx.ZxWithdraw;
import cn.tofocus.lejia.bean.enums.ZxUserType;
import cn.tofocus.lejia.bean.enums.ZxWithdrawStatus;
import cn.tofocus.lejia.bean.enums.v2.ZxCardStatus;
import cn.tofocus.lejia.config.LejiaConfig;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.sys.SysFarmerDao;
import cn.tofocus.lejia.dao.vendor.MktVendorDao;
import cn.tofocus.lejia.dao.zx.ZxUserInfoDao;
import cn.tofocus.lejia.dao.zx.ZxWithdrawDao;
import cn.tofocus.lejia.domain.TjZxManager;
import cn.tofocus.lejia.exception.LejiaErrCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ZxUserManager
{
    private final LejiaConfig lejiaConfig;
    
    @Autowired
    private ZxUserInfoDao zxUserInfoDao;
    
    @Autowired
    private SysFarmerDao sysFarmerDao;
    
    @Autowired
    private MktVendorDao mktVendorDao;
    
    @Autowired
    private TjZxManager tjZxManager;
    
    @Autowired
    private ZxWithdrawDao zxWithdrawDao;
    
    private final static Set<String> PERSON_ID_TYPES = Sets.newHashSet("01",
        "22",
        "23",
        "25",
        "26",
        "27",
        "28",
        "29",
        "30",
        "31",
        "32",
        "33",
        "34",
        "35",
        "36",
        "37",
        "38",
        "39");
    
    private final static Set<String> ENTERPRISE_ID_TYPES = Sets.newHashSet("02", "03", "04", "05", "06", "07", "08");
    
    private final static Set<String> PERSON_ACCT_TYPES = Sets.newHashSet("5", "6");
    
    private final static Set<String> ENTERPRISE_ACCT_TYPES = Sets.newHashSet("1", "2", "3", "4");
    
    private final static List<ZxUserType> QUERY_TYPES =
        Lists.newArrayList(ZxUserType.SYSTEM, ZxUserType.MARKET, ZxUserType.SELF_MARKET, ZxUserType.TRADE_UNION);
    
    ZxUserManager(LejiaConfig lejiaConfig)
    {
        this.lejiaConfig = lejiaConfig;
    }
    
    public PageResult<ZxUserInfoOnPage> query(int page, int pagesize, String name)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        String currentFarmer = CurrentSession.marketPkey();
        // 市场
        if (!(Constant.Operation + ascription).equals(currentFarmer))
        {
            List<ZxUserInfoOnPage> list = new ArrayList<>();
            ZxUserInfoOnPage dto = zxUserInfoDao.getByFarmer(currentFarmer, ZxUserInfoOnPage.class);
            if (dto != null && Boolean.FALSE.equals(dto.getDelFlag()))
                list.add(dto);
            return PageUtil.page(list, PageParameter.of(page, pagesize));
        }
        // 运营中心
        else
        {
            PageResult<ZxUserInfoOnPage> pageResult =
                zxUserInfoDao.query(page, pagesize, ascription, QUERY_TYPES, name, ZxUserInfoOnPage.class);
            ZxUserInfo system = zxUserInfoDao.get(ZxUserType.SYSTEM, currentFarmer, ascription, ZxUserInfo.class);
            if (system != null)
            {
                for (ZxUserInfoOnPage line : pageResult)
                {
                    if (line.getType() == ZxUserType.SELF_MARKET)
                    {
                        line.setUserType(system.getUserType());
                        line.setUserPhone(system.getUserPhone());
                        line.setPan(system.getPan());
                        line.setPanNum(system.getPanNum());
                    }
                }
            }
            return pageResult;
        }
    }
    
    public ZxUserInfoForUpdUser getUserInfo(Integer pkey)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        String currentFarmer = CurrentSession.marketPkey();
        // 市场，获取自己的
        if (!(Constant.Operation + ascription).equals(currentFarmer))
        {
            return zxUserInfoDao.getByFarmer(currentFarmer, ZxUserInfoForUpdUser.class);
        }
        // 运营中心，根据pkey获取
        else
        {
            if (pkey == null)
                throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "pkey不能为空");
            return zxUserInfoDao.get(pkey, ascription, ZxUserInfoForUpdUser.class);
        }
    }
    
    // 更新新的中信主体,原有商户重新注册
    public void runZxUserId()
    {
//        List<ZxUserInfo> list = zxUserInfoDao.select().gt("pkey", 5).isNotNull("zxUserId").eq("delFlag", false).exec();
//        for(ZxUserInfo z : list)
//        {
//            z.setOldZxUserId(z.getZxUserId());
//            z.setCardStatus(ZxCardStatus.NOT_BINDING);
//            tjZxManager.zxRegisterUser(z);
//        }
        List<ZxUserInfo> list = zxUserInfoDao.select()
            .in("pkey", 123,124,125)
            .exec();
        for(ZxUserInfo z : list)
        {
            // 用户类型是个人，或银行账户类型是个人账户，生成用户授权协议信息
            if ("1".equals(z.getUserType()) || "1".equals(z.getAcctType()) || "3".equals(z.getAcctType()))
                z.generateAuthProtocol();
            tjZxManager.zxBindCard(z, true);
            z.setCardStatus(ZxCardStatus.BINDING_SUCCESS);
        }
        zxUserInfoDao.updateAll(list);
    }
    
    @Transactional(rollbackFor = Exception.class)
    public boolean updUserInfo(ZxUserInfoForUpdUser forUpd)
    {
        // 检查合法性
        validBeforeUserInfo(forUpd);
        
        Integer ascription = CurrentSession.ascriptionPkey();
        String currentFarmer = CurrentSession.marketPkey();
        ZxUserInfo old;
        ZxUserInfo bean;
        // 市场，仅更新自己的，不管forUpd.pkey
        if (!(Constant.Operation + ascription).equals(currentFarmer))
        {
            old = zxUserInfoDao.getByFarmer(currentFarmer, ZxUserInfo.class);
            if (old == null)
            {
                SysFarmer farmer = sysFarmerDao.get(currentFarmer);
                if (farmer == null)
                    throw TofocusException.of(LejiaErrCode.MARKET_INEXISTENCE);
                bean = new ZxUserInfo();
                if (farmer.getConfig().getIsEnterprise())
                {
                    bean.setType(ZxUserType.MARKET);
                    bean.setMarketAuto(Boolean.FALSE);
                }
                else
                {
                    bean.setType(ZxUserType.SELF_MARKET);
                    bean.setMarketAuto(null);
                }
                bean.setValue(farmer.getPkey());
                bean.setName(farmer.getName());
                bean.setComms(BigDecimal.ZERO);
                bean.setVendorAuto(Boolean.FALSE);
                bean.setCardStatus(ZxCardStatus.NOT_BINDING);
                bean.setDelFlag(Boolean.FALSE);
                bean.setAscription(ascription);
            }
            else
            {
                bean = BeanUtil.beanFrom(ZxUserInfo.class, old);
            }
        }
        // 运营中心，根据pkey获取
        else
        {
            if (forUpd.getPkey() == null)
                throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "pkey不能为空");
            old = zxUserInfoDao.get(forUpd.getPkey(), ascription, ZxUserInfo.class);
            if (old == null)
                throw TofocusException.of(LejiaErrCode.DATA_INEXISTENCE, "找不到该账户");
            bean = BeanUtil.beanFrom(ZxUserInfo.class, old);
        }
        BeanUtils.copyProperties(forUpd, bean, "pkey", "zxUserId", "name");
        
        // 请求中信新增/修改用户
        zxChangeUser(old, bean);
        
        zxUserInfoDao.put(bean);
        return true;
    }
    
    public ZxUserInfoForUpdBank getUserBank(Integer pkey)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        String currentFarmer = CurrentSession.marketPkey();
        // 市场，获取自己的
        if (!(Constant.Operation + ascription).equals(currentFarmer))
        {
            return zxUserInfoDao.getByFarmer(currentFarmer, ZxUserInfoForUpdBank.class);
        }
        // 运营中心，根据pkey获取
        else
        {
            if (pkey == null)
                throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "pkey不能为空");
            return zxUserInfoDao.get(pkey, ascription, ZxUserInfoForUpdBank.class);
        }
    }
    
    public boolean updUserBank(ZxUserInfoForUpdBank forUpd)
    {
        // 检查合法性
        validBeforeUserBank(forUpd);
        
        Integer ascription = CurrentSession.ascriptionPkey();
        String currentFarmer = CurrentSession.marketPkey();
        ZxUserInfo old;
        // 市场，仅更新自己的，不管forUpd.pkey
        if (!(Constant.Operation + ascription).equals(currentFarmer))
        {
            old = zxUserInfoDao.getByFarmer(currentFarmer, ZxUserInfo.class);
            if (old == null || StringUtil.isBlank(old.getZxUserId()))
            {
                throw TofocusException.of(LejiaErrCode.DATA_INEXISTENCE, "请先编辑账户信息");
            }
        }
        // 运营中心，根据pkey获取
        else
        {
            if (forUpd.getPkey() == null)
                throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "pkey不能为空");
            old = zxUserInfoDao.get(forUpd.getPkey(), ascription, ZxUserInfo.class);
            if (old == null || StringUtil.isBlank(old.getZxUserId()))
            {
                throw TofocusException.of(LejiaErrCode.DATA_INEXISTENCE, "请先编辑账户信息");
            }
        }
        ZxUserInfo newBean = BeanUtil.beanFrom(ZxUserInfo.class, old);
        BeanUtils.copyProperties(forUpd, newBean, "pkey", "zxUserId", "name", "userType");
        
        // 请求中信绑卡
        zxBindCard(old, newBean);
        
        zxUserInfoDao.put(newBean);
        return true;
    }
    
    public boolean enableMarketAuto(Integer pkey, Boolean enabled)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        String currentFarmer = CurrentSession.marketPkey();
        // 市场，不允许操作
        if (!(Constant.Operation + ascription).equals(currentFarmer))
        {
            throw TofocusException.of(LejiaErrCode.NOT_RIGHT);
        }
        ZxUserInfo zxUserInfo = zxUserInfoDao.get(pkey, ascription, ZxUserInfo.class);
        if (zxUserInfo == null)
            throw TofocusException.of(LejiaErrCode.DATA_INEXISTENCE);
        if (zxUserInfo.getType() == ZxUserType.SELF_MARKET)
            throw TofocusException.of(LejiaErrCode.PARAM_VALUE_ERR, "自营市场不支持启停市场自动提现");
        zxUserInfo.setMarketAuto(enabled);
        zxUserInfoDao.update(zxUserInfo);
        return true;
    }
    
    public boolean enableVendorAuto(Integer pkey, Boolean enabled)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        String currentFarmer = CurrentSession.marketPkey();
        // 市场，不允许操作
        if (!(Constant.Operation + ascription).equals(currentFarmer))
        {
            throw TofocusException.of(LejiaErrCode.NOT_RIGHT);
        }
        ZxUserInfo zxUserInfo = zxUserInfoDao.get(pkey, ascription, ZxUserInfo.class);
        if (zxUserInfo == null)
            throw TofocusException.of(LejiaErrCode.DATA_INEXISTENCE);
        zxUserInfo.setVendorAuto(enabled);
        zxUserInfoDao.update(zxUserInfo);
        return true;
    }
    
    public ZxUserInfoForUpdVendorUser getVendorUserInfo(Integer vendor)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        ZxUserInfoForUpdVendorUser res =
            zxUserInfoDao.get(ZxUserType.VENDOR, vendor.toString(), ascription, ZxUserInfoForUpdVendorUser.class);
        if (res == null)
            res = new ZxUserInfoForUpdVendorUser();
        res.setVendor(vendor);
        return res;
    }
    
    public boolean updVendorUserInfo(ZxUserInfoForUpdVendorUser forUpd)
    {
        // 检查合法性
        validBeforeUserInfo(forUpd);
        
        Integer ascription = CurrentSession.ascriptionPkey();
        ZxUserInfo old =
            zxUserInfoDao.get(ZxUserType.VENDOR, forUpd.getVendor().toString(), ascription, ZxUserInfo.class);
        ZxUserInfo bean;
        if (old == null)
        {
            MktVendor vendor = mktVendorDao.getVendor(forUpd.getVendor());
            if (vendor == null)
                throw TofocusException.of(LejiaErrCode.VENDOR_ERROR);
            bean = new ZxUserInfo();
            bean.setType(ZxUserType.VENDOR);
            bean.setValue(vendor.getPkey().toString());
            bean.setName(vendor.getName());
            bean.setComms(BigDecimal.ZERO);
            bean.setCardStatus(ZxCardStatus.NOT_BINDING);
            bean.setDelFlag(Boolean.FALSE);
            bean.setAscription(ascription);
        }
        else
        {
            bean = BeanUtil.beanFrom(ZxUserInfo.class, old);
        }
        BeanUtils.copyProperties(forUpd, bean, "vendor", "zxUserId", "name");
        
        // 请求中信新增/修改用户
        zxChangeUser(old, bean);
        
        zxUserInfoDao.put(bean);
        return true;
    }
    
    public ZxUserInfoForUpdVendorBank getVendorUserBank(Integer vendor)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        ZxUserInfoForUpdVendorBank res =
            zxUserInfoDao.get(ZxUserType.VENDOR, vendor.toString(), ascription, ZxUserInfoForUpdVendorBank.class);
        res.setVendor(vendor);
        return res;
    }
    
    public boolean updVendorUserBank(ZxUserInfoForUpdVendorBank forUpd)
    {
        // 检查合法性
        validBeforeUserBank(forUpd);
        
        Integer ascription = CurrentSession.ascriptionPkey();
        ZxUserInfo old =
            zxUserInfoDao.get(ZxUserType.VENDOR, forUpd.getVendor().toString(), ascription, ZxUserInfo.class);
        if (old == null || StringUtil.isBlank(old.getZxUserId()))
        {
            throw TofocusException.of(LejiaErrCode.DATA_INEXISTENCE, "请先编辑账户信息");
        }
        ZxUserInfo newBean = BeanUtil.beanFrom(ZxUserInfo.class, old);
        BeanUtils.copyProperties(forUpd, newBean, "vendor", "zxUserId", "name", "userType");
        
        // 请求中信绑卡
        zxBindCard(old, newBean);
        
        zxUserInfoDao.put(newBean);
        return true;
    }
    
    private void validBeforeUserInfo(BaseZxUserInfoForUpdUser forUpd)
    {
        // 检查合法性
        if ("1".equals(forUpd.getUserType()))
        {
            // 个人
            if (!PERSON_ID_TYPES.contains(forUpd.getUserIdType()))
                throw TofocusException.of(LejiaErrCode.PARAM_VALUE_ERR, "证件类型不合法");
        }
        else
        {
            // 企业/个体工商户
            if (!ENTERPRISE_ID_TYPES.contains(forUpd.getUserIdType()))
                throw TofocusException.of(LejiaErrCode.PARAM_VALUE_ERR, "证件类型不合法");
            if (StringUtil.isBlank(forUpd.getCorpNm()))
                throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "法人姓名不能为空");
            if (StringUtil.isBlank(forUpd.getCorpIdType()))
                throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "法人证件类型不能为空");
            if (StringUtil.isBlank(forUpd.getCorpIdNo()))
                throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "法人证件号码不能为空");
        }
    }
    
    private void validBeforeUserBank(BaseZxUserInfoForUpdBank forUpd)
    {
        // 检查合法性
        if ("1".equals(forUpd.getUserType()))
        {
            // 个人
            if (StringUtil.isNotBlank(forUpd.getAcctType()) && !PERSON_ACCT_TYPES.contains(forUpd.getAcctType()))
                throw TofocusException.of(LejiaErrCode.PARAM_VALUE_ERR, "银行账户类型不合法");
        }
        else
        {
            // 企业/个体工商户
            if (StringUtil.isBlank(forUpd.getAcctType()))
                throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "银行账户类型不能为空");
            if (!ENTERPRISE_ACCT_TYPES.contains(forUpd.getAcctType()))
                throw TofocusException.of(LejiaErrCode.PARAM_VALUE_ERR, "银行账户类型不合法");
        }
    }
    
    private void zxChangeUser(ZxUserInfo old, ZxUserInfo bean)
    {
        // 如果中信信息没有改动，则不调用中信接口
        if (!bean.hasChangedZxUser(old))
            return;
        // 请求中信新增/修改用户
        if (StringUtil.isBlank(bean.getZxUserId()))
        {
            // 新增
            if (bean.getPkey() == null)
                zxUserInfoDao.generateID(bean);
            tjZxManager.zxRegisterUser(bean);
        }
        else
        {
            // 修改
            tjZxManager.zxChangeUser(bean);
        }
    }
    
    private void zxBindCard(ZxUserInfo old, ZxUserInfo newBean)
    {
        // 如果中信信息没有改动，则不调用中信接口
        if (!newBean.hasChangedZxBank(old))
            return;
        // 用户类型是个人，或银行账户类型是个人账户，生成用户授权协议信息
        if ("1".equals(newBean.getUserType()) || "1".equals(newBean.getAcctType()) || "3".equals(newBean.getAcctType()))
            newBean.generateAuthProtocol();
        // 如果是企业/个体工商户，且银行账户类型是个人账号，则用法人信息，否则用账户信息
        if (!"1".equals(newBean.getUserType())
            && ("1".equals(newBean.getAcctType()) || "3".equals(newBean.getAcctType())))
        {
            newBean.setAcctNm(newBean.getCorpNm());
            newBean.setBankCardType(newBean.getCorpIdType());
            newBean.setBankCardNo(newBean.getCorpIdNo());
        }
        else
        {
            newBean.setAcctNm(newBean.getUserNm());
            newBean.setBankCardType(newBean.getUserIdType());
            newBean.setBankCardNo(newBean.getUserIdNo());
        }
        // 请求中信绑卡
        if (old.getCardStatus() != ZxCardStatus.BINDING_SUCCESS)
        {
            // 直接绑卡
            tjZxManager.zxBindCard(newBean, true);
            newBean.setCardStatus(ZxCardStatus.BINDING_SUCCESS);
        }
        else
        {
            // 先解绑旧卡，失败直接报错
            tjZxManager.zxBindCard(old, false);
            // 再绑新卡
            try
            {
                tjZxManager.zxBindCard(newBean, true);
                // 绑新卡成功，修改系统数据
                newBean.setCardStatus(ZxCardStatus.BINDING_SUCCESS);
            }
            // 绑新卡失败，绑回旧卡
            catch (Exception e)
            {
                try
                {
                    tjZxManager.zxBindCard(old, true);
                }
                // 绑回旧卡也失败，清空旧卡数据并报错
                catch (Exception ex)
                {
                    old.setCardStatus(ZxCardStatus.BINDING_FAILURE);
                    old.setPanNum(null);
                    old.setAcctNm(null);
                    old.setBankCardType(null);
                    old.setBankCardNo(null);
                    old.setPan(null);
                    old.setAcctType(null);
                    old.setBankPhone(null);
                    old.setAuthProtocolNo(null);
                    old.setAuthProtocolVersion(null);
                    zxUserInfoDao.put(old);
                    throw TofocusException.of(LejiaErrCode.REQUEST_OUT_ERROR,
                        "中信绑定新的银行卡失败，且绑回旧卡也失败，请重新编辑银行信息，新卡失败描述：" + e.getMessage());
                }
                // 绑旧卡成功，直接报错不修改数据
                throw TofocusException.of(LejiaErrCode.REQUEST_OUT_ERROR, "中信绑定新的银行卡失败，已绑回旧卡，失败描述：" + e.getMessage());
            }
        }
    }
    
    public List<ZxUserInfoDrop> allocatioDrop()
    {
        return zxUserInfoDao.listDrop(CurrentSession.ascriptionPkey());
    }
    
    public Boolean allocation(Integer pkey, BigDecimal amt, String remark)
    {
        ZxUserInfo info = zxUserInfoDao.get(pkey);
        if (info == null || !info.getCardStatus().equals(ZxCardStatus.BINDING_SUCCESS))
            return false;
        ZxWithdraw zw = new ZxWithdraw(info.getType(), info.getZxUserId(), info.getValue(), info.getAscription());
        zw.setStatus(ZxWithdrawStatus.ALLOCATION);
        zw.setRemark(remark);
        zw.setComms(amt);
        // 划账
        Integer allocation = tjZxManager.allocation(info, amt);
        zw.setFilePkey(allocation);
        zxWithdrawDao.add(zw);
        return true;
    }
}
