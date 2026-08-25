package cn.tofocus.lejia.api.v1.market;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONException;

import cn.tofocus.core.Result;
import cn.tofocus.core.log.LogApi;
import cn.tofocus.core.security.SecurityContextUtil;
import cn.tofocus.file.api.v3.FileApiV3;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.entity.vendor.MktVendor;
import cn.tofocus.lejia.config.SysProConfig;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.vendor.MktVendorDao;
import cn.tofocus.lejia.util.CryptStr;
import cn.tofocus.lejia.util.FileUtil;
import io.micrometer.core.instrument.util.StringUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@RequestMapping("/v1/market/vendor")
@Controller
public class UEditorApiImpl
{
    @Autowired
    private FileApiV3 fileApiV3;
    
    @Autowired
    private SecurityContextUtil securityContextUtil;
    
    @Autowired
    private MktVendorDao vendorDao;
    
    @Value("${zyysc.vendor.qrcode.prefix.url:https://small.xinanshizu.com/merchant_}")
    private String vendorPrefixUrl;
    
    // , method = {RequestMethod.POST, RequestMethod.GET}
    @Operation(summary = "文本框文件上传", tags = ApiTags.custVendor)
    @RequestMapping(value = "/uploadImage")
    public void uploadImage(
        @RequestPart(value = "upfile", required = false) MultipartFile file,
        HttpServletRequest request, HttpServletResponse response)
        throws JSONException
    {
//        Map<String, String[]> map = request.getParameterMap();
//        AuthenticationContext loginAsClient = SecurityContextUtil.loginAsClient();
//        response.setContentType("application/x-javascript");
//        String a = loginAsClient.getAccessToken().getTokenType() + " " + loginAsClient.getAccessToken().getValue();
//        response.setHeader("Authorization", a);
//        String rootPath = request.getSession().getServletContext().getRealPath("/");
//        String exec = "1";
//        try
//        {
//            exec = new ActionEnter(request, rootPath, fileApiV3).exec();
//            PrintWriter writer = response.getWriter();
//            writer.write(exec);
//            writer.flush();
//            writer.close();
//        }
//        catch (IOException e)
//        {
//            e.printStackTrace();
//        }
    }
    
    @Operation(summary = "二维码下载", tags = "合作商户")
    @GetMapping("/down/code")
    @LogApi(operation = "下载合作商户二维码", format = "下载合作商户二维码")
    public Result<Boolean> downExcel(Integer pkey, HttpServletRequest request, HttpServletResponse response)
    {
        BufferedImage img = null;
        MktVendor vendor = vendorDao.get(pkey);
        try
        {
            
            img = FileUtil.createImage(CryptStr.encryptStr(pkey + "", SysProConfig.VENDOR_AES_KEY), 500, 500);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return new Result<>(false);
        }
        FileUtil.buildExcelDocument(vendor.getName(), img, request, response);
        return new Result<>(true);
    }
    
    @PostMapping("/export/zip")
    public Result<Boolean> downExcelZip(
        @RequestParam(value = "flag", required = false, defaultValue = "false") @Parameter(description = "true 市场商城商户下载") Boolean flag,
        HttpServletRequest request, HttpServletResponse response)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        List<MktVendor> list = null;
        String marketPkey = CurrentSession.marketPkey();
        if (StringUtils.isNotBlank(marketPkey) && (Constant.Operation + ascription).equals(marketPkey) && flag)
        {
            list = vendorDao.select().eq("ascription", ascription).eq("idDel", false).notEq("farmer", marketPkey).exec();
        }
        else
        {
            list = vendorDao.select().eq("ascription", ascription).eq("idDel", false).eq("farmer", marketPkey).exec();
        }
        List<File> files = new ArrayList<>();
        for (MktVendor line : list)
        {
            try
            {
                String encryptStr = CryptStr.encryptStr(line.getPkey() + "", SysProConfig.VENDOR_AES_KEY);
                BufferedImage img = FileUtil
                    .createImage(vendorPrefixUrl + line.getAscription() + "?pkey=" + encryptStr, 500, 500);

//                BufferedImage img = FileUtil
//                    .createImage(CryptStr.encryptStr(line.getPkey() + "", SysProConfig.VENDOR_AES_KEY), 500, 500);
                String fileName = "";
                if(StringUtils.isNotBlank(line.getDisplayName()))
                    fileName = line.getDisplayName();
                if(StringUtils.isNotBlank(line.getBooth()))
                {
                    if(fileName.length() > 0)
                        fileName = fileName + "-" + line.getDisplayName();
                    else
                        fileName = line.getDisplayName();
                }
                if(fileName.length() <= 0)
                {
                    fileName = line.getName();
                }
//                File file = new File(fileName);
//                if(!file.exists())
//                {
//                    file.mkdirs();
//                    files.add(file);
//                }
                File outputfile = new File("积分二维码.png");
                ImageIO.write(img, "png", outputfile);
                files.add(outputfile);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        try
        {
            FileUtil.downLoadFiles(files, request, response);
            for (File f : files)
                f.delete();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return new Result<>(true);
    }
}
