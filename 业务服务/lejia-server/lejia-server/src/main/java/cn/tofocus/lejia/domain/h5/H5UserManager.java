package cn.tofocus.lejia.domain.h5;

import java.math.BigDecimal;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.db.redis.lock.RedisLockTemplate;
import cn.tofocus.lejia.bean.dto.h5.H5UserInfo;
import cn.tofocus.lejia.bean.entity.h5.H5User;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.h5.H5UserDao;

@Component
public class H5UserManager
{
    @Autowired
    private RedisLockTemplate lock;
    
    @Autowired
    private H5UserDao h5UserDao;
    
    
    public H5UserInfo getUser()
    {
        return h5UserDao.byPkeyDto(CurrentSession.userPkey());
    }
    
    @Transactional
    public Boolean addUserMoney(String mobile, String farmer, BigDecimal amt)
    {
        H5User user = h5UserDao.byMobileAndFarmer(mobile, farmer);
        updUserMoney(user, amt);
        return true;
    }
    
    @Transactional
    public void updUserMoney(H5User user, BigDecimal money)
    {
        try
        {
            lock.lock("zyysc", "zyysc-server", "h5User" + user.getPkey());
            user.setMoney(user.getMoney().add(money));
            h5UserDao.update(user);
        }
        finally
        {
            lock.unlock("zyysc", "zyysc-server", "h5User" + user.getPkey());
        }
       
    }
}
