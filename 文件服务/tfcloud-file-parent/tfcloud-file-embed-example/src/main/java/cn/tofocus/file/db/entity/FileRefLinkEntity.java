package cn.tofocus.file.db.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.CreatedDate;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.file.RefChange;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

@Data
@Entity
// @formatter:off
@Table(name = "file_ref_link")
//@formatter:on
@FieldNameConstants(innerTypeName = "F")
@NoArgsConstructor
@Schema(description = "文件引用关联表")
public class FileRefLinkEntity implements HasPkey<Long>
{
    @Id
    @Column(nullable = false)
    private Long pkey; //自增主键
    
    @Schema(description = "域")
    @NotNull
    @Size(max = 40)
    @Column(nullable = false, length = 40)
    private String domain;
    
    @Schema(description = "数据库")
    @NotNull
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String db;
    
    @Schema(description = "表")
    @NotNull
    @Size(max = 100)
    @Column(name = "table_name", nullable = false, length = 100)
    private String table;
    
    @Schema(description = "主键或hash")
    @NotNull
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String dataPkey;
    
    @Schema(description = "文件Pkey")
    @NotNull
    @Column(nullable = false)
    private long filePkey;
    
    @NotNull
    @Schema(description = "文件大小")
    @Column(nullable = false)
    private long size; //文件大小
    
    @Schema(description = "机构/公司")
    @Size(max = 40)
    @Column(length = 40)
    private String org;
    
    @Schema(description = "部门/市场")
    @Size(max = 40)
    @Column(length = 40)
    private String dept;
    
    @CreatedDate
    @Column(updatable = false)
    @Schema(description = "创建时间")
    private Date createdTime;
    
    public FileRefLinkEntity(RefChange r)
    {
        this.dataPkey = r.getPkey();
        this.org = r.getOrg();
        this.dept = r.getDept();
    }
}
