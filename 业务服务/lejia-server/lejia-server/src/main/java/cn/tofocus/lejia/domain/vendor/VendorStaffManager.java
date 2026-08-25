package cn.tofocus.lejia.domain.vendor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.vendor.VendorStaffOnPage;
import cn.tofocus.lejia.bean.dto.vendor.VendorStaffUpdInfo;
import cn.tofocus.lejia.bean.entity.vendor.MktVendor;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorStaff;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.vendor.MktVendorDao;
import cn.tofocus.lejia.dao.vendor.MktVendorStaffDao;
import cn.tofocus.lejia.exception.LejiaErrCode;
import io.micrometer.core.instrument.util.StringUtils;

@Component
public class VendorStaffManager
{
    @Autowired
    private MktVendorStaffDao vendorStaffDao;
    
    @Autowired
    private MktVendorDao vendorDao;
    
    public PageResult<VendorStaffOnPage> queryVendorStaff(int page, int pagesize, Integer vendor, String content)
    {
        String marketPkey = CurrentSession.marketPkey();
        Integer ascription = CurrentSession.ascriptionPkey();
        if((Constant.Operation + ascription).equals(marketPkey))
            marketPkey = null;
        return vendorStaffDao.query(page, pagesize, vendor, content, marketPkey, ascription, VendorStaffOnPage.class);
    }
    
    public Integer addVendorStaff(VendorStaffUpdInfo info)
    {
        String marketPkey = CurrentSession.marketPkey();
        Integer ascription = CurrentSession.ascriptionPkey();
        MktVendorStaff bean = new MktVendorStaff();
        BeanUtils.copyProperties(info, bean);
        bean.setIdDel(false);
        bean.setEnabled(true);
        bean.setAscription(ascription);
        MktVendor vendor = vendorDao.get(info.getVendor());
        if (vendor == null) throw TofocusException.of(LejiaErrCode.VENDOR_ERROR);
        bean.setVendorName(vendor.getName());
        if ((Constant.Operation + ascription).equals(marketPkey) && StringUtils.isBlank(info.getFarmer()))
        {
            throw TofocusException.of(LejiaErrCode.MARKET_INEXISTENCE);
        }
        if (!(Constant.Operation + ascription).equals(marketPkey)) bean.setFarmer(marketPkey);
        String mobile = bean.getMobile();
        long count = vendorStaffDao.aggregation().eq("mobile", mobile).eq("ascription", ascription).execCount();
        if (count > 0) throw TofocusException.of(LejiaErrCode.MOBILE_ERROR);
        count = vendorDao.aggregation().eq("mobile", mobile).eq("ascription", ascription).execCount();
        if (count > 0) throw TofocusException.of(LejiaErrCode.MOBILE_ERROR);
        MktVendorStaff add = vendorStaffDao.add(bean);
        return add.getPkey();
    }
    
    public Boolean updVendorStaff(VendorStaffUpdInfo info)
    {
        MktVendorStaff bean = vendorStaffDao.get(info.getPkey());
        String marketPkey = CurrentSession.marketPkey();
        Integer ascription = CurrentSession.ascriptionPkey();
        BeanUtils.copyProperties(info, bean);
        bean.setAscription(ascription);
        if (!(Constant.Operation + ascription).equals(marketPkey)) bean.setFarmer(marketPkey);
        
        String mobile = info.getMobile();
        long count = vendorStaffDao.aggregation().notEq("pkey", info.getPkey())
            .eq("mobile", mobile)
            .eq("ascription", ascription)
            .execCount();
        if (count > 0) throw TofocusException.of(LejiaErrCode.MOBILE_ERROR);
        count = vendorDao.aggregation().eq("mobile", mobile).eq("ascription", ascription).execCount();
        if (count > 0) throw TofocusException.of(LejiaErrCode.MOBILE_ERROR);
        MktVendor vendor = vendorDao.get(info.getVendor());
        if(vendor != null)
            bean.setVendorName(vendor.getName());
        vendorStaffDao.update(bean);
        return true;
    }
    
    public Boolean enabledVendorStaff(Integer pkey, Boolean enabled)
    {
        MktVendorStaff staff = vendorStaffDao.get(pkey);
        if (staff == null) throw TofocusException.of(LejiaErrCode.DATA_INEXISTENCE);
        staff.setEnabled(enabled);
        vendorStaffDao.update(staff);
        return true;
    }
    
    public Boolean deVendorStaff(Integer pkey)
    {
        MktVendorStaff staff = vendorStaffDao.get(pkey);
        if (staff == null) throw TofocusException.of(LejiaErrCode.DATA_INEXISTENCE);
        staff.setIdDel(true);
        String mobile = staff.getMobile();
        String PrefixM = "0000";
        List<MktVendorStaff> delExec = vendorStaffDao.select().like("mobile", mobile).eq("ascription", staff.getAscription()).exec();
        if (delExec != null && delExec.size() > 0)
        {
            List<Integer> prefixMList = new ArrayList<>();
            for (MktVendorStaff c : delExec)
                prefixMList.add(Integer.valueOf(c.getMobile().substring(0, 4)));
            Collections.sort(prefixMList);
            PrefixM = String.format("%04d", prefixMList.get(prefixMList.size() - 1) + 1);
        }
        staff.setMobile(PrefixM + mobile);
        vendorStaffDao.update(staff);
        return true;
    }
}
