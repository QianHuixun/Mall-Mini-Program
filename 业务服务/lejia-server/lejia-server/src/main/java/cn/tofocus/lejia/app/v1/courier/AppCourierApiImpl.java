package cn.tofocus.lejia.app.v1.courier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import cn.tofocus.core.Result;
import cn.tofocus.file.bean.FileInfoV3;
import cn.tofocus.lejia.app.AppTags;
import cn.tofocus.lejia.bean.dto.app.AppCourierDTO;
import cn.tofocus.lejia.domain.app.AppCourierManager;
import cn.tofocus.lejia.domain.app.AppVendorManager;
import io.swagger.v3.oas.annotations.Operation;

@RequestMapping("/v1/app/courier")
@RestController
public class AppCourierApiImpl implements AppCourierApi
{
    
    @Autowired
    private AppCourierManager courierManger;
    
    @Autowired
    private AppVendorManager vendorManager;
    
    @Override
    public Result<AppCourierDTO> getCourier()
    {
        return new Result<>(courierManger.getCourier());
    }
    
    @Operation(summary = "骑手端上传图片", tags = AppTags.mobileCourier)
    @PostMapping("/uploadImage")
    public Result<FileInfoV3> uploadImage(@RequestPart("file") MultipartFile file)
    {
        return vendorManager.uploadImage(file);
    }
    
}
