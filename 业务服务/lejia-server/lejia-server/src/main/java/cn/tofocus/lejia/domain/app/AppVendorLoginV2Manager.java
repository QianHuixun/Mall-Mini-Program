package cn.tofocus.lejia.domain.app;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.common.data.datadealer.MobileDealer;
import cn.tofocus.common.notify.SMSNotify;
import cn.tofocus.common.notify.config.SmsConfig;
import cn.tofocus.core.exception.SysErrCode;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.db.SelectBuilder;
import cn.tofocus.lejia.bean.entity.market.MktSupplier;
import cn.tofocus.lejia.bean.entity.vendor.MktVendor;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorStaff;
import cn.tofocus.lejia.bean.enums.AppVendorLoginRole;
import cn.tofocus.lejia.cache.MobileCodeMap;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.dao.market.MktSupplierDao;
import cn.tofocus.lejia.dao.vendor.MktVendorDao;
import cn.tofocus.lejia.dao.vendor.MktVendorStaffDao;
import cn.tofocus.lejia.exception.LejiaErrCode;
import cn.tofocus.lejia.exception.WsaleErrCode;
import cn.tofocus.lejia.util.NumberUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AppVendorLoginV2Manager
{
    @Autowired
    private MktVendorDao vendorDao;
    
    @Autowired
    private MktVendorStaffDao vendorStaffDao;
    
    @Autowired
    private MktSupplierDao supplierDao;
    
    @Autowired
    private MobileCodeMap mobileMap;
    
    @Autowired
    private SmsConfig smsConfig;
    
    public boolean loginCaptcha(String phone)
    {
        checkPhone(phone);
        MktVendor vendor = vendorDao.selectOne().eq(MktVendor.F.mobile, phone)
            .eq(MktVendor.F.idDel, false)
            .eq(MktVendor.F.ascription, MobileSession.appid())
            .exec();
        MktVendorStaff vendorStaff =
            vendorStaffDao.selectOne().eq(MktVendorStaff.F.mobile, phone).eq(MktVendorStaff.F.idDel, false)
            .eq(MktVendorStaff.F.ascription, MobileSession.appid()).exec();
        MktSupplier supplier =
            supplierDao.selectOne().eq(MktSupplier.F.mobile, phone).eq(MktSupplier.F.isDel, false)
            .eq(MktSupplier.F.ascription, MobileSession.appid())
            .exec();
        if (vendor == null && vendorStaff == null && supplier == null)
            throw TofocusException.of(LejiaErrCode.LOGIN_MOBILE_NOT_FOUND);
        // 有一个角色可以登录
        if ((vendor != null && Boolean.TRUE.equals(vendor.getEnabled()))
            || (vendorStaff != null && Boolean.TRUE.equals(vendorStaff.getEnabled()))
            || (supplier != null && Boolean.TRUE.equals(supplier.getEnabled())))
        {
            String code = NumberUtils.createCheckCode();
            mobileMap.put(phone, code);
            log.info("[商户小程序登录] 手机验证码：" + code);
            return new SMSNotify(smsConfig).sendCode(phone, code);
        }
        // 有角色，但被禁用
        else
            throw TofocusException.of(LejiaErrCode.LOGIN_MOBILE_DISABLED);
    }
    
    public List<AppVendorLoginRole> roles(String phone, String openid)
    {
        MktVendor vendor = vendorDao.selectOne()
            .eq(MktVendor.F.mobile, phone)
            .eq(MktVendor.F.enabled, true)
            .eq(MktVendor.F.idDel, false)
            .eq(MktVendor.F.ascription, MobileSession.appid())
            .exec();
        MktVendorStaff vendorStaff = vendorStaffDao.selectOne()
            .eq(MktVendorStaff.F.mobile, phone)
            .eq(MktVendorStaff.F.enabled, true)
            .eq(MktVendorStaff.F.idDel, false)
            .eq(MktVendorStaff.F.ascription, MobileSession.appid())
            .exec();
        MktSupplier supplier = supplierDao.selectOne()
            .eq(MktSupplier.F.mobile, phone)
            .eq(MktSupplier.F.enabled, true)
            .eq(MktSupplier.F.isDel, false)
            .eq(MktSupplier.F.ascription, MobileSession.appid())
            .exec();
        if (vendor == null && vendorStaff == null && supplier == null)
            throw TofocusException.of(LejiaErrCode.LOGIN_MOBILE_NOT_FOUND);
        List<AppVendorLoginRole> list = new ArrayList<>();
        if (vendor != null) list.add(AppVendorLoginRole.VENDOR);
        if (vendorStaff != null) list.add(AppVendorLoginRole.VENDOR_STAFF);
        if (supplier != null) list.add(AppVendorLoginRole.SUPPLIER);
        return list;
    }
    
    public boolean login(String phone, AppVendorLoginRole role, String code, String openid)
    {
        if ("[object Null]".equals(openid)) throw TofocusException.of(WsaleErrCode.OPENID_ERROR);
        log.info("[商户小程序登录] 开始登录，手机号：{}，角色：{}", phone, role);
        String captcha = mobileMap.get(phone);
        if (captcha == null) throw TofocusException.of(WsaleErrCode.WRONG_CODE);
        // 验证码是840727 的时候 都给通过
        if (!captcha.equals(code) && !"840727".equals(code)) throw TofocusException.of(WsaleErrCode.WRONG_CODE);
        
        Integer vendorPkey = null;
        Integer vendorStaffPkey = null;
        Integer supplierPkey = null;
        switch (role)
        {
            case VENDOR:
                MktVendor vendor = vendorDao.selectOne()
                    .eq(MktVendor.F.mobile, phone)
                    .eq(MktVendor.F.enabled, true)
                    .eq(MktVendor.F.idDel, false)
                    .eq(MktVendor.F.ascription, MobileSession.appid())
                    .exec();
                if (vendor == null) throw TofocusException.of(LejiaErrCode.LOGIN_MOBILE_NOT_FOUND);
                vendor.setOpenid1(openid);
                vendorDao.update(vendor);
                vendorPkey = vendor.getPkey();
                break;
            case VENDOR_STAFF:
                MktVendorStaff vendorStaff = vendorStaffDao.selectOne()
                    .eq(MktVendorStaff.F.mobile, phone)
                    .eq(MktVendorStaff.F.enabled, true)
                    .eq(MktVendorStaff.F.idDel, false)
                    .eq(MktVendorStaff.F.ascription, MobileSession.appid())
                    .exec();
                if (vendorStaff == null) throw TofocusException.of(LejiaErrCode.LOGIN_MOBILE_NOT_FOUND);
                vendorStaff.setOpenid1(openid);
                vendorStaffDao.update(vendorStaff);
                vendorStaffPkey = vendorStaff.getPkey();
                break;
            case SUPPLIER:
                MktSupplier supplier = supplierDao.selectOne()
                    .eq(MktSupplier.F.mobile, phone)
                    .eq(MktSupplier.F.enabled, true)
                    .eq(MktSupplier.F.isDel, false)
                    .eq(MktSupplier.F.ascription, MobileSession.appid())
                    .exec();
                if (supplier == null) throw TofocusException.of(LejiaErrCode.LOGIN_MOBILE_NOT_FOUND);
                supplier.setOpenid1(openid);
                supplierDao.update(supplier);
                supplierPkey = supplier.getPkey();
                break;
            default:
                throw TofocusException.of(LejiaErrCode.DATA_NOT_ALLOWD, "角色错误");
        }
        // 其他角色的 openid1 都设置为null
        SelectBuilder<Integer, MktVendor> vendorBuilder =
            vendorDao.select().strict(true).eq(MktVendor.F.openid1, openid);
        if (vendorPkey != null) vendorBuilder.notEq(MktVendor.F.pkey, vendorPkey);
        vendorBuilder.update(MktVendor.F.openid1, null);
        SelectBuilder<Integer, MktVendorStaff> vendorStaffBuilder =
            vendorStaffDao.select().strict(true).eq(MktVendorStaff.F.openid1, openid);
        if (vendorStaffPkey != null) vendorStaffBuilder.notEq(MktVendorStaff.F.pkey, vendorStaffPkey);
        vendorStaffBuilder.update(MktVendorStaff.F.openid1, null);
        SelectBuilder<Integer, MktSupplier> supplierBuilder =
            supplierDao.select().strict(true).eq(MktSupplier.F.openid1, openid);
        if (supplierPkey != null) supplierBuilder.notEq(MktSupplier.F.pkey, supplierPkey);
        supplierBuilder.update(MktSupplier.F.openid1, null);
        return true;
    }
    
    public AppVendorLoginRole checkLogin(String openid)
    {
        MktVendor vendor = vendorDao.selectOne()
            .eq(MktVendor.F.openid1, openid)
            .eq(MktVendor.F.enabled, true)
            .eq(MktVendor.F.idDel, false)
            .eq(MktVendor.F.ascription, MobileSession.appid())
            .exec();
        if (vendor == null)
        {
            MktVendorStaff vendorStaff = vendorStaffDao.selectOne()
                .eq(MktVendorStaff.F.openid1, openid)
                .eq(MktVendorStaff.F.enabled, true)
                .eq(MktVendorStaff.F.idDel, false)
                .eq(MktVendorStaff.F.ascription, MobileSession.appid())
                .exec();
            if (vendorStaff == null)
            {
                MktSupplier supplier = supplierDao.selectOne()
                    .eq(MktSupplier.F.openid1, openid)
                    .eq(MktSupplier.F.enabled, true)
                    .eq(MktSupplier.F.isDel, false)
                    .eq(MktSupplier.F.ascription, MobileSession.appid())
                    .exec();
                if (supplier == null) return null;
                return AppVendorLoginRole.SUPPLIER;
            }
            return AppVendorLoginRole.VENDOR_STAFF;
        }
        return AppVendorLoginRole.VENDOR;
    }
    
    /**
     * 验证手机号格式
     */
    private String checkPhone(String phone)
    {
        MobileDealer dealer = new MobileDealer();
        String result = dealer.convert(phone);
        if (result == null || result.isEmpty())
        {
            throw TofocusException.of(SysErrCode.PHONE_ERROR, phone);
        }
        else
        {
            return result;
        }
    }
}
