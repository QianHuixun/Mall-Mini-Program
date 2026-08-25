package cn.tofocus.lejia.api.v2;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.core.log.LogApi;
import cn.tofocus.lejia.bean.entity.vendor.MktVendor;
import cn.tofocus.lejia.config.SysProConfig;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.dao.vendor.MktVendorDao;
import cn.tofocus.lejia.util.CryptStr;
import cn.tofocus.lejia.util.FileUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@RequestMapping("/v2/market/vendor")
@Controller
public class UEditorApiV2Impl
{
    
    @Autowired
    private MktVendorDao vendorDao;
    
    @Value("${zyysc.vendor.qrcode.prefix.url:https://small.xinanshizu.com/merchant_}")
    private String vendorPrefixUrl;
    
    @Value("${zyysc.vendor.qrcode.prefix.scanPay.url:https://small.xinanshizu.com/scanPay_}")
    private String vendorScanPayPrefixUrl;
    
    @Operation(summary = "单个商户二维码下载", tags = "合作商户V2")
    @GetMapping("/down/code")
    @LogApi(operation = "下载合作商户二维码", format = "下载合作商户二维码")
    public Result<Boolean> downExcel(Integer pkey, HttpServletRequest request, HttpServletResponse response)
    {
        MktVendor vendor = vendorDao.get(pkey);
        qrCodeZip(Arrays.asList(vendor), request, response);
        return new Result<>(true);
    }
    
    @Operation(summary = "全部商户二维码下载", tags = "合作商户V2")
    @GetMapping("/export/zip")
    public Result<Boolean> downImgZip(
        @RequestParam(value = "flag", required = false, defaultValue = "false") @Parameter(description = "true 市场商城商户下载") Boolean flag,
        @RequestParam(value = "ascription") Integer ascription,
        @RequestParam(value = "marketPkey") String marketPkey,
        HttpServletRequest request, HttpServletResponse response)
    {
        List<MktVendor> list = null;
        if (marketPkey.startsWith(Constant.Operation) && flag)
        {
            // StringUtils.isNotBlank(marketPkey) && (Constant.Operation + ascription).equals(marketPkey) && 
            list = vendorDao.select().eq("ascription", ascription).eq("idDel", false).notEq("farmer", marketPkey).exec();
        }
        else
        {
            list = vendorDao.select().eq("ascription", ascription).eq("idDel", false).eq("farmer", marketPkey).exec();
        }
        qrCodeZip(list, request, response);
        return new Result<>(true);
    }
    
    private void qrCodeZip(List<MktVendor> list,
        HttpServletRequest request, HttpServletResponse response)
    {
        try
        {
            String zippath = request.getSession().getServletContext().getRealPath("/qrcode/");
            String savepath = zippath;
            File saveFile = new File(savepath);
            if (!saveFile.exists() && !(saveFile.isDirectory()))
            {
                saveFile.mkdirs();
            }
            
            
            String zipFileName = request.getSession().getServletContext().getRealPath("") + "qrcode.zip";
            for (MktVendor line : list)
            {
                String encryptStr = CryptStr.encryptStr(line.getPkey() + "", SysProConfig.VENDOR_AES_KEY);
                BufferedImage img = FileUtil
                    .createImage(vendorScanPayPrefixUrl + line.getAscription() + "?pkey=" + encryptStr, 500, 500);
//                BufferedImage img = FileUtil
//                    .createImage(CryptStr.encryptStr(line.getPkey() + "", SysProConfig.VENDOR_AES_KEY), 500, 500);
                File outputfile = new File(savepath + line.getName() + "-" + line.getBooth() +  "/积分二维码.png");
//                File outputfile = new File(savepath + line.getName() + "/" + line.getName()+ "积分二维码.png");
                if(!outputfile.exists())
                    outputfile.mkdirs();
                ImageIO.write(img, "png", outputfile);
                //  店铺二维码  TODO 缺 支付二维码  + "https://amrtest.xinanshizu.com?pkey="
                System.out.println("vendorPrefixUrl + line.getPkey(): " + vendorPrefixUrl + line.getAscription() + "?pkey=" + line.getPkey());
                BufferedImage img2 = FileUtil
                    .createImage(vendorPrefixUrl + line.getAscription() + "?pkey=" + line.getPkey() + "", 500, 500);
//                BufferedImage img2 = FileUtil
//                    .createImage(CryptStr.encryptStr(vendorPrefixUrl + line.getPkey() + "", SysProConfig.VENDOR_AES_KEY), 500, 500);
                File outputfile2 = new File(savepath + line.getName() + "-" + line.getBooth() +  "/店铺二维码.png");
                if(!outputfile2.exists())
                    outputfile2.mkdirs();
                ImageIO.write(img2, "png", outputfile2);
            }
            File zipFile = new File(zipFileName);
            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(zipFile), 1024 * 10);
            ZipOutputStream zos = new ZipOutputStream(outputStream);
            writeZip(new File(savepath), "", zos);
           
            zos.close();
            FileUtil.downloadZip(zipFile, response);
            File df = new File(zippath);
            if(df.exists())
                delFiles(df);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    private void delFiles(File file)
    {
        File[] files = file.listFiles();
        if(files != null)
        {
            if(file.isDirectory())
            {
                for(File f : file.listFiles())
                {
                    delFiles(f);
                }
            }
            else
                file.delete();
        }
        else
        {
            file.delete();
        }
        file.delete();
    }
    
    private void writeZip(File file, String parentPath, ZipOutputStream zos)
    {
        if (file.exists())
        {
            if (file.isDirectory())
            {
                parentPath += file.getName() + File.separator;
                File[] files = file.listFiles();
                for (File f : files)
                {
                    writeZip(f, parentPath, zos);
                }
            }
            else
            {
                DataInputStream dis = null;
                try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file)))
                {
                    dis = new DataInputStream(inputStream);
                    ZipEntry ze = new ZipEntry(parentPath + file.getName());
                    zos.putNextEntry(ze);
                    byte[] content = new byte[1024 * 10];
                    int len;
                    while ((len = dis.read(content)) != -1)
                    {
                        zos.write(content, 0, len);
                        zos.flush();
                    }
                    
                    zos.closeEntry();
                }
                catch (FileNotFoundException e)
                {
                    throw new RuntimeException(e);
                }
                catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
                finally
                {
                    try
                    {
                        if (dis != null)
                        {
                            dis.close();
                        }
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    
}
