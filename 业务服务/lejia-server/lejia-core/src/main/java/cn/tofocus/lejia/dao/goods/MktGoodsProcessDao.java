package cn.tofocus.lejia.dao.goods;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsProcess;

@Component
public class MktGoodsProcessDao extends JpaSpecificationDelegate<Integer, MktGoodsProcess>
{
    public List<Integer> listProcess(Integer goods)
    {
        List<MktGoodsProcess> list = this.select().eq("goods", goods).exec();
        List<Integer> res = new ArrayList<>();
        list.forEach(e -> res.add(e.getProcess()));
        return res;
    }
}
