package cn.tofocus.lejia.dao.market;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.member.MktMemberIndexAdvert;

@Component
public class MktMemberIndexAdvertDao extends JpaSpecificationDelegate<String, MktMemberIndexAdvert>
{
    public List<Integer> listIndexAdver(Integer memberPkey)
    {
        List<MktMemberIndexAdvert> list = this.select().eq("member", memberPkey).exec();
        List<Integer> res = new ArrayList<>();
        list.forEach(e -> res.add(e.getIndexAdvert()));
        return res;
    }
}