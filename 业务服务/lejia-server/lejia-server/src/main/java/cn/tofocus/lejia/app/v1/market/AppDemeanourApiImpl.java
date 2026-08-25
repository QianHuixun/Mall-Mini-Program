package cn.tofocus.lejia.app.v1.market;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.PkeyNameDTO;
import cn.tofocus.lejia.bean.dto.app.AppDemeanourPageDTO;
import cn.tofocus.lejia.bean.dto.app.AppVendor;
import cn.tofocus.lejia.bean.dto.app.goods.AppGoodsV4OnList;
import cn.tofocus.lejia.bean.dto.market.MktVendorDTO;
import cn.tofocus.lejia.bean.dto.market.MktVendorQueryParamDTO;
import cn.tofocus.lejia.bean.entity.market.MktCollection;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.dao.goods.MktGoodsDao;
import cn.tofocus.lejia.dao.market.MktCollectionDao;
import cn.tofocus.lejia.domain.app.AppDemeanourManager;
import cn.tofocus.lejia.domain.app.AppGoodsV4Manager;
import cn.tofocus.lejia.domain.market.VendorManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * 商户风采接口实现类
 */
@RequestMapping("/v1/app/market/demeanour")
@RestController
public class AppDemeanourApiImpl implements AppDemeanourApi {
    /**
     * 商户风采Manager管理类
     */
    @Resource
    private AppDemeanourManager manager;

    /**
     * mkt_vendor表的Manager管理类
     */
    @Resource
    private VendorManager vendorManager;
    
    @Autowired
    private MktGoodsDao goodsDao;
    
    @Autowired
    private AppGoodsV4Manager appGoodsV4Manager;
    
    @Autowired
    private MktCollectionDao collectionDao;

    /**
     * 商户风采：一级分类列表
     * @return   结果
     */
    @Override
    public Result<List<PkeyNameDTO>> gtypePkeyNameList()
    {
        return new Result<>(manager.gtypePkeyNameList());
    }

    /**
     * 商户风采分页数据
     * @param paramDTO 商户名称
     * @return         结果
     */
    @Override
    public Result<PageResult<AppDemeanourPageDTO>> pageList(MktVendorQueryParamDTO paramDTO)
    {
        return new Result<>(manager.pageList(paramDTO));
    }

    /**
     * 获取商户详情
     * @param pkey  商户主键
     * @return      商户详情
     */
    @Override
    public Result<AppVendor> getVendor(Integer pkey)
    {
        // 判断是否登录
        // manager.judgeRight();
        MktVendorDTO vendor = vendorManager.getVendor(pkey);
        String businessScopesName = vendor.getBusinessScopesName();
        // 经营范围转为List
        AppVendor appVendor = BeanUtil.beanFrom(AppVendor.class, vendor);
        appVendor.setBusinessScopesName(Arrays.asList(businessScopesName.split(",")));
        long count = goodsDao.countVendorGoodsNum(pkey, MobileSession.appid());
        appVendor.setGoodsNum((int)count);
        appVendor.setName(vendor.getDisplayName());
        appVendor.setIsCollection(false);
        Integer memberPkey = MobileSession.memberPkey();
        MktCollection collection = collectionDao.selectOne()
        .eq("member", memberPkey)
        .eq("ctype", 2)
        .eq("objKey", pkey)
        .exec();
        if(collection != null)
        {
            appVendor.setIsCollection(true);
            appVendor.setCollectionPkey(collection.getPkey());
        }
        return new Result<>(appVendor);
    }

    @Override
    public Result<PageResult<AppGoodsV4OnList>> queryAppVendorGoods(Integer page, Integer pagesize, Integer vendor,
        String name, Integer goodsMain, Boolean priceSort, Boolean xsNumSort)
    {
        return new Result<>(appGoodsV4Manager.queryAppVendorGoods(page, pagesize, vendor, name, goodsMain, priceSort, xsNumSort));
    }
}
