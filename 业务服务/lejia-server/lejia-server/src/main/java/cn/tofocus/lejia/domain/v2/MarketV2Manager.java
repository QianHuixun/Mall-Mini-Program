package cn.tofocus.lejia.domain.v2;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.db.SelectBuilder;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.bean.dto.market.DropStringDown;
import cn.tofocus.lejia.bean.entity.sys.SysFarmer;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.sys.SysAscriptionDao;
import cn.tofocus.lejia.dao.sys.SysFarmerDao;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MarketV2Manager
{
    @Autowired
    private SysAscriptionDao sysAscriptionDao;
    
    @Autowired
    private SysFarmerDao sysFarmerDao;
    
    public List<DropStringDown> listDropName(boolean includeAscription)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        String companyPkey = CurrentSession.companyPkey();
        String farmerPkey = CurrentSession.marketPkey();
        if (companyPkey == null)
            return new ArrayList<>();
        SelectBuilder<String, SysFarmer> builder = sysFarmerDao.select()
            .notEq("pkey", (Constant.Operation + ascription))
            .eq("ascription", ascription)
            .eq("idDel", false);
        if (!companyPkey.equals(Constant.Operation + ascription))
        {
            builder.eq("org", companyPkey);
        }
        if (farmerPkey != null && !farmerPkey.equals(Constant.Operation + ascription))
        {
            builder.eq("pkey", farmerPkey);
        }
        List<DropStringDown> list = builder.execDto(DropStringDown.class);
        if (includeAscription)
        {
            DropStringDown item = sysFarmerDao.get(Constant.Operation + ascription, DropStringDown.class);
            if (item != null)
                list.add(0, item);
        }
        return list;
    }
}
