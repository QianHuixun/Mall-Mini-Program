package cn.tofocus.lejia.domain.v4;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.lejia.bean.entity.sys.SysFarmerExtend;
import cn.tofocus.lejia.dao.sys.SysFarmerExtendDao;

@Component
public class HandMovementV4Manager
{
    @Autowired
    private SysFarmerExtendDao farmerExtendDao;
    
    public Boolean putFarmerExtend(SysFarmerExtend info)
    {
        farmerExtendDao.put(info);
        return true;
    }
}
