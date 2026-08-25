package cn.tofocus.lejia.app.v2.vendor;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.core.Result;
import cn.tofocus.lejia.bean.dto.app.AppVendorMerchant;
import cn.tofocus.lejia.bean.dto.market.MktVendorDTO;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.domain.app.AppVendorV2Manager;
import cn.tofocus.lejia.domain.market.VendorManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 移动端-商户接口V2实现类
 */
@RequestMapping("/v2/app/vendor")
@RestController
public class AppVendorApiV2Impl implements AppVendorApiV2
{
    /**
     * 移动端-商户接口V2管理类
     */
    @Resource
    private AppVendorV2Manager appVendorV2Manager;

    /**
     * 商户接口管理类
     */
    @Resource
    private VendorManager vendorManager;

    /**
     * 商户信息
     * @return 商户信息
     */
    @Override
    public Result<AppVendorMerchant> getVendor()
    {
        appVendorV2Manager.judgeRight();
        Integer vendorPkey = MobileSession.vendorPkey();
        MktVendorDTO vendor = vendorManager.getVendor(vendorPkey);
        return new Result<>(BeanUtil.beanFrom(AppVendorMerchant.class, vendor));
    }

    /**
     * 更新商户信息
     * @param appVendor 参数
     * @return          是否成功
     */
    @Override
    public Result<Boolean> upd(AppVendorMerchant appVendor)
    {
        return new Result<>(appVendorV2Manager.upd(appVendor));
    }

    /**
     * 运营端是否开启统一配置
     * @return	结果
     */
    @Override
    public Result<Boolean> isUnified()
    {
        return new Result<>(appVendorV2Manager.isUnified());
    }
}
