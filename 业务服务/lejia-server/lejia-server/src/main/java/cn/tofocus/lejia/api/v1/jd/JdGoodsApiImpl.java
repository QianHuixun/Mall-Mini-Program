package cn.tofocus.lejia.api.v1.jd;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.lejia.bean.dto.jd.JdGoodsExcel;
import cn.tofocus.lejia.bean.dto.market.jd.*;
import cn.tofocus.lejia.bean.enums.jd.JdGoodsUpdType;
import cn.tofocus.lejia.exception.LejiaErrCode;
import com.alibaba.excel.exception.ExcelDataConvertException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import cn.tofocus.common.excel.ExcelHelper;
import cn.tofocus.core.Result;
import cn.tofocus.core.data.KeyValue;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.domain.jd.JdAppOrderManager;
import cn.tofocus.lejia.domain.jd.JdGoodsManager;
import cn.tofocus.lejia.domain.jd.JdGoodsManagerV2;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@Slf4j
@RequestMapping("/v1/jd/goods/manager")
@RestController
public class JdGoodsApiImpl
{
    @Autowired
    private JdGoodsManager manager;
   
    @Autowired
    private JdGoodsManagerV2 managerV2;

    @Autowired
    private JdAppOrderManager jdAppOrderManager;
    
    @Autowired
    private ExcelHelper excelHelper;
    
    @Operation(summary = "获取商品列表", tags = ApiTags.JD_GOODS)
    @PostMapping(value = "/query")
    public Result<PageResult<JdGoodsSpuOnPage>> queryGoods(
        @RequestParam(value = "page", defaultValue = "0", required = false) @Parameter(description = "页号") int page, 
        @RequestParam(value = "pagesize", defaultValue = "10", required = false) @Parameter(description = "每页大小") int pagesize, 
        @RequestParam(value = "title", required = false) @Parameter(description = "标题") String title, 
        @RequestParam(value = "category", required = false) @Parameter(description = "分类id") Long category, 
        @RequestParam(value = "spuId", required = false) @Parameter(description = "主商品id") Long spuId, 
        @RequestParam(value = "skuId", required = false) @Parameter(description = "商品id") Long skuId, 
        @RequestParam(value = "enabled", required = false) @Parameter(description = "是否上架") Boolean enabled)
    {
        return new Result<>(manager.queryGoods(page, pagesize, title, category, spuId, skuId, enabled));
    }
    
    @Operation(summary = "获取一级分类下拉", tags = ApiTags.JD_GOODS)
    @PostMapping(value = "/category/drop")
    public Result<List<JdCategoryDrop>> listCategory()
    {
        return new Result<>(manager.listCategory());
    }
    
    @Operation(summary = "获取三级分类下拉", tags = ApiTags.JD_GOODS)
    @PostMapping(value = "/category/three/drop")
    public Result<List<JdCategoryThreeDrop>> listThreeCategory()
    {
        return new Result<>(manager.listThreeCategory());
    }
    
    @Operation(summary = "获取spu下所有的sku数据", tags = ApiTags.JD_GOODS)
    @PostMapping(value = "/spu/list")
    public Result<JdGoodsSpuOnInfo> listSku(@RequestParam(value = "spuId") Long spuId)
    {
        return new Result<>(manager.listSku(spuId));
    }
    
    @Operation(summary = "获取专区名称", tags = ApiTags.JD_GOODS)
    @PostMapping("/get/specialZone")
    public Result<String> getSpecialZone()
    {
        return new Result<>(manager.getJdGoodsZoneConfig());
    }
    
    @Operation(summary = "设置专区名称", tags = ApiTags.JD_GOODS)
    @PostMapping("/set/specialZone")
    public Result<Boolean> setSpecialZone(@RequestParam(value = "jdGoodsName") @Parameter(description = "专区名称") String jdGoodsName)
    {
        return new Result<>(manager.setJdGoodsZoneConfig(jdGoodsName));
    }

    @Operation(summary = "获取服务内容", tags = ApiTags.JD_GOODS)
    @PostMapping("/get/service/content")
    public Result<List<String>> getServiceContent()
    {
        return new Result<>(manager.getServiceContent());
    }
    
    @Operation(summary = "设置服务内容", tags = ApiTags.JD_GOODS)
    @PostMapping("/set/service/content")
    public Result<Boolean> setServiceContente(@RequestBody List<String> info)
    {
        return new Result<>(manager.setServiceContent(info));
    }
    
    
    @Operation(summary = "修改商品", tags = ApiTags.JD_GOODS)
    @PostMapping("/upd")
    public Result<Boolean> updGoods(@RequestBody JdGoodsSpuOnInfo info)
    {
        return new Result<>(manager.updGoods(info));
    }
    
    @Operation(summary = "根据spuId商品批量上下架", tags = ApiTags.JD_GOODS)
    @PostMapping("/spuId/enable")
    public Result<Boolean> enableSpuIdGoods(@RequestParam(name = "spuId")
    List<Long> spuIds, @RequestParam(name = "enabled")
    boolean enabled)
    {
        return new Result<>(manager.enableSpuIdGoods(spuIds, enabled));
    }
    
    @Operation(summary = "根据skuId(pkey)商品批量上下架", tags = ApiTags.JD_GOODS)
    @PostMapping("/enable")
    public Result<Boolean> enableGoods(@RequestParam(name = "pkeys")
    List<Long> pkeys, @RequestParam(name = "enabled")
    boolean enabled)
    {
        return new Result<>(manager.enableGoods(pkeys, enabled));
    }
    
    @Operation(summary = "手动同步商品", tags = ApiTags.JD_GOODS)
    @PostMapping("/manual/sync")
    public Result<Boolean> manualSyncGoods(@RequestParam(name = "pkey")
    long pkey)
    {
        return new Result<>(manager.manualSyncGoods(pkey));
    }
    
    @Operation(summary = "手动同步未查询详情的数据", tags = ApiTags.JD_GOODS)
    @PostMapping("/manual/sync/noTitle")
    public Result<Boolean> manualSyncGoodsNoTitle()
    {
        return new Result<>(manager.manualSyncGoodsNoTitle());
    }
    
    @Operation(summary = "手动同步无规格的数据", tags = ApiTags.JD_GOODS)
    @PostMapping("/manual/sync/noSpace")
    public Result<Boolean> manualSyncGoodsNoSpace()
    {
        return new Result<>(manager.manualSyncGoodsNoSpace());
    }
    
    @Operation(summary = "手动同步最低起购量字段", tags = ApiTags.JD_GOODS)
    @PostMapping("/manual/sync/lowestBuy")
    public Result<Boolean> manualSyncGoodsLowestBuy()
    {
        return new Result<>(manager.manualSyncGoodsLowestBuy());
    }
    @Operation(summary = "手动修改商品名称,去掉特殊字符", tags = ApiTags.JD_GOODS)
    @PostMapping("/manual/sync/specialTitle")
    public Result<Boolean> manualSyncGoodsSpecialTitle()
    {
        return new Result<>(manager.specialTitle());
    }
    
    @Operation(summary = "删除不可售商品", tags = ApiTags.JD_GOODS)
    @PostMapping("/manual/NoSaleState")
    public Result<Boolean> removeNoSaleStateGoods()
    {
        managerV2.removeNoSaleStateGoods();
        return new Result<>(true);
    }
    
    @Operation(summary = "测试接口", tags = ApiTags.JD_GOODS)
    @PostMapping("/manual/sync/test")
    public Result<Boolean> manualSyncGoodsTest(@RequestParam(name = "bizPoolId", required = false)
    String bizPoolId)
    {
        manager.runJdGoodsInfo(bizPoolId);
        return new Result<>(true);
    }

    @Operation(summary = "测试接口2", tags = ApiTags.JD_GOODS)
    @PostMapping("/manual/sync/test2")
    public Result<Boolean> manualSyncGoodsTest2(@RequestParam(name = "bizPoolId", required = false)
    String bizPoolId)
    {
        managerV2.runJdGoodsInfoV2(bizPoolId);
        return new Result<>(true);
    }

    @Operation(summary = "测试接口3", tags = ApiTags.JD_GOODS)
    @PostMapping("/manual/sync/test3")
    public Result<Boolean> jdTest3(@RequestParam(name = "jdCode") Long jdCode)
    {
        jdAppOrderManager.orderSplitTest(jdCode);
        return new Result<>(true);
    }
    
    @Operation(summary = "查询商品变更通知", tags = ApiTags.JD_GOODS)
    @PostMapping(value = "/updNotice/query")
    public Result<PageResult<JdGoodsUpdNoticeOnPage>> queryJdGoodsUpdNotice(
        @RequestParam(value = "page", defaultValue = "0", required = false) @Parameter(description = "页号") int page,
        @RequestParam(value = "pagesize", defaultValue = "10", required = false) @Parameter(description = "每页大小") int pagesize,
        @RequestParam(value = "title", required = false) @Parameter(description = "标题") String title,
        @RequestParam(value = "skuId", required = false) @Parameter(description = "商品skuid") Long skuId,
        @RequestParam(value = "type", required = false) @Parameter(description = "变更类型") JdGoodsUpdType type,
        @RequestParam(value = "startDate", required = false) @Parameter(description = "开始时间") String startDate,
        @RequestParam(value = "endDate", required = false) @Parameter(description = "结束时间") String endDate)
    {
        return new Result<>(manager.queryJdGoodsUpdNotice(page, pagesize, title, skuId, type, startDate, endDate));
    }

    @Operation(summary = "商品变更类型枚举列表", tags = ApiTags.JD_GOODS)
    @PostMapping(value = "/updNotice/type/list")
    public Result<List<KeyValue<String, String>>> listJdGoodsUpdType()
    {
        return new Result<>(Arrays.stream(JdGoodsUpdType.values())
            .map(type -> new KeyValue<>(type.name(), type.getName()))
            .collect(Collectors.toList()));
    }

    @Operation(summary = "获取京东专区运费配置", tags = ApiTags.JD_GOODS)
    @PostMapping(value = "/config/postage/get")
    public Result<JdPostageConfigDTO> getJdPostageConfig()
    {
        return new Result<>(manager.getJdPostageConfig());
    }

    @Operation(summary = "保存京东专区运费配置", tags = ApiTags.JD_GOODS)
    @PostMapping(value = "/config/postage/set")
    public Result<Boolean> setJdPostageConfig(JdPostageConfigDTO dto)
    {
        return new Result<>(manager.setJdPostageConfig(dto));
    }
    
    @Operation(summary = "导出商品", tags = ApiTags.JD_GOODS)
    @PostMapping(value = "/export")
    public void exportGoods(@RequestParam(value = "title", required = false) @Parameter(description = "标题") String title, 
        @RequestParam(value = "category", required = false) @Parameter(description = "分类id") Long category, 
        @RequestParam(value = "spuId", required = false) @Parameter(description = "主商品id") Long spuId, 
        @RequestParam(value = "skuId", required = false) @Parameter(description = "商品id") Long skuId, 
        @RequestParam(value = "enabled", required = false) @Parameter(description = "是否上架") Boolean enabled,
        HttpServletResponse response)
    {
        List<JdGoodsExcel> exportGoods = manager.exportGoods(title, category, spuId, skuId, enabled);
        OutputStream out = null;
        try
        {
            String fileName = java.net.URLEncoder.encode("京东商品", "UTF-8") + ".xlsx";
            response.setHeader("Content-disposition", "attachment; filename = " + fileName);
            out = response.getOutputStream();
            excelHelper.exportExcel(exportGoods, "Sheet1", out, JdGoodsExcel.class, null);
            out.flush();
            out.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (out != null)
            {
                try
                {
                    out.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
    
    @Operation(summary = "导入商品", tags = ApiTags.JD_GOODS)
    @PostMapping(value = "/import")
    public Result<Boolean> importGoods(MultipartFile myfile, HttpServletResponse response)
    {
        try
        {
            manager.importGoods(myfile, response.getOutputStream());
            return new Result<>(true);
        }
        catch (ExcelDataConvertException dataConvertException)
        {
            log.error("数据格式转换异常", dataConvertException);
            throw TofocusException.of(LejiaErrCode.IMPORT_ERROR, "存在格式异常数据，导入失败");
        }
        catch (TofocusException e)
        {
            // 业务异常（如表头不匹配）直接透传，保留具体提示信息
            throw e;
        }
        catch (Exception e)
        {
            log.error("导入京东商品失败", e);
            throw TofocusException.of(LejiaErrCode.IMPORT_ERROR);
        }
    }
        
}
