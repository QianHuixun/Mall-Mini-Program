package cn.tofocus.lejia.app.v3;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import cn.tofocus.lejia.bean.dto.app.vendor.AppWalletVendorOrderInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.common.data.datadealer.MobileDealer;
import cn.tofocus.core.Result;
import cn.tofocus.core.exception.SysErrCode;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.app.AppTags;
import cn.tofocus.lejia.bean.dto.app.vendor.AppWalletBillOnInfo;
import cn.tofocus.lejia.bean.dto.app.vendor.AppWalletOnInfo;
import cn.tofocus.lejia.bean.dto.app.vendor.AppWalletOrderOnInfo;
import cn.tofocus.lejia.bean.dto.app.vendor.VendorWalletBankInfo;
import cn.tofocus.lejia.bean.dto.vendor.WalletDetailsOnPage;
import cn.tofocus.lejia.bean.enums.SettlementType;
import cn.tofocus.lejia.bean.enums.vendor.VendorWalletSource;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.domain.app.AppVendorWalletV3Manager;
import cn.tofocus.lejia.domain.vendor.VendorWalletManager;
import cn.tofocus.lejia.domain.vendor.VendorWalletUpdManager;
import io.swagger.v3.oas.annotations.Operation;

@RequestMapping("/v3/app/vendor/wallet")
@RestController
public class AppVendorWalletV3ApiImpl implements AppVendorWalletV3Api
{
    @Autowired
    private VendorWalletUpdManager vendorWalletUpdManager;
    
    @Autowired
    private AppVendorWalletV3Manager appVendorWalletV3Manager;
    
    @Autowired
    private VendorWalletManager manager;
    
    @Override
    public Result<AppWalletOnInfo> getAppWalletOnInfo()
    {
        return new Result<>(vendorWalletUpdManager.loadWalletAmount(MobileSession.vendorPkey()));
    }

    @Override
    public Result<AppWalletBillOnInfo> listBill(Integer day, String startDate, String endDate)
    {
        return new Result<>(appVendorWalletV3Manager.listBill(day, startDate, endDate));
    }
    
    @Override
    public Result<AppWalletOrderOnInfo> listOrder(String time, List<SettlementType> status)
    {
        return new Result<>(appVendorWalletV3Manager.listOrder(time, status));
    }

    @Override
    public Result<AppWalletVendorOrderInfo> getVendorOrderWallet(Integer pkey)
    {
        return new Result<>(appVendorWalletV3Manager.getVendorOrderWallet(pkey));
    }

    @Override
    public Result<PageResult<WalletDetailsOnPage>> queryAppWalletLine(int page, int pagesize)
    {
        PageResult<WalletDetailsOnPage> res = manager.queryWalletLine(page, pagesize, MobileSession.vendorPkey());
        for (WalletDetailsOnPage wd : res.getContent())
        {
            if(VendorWalletSource.WITHDRAWAL.equals(wd.getSource()))
            {
                if("打款中".equals(wd.getStatus()))
                    wd.setStatus("提现打款中");
                if("成功".equals(wd.getStatus()))
                    wd.setStatus("提现成功");
            }
            else
                wd.setStatus("");
        }
        return new Result<>(res);
    }
    
    @Operation(summary = "获取手机提现保存银行信息验证码", tags = AppTags.mobileVendorV3)
    @PostMapping(value = "/captcha")
    public Result<Boolean> loginCaptcha(@RequestParam("phone") String phone) {
        Boolean result = appVendorWalletV3Manager.createCaptcha(checkPhone(phone));
        return new Result<>(result);
    }

    /**
     * 验证手机号码
     * <p/>
     * <功能详细描述>
     *
     * @param phone
     * @return
     */
    private String checkPhone(String phone) {
        MobileDealer dealer = new MobileDealer();
        String result = dealer.convert(phone);
        if (result == null || result.length() == 0) {
            throw TofocusException.of(SysErrCode.PHONE_ERROR, phone);
        } else {
            return result;
        }
    }

    @Override
    public Result<VendorWalletBankInfo> getBankOnInfo()
    {
        return new Result<>(appVendorWalletV3Manager.getBankOnInfo());
    }

    @Override
    public Result<Boolean> updBankOnInfo(VendorWalletBankInfo info)
    {
        return new Result<>(appVendorWalletV3Manager.updBankOnInfo(info));
    }

    @Override
    public Result<Boolean> applyWithdrawal(BigDecimal amount)
    {
        return new Result<>(appVendorWalletV3Manager.applyWithdrawal(amount));
    }
}
