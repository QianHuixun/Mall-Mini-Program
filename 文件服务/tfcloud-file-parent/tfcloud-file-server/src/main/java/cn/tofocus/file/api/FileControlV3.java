package cn.tofocus.file.api;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.connector.ClientAbortException;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.common.util.StringUtil;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.db.file.IdUtil;
import cn.tofocus.file.bean.Constant;
import cn.tofocus.file.bean.FileResponseV3;
import cn.tofocus.file.bean.ThumbType;
import cn.tofocus.file.bean.UploadType;
import cn.tofocus.file.domain.FileServerV3;
import cn.tofocus.file.log.FileAccessLogger;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class FileControlV3
{
    @Value("${tofocus.file.imageCache:2592000}")
    private int imageCache;
    
    @Autowired
    private FileServerV3 server;
    
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
     * 获取图片
     * @param file
     * @param thumb
     * @param response
     */
    @GetMapping(value = Constant.imgUrl)
    public void viewImage(@RequestParam("file") String file, @RequestParam("code") String code,
        @RequestParam(defaultValue = "big") ThumbType thumb, HttpServletResponse response, HttpServletRequest request)
    {
        outputFile(file, code, UploadType.image, thumb, request, response, null);
    }
    
    /**
     * 下载图片文件
     * @param file
     * @param thumb
     * @param response
     */
    @GetMapping(value = Constant.imgDownLoadUrl)
    public void downloadImage(@RequestParam("file") String file, @RequestParam("code") String code,
        @RequestParam(defaultValue = "orgin") ThumbType thumb, HttpServletResponse response, HttpServletRequest request)
    {
        outputFile(file, code, UploadType.image, thumb, request, response, "application/x-download");
    }
    
    /**
     * 下载文件
     * @param file
     * @param response
     */
    @GetMapping(value = Constant.fileDownLoadUrl)
    public void downloadFile(@RequestParam("file") String file, @RequestParam("code") String code,
        HttpServletResponse response, HttpServletRequest request)
    {
        outputFile(file, code, UploadType.file, null, request, response, "application/x-download");
    }
    
    private void outputFile(String id, String code, UploadType type, ThumbType thumb, HttpServletRequest request,
        HttpServletResponse response, String contentType)
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
        
        FileResponseV3 fileResponse;
        try
        {
            response.setHeader("Cache-Control", "max-age=" + imageCache);
            logger.prepareLog(pkey, code, 0, thumb, address, referer, userAgent);
            fileResponse = server.download(type, pkey, code, thumb);
            if (fileResponse != null)
            {
                //获取请求头中Range的值
                String rangeString = request.getHeader(HttpHeaders.RANGE);
                //类型
                if (contentType != null)
                    response.setContentType(contentType);
                else if (fileResponse.getContentType() != null)
                    response.setContentType(fileResponse.getContentType());
                else
                    response.setContentType("image/jpeg");
                if (fileResponse.getFileName() != null
                    && response.getContentType().equals("application/x-download"))
                {
                    response.addHeader("Content-Disposition",
                        "attachment;filename="
                            + java.net.URLEncoder.encode(fileResponse.getFileName(), "UTF-8"));//重新定义下载后名称
                }
                //是否断点下载
                if (StringUtil.isNotBlank(rangeString))
                {
                    try (OutputStream outputStream = response.getOutputStream();
                        RandomAccessFile targetFile = new RandomAccessFile(fileResponse.getFile(), "r");)
                    {
                        response.reset();
                        long fileLength = targetFile.length();
                        long requestSize = (int)fileLength;
                        
                        //从Range中提取需要获取数据的开始和结束位置
                        long requestStart = 0;
                        long requestEnd = 0;
                        String[] ranges = rangeString.split("=");
                        if (ranges.length > 1)
                        {
                            String[] rangeDatas = ranges[1].split("-");
                            requestStart = Integer.parseInt(rangeDatas[0]);
                            if (rangeDatas.length > 1)
                            {
                                requestEnd = Integer.parseInt(rangeDatas[1]);
                            }
                        }
                        if (requestEnd != 0 && requestEnd > requestStart)
                        {
                            requestSize = requestEnd - requestStart + 1;
                        }
                        //根据协议设置请求头
                        response.setHeader(HttpHeaders.ACCEPT_RANGES, "bytes");
                        if (!StringUtil.isNotBlank(rangeString))
                        {
                            response.setHeader(HttpHeaders.CONTENT_LENGTH, fileLength + "");
                        }
                        else
                        {
                            long length;
                            if (requestEnd > 0)
                            {
                                length = requestEnd - requestStart + 1;
                                response.setHeader(HttpHeaders.CONTENT_LENGTH, "" + length);
                                response.setHeader(HttpHeaders.CONTENT_RANGE,
                                    "bytes " + requestStart + "-" + requestEnd + "/" + fileLength);
                            }
                            else
                            {
                                length = fileLength - requestStart;
                                response.setHeader(HttpHeaders.CONTENT_LENGTH, "" + length);
                                response.setHeader(HttpHeaders.CONTENT_RANGE,
                                    "bytes " + requestStart + "-" + (fileLength - 1) + "/" + fileLength);
                            }
                        }
                        //分段下载视频返回206
                        response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
                        //设置targetFile，从自定义位置开始读取数据
                        targetFile.seek(requestStart);
                        
                        //从磁盘读取数据流返回
                        byte[] cache = new byte[4096];
                        while (requestSize > 0)
                        {
                            int len = targetFile.read(cache);
                            if (requestSize < cache.length)
                            {
                                outputStream.write(cache, 0, (int)requestSize);
                            }
                            else
                            {
                                outputStream.write(cache, 0, len);
                                if (len < cache.length)
                                {
                                    break;
                                }
                            }
                            requestSize -= cache.length;
                        }
                        outputStream.flush();
                    }
                    catch (ClientAbortException ioe)
                    {
                        //忽略
                    }
                    catch (Exception e)
                    {
                        log.warn("发生异常", e);
                    }
                }
                else
                {
                    try (InputStream inputStream = new FileInputStream(fileResponse.getFile());
                        OutputStream outputStream = response.getOutputStream();)
                    {
                        //将文件输入流复制到输出流
                        IOUtils.copy(inputStream, outputStream);
                        outputStream.flush();
                        logger.setSize(fileResponse.getFile().length());
                        logger.setStatus("ok");
                    }
                }
            }
            else
            {
                logger.setStatus("404");
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
            log.warn("下载文件{}失败", id, e1);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
        finally
        {
            logger.flushLog();
        }
    }
    
}
