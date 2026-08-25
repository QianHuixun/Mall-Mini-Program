package cn.tofocus.lejia.dao.h5;

import org.springframework.stereotype.Component;

import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.dto.h5.H5UserInfo;
import cn.tofocus.lejia.bean.entity.h5.H5User;
import cn.tofocus.lejia.bean.entity.h5.H5User.F;

@Component
public class H5UserDao extends JpaSpecificationDelegate<Integer, H5User>
{
    public H5User byMobileAndFarmer(String mobile, String farmer)
    {
        return this.selectOne().eq(F.mobile, mobile).eq(F.farmer, farmer).exec();
    }
    
    public H5User byUserid(String userid)
    {
        return this.selectOne().eq(F.userid, userid).exec();
    }
    
    public H5UserInfo byPkeyDto(Integer pkey)
    {
        return this.selectOne().eq(F.pkey, pkey).execDto(H5UserInfo.class);
    }
}
