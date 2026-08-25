package cn.tofocus.lejia.dao.jd;

import java.util.List;

import org.springframework.stereotype.Component;

import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.jd.JdAddress;
import cn.tofocus.lejia.bean.entity.jd.JdAddress.F;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JdAddressDao extends JpaSpecificationDelegate<Long, JdAddress>
{
    public List<JdAddress> listByName(String name)
    {
        return this.select().or().eq(F.areaName, name).eq(F.clientName, name).done().exec();
    }
    
    public <T> List<T> listByParent(Long parent, Class<T> clazz)
    {
        return this.select().eq(F.parent, parent).execDto(clazz);
    }
    
    public void listAllChildren(List<JdAddress> list, Long parent)
    {
        List<JdAddress> children = this.select().eq(F.parent, parent).exec();
        for (JdAddress child : children)
        {
            list.add(child);
            // 递归处理子区域的子区域
            listAllChildren(list, child.getAreaId());
        }
    }
    
    public Integer byAreaId(String name, Integer level)
    {
        JdAddress ja = this.selectOne()
        .eq(F.areaLevel, level)
        .or().eq(F.areaName, name).eq(F.clientName, name).done()
        .exec();
        if(ja != null)
            return ja.getAreaId().intValue();
        return null;
    }
    
    public String getNameById(Long id)
    {
        JdAddress address = this.get(id);
        if (address == null)
            return null;
        return address.getAreaName();
    }
}
