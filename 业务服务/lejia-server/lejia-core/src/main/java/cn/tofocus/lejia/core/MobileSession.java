package cn.tofocus.lejia.core;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.common.util.StringUtil;
import cn.tofocus.lejia.bean.entity.market.MktCourier;
import cn.tofocus.lejia.bean.entity.market.MktSupplier;
import cn.tofocus.lejia.bean.entity.member.MktMember;
import cn.tofocus.lejia.bean.entity.sys.SysFarmer;
import cn.tofocus.lejia.bean.entity.vendor.MktVendor;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorStaff;
import cn.tofocus.lejia.dao.market.MktCourierDao;
import cn.tofocus.lejia.dao.market.MktMemberDao;
import cn.tofocus.lejia.dao.market.MktSupplierDao;
import cn.tofocus.lejia.dao.sys.SysFarmerDao;
import cn.tofocus.lejia.dao.vendor.MktVendorDao;
import cn.tofocus.lejia.dao.vendor.MktVendorStaffDao;

@Component
public class MobileSession
{
    private static MobileSession instance;
    
    @Autowired
    private SysFarmerDao farmerDao;
    
    @Autowired
    private MktMemberDao memberDao;
    
    @Autowired
    private MktVendorDao vendorDao;
    
    @Autowired
    private MktVendorStaffDao vendorStaffDao;

    @Autowired
    private MktSupplierDao supplierDao;
    
    @Autowired
    private MktCourierDao courierDao;
    
    private ThreadLocal<MktMember> member = new ThreadLocal<>();
    
    private ThreadLocal<MktVendor> vendor = new ThreadLocal<>();

    private ThreadLocal<MktSupplier> supplier = new ThreadLocal<>();
    
    private ThreadLocal<MktCourier> courier = new ThreadLocal<>();
    
    private ThreadLocal<SysFarmer> farmer = new ThreadLocal<>();
    
    private ThreadLocal<String> openidLocal = new ThreadLocal<>();
    
    private ThreadLocal<String> tokenLocal = new ThreadLocal<>();
    
    private ThreadLocal<String> billIp = new ThreadLocal<>();
    
    private ThreadLocal<Integer> appid = new ThreadLocal<>();
    
    private ThreadLocal<Integer> qrCode = new ThreadLocal<>();
    
    private ThreadLocal<String> source = new ThreadLocal<>();
    
    private MobileSession()
    {
        instance = this;
    }
    
    public static MobileSession instance()
    {
        return instance;
    }
    
    /**
     * 当前用户
     * 
     * @return
     */
    private MktMember loginUser()
    {
        return member.get();
    }
    
    /**
     * 当前商户
     * 
     * @return
     */
    private MktVendor loginVendor()
    {
        return vendor.get();
    }

    /**
     * 当前供应商
     *
     * @return
     */
    private MktSupplier loginSupplier()
    {
        return supplier.get();
    }
    
    /**
     * 当前骑手
     * 
     * @return
     */
    private MktCourier loginCourier()
    {
        return courier.get();
    }
    
    /**
     * 当前市场
     * 
     * @return
     */
    private SysFarmer lastFarmer()
    {
        return farmer.get();
    }
    
    /**
     * 当前IP
     * 
     * @return
     */
    public String localIp()
    {
        if (StringUtil.isNotEmpty(billIp.get())) return billIp.get();
        return "47.114.144.93";
    }
    
    public Integer localAppid()
    {
        if (appid.get() != null) return appid.get();
        return null;
    }
    
    public Integer localQrCode()
    {
        if (qrCode.get() != null) return qrCode.get();
        return null;
    }
    
    public String localSource()
    {
        if (source.get() != null) return source.get();
        return null;
    }
    
    
    /**
     * 当前openid
     * 
     * @return
     */
    public String localOpenid()
    {
        return openidLocal.get();
    }
    
    /**
     * 当前市场
     * 
     * @return
     */
    public static String farmerPkey()
    {
        
        if (instance.lastFarmer() != null) return instance.lastFarmer().getPkey();
        return null;
    }
    
    public static SysFarmer farmer()
    {
        if (instance.lastFarmer() != null) return instance.lastFarmer();
        return null;
    }
    
    /**
     * 当前用户
     * 
     * @return
     */
    public static MktMember member()
    {
        return instance.loginUser();
    }
    
    /**
     * 当前商户
     * 
     * @return
     */
    public static MktVendor vendor()
    {
        return instance.loginVendor();
    }

    /**
     * 当前供应商
     *
     * @return
     */
    public static MktSupplier supplier()
    {
        return instance.loginSupplier();
    }
    
    /**
     * 当前快递员
     * 
     * @return
     */
    public static MktCourier courier()
    {
        return instance.loginCourier();
    }
    
    /**
     * 当前IP
     * 
     * @return
     */
    public static String billIp()
    {
        return instance.localIp();
    }
    
    public static Integer appid()
    {
        return instance.localAppid();
    }
    
    public static Integer qrCode()
    {
        return instance.localQrCode();
    }
    
    public static String source()
    {
        return instance.localSource();
    }
    
    /**
     * 当前openid
     * 
     * @return
     */
    public static String openid()
    {
        return instance.localOpenid();
    }
    
    /**
     * 当前用户
     * 
     * @return
     */
    public static Integer memberPkey()
    {
        if (instance.loginUser() != null) return instance.loginUser().getPkey();
        return null;
    }
    
    /**
     * 当前商户
     * 
     * @return
     */
    public static Integer vendorPkey()
    {
        if (instance.loginVendor() != null) return instance.loginVendor().getPkey();
        return null;
    }

    /**
     * 当前供应商
     *
     * @return
     */
    public static Integer supplierPkey()
    {
        if (instance.loginSupplier() != null) return instance.loginSupplier().getPkey();
        return null;
    }
    
    /**
     * 当前快递员
     * 
     * @return
     */
    public static Integer courierPkey()
    {
        if (instance.loginCourier() != null) return instance.loginCourier().getPkey();
        return null;
    }
    
    /*
     * 设置当前用户
     */
    public void setMember(MktMember mem)
    {
        member.set(mem);
    }
    
    /*
     * 设置当前商户
     */
    public void setVendor(MktVendor ven)
    {
        vendor.set(ven);
    }

    /*
     * 设置当前供应商
     */
    public void setSupplier(MktSupplier sup)
    {
        supplier.set(sup);
    }
    
    /*
     * 设置当前快递员
     */
    public void setCourier(MktCourier co)
    {
        courier.set(co);
    }
    
    /*
     * 设置当前用户
     */
    public void setMember(String openid)
    {
        if (StringUtil.isNotEmpty(openid))
        {
            openidLocal.set(openid);
            MktMember obj = memberDao.selectOne().or().eq("openid1", openid).eq("openid2", openid).done().exec();
            if (obj != null)
                member.set(obj);
            else
                member.remove();
        }
        else
        {
            member.remove();
        }
    }
    
    /*
     * 设置当前商户
     */
    public void setVendor(String openid)
    {
        if (StringUtil.isNotEmpty(openid))
        {
            openidLocal.set(openid);
            MktVendor obj = vendorDao.selectOne().eq("openid1", openid).eq("enabled", true).eq("idDel", false).exec();
            if (obj != null)
            {
                vendor.set(obj);
            }
            else
            {
                MktVendorStaff staff = vendorStaffDao.selectOne().eq("openid1", openid).eq("enabled", true).eq("idDel", false).exec();
                if(staff != null)
                {
                    obj = vendorDao.selectOne().eq("pkey", staff.getVendor()).eq("enabled", true).eq("idDel", false).exec();
                    if (obj != null)
                    {
                        vendor.set(obj);
                    }
                    else
                        vendor.remove();
                }
                else
                    vendor.remove();
            }
        }
        else
        {
            vendor.remove();
        }
    }

    /*
     * 设置当前供应商
     */
    public void setSupplier(String openid)
    {
        if (StringUtil.isNotEmpty(openid))
        {
            openidLocal.set(openid);
            MktSupplier obj = supplierDao.selectOne()
                .eq(MktSupplier.F.openid1, openid)
                .eq(MktSupplier.F.enabled, true)
                .eq(MktSupplier.F.isDel, false)
                .exec();
            if (obj != null)
            {
                supplier.set(obj);
            }
            else
            {
                supplier.remove();
            }
        }
        else
        {
            supplier.remove();
        }
    }

    /*
     * 设置当前商户
     */
    public void setVendorToken(String token)
    {
        if (StringUtil.isNotEmpty(token))
        {
            tokenLocal.set(token);
            MktVendor obj = vendorDao.selectOne()
                .eq("token", token)
                .eq("enabled", true).eq("idDel", false).exec();
            if (obj != null)
                vendor.set(obj);
            else
                vendor.remove();
        }
        else
        {
            vendor.remove();
        }
    }
    
    /*
     * 设置当前快递
     */
    public void setCourier(String openid)
    {
        if (StringUtil.isNotEmpty(openid))
        {
            openidLocal.set(openid);
            MktCourier obj = courierDao.selectOne().eq("openid1", openid).eq("enabled", true).eq("idDel", false).exec();
            if (obj != null)
            {
                courier.set(obj);
                setFarmer(obj.getFarmer());
            }
            else
            {
                courier.remove();
            }
        }
        else
        {
            courier.remove();
        }
    }
    
    /*
     * 设置当前市场
     */
    public void setFarmer(String pkey)
    {
        if (pkey != null) farmer.set(farmerDao.get(pkey));
    }
    
    /*
     * 设置当前市场
     */
    public void setBillIp(String billIP)
    {
        billIp.set(billIP);
    }
    
    public void setAppid(String appid)
    {
        this.appid.set(Integer.valueOf(appid));
    }
    
    public void setQrCode(String qrCode)
    {
        if(StringUtils.isNotBlank(qrCode))
            this.qrCode.set(Integer.valueOf(qrCode));
    }
    
    public void setSource(String source)
    {
        if(StringUtils.isNotBlank(source) && !"[object Null]".equals(source))
            this.source.set(source);
    }
    
    public void removeAll()
    {
        member.remove();
        vendor.remove();
        supplier.remove();
        courier.remove();
        farmer.remove();
        openidLocal.remove();
        tokenLocal.remove();
        billIp.remove();
        appid.remove();
        qrCode.remove();
        source.remove();
    }
}
