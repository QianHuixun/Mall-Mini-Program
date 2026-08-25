package cn.tofocus.lejia.api.v3;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.goods.GoodsCouponOnPage;
import cn.tofocus.lejia.bean.dto.market.MktGoodsDetailsDTO;
import cn.tofocus.lejia.bean.enums.MType;
import cn.tofocus.lejia.bean.enums.v3.SortType;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.domain.v3.GoodsV3Manager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@RequestMapping("/v3/market/goods")
@RestController
public class GoodsV3ApiImpl implements GoodsV3Api
{
    @Autowired
    private GoodsV3Manager manager;
    
    @Override
    public Result<PageResult<MktGoodsDetailsDTO>> queryGoods(int page, int pagesize, MType mType, SortType sortType,
        Boolean sort, String title, Integer gtype, Integer goodsMain, Integer threeGtype, Boolean enabled,
        Integer status, Integer vendor, String booth, Integer supplier)
    {
        return new Result<>(manager.queryGoods(page,
            pagesize,
            mType,
            sortType,
            sort,
            title,
            gtype,
            goodsMain,
            threeGtype,
            enabled,
            status,
            vendor,
            booth,
            supplier));
    }
    
    @Override
    public Result<PageResult<GoodsCouponOnPage>> queryGoodsCoupon(Integer page, Integer pagesize, String title,
        SortType sortType, Boolean sort, Boolean enabled)
    {
        return new Result<>(manager.queryCouponGoods(page, pagesize, sortType, sort, title, enabled));
    }
    
    @Operation(summary = "导出商品列表", tags = ApiTags.LEJIA_V3_GOODS)
    @PostMapping(value = "/export")
    public Result<Boolean> exportGoods(
        @RequestParam(value = "mType", defaultValue = "MARKET_GOODS") @Parameter(description = "商品属性") MType mType,
        @RequestParam(value = "sortType", required = false) @Parameter(description = "排序") SortType sortType,
        @RequestParam(value = "sort", required = false, defaultValue = "false") @Parameter(description = "true:正序，false:倒序") Boolean sort,
        @RequestParam(value = "title", required = false) @Parameter(description = "标题") String title,
        @RequestParam(value = "gtype", required = false) @Parameter(description = "分类id") Integer gtype,
        @RequestParam(value = "goodsMain", required = false) @Parameter(description = "二级分类id") Integer goodsMain,
        @RequestParam(value = "threeGtype", required = false) @Parameter(description = "三级分类id") Integer threeGtype,
        @RequestParam(value = "enabled", required = false) @Parameter(description = "是否上架") Boolean enabled,
        @RequestParam(value = "status", defaultValue = "0") @Parameter(description = "发售状态") Integer status,
        @RequestParam(value = "vendor", required = false) @Parameter(description = "商户主键") Integer vendor,
        @RequestParam(value = "booth", required = false) @Parameter(description = "摊位号") String booth,
        @RequestParam(value = "supplier", required = false) @Parameter(description = "供应商主键") Integer supplier,
        HttpServletResponse response)
    {
        OutputStream out = null;
        String marketPkey = CurrentSession.marketPkey();
        try
        {
            String fileName = new String("市场商品清单.xlsx".getBytes(), "iso-8859-1");
            response.setHeader("Content-disposition", "attachment; filename = " + fileName);
            out = response.getOutputStream();
            manager.exportGoods(mType,
                sortType,
                sort,
                title,
                gtype,
                goodsMain,
                threeGtype,
                enabled,
                status,
                vendor,
                booth,
                supplier,
                marketPkey,
                out);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return new Result<>(true);
    }
    
    @Operation(summary = "导出商品列表-后端使用", tags = ApiTags.LEJIA_V3_GOODS)
    @PostMapping(value = "/exportV2")
    public Result<Boolean> exportGoodsV2(
        @RequestParam(value = "mType", defaultValue = "MARKET_GOODS") @Parameter(description = "商品属性") MType mType,
        @RequestParam(value = "sortType", required = false) @Parameter(description = "排序") SortType sortType,
        @RequestParam(value = "sort", required = false, defaultValue = "false") @Parameter(description = "true:正序，false:倒序") Boolean sort,
        @RequestParam(value = "title", required = false) @Parameter(description = "标题") String title,
        @RequestParam(value = "gtype", required = false) @Parameter(description = "分类id") Integer gtype,
        @RequestParam(value = "goodsMain", required = false) @Parameter(description = "二级分类id") Integer goodsMain,
        @RequestParam(value = "threeGtype", required = false) @Parameter(description = "三级分类id") Integer threeGtype,
        @RequestParam(value = "enabled", required = false) @Parameter(description = "是否上架") Boolean enabled,
        @RequestParam(value = "status", defaultValue = "0") @Parameter(description = "发售状态") Integer status,
        @RequestParam(value = "vendor", required = false) @Parameter(description = "商户主键") Integer vendor,
        @RequestParam(value = "booth", required = false) @Parameter(description = "摊位号") String booth,
        @RequestParam(value = "supplier", required = false) @Parameter(description = "供应商主键") Integer supplier,
        HttpServletResponse response)
    {
        OutputStream out = null;
        String marketPkey = CurrentSession.marketPkey();
        try
        {
            String fileName = new String("市场商品清单.xlsx".getBytes(), "iso-8859-1");
            response.setHeader("Content-disposition", "attachment; filename = " + fileName);
            out = response.getOutputStream();
            manager.exportGoodsV2(mType,
                sortType,
                sort,
                title,
                gtype,
                goodsMain,
                threeGtype,
                enabled,
                status,
                vendor,
                booth,
                supplier,
                marketPkey,
                out);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return new Result<>(true);
    }
    
}
