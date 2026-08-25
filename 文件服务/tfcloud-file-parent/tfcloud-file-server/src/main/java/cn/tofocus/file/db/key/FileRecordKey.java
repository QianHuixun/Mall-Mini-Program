package cn.tofocus.file.db.key;

import java.io.Serializable;

import lombok.Data;

@Data
public class FileRecordKey implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String md5;  //文件内容的md5
    
    private long size;  //文件大小

    public FileRecordKey()
    {
        super();
    }

    public FileRecordKey(String md5, long size)
    {
        super();
        this.md5 = md5;
        this.size = size;
    }
}
