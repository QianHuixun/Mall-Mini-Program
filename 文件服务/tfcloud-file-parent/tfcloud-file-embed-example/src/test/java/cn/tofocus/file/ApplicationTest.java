package cn.tofocus.file;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.web.multipart.MultipartFile;

import cn.tofocus.core.Result;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.file.FileUrlUtil;
import cn.tofocus.file.api.FileControlV3;
import cn.tofocus.file.api.v3.FileApiV3Impl;
import cn.tofocus.file.bean.Constant;
import cn.tofocus.file.bean.FileByteResponse;
import cn.tofocus.file.bean.FileInfoV3;
import cn.tofocus.file.bean.FileRefInfo;
import cn.tofocus.file.bean.FileReport;
import cn.tofocus.file.bean.MemoryMultipartFile;
import cn.tofocus.file.bean.ThumbType;
import cn.tofocus.file.db.dao.FileRecordDao;
import cn.tofocus.file.db.entity.FileRecordEntity;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ApplicationTest
{
    
    @Autowired
    private FileApiV3Impl api;
    
    @Autowired
    private FileControlV3 action;

    @Autowired
    private FileRecordDao fileRecordDao;
    
    private MultipartFile img;
    
    private MultipartFile file;

    @Autowired
    private cn.tofocus.db.file.FileServer fileManageServer;
    
    @BeforeAll
    public void init()
    {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try
        {
            Resource res = resolver.getResource("img.jpg");
            img = new MemoryMultipartFile("file", "demo.jpg", null, res.getInputStream());
            res = resolver.getResource("demo.zip");
            file = new MemoryMultipartFile("file", "demo.zip", "application/zip", res.getInputStream());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    @Test
    public void testUpload()
    {
        String baseUrl = fileManageServer.getFileBaseUrl();
        //上传图片
        Result<FileInfoV3> r1 = api.uploadImage(img, "图片1", null);
        FileInfoV3 info = r1.getResult();
        assertEquals(true, r1.isSuccess());
        assertEquals("5D29B71902EA573AF58C111D1BD42796", info.getMd5());
        assertEquals(205218, info.getSize());
        assertEquals("图片1", info.getTitle());
        assertEquals("demo.jpg", info.getFileName());
        assertEquals("image/jpeg", info.getContentType());
        assertEquals(FileUrlUtil.buildUrl(baseUrl, Constant.imgUrl, info.getId(), info.getExtName(), info.getMd5()), info.getUrl());
        assertEquals(FileUrlUtil.buildUrl(baseUrl, Constant.imgDownLoadUrl, info.getId(), info.getExtName(), info.getMd5()), info.getDownloadUrl());

        //查看原图
        FileByteResponse f1 = action.viewImage(info.getUrl(), info.getMd5(), ThumbType.orgin);
        assertEquals(205218, f1.getFileContent().length);
        assertEquals("image/jpeg", f1.getContentType());
        assertEquals("demo.jpg", f1.getFileName());
        assertEquals("200", f1.getStatus());
        //查看大图
        f1 = action.viewImage(info.getUrl(), info.getMd5(), ThumbType.big);
        int size = f1.getFileContent().length;
        assertEquals(true, size < 205218);
        assertEquals("image/jpeg", f1.getContentType());
        assertEquals("demo.jpg", f1.getFileName());
        assertEquals("200", f1.getStatus());
        //查看小图
        f1 = action.viewImage(info.getUrl(), info.getMd5(), ThumbType.small);
        assertEquals(true, f1.getFileContent().length < size);
        assertEquals("image/jpeg", f1.getContentType());
        assertEquals("demo.jpg", f1.getFileName());
        assertEquals("200", f1.getStatus());
        
        //图片拒绝
        f1 = action.viewImage(info.getUrl(), "md5", null);
        assertNull(f1.getFileContent());
        assertEquals("404", f1.getStatus());
        
        //上传文件
        r1 = api.uploadFile(file, "文件1", null);
        info = r1.getResult();
        assertEquals(true, r1.isSuccess());
        assertEquals("D9C3B604CB2D2111684A38142EF2384A", info.getMd5());
        assertEquals(990, info.getSize());
        assertEquals("文件1", info.getTitle());
        assertEquals("demo.zip", info.getFileName());
        assertEquals("application/zip", info.getContentType());
        assertEquals(FileUrlUtil.buildUrl(baseUrl, Constant.fileDownLoadUrl, info.getId(), info.getExtName(), info.getMd5()), info.getUrl());
        assertEquals(FileUrlUtil.buildUrl(baseUrl, Constant.fileDownLoadUrl, info.getId(), info.getExtName(), info.getMd5()), info.getDownloadUrl());

        //下载文件
        f1 = action.downloadFile(info.getUrl(), info.getMd5());
        assertEquals(990, f1.getFileContent().length);
        assertEquals("application/x-download", f1.getContentType());
        assertEquals("demo.zip", f1.getFileName());
        assertEquals("200", f1.getStatus());
        
        //上传错误图片
        r1 = api.uploadImage(file, "图片2", null);
        assertEquals(false, r1.isSuccess());
        assertEquals("90000020", r1.getCode());
        
        //重复上传
        r1 = api.uploadImage(img, "图片1", null);
        assertEquals("200", f1.getStatus());
        
        //查询
        List<FileRecordEntity> list =  fileRecordDao.select().exec();
        assertEquals(2, list.size());
        
        Result<PageResult<FileRefInfo>> pageResult = api.queryRef(0, 100, null, null, null, null, null, null, null, null, null, null, null, null);
        assertEquals(true, pageResult.isSuccess());
        assertEquals(3, pageResult.getResult().getTotalElements());
        
        Result<FileReport> report = api.aggFileByMime(null, null);
        assertEquals(true, report.isSuccess());
        assertEquals(2, report.getResult().getList().size());
        
        report =  api.aggRefByExt(null, null);
        assertEquals(true, report.isSuccess());
        assertEquals(2, report.getResult().getList().size());
        
        report =  api.aggRefByApp(null, null);
        assertEquals(true, report.isSuccess());
        assertEquals(1, report.getResult().getList().size());
    }
    
}
