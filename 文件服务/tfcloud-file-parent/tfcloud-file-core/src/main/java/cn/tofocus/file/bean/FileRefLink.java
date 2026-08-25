package cn.tofocus.file.bean;

import java.util.Date;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.file.RefChange;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "文件引用关联表")
public class FileRefLink implements HasPkey<Long>
{
    private Long pkey; //自增主键
    
    @Schema(description = "域")
    private String domain;
    
    @Schema(description = "数据库")
    private String db;
    
    @Schema(description = "表")
    private String table;
    
    @Schema(description = "主键或hash")
    private String dataPkey;
    
    @Schema(description = "文件Pkey")
    private long filePkey;
    
    @Schema(description = "文件大小")
    private long size; //文件大小
    
    @Schema(description = "机构/公司")
    private String org;
    
    @Schema(description = "部门/市场")
    private String dept;
    
    @Schema(description = "创建时间")
    private Date createdTime;

    public FileRefLink(RefChange r)
    {
        this.dataPkey = r.getPkey();
        this.org = r.getOrg();
        this.dept = r.getDept();
    }
}
