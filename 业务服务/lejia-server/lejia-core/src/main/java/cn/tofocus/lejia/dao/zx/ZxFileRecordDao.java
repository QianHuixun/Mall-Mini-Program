package cn.tofocus.lejia.dao.zx;

import java.util.List;

import org.springframework.stereotype.Component;

import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.zx.ZxFileRecord;
import cn.tofocus.lejia.bean.enums.ZxFileStatus;
import cn.tofocus.lejia.bean.enums.ZxFileType;

@Component
public class ZxFileRecordDao extends JpaSpecificationDelegate<Integer, ZxFileRecord>
{
    // day yyyyMMdd
    public String getNextXuhao(String day)
    {
        long count = this.aggregation().like("name", day).execCount();
        count += 1;
        if(count < 10)
            return "0" + count;
        else
            return count + "";
    }
    
    public ZxFileRecord byName(String name)
    {
        return this.selectOne().eq("name", name).exec();
    }
    
    public List<ZxFileRecord> listAllocation(ZxFileType type, String start, String end)
    {
        return this.select().eq("type", type)
        .eq("status", ZxFileStatus.UPLOAD_SYCCESS)
        .between("createdTime", start, end)
        .exec();
    }
}