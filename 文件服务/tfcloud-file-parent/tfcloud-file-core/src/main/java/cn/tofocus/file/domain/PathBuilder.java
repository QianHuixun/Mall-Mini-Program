package cn.tofocus.file.domain;

import java.io.File;

public class PathBuilder
{
    private String root;
    
    private String md5;
    
    private long size;
    
    private Integer thumb;
    
    public PathBuilder root(String root)
    {
        this.root = root;
        return this;
    }
    
    public PathBuilder md5(String md5)
    {
        this.md5 = md5;
        return this;
    }
    
    public PathBuilder size(long filesize)
    {
        this.size = filesize;
        return this;
    }
    
    public PathBuilder thumb(int thumb)
    {
        this.thumb = thumb;
        return this;
    }
    
    public String build()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(root);
        sb.append(File.separator);
        sb.append(md5.substring(0, 2));
        sb.append(File.separator);
        sb.append(md5.substring(2, 4));
        sb.append(File.separator);
        sb.append(md5);
        sb.append("_");
        sb.append(size);
        if (thumb != null)
        {
            sb.append("_");
            sb.append(thumb);
        }
        return sb.toString();
    }

    public String buildPath()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(root);
        sb.append(File.separator);
        sb.append(md5.substring(0, 2));
        sb.append(File.separator);
        sb.append(md5.substring(2, 4));
        return sb.toString();
    }

    public String getRoot()
    {
        return root;
    }

    public String getMd5()
    {
        return md5;
    }

    public long getSize()
    {
        return size;
    }

    public Integer getThumb()
    {
        return thumb;
    }
}
