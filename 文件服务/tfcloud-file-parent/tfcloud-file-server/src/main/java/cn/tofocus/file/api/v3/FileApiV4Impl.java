package cn.tofocus.file.api.v3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import cn.tofocus.common.util.security.AESUtils;
import cn.tofocus.core.Result;
import cn.tofocus.core.exception.SysErrCode;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.file.bean.FileInfoV3;
import cn.tofocus.file.domain.FileServerV3;

@RequestMapping("/v4")
@RestController
public class FileApiV4Impl implements FileApiV4
{
    private static final String key = "u98@oY34j5.Ihg7";
    
    @Autowired
    private FileServerV3 server;

    @Override
    public Result<FileInfoV3> uploadImage(MultipartFile file, String fileType, String title, String memo)
    {
        String[] exts = decryptExts(fileType);
        FileInfoV3 info = server.uploadImage(file, exts, title, memo);
        return new Result<>(info);
    }
    
    @Override
    public Result<FileInfoV3> uploadFile(MultipartFile file, String fileType, String title, String memo)
    {
        String[] exts = decryptExts(fileType);
        FileInfoV3 info = server.uploadFile(file, exts, title, memo);
        return new Result<>(info);
    }
    
    private static String[] decryptExts(String data)
    {
        try
        {
            String[] arr = data.split("_");
            if (arr.length < 2 && !"1".equals(arr[0]))
                throw TofocusException.of(SysErrCode.REQUIRED_PRARAM_ERROR, "文件类型不匹配");
            String[] exts = AESUtils.decryptStr(arr[1], key).split(",");
            if (exts == null || exts.length == 0)
                throw TofocusException.of(SysErrCode.REQUIRED_PRARAM_ERROR, "文件类型不匹配");
            else
                return exts;
        }
        catch (Exception e)
        {
            throw TofocusException.of(SysErrCode.REQUIRED_PRARAM_ERROR, e, "文件类型不匹配");
        }
    }
}
