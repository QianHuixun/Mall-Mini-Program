package cn.tofocus.file.bean;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
@Deprecated
public class FileInfo
{
    private long id;
    
    private String url;
    
    private List<Integer> thumbs;
    
    private String md5;  //文件内容的md5
    
    private long size;  //文件大小

    private String contentType;  //内容MIME类型

    private String fileName;  //原文件名

    private String extName;   //扩展名
    
    private String title;     //标题
    
    private String appid;     //来源应用
    
    private Long userkey;    //来源用户
    
    private String refUrl;    //来源引用地址

    private long accessCount;   //访问次数
    
    private Date createdTime;
    
    private Date lastAccess;   //最近访问时间
    
    public FileInfo()
    {
    }
    
    public FileInfo(Long id, String baseUrl, String url, String ext, String md5)
    {
        this.id = id;
        StringBuilder sb = new StringBuilder();
        sb.append(baseUrl).append(url).append("?file=").append(id);
        if(ext != null && ext.length() > 0)
        {
            sb.append(".").append(ext);
        }
        sb.append("&code=").append(md5);
        this.url = sb.toString();
    }

}
