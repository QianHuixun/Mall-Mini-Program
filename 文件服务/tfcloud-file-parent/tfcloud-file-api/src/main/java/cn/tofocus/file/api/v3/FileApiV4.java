package cn.tofocus.file.api.v3;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import cn.tofocus.core.Result;
import cn.tofocus.core.exception.DefaultFallbackFactory;
import cn.tofocus.core.feign.FeignConfig;
import cn.tofocus.file.api.ApiTags;
import cn.tofocus.file.bean.FileInfoV3;
import io.swagger.v3.oas.annotations.Operation;

@FeignClient(value = "file", path = "/v4", contextId = "v4", fallbackFactory = DefaultFallbackFactory.class, configuration = FeignConfig.class)
public interface FileApiV4
{
    /**
     * 上传图片
     * @param file
     * @param title
     * @param memo
     * @return
     */
    @Operation(summary = "上传图片", tags = ApiTags.UPLOAD)
    @PostMapping(value = "/image/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Result<FileInfoV3> uploadImage(@RequestPart("file") MultipartFile file,
        @RequestParam(value = "fileType") String fileType, @RequestParam(value = "title") String title,
        @RequestParam(value = "memo") String memo);
    
    /**
     * 上传文件
     * @param file
     * @param title
     * @param memo
     * @return
     */
    @Operation(summary = "上传文件", tags = ApiTags.UPLOAD)
    @PostMapping(value = "/file/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Result<FileInfoV3> uploadFile(@RequestPart("file")
    MultipartFile file, @RequestParam(value = "fileType")
    String fileType, @RequestParam(value = "title")
    String title, @RequestParam(value = "memo")
    String memo);
}
