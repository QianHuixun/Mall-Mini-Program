package cn.tofocus.lejia.api.v1.market;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.core.log.LogApi;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.market.DropIntegerDown;
import cn.tofocus.lejia.bean.dto.market.MarketPkeyNameDTO;
import cn.tofocus.lejia.bean.dto.market.MktVendorDTO;
import cn.tofocus.lejia.bean.dto.market.MktVendorOnList;
import cn.tofocus.lejia.bean.dto.market.MktVendorPkeyNameDTO;
import cn.tofocus.lejia.bean.dto.market.MktVendorPointLineOnList;
import cn.tofocus.lejia.bean.dto.market.MktVendorQueryParamDTO;
import cn.tofocus.lejia.bean.dto.market.XaszVendorInfo;
import cn.tofocus.lejia.bean.enums.SourceType;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.vendor.MktVendorDao;
import cn.tofocus.lejia.domain.market.VendorManager;
import io.micrometer.core.instrument.util.StringUtils;

@RequestMapping("/v1/market/vendor")
@RestController
public class VendorApiImpl implements VendorApi
{
    
    @Autowired
    private VendorManager vendorManager;
    
    @Autowired
    private MktVendorDao vendorDao;
    
    @Override
    public Result<MktVendorDTO> getVendor(Integer pkey)
    {
        return new Result<>(vendorManager.getVendor(pkey));
    }
    
    @Override
    @LogApi(operation = "删除合作商户", format = "删除合作商户")
    public Result<Boolean> delVendor(Integer pkey)
    {
        return new Result<>(vendorManager.delVendor(pkey));
    }
    
    @Override
    @LogApi(operation = "启动合作商户", format = "启动合作商户")
    public Result<Boolean> startVendor(Integer pkey)
    {
        return new Result<>(vendorManager.enabledVendor(pkey, true));
    }
    
    @Override
    @LogApi(operation = "停止合作商户", format = "停止合作商户")
    public Result<Boolean> stopVendor(Integer pkey)
    {
        return new Result<>(vendorManager.enabledVendor(pkey, false));
    }
    
    @Override
    @LogApi(operation = "新增合作商户", format = "新增合作商户：名字:{dto.name}，地址:{dto.addr}，电话:{dto.mobile}，市场pkey{dto.farmer}", resultFormat = "")
    public Result<Integer> insVendor(MktVendorDTO dto)
    {
        return new Result<>(vendorManager.insVendor(dto));
    }
    
    @Override
    public Result<PageResult<MktVendorOnList>> queryVendor(MktVendorQueryParamDTO paramDTO)
    {
        return new Result<>(vendorManager.queryVendor(paramDTO));
    }
    
    @Override
    @LogApi(operation = "更新合作商户", format = "修改合作商户：数据pkey:{dto.pkey}，名称: {dto.name}，地址: {dto.addr}，联系方式: {dto.mobile}，市场pkey={dto.farmer}")
    public Result<Boolean> updVendor(MktVendorDTO dto)
    {
        return new Result<>(vendorManager.updVendor(dto));
    }
    
    @Override
    public Result<PageResult<MktVendorPointLineOnList>> queryVendorPointLine(int page, int pagesize, Integer vendor,
        SourceType source, String mobile, String name, String startDate, String endDate)
    {
        return new Result<>(
            vendorManager.queryVendorPointLine(page, pagesize, vendor, source, mobile, name, startDate, endDate));
    }
    
    /**
     * 运营端-市场商城-市场列表
     * @return 			   结果
     */
    @Override
    public Result<List<MarketPkeyNameDTO>> marketList()
    {
        return new Result<>(vendorManager.marketList());
    }
    
    /**
     * 经营范围
     * @return   结果
     */
    @Override
    public Result<List<MktVendorPkeyNameDTO>> gtypeList(String farmer)
    {
        return new Result<>(vendorManager.gtypeList(farmer));
    }
    
    /**
     * 运营端是否开启统一配置
     * @return	结果
     */
    @Override
    public Result<Boolean> isUnified()
    {
        return new Result<>(vendorManager.isUnified());
    }
    
    @Override
    public Result<List<DropIntegerDown>> listDropName(String farmer, Boolean enabled)
    {
        String marketPkey = CurrentSession.marketPkey();
        Integer ascription = CurrentSession.ascriptionPkey();
        if((Constant.Operation + ascription).equals(marketPkey))
            marketPkey = null;
        if((Constant.Operation + ascription).equals(marketPkey) && StringUtils.isNotBlank(farmer))
            marketPkey = farmer;
        List<DropIntegerDown> res = vendorDao.listDropName(marketPkey, enabled, ascription);
        return new Result<>(res);
    }
    
    @Override
    public Result<List<DropIntegerDown>> listDropNameV2(String farmer)
    {
        List<DropIntegerDown> res = vendorDao.listDropName(farmer, null, CurrentSession.ascriptionPkey());
        return new Result<>(res);
    }
    
    @Override
    public Result<Integer> insVendorPoint(MktVendorDTO dto)
    {
        return new Result<>(vendorManager.insVendorPoint(dto.getName(), dto.getAddr(), dto.getMobile()));
    }
    
    @Override
    public Result<Boolean> updVendorPoint(MktVendorDTO dto)
    {
        return new Result<>(vendorManager.updVendorPoint(dto.getPkey(), dto.getName(), dto.getAddr(), dto.getMobile()));
    }

    @Override
    public Result<?> sendZxObject(String transCode, Integer vendorKey, Boolean flag)
    {
        return null;
    }

    @Override
    public Result<Integer> putVendor(XaszVendorInfo dto)
    {
        return new Result<>(vendorManager.insertOrUpdateVendorV3(dto));
    }
    
}
