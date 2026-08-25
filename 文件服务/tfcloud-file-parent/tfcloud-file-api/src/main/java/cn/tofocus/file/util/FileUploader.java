package cn.tofocus.file.util;

import java.io.File;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import cn.tofocus.common.util.file.FileUtil;
import cn.tofocus.core.security.SecurityContextUtil;
import cn.tofocus.file.api.v3.FileApiV3;
import cn.tofocus.file.bean.FileInfoV3;
import cn.tofocus.file.bean.MemoryMultipartFile;
import lombok.extern.slf4j.Slf4j;
import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicException;
import net.sf.jmimemagic.MagicMatch;
import net.sf.jmimemagic.MagicMatchNotFoundException;
import net.sf.jmimemagic.MagicParseException;

@Component
@Slf4j
public class FileUploader
{
    @Autowired
    private FileApiV3 fileApi;
    
    public FileInfoV3 uploadImage(@NotNull File file, @NotBlank String title, @NotBlank String memo)
    {
        String contentType = null;
        try
        {
            MagicMatch match = Magic.getMagicMatch(file, true, true);
            contentType = match.getMimeType();
        }
        catch (MagicParseException | MagicMatchNotFoundException | MagicException e)
        {
            log.warn("{} 的contentType分析失败，title {}，memo {}", file.getName(), title, memo);
        }
        return uploadImage(FileUtil.getBytes(file), contentType, file.getName(), title, memo);
    }
    
    public FileInfoV3 uploadImage(@NotNull byte[] fileContent, String filename, @NotBlank String title,
        @NotBlank String memo)
    {
        String contentType = null;
        try
        {
            MagicMatch match = Magic.getMagicMatch(fileContent, true);
            contentType = match.getMimeType();
        }
        catch (MagicParseException | MagicMatchNotFoundException | MagicException e)
        {
            log.warn("{} 的contentType分析失败，title {}，memo {}", filename, title, memo);
        }
        return uploadImage(fileContent, contentType, filename, title, memo);
    }
    
    private FileInfoV3 uploadImage(byte[] fileContent, String contentType, String filename, String title, String memo)
    {
        MultipartFile tmpfile = new MemoryMultipartFile("file", filename, contentType, fileContent);
        FileInfoV3 fileinfo = SecurityContextUtil.callApiAutoLogin(k -> fileApi.uploadImage(tmpfile, title, memo));
        return fileinfo;
    }
    
    public FileInfoV3 uploadFile(@NotNull File file, @NotBlank String title, @NotBlank String memo)
    {
        String contentType = null;
        try
        {
            MagicMatch match = Magic.getMagicMatch(file, true, true);
            contentType = match.getMimeType();
        }
        catch (MagicParseException | MagicMatchNotFoundException | MagicException e)
        {
            log.warn("{} 的contentType分析失败，title {}，memo {}", file.getName(), title, memo);
        }
        return uploadFile(FileUtil.getBytes(file), contentType, file.getName(), title, memo);
    }
    
    public FileInfoV3 uploadFile(@NotNull byte[] fileContent, String filename, @NotBlank String title,
        @NotBlank String memo)
    {
        String contentType = null;
        try
        {
            MagicMatch match = Magic.getMagicMatch(fileContent, true);
            contentType = match.getMimeType();
        }
        catch (MagicParseException | MagicMatchNotFoundException | MagicException e)
        {
            log.warn("{} 的contentType分析失败，title {}，memo {}", filename, title, memo);
        }
        return uploadFile(fileContent, contentType, filename, title, memo);
    }
    
    private FileInfoV3 uploadFile(byte[] fileContent, String contentType, String filename, String title, String memo)
    {
        MultipartFile tmpfile = new MemoryMultipartFile("file", filename, contentType, fileContent);
        FileInfoV3 fileinfo = SecurityContextUtil.callApiAutoLogin(k -> fileApi.uploadFile(tmpfile, title, memo));
        return fileinfo;
    }
}
