package cn.tofocus.file.api;

import java.io.InputStream;
import java.io.OutputStream;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.common.util.StringUtil;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.db.file.IdUtil;
import cn.tofocus.file.bean.FileRef;
import cn.tofocus.file.bean.FileResponse;
import cn.tofocus.file.domain.FileServer;
import cn.tofocus.file.log.FileAccessLogger;
import lombok.extern.slf4j.Slf4j;

@Controller
@Deprecated
@Slf4j
public class FileControl
{
    @Autowired
    private FileServer server;
    
    @Value("${tofocus.file.oldApiLimit}")
    private long oldApiLimit;
    
    @Value("${tofocus.file.imageCache:2592000}")
    private int imageCache;
    
    @Autowired(required = false)
    private FileAccessLogger logger;
    
    @PostConstruct
    public void init()
    {
        if (logger == null)
            logger = new FileAccessLogger()
            {
            };
    }
    
    /**
     * 下载文件
     * @param id
     * @param response
     */
    @Deprecated
    @GetMapping(value = "/v1/download/{id}")
    public void download(@PathVariable String id, HttpServletRequest request, HttpServletResponse response)
    {
        String pkey = id;
        int idx = id.indexOf(".");
        if (idx > 0)
        {
            pkey = id.substring(0, idx);
        }
        if (oldApiLimit > 0 && Long.parseLong(pkey) > oldApiLimit)
        {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        response.setHeader("Cache-Control", "max-age=" + imageCache);
        FileRef ref = server.getFileRef(Long.parseLong(pkey));
        if (ref == null)
        {
            log.warn("下载文件{}失败，可能已升级V3接口", id);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        download2(id, ref.getMd5(), request, response);
    }
    
    /**
     * 下载文件
     * @param id
     * @param response
     */
    @Deprecated
    @GetMapping(value = "/v2/download")
    public void download2(@RequestParam("file") String id, @RequestParam("code") String code,
        HttpServletRequest request, HttpServletResponse response)
    {
        Long pkey = IdUtil.extractId(id);
        if (pkey == null)
        {
            log.warn("下载文件{}失败", id);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        
        String address = null;
        String xff = request.getHeader("x-forwarded-for");
        if (xff != null)
        {
            int index = xff.indexOf(',');
            if (index != -1)
            {
                xff = xff.substring(0, index);
            }
            address = xff.trim();
        }
        if (StringUtil.isEmpty(xff))
            address = request.getRemoteAddr();
        
        String referer = request.getHeader("Referer");
        String userAgent = request.getHeader("User-Agent");
        
        FileResponse fileResponse;
        try
        {
            response.setHeader("Cache-Control", "max-age=" + imageCache);
            logger.prepareLog(pkey, code, 0, null, address, referer, userAgent);
            fileResponse = server.getFile(pkey, code);
            if (fileResponse != null)
            {
                try (InputStream inputStream = fileResponse.getInputStream();
                    OutputStream outputStream = response.getOutputStream();)
                {
                    logger.setSize(inputStream.available());
                    response.setContentType("application/x-download");
                    if (fileResponse.getFileName() != null)
                    {
                        response.addHeader("Content-Disposition",
                            "attachment;filename=" + java.net.URLEncoder.encode(fileResponse.getFileName(), "UTF-8"));//重新定义下载后名称
                    }
                    //将文件输入流复制到输出流
                    IOUtils.copy(inputStream, outputStream);
                    outputStream.flush();
                    logger.setStatus("ok");
                }
            }
            else
            {
                log.warn("下载文件{}不存在", id);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        }
        catch (TofocusException e1)
        {
            log.warn("下载文件{}，禁止访问", id);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
        catch (Exception e1)
        {
            log.error("下载文件{}失败", id, e1);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
        finally
        {
            logger.flushLog();
        }
    }
    
    /**
     * 获取图片
     * @param id
     * @param thumb
     * @param response
     */
    @Deprecated
    @GetMapping(value = "/v1/image/{id}")
    public void downloadImage(@PathVariable String id, HttpServletRequest request, HttpServletResponse response)
    {
        String pkey = id;
        int idx = id.indexOf(".");
        if (idx > 0)
        {
            pkey = id.substring(0, idx);
        }
        if (oldApiLimit > 0 && Long.parseLong(pkey) > oldApiLimit)
        {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        response.setHeader("Cache-Control", "max-age=" + imageCache);
        FileRef ref = server.getFileRef(Long.parseLong(pkey));
        if (ref == null)
        {
            log.warn("下载图片{}失败，可能已升级V3接口", id);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        downloadImage2(id, ref.getMd5(), request, response);
    }
    
    /**
     * 获取图片
     * @param id
     * @param thumb
     * @param response
     */
    @Deprecated
    @GetMapping(value = "/v2/image")
    public void downloadImage2(@RequestParam("file") String id, @RequestParam("code") String code,
        HttpServletRequest request, HttpServletResponse response)
    {
        Long pkey = IdUtil.extractId(id);
        if (pkey == null)
        {
            log.warn("下载图片{}失败", id);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        
        String address = null;
        String xff = request.getHeader("x-forwarded-for");
        if (xff != null)
        {
            int index = xff.indexOf(',');
            if (index != -1)
            {
                xff = xff.substring(0, index);
            }
            address = xff.trim();
        }
        if (StringUtil.isEmpty(xff))
            address = request.getRemoteAddr();
        
        String referer = request.getHeader("Referer");
        String userAgent = request.getHeader("User-Agent");
        
        FileResponse fileResponse;
        try
        {
            response.setHeader("Cache-Control", "max-age=" + imageCache);
            logger.prepareLog(pkey, code, 0, null, address, referer, userAgent);
            fileResponse = server.getFile(pkey, code);
            if (fileResponse != null)
            {
                try (InputStream inputStream = fileResponse.getInputStream();
                    OutputStream outputStream = response.getOutputStream();)
                {
                    logger.setSize(inputStream.available());
                    if (fileResponse.getContentType() != null)
                        response.setContentType(fileResponse.getContentType());
                    else
                        response.setContentType("image/png");
                    //将文件输入流复制到输出流
                    IOUtils.copy(inputStream, outputStream);
                    outputStream.flush();
                    logger.setStatus("ok");
                }
            }
            else
            {
                log.warn("下载图片{}不存在", id);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        }
        catch (TofocusException e1)
        {
            log.warn("下载图片{}，禁止访问", id);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
        catch (Exception e1)
        {
            log.error("下载图片{}失败", id, e1);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
        finally
        {
            logger.flushLog();
        }
    }
}
