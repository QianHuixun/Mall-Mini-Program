package cn.tofocus.account.db.dao.user;

import org.springframework.stereotype.Component;

import cn.tofocus.account.bean.AccountConstant;
import cn.tofocus.account.db.entity.user.UserEntity;
import cn.tofocus.account.db.entity.user.UserEntity.F;
import cn.tofocus.common.Constant;
import cn.tofocus.db.jpa.dao.JpaNotifyedDao;

@Component
public class UserDao extends JpaNotifyedDao<Long, UserEntity>
{
    @Override
    protected String domain()
    {
        return Constant.TfDomain;
    }
    
    @Override
    protected String notifyedCacheName()
    {
        return AccountConstant.UserNameAccess;
    }
    
    public UserEntity getUserByUserid(String userid)
    {
        return this.selectOne().strict(true).eq(F.userid, userid).exec();
    }
    
    public UserEntity getUserbyPhone(String domain, String phone)
    {
        return this.selectOne().strict(true).eq(F.registFromDomain, domain).eq(F.bindPhone, phone).exec();
    }
    
    public boolean isPhoneUsed(String domain, String excludUserid, String phone)
    {
        UserEntity u = this.selectOne()
            .strict(true)
            .eq(F.registFromDomain, domain)
            .eq(F.bindPhone, phone)
            .notEq(F.userid, excludUserid)
            .exec();
        return u != null;
    }
    
    public boolean isPhoneUsed(String domain, Long excludPkey, String phone)
    {
        UserEntity u = this.selectOne()
            .strict(true)
            .eq(F.registFromDomain, domain)
            .eq(F.bindPhone, phone)
            .notEq(F.pkey, excludPkey)
            .exec();
        return u != null;
    }
}
