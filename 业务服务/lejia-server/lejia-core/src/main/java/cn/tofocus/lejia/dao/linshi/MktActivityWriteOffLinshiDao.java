package cn.tofocus.lejia.dao.linshi;

import org.springframework.stereotype.Component;

import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.linshi.MktActivityWriteOffLinshi;
import cn.tofocus.lejia.bean.entity.linshi.MktActivityWriteOffLinshi.F;

@Component
public class MktActivityWriteOffLinshiDao extends JpaSpecificationDelegate<String, MktActivityWriteOffLinshi>
{
    public boolean exist(String name, Integer member, Integer ascription)
    {
        MktActivityWriteOffLinshi exist =
            this.selectOne().eq(F.name, name).eq(F.member, member).eq(F.ascription, ascription).exec();
        return exist != null;
    }
}
