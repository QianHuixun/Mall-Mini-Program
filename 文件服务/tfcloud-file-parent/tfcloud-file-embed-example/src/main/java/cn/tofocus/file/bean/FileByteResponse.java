package cn.tofocus.file.bean;

import lombok.Data;

@Data
public class FileByteResponse
{
    private byte[] fileContent;

    private String fileName;  //原文件名
    
    private String contentType;  //内容MIME类型
    
    private String status;
}
