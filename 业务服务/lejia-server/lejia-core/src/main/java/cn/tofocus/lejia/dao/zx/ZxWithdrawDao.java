package cn.tofocus.lejia.dao.zx;

import java.util.List;

import org.springframework.stereotype.Component;

import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.zx.ZxWithdraw;
import cn.tofocus.lejia.bean.entity.zx.ZxWithdraw.F;

@Component
public class ZxWithdrawDao extends JpaSpecificationDelegate<Integer, ZxWithdraw>
{
    public List<ZxWithdraw> listFilePkey(Integer filePkey)
    {
        return this.select().eq(F.filePkey, filePkey).exec();
    }
    public ZxWithdraw byFilePkey(Integer filePkey)
    {
        return this.selectOne().eq(F.filePkey, filePkey).exec();
    }
}
