package cn.tofocus.lejia.dao.zx;

import java.util.Date;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.zx.ZxPostRecord;

@Component
public class ZxPostRecordDao extends JpaSpecificationDelegate<Integer, ZxPostRecord>
{
    @Transactional
    public ZxPostRecord addPostRecord(String reqInterface, String reqContent)
    {
        ZxPostRecord bean = new ZxPostRecord();
        bean.setReqInterface(reqInterface);
        bean.setReqContent(reqContent);
        ZxPostRecord add = this.add(bean);
        return add;
    }
    
    @Transactional
    public Boolean updPostRecord(ZxPostRecord bean, String content)
    {
        bean.setContent(content);
        bean.setTime(new Date());
        this.update(bean);
        return true;
    }
    
}