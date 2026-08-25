package cn.tofocus.file.db.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.tofocus.common.cachemap.bean.HasPkey;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Entity
@Table(name = "file_ref")
@Schema(description = "文件引用（弃用）")
public class FileRefEntity implements HasPkey<Long>
{
    @Id
    private Long pkey;  //自增主键

    @Schema(description = "文件内容的md5")
    private String md5;  //文件内容的md5

    @Schema(description = "文件大小")
    private long size;  //文件大小

    @Schema(description = "原文件名")
    private String fileName;  //原文件名

    @Schema(description = "扩展名")
    private String extName;   //扩展名

    @Schema(description = "标题")
    private String title;     //标题

    @Schema(description = "来源应用")
    private String appid;     //来源应用

    @Schema(description = "来源用户")
    private Long userkey;     //来源用户

    @Schema(description = "来源引用地址")
    private String refUrl;    //来源引用地址

    @Schema(description = "引用数量")
    private Integer refCount;     //引用数量

    @Schema(description = "创建时间")
    private Date createdTime;
}
