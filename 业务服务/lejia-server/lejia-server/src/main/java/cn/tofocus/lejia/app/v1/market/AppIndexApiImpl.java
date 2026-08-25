package cn.tofocus.lejia.app.v1.market;

import java.math.BigDecimal;
import java.util.List;

import cn.tofocus.lejia.bean.dto.app.AppAscriptionConfigDTO;
import cn.tofocus.lejia.bean.dto.app.market.AppIndexZoneConfig;
import cn.tofocus.lejia.bean.dto.app.market.AppIndexZoneGoodsList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.app.AppCardDTO;
import cn.tofocus.lejia.bean.dto.app.AppConfigDTO;
import cn.tofocus.lejia.bean.dto.app.goods.AppGtypeDTO;
import cn.tofocus.lejia.bean.dto.app.market.AppCheckFarmerRangInfo;
import cn.tofocus.lejia.bean.dto.app.market.SysFarmerAppOnList;
import cn.tofocus.lejia.bean.dto.app.vendor.AppVendorBoutiquerIndexInfo;
import cn.tofocus.lejia.bean.entity.market.MktAppConfig;
import cn.tofocus.lejia.bean.enums.AccountType;
import cn.tofocus.lejia.domain.app.AppIndexManager;
import cn.tofocus.lejia.domain.market.CardManager;
import cn.tofocus.lejia.domain.market.mall.AppConfigManager;
import cn.tofocus.lejia.domain.market.mall.AppOrderManager;
import cn.tofocus.lejia.domain.vendor.VendorBoutiqueManager;

@RequestMapping("/v1/app/market/index")
@RestController
public class AppIndexApiImpl implements AppIndexApi
{
    
    @Autowired
    private AppIndexManager appIndexManager;
    
    @Autowired
    private AppOrderManager appOrderManager;
    
    @Autowired
    private AppConfigManager appConfigManager;
    
    @Autowired
    private CardManager cardManager;
    
    @Autowired
    private VendorBoutiqueManager vendorBoutiqueManager;

    @Override
    public Result<AppAscriptionConfigDTO> getAscriptionConfig()
    {
        return new Result<>(appIndexManager.getAscriptionConfig());
    }

    @Override
    public Result<PageResult<SysFarmerAppOnList>> getNearbyMarket(Integer page, Integer pagesize, BigDecimal longitude,
        BigDecimal latitude, String area, String name, String version, AccountType accountType)
    {
        return new Result<>(
            appIndexManager.queryMarket(page, pagesize, longitude, latitude, area, name, version, accountType));
    }
    
    @Override
    public Result<SysFarmerAppOnList> currentFarmer(BigDecimal longitude, BigDecimal latitude, String version,
        AccountType accountType)
    {
        return new Result<>(appIndexManager.getMarket(longitude, latitude, version, accountType));
    }

    @Override
    public Result<AppConfigDTO> getAppConfig()
    {
        MktAppConfig config = appConfigManager.getAppConfig();
        return new Result<>(BeanUtil.beanFrom(AppConfigDTO.class, config));
    }
    
    @Override
    public Result<List<AppCardDTO>> queryCard()
    {
        return new Result<>(cardManager.queryAppCard());
    }
    
    @Override
    public Result<List<AppCardDTO>> queryNewCard()
    {
        return new Result<>(cardManager.queryNewCard());
    }
    
    @Override
    public Result<Boolean> insCardList(List<Integer> cardPkeys)
    {
        return new Result<>(cardManager.insCardList(cardPkeys));
    }
    
    @Override
    public Result<Boolean> isFinish()
    {
        return new Result<>(cardManager.isFinish());
    }

    @Override
    public Result<PageResult<AppVendorBoutiquerIndexInfo>> queryVendorBoutique(Integer page, Integer pagesize)
    {
        return new Result<>(vendorBoutiqueManager.queryAppVendorBoutique(page, pagesize));
    }

    @Override
    public Result<AppCheckFarmerRangInfo> checkFarmerInRange(BigDecimal longitude, BigDecimal latitude, String farmer, Boolean addrBoolean)
    {
        return new Result<>(appIndexManager.checkFarmerInRange(longitude, latitude, farmer, addrBoolean));
    }

    @Override
    public Result<String> getPsTime()
    {
        return new Result<>(appOrderManager.getPsTime(false, null));
    }

    @Override
    public Result<AppIndexZoneConfig> getZoneConfig()
    {
        return new Result<>(appIndexManager.getZoneConfig());
    }
    
    @Override
    public Result<AppIndexZoneGoodsList> listZoneGoods()
    {
        return new Result<>(appIndexManager.listZoneGoods());
    }

    @Override
    public Result<List<AppGtypeDTO>> listGtype()
    {
        return new Result<>(appIndexManager.listGtype());
    }
}
