package cn.tofocus.account.db.dao.application;

import java.util.List;

import org.springframework.stereotype.Component;

import cn.tofocus.account.bean.AccountConstant;
import cn.tofocus.account.db.entity.application.MenuEntity;
import cn.tofocus.account.db.entity.application.MenuEntity.F;
import cn.tofocus.common.Constant;
import cn.tofocus.core.enums.MenuType;
import cn.tofocus.db.jpa.dao.JpaNotifyedDao;

@Component
public class MenuDao extends JpaNotifyedDao<String, MenuEntity>
{
    @Override
    protected String domain()
    {
        return Constant.TfDomain;
    }
    
    @Override
    protected String notifyedCacheName()
    {
        return AccountConstant.MenuNameAccess;
    }
    
    public List<MenuEntity> listEnableMenuByApp(String appid)
    {
        return this.select().strict(true).eq(F.appid, appid).eq(F.enable, true).exec();
    }
    
    public List<MenuEntity> listByApp(String appid)
    {
        return this.select().strict(true).eq(F.appid, appid).exec();
    }
    
    /**
     * 包括模块类型和model下的菜单
     * @param appid
     * @param model
     * @return
     */
    public List<MenuEntity> listByApp(String appid, String model)
    {
        // @formatter:off
        return this.select()
            .strict(true)
            .and()
              .eq(F.appid, appid)
              .or()
                .eq(F.type, MenuType.model)
                .eq(F.modelId, model)
              .close()
            .done()
            .exec();
        // @formatter:on
    }
    
    public List<MenuEntity> listMenuAndButtonByModel(String appid, String model)
    {
        // @formatter:off
        return this.select()
            .strict(true)
            .and()
              .eq(F.appid, appid)
              .notEq(F.type, MenuType.model)
              .eq(F.modelId, model)
            .done()
            .exec();
        // @formatter:on
    }
    
    public List<MenuEntity> listByDomain(String domain)
    {
        return this.select().strict(true).eq(F.domainid, domain).exec();
    }
    
    public int countByModel(String model)
    {
        return (int)this.aggregation().eq(F.modelId, model).execCount();
    }
}
