package cn.tofocus.lejia.api.v1.market;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.market.DesktopOnInfo;
import cn.tofocus.lejia.bean.entity.market.MktDesktop;
import cn.tofocus.lejia.dao.market.MktDesktopDao;
import cn.tofocus.lejia.domain.market.DesktopManager;
import cn.tofocus.lejia.util.FileUtil;
import io.swagger.v3.oas.annotations.Operation;

@RequestMapping("/v1/market/desktop")
@RestController
public class DesktopApiImpl
{
    @Autowired
    private DesktopManager manager;
    
    @Autowired
    private MktDesktopDao desktopDao;
    
    @Operation(summary = "获取桌位管理列表", tags = ApiTags.DESKTOP_MANAGE)
    @PostMapping(value = "/query")
    public Result<PageResult<DesktopOnInfo>> query(
        @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
        @RequestParam(value = "pagesize", required = false, defaultValue = "10") Integer pagesize,
        @RequestParam(value = "name", required = false) String name)
    {
        return new Result<>(manager.query(page, pagesize, name));
    }
    
    @Operation(summary = "新增/编辑桌位", tags = ApiTags.DESKTOP_MANAGE)
    @PostMapping(value = "/put")
    public Result<Boolean> put(@RequestBody DesktopOnInfo info)
    {
        return new Result<>(manager.put(info));
    }
    
    @Operation(summary = "删除桌位", tags = ApiTags.DESKTOP_MANAGE)
    @PostMapping(value = "/del")
    public Result<Boolean> del(@RequestParam(value = "pkey", required = false) Integer pkey)
    {
        return new Result<>(manager.del(pkey));
    }
    
    @Operation(summary = "下载二维码", tags = ApiTags.DESKTOP_MANAGE)
    @GetMapping(value = "/download/qrCode")
    public void downloadQrCode(       
        @RequestParam(value = "ascription") Integer ascription,
        @RequestParam(value = "marketPkey") String marketPkey,
        @RequestParam(value = "name", required = false) String name, HttpServletRequest request,
        HttpServletResponse response)
    {
        List<MktDesktop> list = desktopDao.list(name, marketPkey, ascription);
        qrCodeZip(list, request, response);
    }
    
    private void qrCodeZip(List<MktDesktop> qrCode, HttpServletRequest request, HttpServletResponse response)
    {
        try
        {
            String zippath = request.getSession().getServletContext().getRealPath("/desktopQrcode/");
            String savepath = zippath;
            File saveFile = new File(savepath);
            if (!saveFile.exists() && !(saveFile.isDirectory()))
            {
                saveFile.mkdirs();
            }
            
            String zipFileName = request.getSession().getServletContext().getRealPath("") + "desktopQrcode.zip";
            for (MktDesktop line : qrCode)
            {
                BufferedImage img =
                    FileUtil.createImage(line.getQrCode(), 500, 500);
//                FileUtil.createImage(CryptStr.encryptStr(line.getQrCode(), SysProConfig.VENDOR_AES_KEY), 500, 500);
                // line.getPkey() +
                File outputfile = new File(savepath + line.getName() + "二维码.png");
                if (!outputfile.exists()) outputfile.mkdirs();
                ImageIO.write(img, "png", outputfile);
            }
            File zipFile = new File(zipFileName);
            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(zipFile), 1024 * 10);
            ZipOutputStream zos = new ZipOutputStream(outputStream);
            writeZip(new File(savepath), "", zos);
            
            zos.close();
            FileUtil.downloadZip(zipFile, response);
            File df = new File(zippath);
            if (df.exists()) delFiles(df);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    private void delFiles(File file)
    {
        File[] files = file.listFiles();
        if (files != null)
        {
            if (file.isDirectory())
            {
                for (File f : file.listFiles())
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
