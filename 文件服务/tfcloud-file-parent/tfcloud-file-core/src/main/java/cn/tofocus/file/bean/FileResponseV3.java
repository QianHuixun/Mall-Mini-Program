package cn.tofocus.file.bean;

import java.io.File;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileResponseV3
{
    private File file;

    private String fileName;  //原文件名
    
    private String contentType;  //内容MIME类型
}
