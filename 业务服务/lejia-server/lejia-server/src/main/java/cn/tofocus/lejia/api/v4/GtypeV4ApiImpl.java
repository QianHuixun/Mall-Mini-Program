package cn.tofocus.lejia.api.v4;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import cn.tofocus.common.excel.ExcelHelper;
import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.core.Result;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.GtypeV4TemplateExcel;
import cn.tofocus.lejia.bean.dto.gtype.GtypeThreeUpdV4Info;
import cn.tofocus.lejia.bean.dto.gtype.GtypeTwoUpdV4Info;
import cn.tofocus.lejia.bean.dto.gtype.GtypeUpdV4Info;
import cn.tofocus.lejia.bean.dto.gtype.GtypeV4OnList;
import cn.tofocus.lejia.domain.GoodListQueryer;
import cn.tofocus.lejia.domain.GtypeV4Manager;
import cn.tofocus.lejia.domain.market.mall.AppOrderManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.RequestBody;

@RequestMapping("/v4/goods/gtype")
@RestController
public class GtypeV4ApiImpl
{
    @Autowired
    private GtypeV4Manager manager;
    
    @Autowired
    private ExcelHelper excelHelper;
    
    @Operation(summary = "获取分类列表", tags = ApiTags.GOODS_GTYPE_V4)
    @PostMapping("/list")
    public Result<List<GtypeV4OnList>> listGtypeV4(
        @RequestParam(value = "enabled", required = false) @Parameter(description = "状态") Boolean enabled,
        @RequestParam(value = "name", required = false) @Parameter(description = "分类名称") String name)
    {
        return new Result<>(manager.listGtypeV4(enabled, name));
    }
    
    @Operation(summary = "新增一级分类", tags = ApiTags.GOODS_GTYPE_V4)
    @PostMapping("/add")
    public Result<Boolean> insGtype(@RequestBody GtypeUpdV4Info info)
    {
        return new Result<>(manager.putGtype(BeanUtil.beanFrom(GtypeThreeUpdV4Info.class, info)));
    }
    
    @Operation(summary = "编辑一级分类", tags = ApiTags.GOODS_GTYPE_V4)
    @PostMapping("/upd")
    public Result<Boolean> updGtype(@RequestBody GtypeUpdV4Info info)
    {
        return new Result<>(manager.putGtype(BeanUtil.beanFrom(GtypeThreeUpdV4Info.class, info)));
    }
    
    @Operation(summary = "新增二级分类", tags = ApiTags.GOODS_GTYPE_V4)
    @PostMapping("/two/add")
    public Result<Boolean> insGtypeTwo(@RequestBody GtypeTwoUpdV4Info info)
    {
        return new Result<>(manager.putGtype(BeanUtil.beanFrom(GtypeThreeUpdV4Info.class, info)));
    }
    
    @Operation(summary = "编辑二级分类", tags = ApiTags.GOODS_GTYPE_V4)
    @PostMapping("/two/upd")
    public Result<Boolean> updGtypeTwo(@RequestBody GtypeTwoUpdV4Info info)
    {
        return new Result<>(manager.putGtype(BeanUtil.beanFrom(GtypeThreeUpdV4Info.class, info)));
    }
    
    @Operation(summary = "新增三级分类", tags = ApiTags.GOODS_GTYPE_V4)
    @PostMapping("/three/add")
    public Result<Boolean> insGtypeThree(@RequestBody GtypeThreeUpdV4Info info)
    {
        return new Result<>(manager.putGtype(info));
    }
    
    @Operation(summary = "编辑三级分类", tags = ApiTags.GOODS_GTYPE_V4)
    @PostMapping("/three/upd")
    public Result<Boolean> updGtypeThree(@RequestBody GtypeThreeUpdV4Info info)
    {
        return new Result<>(manager.putGtype(info));
    }
    
    //    @Operation(summary = "分类排序修改", tags = ApiTags.GOODS_GTYPE_V4)
    //    @PostMapping("/drag/sort")
    //    public Result<Boolean> dragGtypeSort(@RequestParam(value = "pkey") @Parameter(description = "分类主键") Integer pkey,
    //        @RequestParam(value = "sort") @Parameter(description = "排序") Integer sort,
    //        @RequestParam(value = "level") @Parameter(description = "1:一级分类,2:二级分类,3:三级分类") int level)
    //    {
    //        return new Result<>(manager.dragGtypeSort(pkey, sort, level));
    //    }
    
    @Operation(summary = "分类拖动排序", tags = ApiTags.GOODS_GTYPE_V4)
    @PostMapping("/drag/sort/ago")
    public Result<Boolean> dragGtypeSortAgo(@RequestParam(value = "pkey") @Parameter(description = "分类主键") Integer pkey,
        @RequestParam(value = "agoPkey", required = false) @Parameter(description = "前一个分类的主键") Integer agoPkey,
        @RequestParam(value = "afterPkey", required = false) @Parameter(description = "后一个分类的主键") Integer afterPkey,
        @RequestParam(value = "level") @Parameter(description = "1:一级分类,2:二级分类,3:三级分类") int level)
    {
        return new Result<>(manager.dragGtypeSortAgo(pkey, agoPkey, afterPkey, level));
    }
    
    @Operation(summary = "模板下载", tags = ApiTags.GOODS_GTYPE_V4)
    @PostMapping("/downTemplate")
    public void downTemplate(HttpServletRequest request, HttpServletResponse response)
    {
        OutputStream out;
        try
        {
            out = response.getOutputStream();
            excelHelper.excelTemplate(out, GtypeV4TemplateExcel.class);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    @Operation(summary = "导入分类", tags = ApiTags.GOODS_GTYPE_V4)
    @PostMapping("/importexcel")
    public void importExcel(MultipartFile myfile, HttpServletRequest request, HttpServletResponse response)
    {
        try (OutputStream out = response.getOutputStream())
        {
            manager.importExcel(myfile, out);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    @Operation(summary = "导出分类", tags = ApiTags.GOODS_GTYPE_V4)
    @PostMapping("/exportExcel")
    public void exportExcel(
        @RequestParam(value = "enabled", required = false) @Parameter(description = "状态") Boolean enabled,
        @RequestParam(value = "name", required = false) @Parameter(description = "分类名称") String name,
        HttpServletResponse response)
    {
        List<GtypeV4TemplateExcel> export = new ArrayList<>();
        List<GtypeV4OnList> list = manager.listGtypeV4(enabled, name);
        for (GtypeV4OnList g : list)
        {
            if (g.getGtypeLowerList() != null)
            {
                for (GtypeV4OnList gm : g.getGtypeLowerList())
                {
                    if (gm.getGtypeLowerList() != null)
                    {
                        for (GtypeV4OnList gmt : gm.getGtypeLowerList())
                        {
                            GtypeV4TemplateExcel d = new GtypeV4TemplateExcel();
                            d.setGtypeName(g.getName());
                            d.setGtypeTwoName(gm.getName());
                            d.setGtypeThreeName(gmt.getName());
                            d.setSysTwoGtypeName(gm.getSysTwoGtypeName());
                            export.add(d);
                        }
                    }
                    else
                    {
                        GtypeV4TemplateExcel d = new GtypeV4TemplateExcel();
                        d.setGtypeName(g.getName());
                        d.setGtypeTwoName(gm.getName());
                        d.setSysTwoGtypeName(gm.getSysTwoGtypeName());
                        export.add(d);
                    }
                }
            }
            else
            {
                GtypeV4TemplateExcel d = new GtypeV4TemplateExcel();
                d.setGtypeName(g.getName());
                export.add(d);
            }
        }
        try (OutputStream out = response.getOutputStream())
        {
            excelHelper.exportExcel(export, "Sheet1", out, GtypeV4TemplateExcel.class, null);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    @Autowired
    private GoodListQueryer goodListQueryer;
    // 5.22 上线跑批使用
    @PostMapping("/runGtype")
    public void runGtype(@RequestParam(value = "flag", required = false, defaultValue = "true") Boolean flag,
        @RequestParam(value = "farmer", required = false)String farmer)
    {
        if(Boolean.TRUE.equals(flag))
        {
//            manager.oldDataMigrate();
//            manager.changeGtype();
        }
        manager.updGtypeAndTwoAndThreeSort(farmer);
//        manager.runOrderExpressType();
        
        goodListQueryer.resetAll(farmer, null);
    }
    
    @Autowired
    private AppOrderManager orderManager;
    @PostMapping("/runOrder/test")
    public void runGtype(
        @RequestParam(value = "order", required = false)String order)
    {
        orderManager.payOrder(order);
    }
}
