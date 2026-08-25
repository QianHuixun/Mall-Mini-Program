package cn.tofocus.lejia.dao.sys;

import java.util.List;

import org.springframework.stereotype.Component;

import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.sys.SysFarmerTime;

@Component
public class SysFarmerTimeDao extends JpaSpecificationDelegate<Integer, SysFarmerTime>
{
    public List<SysFarmerTime> listTime(String farmer, Integer ascription)
    {
        return this.select()
            .eq("farmer", farmer)
            .eq("ascription", ascription)
            .exec();
    }
    
}
