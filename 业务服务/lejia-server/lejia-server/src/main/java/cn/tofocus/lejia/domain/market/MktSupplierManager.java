package cn.tofocus.lejia.domain.market;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.common.util.CollectionUtil;
import cn.tofocus.common.util.StringUtil;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.market.MktSupplierInfo;
import cn.tofocus.lejia.bean.dto.market.MktSupplierOnPage;
import cn.tofocus.lejia.bean.dto.market.MktSupplierOption;
import cn.tofocus.lejia.bean.entity.market.MktSupplier;
import cn.tofocus.lejia.bean.entity.market.MktSupplierPickupLocation;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.goods.MktGoodsDao;
import cn.tofocus.lejia.dao.market.MktSupplierDao;
import cn.tofocus.lejia.dao.market.MktSupplierPickupLocationDao;
import cn.tofocus.lejia.dao.vendor.MktVendorDao;
import cn.tofocus.lejia.exception.LejiaErrCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MktSupplierManager
{
    @Autowired
    private MktSupplierDao supplierDao;
    
    @Autowired
    private MktSupplierPickupLocationDao supplierPickupLocationDao;
    
    @Autowired
    private MktGoodsDao goodsDao;
    
    @Autowired
    private MktVendorDao vendorDao;
    
    public PageResult<MktSupplierOnPage> query(int page, int pagesize, String name, String mobile, Boolean enabled)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        return supplierDao.query(page, pagesize, ascription, name, mobile, enabled, MktSupplierOnPage.class);
    }
    
    public MktSupplierInfo get(Integer pkey)
    {
        MktSupplierInfo info = supplierDao.get(pkey, MktSupplierInfo.class);
        if (info == null || !Objects.equals(info.getAscription(), CurrentSession.ascriptionPkey()))
            throw TofocusException.of(LejiaErrCode.SUPPLIER_NOT_FOUND);
        List<MktSupplierInfo.PickupLocation> pickupLocations =
            supplierPickupLocationDao.findBySupplier(pkey, info.getAscription(), MktSupplierInfo.PickupLocation.class);
        info.setPickupLocations(pickupLocations);
        return info;
    }
    
    public boolean save(MktSupplierInfo info)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        
        // 检查
        valid(info);
        // 同运营端下，名称不能重复
        if (supplierDao.existName(info.getName(), info.getPkey(), ascription))
            throw TofocusException.of(LejiaErrCode.TAG_NAME_EXIST);
        // 同运营端下，手机号和商户手机号都不能重复
        if (supplierDao.existMobile(info.getMobile(), info.getPkey(), ascription))
            throw TofocusException.of(LejiaErrCode.MOBILE_ERROR);
        if (vendorDao.existMobile(info.getMobile(), null, ascription))
            throw TofocusException.of(LejiaErrCode.MOBILE_ERROR, "手机号已被占用");
        
        if(info.getAllowedDelivery() == null && info.getAllowedPickup() == null)
            throw TofocusException.of(LejiaErrCode.DELIVERY_PICKUP_ERRPOR);
        
        MktSupplier bean = null;
        List<MktSupplierPickupLocation> oldPickupLocations = null;
        if (info.getPkey() == null)
        {
            bean = BeanUtil.beanFrom(MktSupplier.class, info);
            bean.setEnabled(true);
            bean.setIsDel(false);
            bean.setFarmer(CurrentSession.marketPkey());
            bean.setCompany(CurrentSession.companyPkey());
            bean.setAscription(CurrentSession.ascriptionPkey());
            oldPickupLocations = new ArrayList<>();
        }
        else
        {
            bean = supplierDao.get(info.getPkey());
            if (bean == null) throw TofocusException.of(LejiaErrCode.SUPPLIER_NOT_FOUND);
            BeanUtils.copyProperties(info, bean);
            oldPickupLocations = supplierPickupLocationDao.findBySupplier(bean.getPkey(), ascription);
        }
        bean = supplierDao.put(bean);
        List<MktSupplierPickupLocation> pickupLocations =
            Lists.newArrayListWithCapacity(info.getPickupLocations().size());
        for (MktSupplierInfo.PickupLocation location : info.getPickupLocations())
        {
            MktSupplierPickupLocation locationBean = BeanUtil.beanFrom(MktSupplierPickupLocation.class, location);
            locationBean.setSupplier(bean.getPkey());
            locationBean.setAscription(ascription);
            pickupLocations.add(locationBean);
        }
        supplierPickupLocationDao.removeAndPutAll(oldPickupLocations, pickupLocations);
        return true;
    }
    
    private void valid(MktSupplierInfo info)
    {
        if (Boolean.TRUE.equals(info.getAllowedPickup()) && CollectionUtil.isEmpty(info.getPickupLocations()))
            throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "请至少添加一个自提点");
        // 顺丰快递字段，有任何一个填了，其他都要填
        if (StringUtil.isNotBlank(info.getExpressSender()) || StringUtil.isNotBlank(info.getExpressMobile())
            || StringUtil.isNotBlank(info.getExpressPro()) || StringUtil.isNotBlank(info.getExpressCity())
            || StringUtil.isNotBlank(info.getExpressArea()) || StringUtil.isNotBlank(info.getExpressAddress())
            || StringUtil.isNotBlank(info.getSfMonthlyCard()) || StringUtil.isNotBlank(info.getSfAppId())
            || StringUtil.isNotBlank(info.getSfSk()))
        {
            if (StringUtil.isBlank(info.getExpressSender()))
                throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "请输入寄件人姓名");
            if (StringUtil.isBlank(info.getExpressMobile()))
                throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "请输入寄件人手机号");
            if (StringUtil.isBlank(info.getExpressPro()) || StringUtil.isBlank(info.getExpressCity())
                || StringUtil.isBlank(info.getExpressArea()))
                throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "请选择寄件地区");
            if (StringUtil.isBlank(info.getExpressAddress()))
                throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "请输入寄件详细地址");
            if (StringUtil.isBlank(info.getSfMonthlyCard()))
                throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "请输入顺丰月结卡号");
            if (StringUtil.isBlank(info.getSfAppId()))
                throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "请输入顺丰寄件appId");
            if (StringUtil.isBlank(info.getSfSk())) throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "请输入顺丰寄件sk");
        }
    }
    
    public boolean del(Integer pkey)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        MktSupplier bean = supplierDao.get(pkey);
        if (bean == null || !Objects.equals(bean.getAscription(), ascription))
            throw TofocusException.of(LejiaErrCode.SUPPLIER_NOT_FOUND);
        if (Boolean.TRUE.equals(bean.getEnabled())) throw TofocusException.of(LejiaErrCode.SUPPLIER_ENABLED_CANNOT_DEL);
        // 将供应商关联的商品下架并置空所属供应商
        goodsDao.clearGoodsSupplier(ascription, pkey);
        // 删除供应商
        bean.setName(bean.getPkey() + "_" + bean.getName());
        bean.setMobile(bean.getPkey() + "_" + bean.getMobile());
        bean.setEnabled(false);
        bean.setIsDel(true);
        supplierDao.update(bean);
        return true;
    }
    
    public boolean enable(Integer pkey, Boolean enabled)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        MktSupplier bean = supplierDao.get(pkey);
        if (bean == null || !Objects.equals(bean.getAscription(), ascription))
            throw TofocusException.of(LejiaErrCode.SUPPLIER_NOT_FOUND);
        bean.setEnabled(enabled);
        supplierDao.update(bean);
        return true;
    }
    
    public List<MktSupplierOption> options(String keyword)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        return supplierDao.list(ascription, keyword, MktSupplierOption.class);
    }
}
