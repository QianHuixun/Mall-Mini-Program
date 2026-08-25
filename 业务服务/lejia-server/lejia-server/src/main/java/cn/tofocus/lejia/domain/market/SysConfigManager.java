package cn.tofocus.lejia.domain.market;

import cn.tofocus.account.api.v2.user.UserApi;
import cn.tofocus.account.api.v4.MenuApiV4;
import cn.tofocus.account.bean.AppMenu;
import cn.tofocus.lejia.bean.entity.sys.SysFarmer;
import cn.tofocus.lejia.bean.enums.PointType;
import cn.tofocus.lejia.bean.enums.v5.FarmerType;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.sys.SysFarmerDao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SysConfigManager
{
    @Autowired
    private UserApi userApi;
    
    @Autowired
    private MenuApiV4 menuApiV4;
    
    @Autowired
    private SysFarmerDao sysFarmerDao;
    
    @Value("${zx.qingfen.ascription:13}")
    private Integer qfAscription;
    
    /**
     * 运营端/市场端/公司端判断
     * @return 结果
     */
    public PointType judgePoint()
    {
        PointType result;
        String marketPkey = CurrentSession.marketPkey();
        Integer ascription = CurrentSession.ascriptionPkey();
        if (marketPkey != null)
        {
            // 1代表运营端
            if ((Constant.Operation + ascription).equals(marketPkey))
            {
                result = PointType.OPERATION;
            }
            // 2代表市场端
            else
            {
                result = PointType.MARKET;
            }
        }
        // 3代表公司端
        else
        {
            result = PointType.COMPANY;
        }

        return result;
    }
    
    public List<AppMenu> getMenu()
    {
        List<AppMenu> menus = new ArrayList<>();
        PointType judgePoint = judgePoint();
        if(PointType.MARKET.equals(judgePoint))
        {
            String marketPkey = CurrentSession.marketPkey();
            menus = userApi.myAppMenu(null, marketPkey).fetchResult();
            SysFarmer farmer = sysFarmerDao.get(marketPkey);
            if(FarmerType.MARKET_SHOPPING_MALL.equals(farmer.getType()))
            {
                for(AppMenu a : menus)
                {
                    if("合作商户".equals(a.getName()))
                    {
                        for(AppMenu am : a.getSub())
                        {
                            if("精选商户管理".equals(am.getName()))
                            {
                                a.getSub().remove(am);
                                break;
                            }
                        }
                    }
                    if("商品管理".equals(a.getName()))
                    {
                        Iterator<AppMenu> iterator = a.getSub().iterator();
                        while(iterator.hasNext())
                        {
                            AppMenu am = iterator.next();
                            if("市场商品管理".equals(am.getName()) 
                                || "特价商品管理".equals(am.getName())
                                || "商品供应库".equals(am.getName()))
                            {
                                
                            }
                            else
                                iterator.remove();
                        }
                    }
                }
            }
            if(FarmerType.VENDOR_SHOPPING_MALL.equals(farmer.getType()))
            {
                for(AppMenu a : menus)
                {
                    if("商品管理".equals(a.getName()))
                    {
                        Iterator<AppMenu> iterator = a.getSub().iterator();
                        while(iterator.hasNext())
                        {
                            AppMenu am = iterator.next();
                            if("市场商品管理".equals(am.getName()) 
                                || "特价商品管理".equals(am.getName())
                                || "商品供应库".equals(am.getName()))
                            {
                                
                            }
                            else
                                iterator.remove();
                        }
                    }
                }
            }
            if(!Boolean.TRUE.equals(farmer.getConfig().getIsEnterprise()))
            {
                Iterator<AppMenu> iterator = menus.iterator();
                while(iterator.hasNext())
                {
                    AppMenu next = iterator.next();
                    if("财务管理".equals(next.getName()))
                    {
                        iterator.remove();
                    }
                }
            }
            if(!qfAscription.equals(CurrentSession.ascriptionPkey()))
            {
                Iterator<AppMenu> it = menus.iterator();
                while(it.hasNext())
                {
                    AppMenu next = it.next();
                    List<AppMenu> sub = next.getSub();
                    if(sub != null)
                    {
                        Iterator<AppMenu> its = sub.iterator();
                        while(its.hasNext())
                        {
                            AppMenu am = its.next();
                            if("功能菜单配置".equals(am.getName()))
                            {
                                its.remove();
                            }
                            if("桌位管理".equals(am.getName()))
                            {
                                its.remove();
                            }
                            if("打包物料费明细".equals(am.getName()))
                            {
                                its.remove();
                            }
                        }
                    }
                }
            }
        }
        else
        {
            menus = userApi.myAppMenu(null, null).fetchResult();
            if(qfAscription.equals(CurrentSession.ascriptionPkey()))
            {
                for(AppMenu a : menus)
                {
                    if("商城管理".equals(a.getName()))
                    {
                        Iterator<AppMenu> iterator = a.getSub().iterator();
                        while(iterator.hasNext())
                        {
                            AppMenu am = iterator.next();
                            if("商品管理".equals(am.getName()))
                            {
                                am.setName("滨海自营");
                            }
                        }
                    }
                }
            }
            else
            {
                Iterator<AppMenu> it = menus.iterator();
                while(it.hasNext())
                {
                    AppMenu next = it.next();
                    if("财务管理".equals(next.getName()))
                    {
                        it.remove();
                    }
                    if("京东优选".equals(next.getName()))
                    {
                        it.remove();
                    }
                    List<AppMenu> sub = next.getSub();
                    if(sub != null)
                    {
                        Iterator<AppMenu> its = sub.iterator();
                        while(its.hasNext())
                        {
                            AppMenu am = its.next();
                            if("滨农优品管理".equals(am.getName()))
                            {
                                its.remove();
                            }
                            if("民生专区管理".equals(am.getName()))
                            {
                                its.remove();
                            }
                            if("热力豆管理".equals(am.getName()))
                            {
                                its.remove();
                            }
                            if("京东销售统计".equals(am.getName()))
                            {
                                its.remove();
                            }
                        }
                    }
                }
            }
        }
        return menus;
    }
}
