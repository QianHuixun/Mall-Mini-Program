package cn.tofocus.lejia.api.v1.vendor;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.common.util.file.FileUtil;
import cn.tofocus.common.util.security.Base64;
import cn.tofocus.core.Result;
import cn.tofocus.lejia.bean.entity.vendor.MktVendor;
import cn.tofocus.lejia.bean.enums.ZxFileType;
import cn.tofocus.lejia.dao.vendor.MktVendorDao;
import cn.tofocus.lejia.domain.app.AppZxEqManager;
import cn.tofocus.lejia.domain.vendor.ZxTaskManager;
import cn.tofocus.lejia.utils.DateUtil;
import cn.tofocus.lejia.zx.bean.T21000029Response;
import cn.tofocus.lejia.zx.bean.T21000035Response;
import cn.tofocus.lejia.zx.bean.T21000036Response;

@RequestMapping("/v1/vendor/zx/settlement")
@RestController
public class ZxSettlementApiImpl
{
    @Autowired
    private ZxTaskManager zxTaskManager;
    
    @Autowired
    private AppZxEqManager appZxEqManager;
    
    @Autowired
    private MktVendorDao vendorDao;
    
    @PostMapping(value = "/add/report")
    public Result<Boolean> runFileUpload()
    {
        zxTaskManager.runFileUpload();
        return new Result<>(true);
    }
    
    @PostMapping(value = "/add/report/key")
    public Result<Boolean> runFileUploadKey(Integer pkey, String fileCount)
    {
        if(pkey == null || StringUtils.isBlank(fileCount))
            return new Result<>(false);
        appZxEqManager.runFileUploadKey(pkey, fileCount);
        return new Result<>(true);
    }
    
    @PostMapping(value = "/add/report/test")
    public Result<Boolean> runFileUploadTest(Date date)
    {
        System.out.println("date: " + DateUtil.formatDate(date));
        // 生成报表
        if (date == null)
        {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_WEEK, -1);
            date = cal.getTime();
        }
        zxTaskManager.runFileUploadTest(date);
        return new Result<>(true);
    }
    @PostMapping(value = "/add/report/date")
    public Result<Boolean> runFileUploadTest2(Date date, String settleKey, Boolean flag)
    {
        System.out.println("date: " + DateUtil.formatDate(date));
        // 生成报表
        if(flag == null)
            flag = false;
        zxTaskManager.runFileUploadTest2(date, settleKey, flag);
        return new Result<>(true);
    }
    
    @PostMapping(value = "/get/report")
    public Result<String> getReport()
    {
        // 查询文件状态
        String res = zxTaskManager.runQueryFile();
        return new Result<>(res);
    }
    
    @PostMapping(value = "/get/report/filename")
    public Result<String> getReport(String filename)
    {
        // 查询文件状态
        String res = appZxEqManager.getFileStatus(filename);
        return new Result<>(res);
    }
    
    @PostMapping(value = "/withdraw")
    public Result<Boolean> withdraw()
    {
        // 提现 
        zxTaskManager.runWithdraw();
        return new Result<>(true);
    }
    
    @PostMapping(value = "/withdraw/vendor")
    public Result<Boolean> withdrawVendor(Integer vendor, BigDecimal amt)
    {
        // 指定提现
        System.out.println("指定提现: " + vendor + "   指定金额: " + amt);
        if(vendor == null)
            return new Result<>(false);
        MktVendor mktVendor = vendorDao.get(vendor);
        if(mktVendor == null)
            return new Result<>(false);
        appZxEqManager.withdraw(mktVendor, amt);
        return new Result<>(true);
    }
    
    @PostMapping(value = "/failFile")
    public Result<Boolean> runFailFile()
    {
        // 失败处理  对文件上传 失败的处理
        zxTaskManager.runFailFile();
        return new Result<>(true);
    }
    
    @PostMapping(value = "/successFile")
    public Result<Boolean> runSuccessFile()
    {
        // 成功处理
        zxTaskManager.runSuccessFile();
        return new Result<>(true);
    }
    
    @PostMapping(value = "/test/report")
    public Result<Boolean> testfileUpload(String fileSaveLj)
    {
        try
        {
            byte[] bs = FileUtil.readFileContent(fileSaveLj);
            String fromFile = Base64.encodeBytes(bs, Base64.DONT_BREAK_LINES);
            System.out.println(fromFile);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        return new Result<>(true);
    }
    
    @PostMapping(value = "/inquire/balance")
    public Result<String> getBalance(String userId)
    {
        // 用户余额查询
        String res = appZxEqManager.getBalance(userId);
        return new Result<>(res);
    }
    
    @PostMapping(value = "/inquire/tradeBalance")
    public Result<T21000036Response> getTradeBalance()
    {
        // 交易资金账户余额查询
        T21000036Response res = appZxEqManager.getTradeBalance();
        return new Result<>(res);
    }
    
    @PostMapping(value = "/register/detail")
    public Result<T21000029Response> getRegisterDetail(String userId, String date, String transType)
    {
        // 登记簿交易明细查询  date格式: yyyyMMdd
        T21000029Response res = appZxEqManager.getRegisterDetail(userId, date, transType);
        return new Result<>(res);
    }
    
    @PostMapping(value = "/register/balance")
    public Result<T21000035Response> getRegisterBalance(String registerAttr)
    {
        // 公共登记簿交易明细查询
        /*
        00-公共计息收费登记薄
        12-自有资金登记薄
        13-担保登记薄
        17-待结算手续费登记簿
         */
        if (StringUtils.isBlank(registerAttr)) registerAttr = "00";
        T21000035Response res = appZxEqManager.getRegisterBalance(registerAttr);
        return new Result<>(res);
    }
    
    @PostMapping(value = "/download/file")
    public Result<String> downloadFile(String fileName, ZxFileType fileType, String settleDt)
    {
        /**
         * 文件下载
         * @param fileName
         * @param fileType  101: 清分文件  102:用户提现文件  104: 用户退汇明细文件  999-人行联行号 113-内部户明细
         * 111-登记簿资金变动  109-自有资金明细文件   114-客户账对账明细
         * @param settleDt  清算日期
         */
        String res = appZxEqManager.downloadFile(fileName, fileType, settleDt);
        return new Result<>(res);
    }
    
    @PostMapping(value = "/information/change")
    public Result<String> informationChange2(Integer key)
    {
        MktVendor vendor = vendorDao.get(key);
        appZxEqManager.informationChange(vendor);
        return new Result<>();
    }
    
    
    @PostMapping(value = "/tiedCard")
    public Result<String> tiedCard(Integer key, Boolean flag)
    {
        MktVendor vendor = vendorDao.get(key);
        appZxEqManager.tiedCard(vendor, flag);
        return new Result<>();
    }
}
