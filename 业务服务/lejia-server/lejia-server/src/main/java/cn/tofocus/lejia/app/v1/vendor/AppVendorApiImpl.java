package cn.tofocus.lejia.app.v1.vendor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.file.bean.FileInfoV3;
import cn.tofocus.lejia.app.AppTags;
import cn.tofocus.lejia.bean.dto.app.AppCardCheckDTO;
import cn.tofocus.lejia.bean.dto.app.AppUsePointsRecordOnList;
import cn.tofocus.lejia.bean.dto.app.AppVendorDTO;
import cn.tofocus.lejia.bean.dto.app.vendor.VendorOrderInfoV2;
import cn.tofocus.lejia.bean.dto.market.MktGiftOnPage;
import cn.tofocus.lejia.bean.dto.order.MktVendorOrderMainDTO;
import cn.tofocus.lejia.bean.enums.CardStatus;
import cn.tofocus.lejia.bean.enums.SettlementType;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.dao.goods.MktGoodsGiftDao;
import cn.tofocus.lejia.domain.app.AppVendorManager;
import cn.tofocus.lejia.domain.market.GiftManager;
import cn.tofocus.lejia.domain.market.VendorOrderManager;
import io.swagger.v3.oas.annotations.Operation;

/**
 * 移动端-商户接口V1实现类
 */
@RequestMapping("/v1/app/vendor")
@RestController
public class AppVendorApiImpl implements AppVendorApi
{
    
    @Autowired
    private AppVendorManager vendorManager;
    
    @Autowired
    private VendorOrderManager orderMng;
    
    @Autowired
    private GiftManager giftManager;
    
    @Autowired
    private MktGoodsGiftDao goodsGiftDao;
    
    @Override
    public Result<AppVendorDTO> getVendor()
    {
        return new Result<>(vendorManager.getVendor());
    }
    
    @Override
    public Result<PageResult<AppUsePointsRecordOnList>> queryUsePointsRecord(int page, int pagesize)
    {
        return new Result<>(vendorManager.queryUsePointsRecord(page, pagesize));
    }
    
    @Override
    public Result<MktVendorOrderMainDTO> queryOrder(int page, int pagesize, String startDate, String endDate)
    {
        Integer vendorPkey = MobileSession.vendorPkey();
        List<Integer> vendorPkeys = new ArrayList<>();
        vendorPkeys.add(vendorPkey);
        return new Result<>(
            orderMng.queryVendorOrder(null, page, pagesize, vendorPkeys, startDate, endDate, null, true, false, MobileSession.appid()));
    }
    
    @Override
    public Result<VendorOrderInfoV2> queryOrder(int page, int pagesize, SettlementType status, String startDate,
        String endDate, Boolean flag)
    {
        return new Result<>(vendorManager.queryOrder(page, pagesize, status, startDate, endDate, flag));
    }
    
    @Override
    public Result<Boolean> finishPurchase(Integer pkey)
    {
        return new Result<>(vendorManager.finishPurchase(pkey));
    }
    
    @Override
    public Result<String> getCardName(String cardNumber)
    {
        return new Result<>(vendorManager.getCardName(cardNumber));
    }
    
    @Override
    public Result<Boolean> insCard(String cardNumber)
    {
        return new Result<>(vendorManager.insCard(cardNumber));
    }
    
    @Override
    public Result<PageResult<AppCardCheckDTO>> queryCard(int page, int pagesize)
    {
        return new Result<>(vendorManager.queryCard(page, pagesize));
    }
    
    @Override
    public Result<Boolean> writeOffGift(String cardNumber)
    {
        giftManager.hxMemberGift(cardNumber);
        return new Result<>(true);
    }
    
    @Override
    public Result<PageResult<MktGiftOnPage>> giftList(int page, int pagesize, String startDate, String endDate,
        CardStatus status)
    {
        return new Result<>(giftManager.giftList(page, pagesize, startDate, endDate, status));
    }
    
    @Override
    public Result<BigDecimal> giftSumAmtn(String startDate, String endDate)
    {
        return new Result<>(goodsGiftDao.sumAmtn(MobileSession.vendorPkey(), startDate, endDate));
    }
    
    @Override
    public Result<Map<String, String>> getGiftName(String cardNumber)
    {
        return new Result<>(giftManager.loadMemberGift(cardNumber));
    }
    
    @Operation(summary = "商户端上传图片", tags = AppTags.mobileVendor)
    @PostMapping("/uploadImage")
    public Result<FileInfoV3> uploadImage(@RequestPart("file") MultipartFile file)
    {
        return vendorManager.uploadImage(file);
    }
    
}
