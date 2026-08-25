package cn.tofocus.lejia.api.v1.vendor;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.common.excel.ExcelHelper;
import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.vendor.WalletDetailsOnPage;
import cn.tofocus.lejia.bean.dto.vendor.WalletOnInfo;
import cn.tofocus.lejia.bean.dto.vendor.WithdrawalOnInfo;
import cn.tofocus.lejia.bean.dto.vendor.WithdrawalOnPage;
import cn.tofocus.lejia.bean.enums.vendor.WithdrawalStatus;
import cn.tofocus.lejia.bean.excel.ExportWallet;
import cn.tofocus.lejia.bean.excel.ExportWithdrawal;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.market.MktOrderDao;
import cn.tofocus.lejia.domain.app.AppGoodsV4Manager;
import cn.tofocus.lejia.domain.market.CardManager;
import cn.tofocus.lejia.domain.market.MemberManager;
import cn.tofocus.lejia.domain.market.mall.AppOrderManager;
import cn.tofocus.lejia.domain.pay.WxRefundManager;
import cn.tofocus.lejia.domain.vendor.VendorWalletManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@RequestMapping("/v1/vendor/wallet")
@RestController
public class VendorWalletApiImpl implements VendorWalletApi
{
    @Autowired
    private VendorWalletManager manager;
    
    @Autowired
    private ExcelHelper excelHelper;
    
    @Override
    public Result<WalletOnInfo> queryWallet(int page, int pagesize, String vendorName, String booth)
    {
        String marketPkey = CurrentSession.marketPkey();
        Integer ascription = CurrentSession.ascriptionPkey();
        if ((Constant.Operation + ascription).equals(marketPkey))
        {
            marketPkey = null;
        }
        return new Result<>(manager.queryWallet(page, pagesize, vendorName, booth, marketPkey, ascription));
    }
    
    @Override
    public Result<PageResult<WalletDetailsOnPage>> queryWalletLine(int page, int pagesize, Integer pkey)
    {
        return new Result<>(manager.queryWalletLine(page, pagesize, pkey));
    }
    
    @Operation(summary = "商户钱包导出", tags = ApiTags.ZYYSC_VENDOR_WALLET)
    @PostMapping(value = "/export")
    public void export(
        @RequestParam(value = "vendorName", required = false) @Parameter(description = "商户名称") String vendorName,
        @RequestParam(value = "booth", required = false) @Parameter(description = "摊位号") String booth,
        HttpServletResponse response)
        throws Exception
    {
        try (OutputStream outputStream = response.getOutputStream();)
        {
            String marketPkey = CurrentSession.marketPkey();
            Integer ascription = CurrentSession.ascriptionPkey();
            if ((Constant.Operation + ascription).equals(marketPkey))
            {
                marketPkey = null;
            }
            WalletOnInfo info = manager.queryWallet(0, 10000, vendorName, booth, marketPkey, ascription);
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;  CHARSET=utf8");
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode("商户钱包", "UTF-8"));
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
            response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            excelHelper.exportExcel(BeanUtil.beanListFrom(ExportWallet.class,
                info.getWalletOnPage().getContent()), null, outputStream, ExportWallet.class, null);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    @Autowired
    private MemberManager memberManager;
    
    @Autowired
    private AppGoodsV4Manager appGoodsV4Manager;
    
    @Autowired
    private AppOrderManager appOrderManager;
    
    @Autowired
    private MktOrderDao orderDao;
    
    @Autowired
    private WxRefundManager wxReManager;
    
    @Autowired
    private CardManager cardManager;
    
    @PostMapping(value = "/test/run")
    public Result<Boolean> runSettlementWallet(
        @RequestParam(value = "day", required = false, defaultValue = "3") int day)
    {
        //        appGoodsV4Manager.putThreeGtypeSort(null, null);
        //        try
        //        {
        //            memberManager.accessNum();
        //        }
        //        catch (ParseException e)
        //        {
        //            e.printStackTrace();
        //        }
        //        List<MktOrder> list1 = orderDao.select()
        //            .in("status", OrderStatus.SHIPPED_ORDER, OrderStatus.ARRIVED_ORDER)
        //            .notEq("farmer", Constant.Operation + CurrentSession.ascriptionPkey())
        //            .exec();
        //        for (MktOrder line : list1)
        //        {
        //            appOrderManager.drOrder(line);
        //        }
        //        manager.testUpdVendorOrderAndWalletLineTime("2024-03-05", "2024-03-05");
        manager.runSettlementWallet(day, null);
        return new Result<>(true);
    }
    
    //@PostMapping(value = "/test/addCard")
    //public Result<Boolean> testAddCardMember(@RequestParam(value = "mobiles") List<String> mobiles,
    //    @RequestParam(value = "ascription") Integer ascription)
    //{
    //    cardManager.insMemberCardLinshiTest(mobiles, ascription);
    //    return new Result<>(true);
    //}
    
    @GetMapping(value = "/test/gethdNum")
    public String testNum()
    {
        return cardManager.gethdNum();
    }
    
    @PostMapping(value = "/test/member")
    public Result<Boolean> testMember(@RequestParam(value = "pkey") int pkey,
        @RequestParam(value = "amt") BigDecimal amt)
    {
        memberManager.memberRechargeTest(pkey, amt);
        return new Result<>(true);
    }
    
    @Override
    public Result<WithdrawalOnInfo> queryWithdrawal(int page, int pagesize, String startDate, String endDate,
        String vendorName, String booth, WithdrawalStatus status)
    {
        String marketPkey = CurrentSession.marketPkey();
        Integer ascription = CurrentSession.ascriptionPkey();
        if ((Constant.Operation + ascription).equals(marketPkey))
        {
            marketPkey = null;
        }
        return new Result<>(manager
            .queryWithdrawal(page, pagesize, startDate, endDate, vendorName, booth, status, marketPkey, ascription));
    }
    
    @Operation(summary = "提现打款导出", tags = ApiTags.ZYYSC_VENDOR_WALLET)
    @PostMapping(value = "/withdrawal/export")
    public void exportWithdrawal(
        @RequestParam(value = "startDate", required = false) @Parameter(description = "申请时间-开始") String startDate,
        @RequestParam(value = "endDate", required = false) @Parameter(description = "申请时间-结束") String endDate,
        @RequestParam(value = "vendorName", required = false) @Parameter(description = "商户名称") String vendorName,
        @RequestParam(value = "booth", required = false) @Parameter(description = "摊位号") String booth,
        @RequestParam(value = "status", required = false) @Parameter(description = "打款状态") WithdrawalStatus status,
        HttpServletResponse response)
        throws Exception
    {
        try (OutputStream outputStream = response.getOutputStream();)
        {
            String marketPkey = CurrentSession.marketPkey();
            Integer ascription = CurrentSession.ascriptionPkey();
            if ((Constant.Operation + ascription).equals(marketPkey))
            {
                marketPkey = null;
            }
            List<ExportWithdrawal> list = new ArrayList<>();
            WithdrawalOnInfo info = manager
                .queryWithdrawal(0, 10000, startDate, endDate, vendorName, booth, status, marketPkey, ascription);
            for (WithdrawalOnPage w : info.getWithdrawalOnPage())
            {
                ExportWithdrawal ew = BeanUtil.beanFrom(ExportWithdrawal.class, w);
                ew.setTime(DateUtil.formatDate(w.getCreatedTime()));
                list.add(ew);
            }
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;  CHARSET=utf8");
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode("提现打款", "UTF-8"));
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
            response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            excelHelper.exportExcel(list, null, outputStream, ExportWithdrawal.class, null);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    @Override
    public Result<Boolean> confirmWithdrawal(int pkey)
    {
        return new Result<>(manager.confirmWithdrawal(pkey));
    }
}
