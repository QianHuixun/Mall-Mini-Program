package cn.tofocus.lejia.api.v1.sys;

import cn.tofocus.account.bean.AppMenu;
import cn.tofocus.core.Result;
import cn.tofocus.core.security.SecurityContextUtil;
import cn.tofocus.lejia.bean.entity.sys.SysConfigEntity;
import cn.tofocus.lejia.bean.enums.PointType;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.sys.SysConfigDao;
import cn.tofocus.lejia.domain.market.SysConfigManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import javax.annotation.Resource;

@RequestMapping("/v1/sys/config")
@RestController
public class SysConfigApiImpl implements SysConfigApi
{
    @Autowired
    private SysConfigDao sysConfigDao;

    @Resource
    private SysConfigManager manager;
    
    @Autowired
    private SecurityContextUtil securityContextUtil;
    
    @Override
    public Result<Boolean> upd(String pkey, Boolean flag)
    {
        SysConfigEntity c = sysConfigDao.get(pkey + "_" + CurrentSession.ascriptionPkey());
        if (c != null)
        {
            if (flag)
            {
                c.setValue("1");
            }
            else
            {
                c.setValue("0");
            }
            sysConfigDao.update(c);
        }
        else
            return new Result<>(false);
        return new Result<>(true);
    }
    
    @Override
    public Result<Boolean> get(String pkey)
    {
        SysConfigEntity c = sysConfigDao.get(pkey + "_" + CurrentSession.ascriptionPkey());
        Boolean res = false;
        try
        {
            Integer of = Integer.valueOf(c.getValue());
            if (of != null && of == 1) res = true;
        }
        catch (Exception e)
        {
            
        }
        return new Result<>(res);
    }


    /**
     * 运营端/市场端/公司端判断
     * @return 结果
     */
    @Override
    public Result<PointType> judgePoint()
    {
        return new Result<>(manager.judgePoint());
    }

    @Override
    public Result<List<AppMenu>> getMenu()
    {
        return new Result<>(manager.getMenu());
    }
    
}
