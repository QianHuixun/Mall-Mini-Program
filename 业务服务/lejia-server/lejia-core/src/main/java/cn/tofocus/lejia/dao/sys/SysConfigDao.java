package cn.tofocus.lejia.dao.sys;

import org.springframework.stereotype.Component;

import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.sys.SysConfigEntity;

import java.util.List;

@Component
public class SysConfigDao extends JpaSpecificationDelegate<String, SysConfigEntity>
{
    public Boolean getValue(String pkey, Integer ascription)
    {
        Boolean res = false;
        SysConfigEntity entity = this.get(pkey + "_" + ascription);
        if(entity == null)
        {
            return res;
        }
        try
        {
            Integer of = Integer.valueOf(entity.getValue());
            if(of != null && of == 1)
            {
                res = true;
            }
        }
        catch (Exception e)
        {
        }
        return res;
    }

    public SysConfigEntity getBean(String pkey, Integer ascription)
    {
        return this.get(pkey + "_" + ascription);
    }

    public String getTemplate(String prefix, Integer ascription)
    {
        SysConfigEntity e = this.get(prefix + "_" + ascription);
        if(e != null)
            return e.getValue();
        else
            return null;
    }
    
    /**
     * 批量修改配置
     * @param list  配置列表
     * @return      是否成功
     */
    public Boolean updateBatch(List<SysConfigEntity> list)
    {
        this.putAll(list);
        return true;
    }
}