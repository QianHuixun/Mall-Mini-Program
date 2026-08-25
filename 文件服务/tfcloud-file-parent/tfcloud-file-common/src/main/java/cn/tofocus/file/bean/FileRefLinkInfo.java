package cn.tofocus.file.bean;

import java.util.Date;

import cn.tofocus.db.dto.DeptName;
import cn.tofocus.db.dto.JoinDTO;
import cn.tofocus.db.dto.OrgName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class FileRefLinkInfo
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
    
    @Schema(description = "文件Id")
    private long filePkey;

    @JoinDTO(dataQuery = "fileRef2Dao", from = "filePkey")
    @Schema(description = "文件引用")
    private FileRefInfo ref;

    @Schema(description = "文件大小")
    private long size; //文件大小
    
    @Schema(description = "机构/公司")
    private String org;
    
    @OrgName(from = "org")
    @Schema(description = "机构/公司")
    private String orgName;
    
    @Schema(description = "部门/市场")
    private String dept;
    
    @DeptName(from = "dept")
    @Schema(description = "部门/市场")
    private String deptName;
    
    @Schema(description = "创建时间")
    private Date createdTime;

    @Schema(description = "查看地址")
    private String url;
}
