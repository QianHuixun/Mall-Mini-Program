package cn.tofocus.file.bean;

import java.io.InputStream;

import lombok.Data;

@Data
@Deprecated
public class FileResponse
{
    private InputStream inputStream;

    private String fileName;  //原文件名
    
    private String contentType;  //内容MIME类型
}
