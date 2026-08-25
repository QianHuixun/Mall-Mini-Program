package cn.tofocus.lejia.api.v1.market;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.ExpressExportExcel;
import cn.tofocus.lejia.bean.dto.market.MktExpressOnList;
import cn.tofocus.lejia.bean.enums.ExpressStatus;
import cn.tofocus.lejia.domain.market.MktExpressManager;
import cn.tofocus.lejia.util.ExportUtil;

@RequestMapping("/v1/market/express")
@RestController
public class MktExpressApiImpl implements MktExpressApi
{
    @Autowired
    private MktExpressManager expressManager;
    
    @Override
    public Result<PageResult<MktExpressOnList>> queryExpress(int page, int pagesize, ExpressStatus status,
        String startTime, String endTime, String courierName, String orderId)
    {
        return new Result<>(
            expressManager.queryExpress(page, pagesize, status, startTime, endTime, courierName, orderId));
    }
    
    // 导出骑手订单
    @PostMapping("/exportexcel")
    public void exportexcel(HttpServletResponse response)
    {
        List<ExpressExportExcel> list = expressManager.exportexcel();
        ExportUtil.exportData(ExpressExportExcel.class, list, response, "骑手订单", "Sheet1");
    }
}
