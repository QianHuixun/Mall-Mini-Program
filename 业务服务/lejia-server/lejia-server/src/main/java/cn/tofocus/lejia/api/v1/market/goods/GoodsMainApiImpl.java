package cn.tofocus.lejia.api.v1.market.goods;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import cn.tofocus.common.excel.ExcelHelper;
import cn.tofocus.core.Result;
import cn.tofocus.core.log.LogApi;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.MktGoodsMainExcel;
import cn.tofocus.lejia.bean.dto.MktGoodsMainThreeExcel;
import cn.tofocus.lejia.bean.dto.PkeyNameDTO;
import cn.tofocus.lejia.bean.dto.gtype.GtypeInfo;
import cn.tofocus.lejia.bean.dto.market.MktGoodsMainOnList;
import cn.tofocus.lejia.bean.dto.market.MktGoodsMainThreeOnList;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsMain;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsMainThree;
import cn.tofocus.lejia.dao.DeviceExcelImportListener;
import cn.tofocus.lejia.dao.DeviceThreeExcelImportListener;
import cn.tofocus.lejia.dao.goods.MktGoodsMainDao;
import cn.tofocus.lejia.dao.goods.MktGoodsMainThreeDao;
import cn.tofocus.lejia.domain.market.goods.GoodsMainManager;
import io.swagger.v3.oas.annotations.Operation;

@RequestMapping("/v1/market/goods/main")
@RestController
public class GoodsMainApiImpl implements GoodsMainApi
{
    @Autowired
    private GoodsMainManager goodsMainManager;
    
    @Autowired
    private MktGoodsMainDao goodsMainDao;
    
    @Autowired
    private MktGoodsMainThreeDao goodsMainThreeDao;
    
    @Autowired
    private ExcelHelper excelHelper;
    
    @Override
    @LogApi(operation = "新增商品-商品库", format = "新增商品库商品名称:{entity.name}", resultFormat = "")
    public Result<Integer> insGoodsMain(MktGoodsMainOnList entity)
    {
        return new Result<>(goodsMainManager.insGoodsMain(entity));
    }
    
    @Override
    public Result<PageResult<MktGoodsMainOnList>> queryGoodsMain(Integer page, Integer pagesize, Integer gtype,
        String name, Boolean enabled)
    {
        return new Result<>(goodsMainManager.queryGoodsMain(page, pagesize, gtype, name, enabled));
    }
    
    @Override
    @LogApi(operation = "修改商品-商品库", format = "修改商品库商品,名称:{name}")
    public Result<MktGoodsMainOnList> updGoodsMain(Integer pkey, String name, Integer sort, Integer gtype,
        String remark)
    {
        return new Result<>(goodsMainManager.updGoodsMain(pkey, name, sort, gtype, remark));
    }
    
    @Override
    @LogApi(operation = "删除商品-商品库", format = "删除商品库商品")
    public Result<Boolean> delGoodsMain(Integer pkey)
    {
        return new Result<>(goodsMainManager.delGoodsMain(pkey));
    }
    
    @Override
    @LogApi(operation = "启动商品-商品库", format = "启动商品库商品")
    public Result<Boolean> startGoodsMain(Integer pkey)
    {
        return new Result<>(goodsMainManager.enabledGoodsMain(pkey, true));
    }
    
    @Override
    @LogApi(operation = "停止商品-商品库", format = "停止商品库商品")
    public Result<Boolean> stopGoodsMain(Integer pkey)
    {
        return new Result<>(goodsMainManager.enabledGoodsMain(pkey, false));
    }
    
    @Autowired
    private DeviceExcelImportListener deviceExcelImportListener;
    
    @LogApi(operation = "导入商品库信息excel", format = "导入商品库信息excel")
    @Operation(summary = "商品库excle导入", tags = ApiTags.custGoodsMain)
    @PostMapping(value = "/importexcel")
    public void importExcel(MultipartFile myfile, HttpServletResponse response)
    {
        OutputStream out = null;
        try
        {
            String fileName = new String("商品库导入失败数据.xlsx".getBytes(), "iso-8859-1");
            response.setHeader("Content-disposition", "attachment; filename = " + fileName);
            out = response.getOutputStream();
            excelHelper.importExcelToDb(goodsMainDao, MktGoodsMain.class, myfile.getInputStream(), MktGoodsMainExcel.class, deviceExcelImportListener, out);
            out.flush();
            out.close();
        }
        catch (Exception e)
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

    @Override
    public Result<List<PkeyNameDTO>> listGift()
    {
        return new Result<>(goodsMainManager.listDrop(true));
    }

    @Override
    public Result<List<PkeyNameDTO>> listCoupon()
    {
        return new Result<>(goodsMainManager.listDrop(false));
    }

    @Override
    public Result<Integer> insGoodsMainThree(MktGoodsMainThreeOnList entity)
    {
        return new Result<>(goodsMainManager.insGoodsMainThree(entity));
    }

    @Override
    public Result<PageResult<MktGoodsMainThreeOnList>> queryGoodsMainThree(Integer page, Integer pagesize,
        Integer twoGtype, String name, Boolean enabled)
    {
        return new Result<>(goodsMainManager.queryGoodsMainThree(page, pagesize, twoGtype, name, enabled));
    }

    @Override
    public Result<MktGoodsMainThreeOnList> updGoodsMainThree(Integer pkey, String name, Integer sort, Integer twoGtype,
        String remark)
    {
        return new Result<>(goodsMainManager.updGoodsMainThree(pkey, name, sort, twoGtype, remark));
    }

    @Override
    public Result<Boolean> delGoodsMainThree(Integer pkey)
    {
        return new Result<>(goodsMainManager.delGoodsMainThree(pkey));
    }

    @Override
    public Result<Boolean> startGoodsMainThree(Integer pkey)
    {
        return new Result<>(goodsMainManager.enabledGoodsMainThree(pkey, true));
    }

    @Override
    public Result<Boolean> stopGoodsMainThree(Integer pkey)
    {
        return new Result<>(goodsMainManager.enabledGoodsMainThree(pkey, false));
    }
    
    @Autowired
    private DeviceThreeExcelImportListener deviceThreeExcelImportListener;

    @LogApi(operation = "导入三级分类信息excel", format = "导入商品库信息excel")
    @Operation(summary = "三级分类excle导入", tags = ApiTags.custGoodsMain)
    @PostMapping(value = "/three/importexcel")
    public void importExcelThree(MultipartFile myfile, HttpServletResponse response)
    {
        OutputStream out = null;
        try
        {
            String fileName = new String("三级分类导入失败数据.xlsx".getBytes(), "iso-8859-1");
            response.setHeader("Content-disposition", "attachment; filename = " + fileName);
            out = response.getOutputStream();
            excelHelper.importExcelToDb(goodsMainThreeDao, MktGoodsMainThree.class, myfile.getInputStream(), MktGoodsMainThreeExcel.class, deviceThreeExcelImportListener, out);
            out.flush();
            out.close();
        }
        catch (Exception e)
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

    @Override
    public Result<List<GtypeInfo>> listSys()
    {
         return new Result<>(goodsMainManager.listSys());
    }
    
}
