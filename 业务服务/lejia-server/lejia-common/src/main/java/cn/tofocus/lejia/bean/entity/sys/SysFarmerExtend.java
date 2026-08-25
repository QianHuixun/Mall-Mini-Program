package cn.tofocus.lejia.bean.entity.sys;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.tofocus.common.cachemap.bean.HasPkey;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

/**
 * 
 * 市场拓展表
 */
@Entity
@Data
@Table(name = "sys_farmer_extend")
@FieldNameConstants(innerTypeName = "F")
public class SysFarmerExtend implements HasPkey<String>
{
    @Id
    @Schema(description = "pkey")
    private String pkey;
    
    @Schema(description = "打印机编码")
    private String printCode;
    
    @Schema(description = "小票内容")
    private String content;
    
    @Schema(description = "图1")
    private String photo1;
    
    @Schema(description = "图1文字")
    @Column(name = "photo1_text")
    private String photo1Text;
    
    @Schema(description = "图2")
    private String photo2;
    
    @Schema(description = "图2文字")
    @Column(name = "photo2_text")
    private String photo2Text;
    
    
}
