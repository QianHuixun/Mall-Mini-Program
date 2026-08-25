package cn.tofocus.lejia.api.v1.jd;

import cn.tofocus.lejia.bean.dto.market.jd.JdCategoryHasChildrenDrop;
import cn.tofocus.lejia.bean.dto.market.jd.JdCategoryThreeDrop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.market.jd.JdCategoryRelOnList;
import cn.tofocus.lejia.domain.jd.JdCategoryManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import java.util.List;

@RequestMapping("/v1/jd/category/manager")
@RestController
public class JdCategoryApiImpl
{
    @Autowired
    private JdCategoryManager jdCategoryManager;
    
    @Operation(summary = "手动同步商品", tags = ApiTags.JD_CATEGORY)
    @PostMapping("/manual/sync")
    public Result<Boolean> manualSyncGoods()
    {
        return null;
    }
    
    @Operation(summary = "查询京东分类关联", tags = ApiTags.JD_CATEGORY)
    @PostMapping("/rel/query")
    public Result<PageResult<JdCategoryRelOnList>> queryCategoryRel(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页码") int page,
        @RequestParam(value = "pagesize", defaultValue = "10") @Parameter(description = "每页大小") int pagesize,
        @Parameter(description = "京东分类pkey，可为1级/2级") @RequestParam(value = "jdCategory", required = false) Long jdCategory,
        @Parameter(description = "商城1级分类pkey") @RequestParam(value = "mallGtype", required = false) Integer mallGtype,
        @Parameter(description = "商城2级分类pkey") @RequestParam(value = "mallGoodsMain", required = false) Integer mallGoodsMain)
    {
        return new Result<>(
            jdCategoryManager.queryCategoryRel(page, pagesize, jdCategory, mallGtype, mallGoodsMain));
    }

    @Operation(summary = "编辑京东分类关联商城分类", tags = ApiTags.JD_CATEGORY)
    @PostMapping("/rel/upd")
    public Result<Boolean> updCategoryRel(
        @Parameter(description = "京东二级分类pkey") @RequestParam(value = "jdCategory") Long jdCategory,
        @Parameter(description = "商城二级分类pkey（mkt_goods_main.pkey），为空表示取消关联") @RequestParam(value = "mallCategory", required = false) Integer mallCategory)
    {
        return new Result<>(jdCategoryManager.updCategoryRel(jdCategory, mallCategory));
    }
    
    @Operation(summary = "获取京东多级分类下拉", tags = ApiTags.JD_CATEGORY)
    @PostMapping("/multi/drop")
    public Result<List<JdCategoryHasChildrenDrop>> multiDrop(
        @Parameter(description = "需要的分类层级(1:一级，2:二级，3:三级)") @RequestParam(value = "levels", defaultValue = "3") Integer levels)
    {
        return new Result<>(jdCategoryManager.listMultiDrop(levels));
    }
}
