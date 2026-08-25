package cn.tofocus.lejia.dao.sys;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.sys.SysFarmerMtype;
import cn.tofocus.lejia.bean.enums.MType;

@Component
public class SysFarmerMtypeDao extends JpaSpecificationDelegate<Integer, SysFarmerMtype>
{
    public Map<MType,SysFarmerMtype> mapMType(String farmer)
    {
        Map<MType,SysFarmerMtype> res = new HashMap<>();
        List<SysFarmerMtype> list = this.select().eq("farmer", farmer).exec();
        list.forEach(e -> {
            res.put(e.getMType(), e);
        });
        return res;
    }
    
    public SysFarmerMtype byFarmerMtype(String farmer, MType mType)
    {
        return this.selectOne().eq("farmer", farmer).eq("mType", mType).exec();
    }
}