package cn.tofocus.lejia.api.h5;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.h5.H5GoodsInfo;
import cn.tofocus.lejia.bean.dto.h5.H5GoodsOnPage;
import cn.tofocus.lejia.domain.h5.H5GoodsManager;
import io.swagger.v3.oas.annotations.Operation;

@RequestMapping("/v1/h5/goods")
@RestController
public class H5GoodsApiImpl
{
    @Autowired
    private H5GoodsManager manager;
    
    @Operation(summary = "新增/编辑包厢商品", tags = ApiTags.H5_WEB_GOODS)
    @PostMapping(value = "/put")
    public Result<Integer> putGoods(@RequestBody @Valid H5GoodsInfo info)
    {
        Integer res = manager.putGoods(info);
        return new Result<>(res);
    }
    
    @Operation(summary = "删除包厢商品", tags = ApiTags.H5_WEB_GOODS)
    @PostMapping(value = "/del")
    public Result<Boolean> delGoods(@RequestBody Integer pkey)
    {
        Boolean res = manager.delGoods(pkey);
        return new Result<>(res);
    }
    
    @Operation(summary = "H5获取包厢商品-带token", tags = ApiTags.H5_WEB_GOODS)
    @PostMapping(value = "/query")
    public Result<PageResult<H5GoodsOnPage>> query(@RequestParam(value = "page", defaultValue = "0", required = false)int page,
        @RequestParam(value = "pagesize", defaultValue = "10", required = false)int pagesize)
    {
        PageResult<H5GoodsOnPage> res = manager.queryGoodsBox(page, pagesize, true);
        return new Result<>(res);
    }
    
    @Operation(summary = "H5获取包厢商品-不带token", tags = ApiTags.H5_WEB_GOODS)
    @PostMapping(value = "/query/nottoken")
    public Result<PageResult<H5GoodsOnPage>> queryNotToken(@RequestParam(value = "page", defaultValue = "0", required = false)int page,
        @RequestParam(value = "pagesize", defaultValue = "10", required = false)int pagesize)
    {
        PageResult<H5GoodsOnPage> res = manager.queryGoodsBox(page, pagesize, false);
        return new Result<>(res);
    }
    
    @PostMapping(value = "/runAddGoodsSpace")
    public Result<Boolean> runAddGoodsSpace()
    {
        manager.runAddGoodsSpace();
        return new Result<>(true);
    }
}
