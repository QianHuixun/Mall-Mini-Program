package cn.tofocus.lejia.bean.entity.sys;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import cn.tofocus.lejia.bean.enums.v5.FarmerType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Entity
@Data
@Table(name = "sys_farmer")
@FieldNameConstants(innerTypeName = "F")
public class SysFarmer implements HasPkey<String>
{
    
    /**
     * pkey
     */
    @Id
    @AutoRedisID(domain = "zyysc", sequence = "sys_farmer", strPrefix = "zy_mkt_", strMinimumIntegerDigits = 4)
    @Schema(description = "pkey")
    private String pkey;
    
    /**
     * 菜场名称
     */
    @Schema(description = "菜场名称")
    private String name;
    
    @Schema(description = "市场类别")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    private FarmerType type;
    
    /**
     * 菜场编码
     */
    @Schema(description = "菜场编码")
    @Column(name = "kc_code")
    private String code;
    
    /**
     * 管理员主键
     */
    @Schema(description = "管理员主键")
    private Integer managerUser;
    
    /**
     * 管理员
     */
    @Schema(description = "管理员")
    private String manager;
    
    /**
     * 负责人手机
     */
    @Schema(description = "负责人手机")
    private String mobile;
    
    /**
     * 市场logo
     */
    @Schema(description = "市场logo")
    private String logo;
    
    /**
     * 介绍
     */
    @Schema(description = "介绍")
    private String content;
    
    /**
     * 售后电话
     */
    @Schema(description = "售后电话")
    private String tel;
    
    /**
     * 市场照片
     */
    @Schema(description = "市场照片")
    private String photo1;
    
    /**
     * 市场照片
     */
    @Schema(description = "市场照片")
    private String photo2;
    
    /**
     * 市场照片
     */
    @Schema(description = "市场照片")
    private String photo3;
    
    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;
    
    /**
     * 启用标志
     */
    @Schema(description = "启用标志")
    private Boolean enabled;
    
    /**
     * 是否已删除
     */
    @Schema(description = "是否已删除")
    private Boolean idDel;
    
    /**
     * 部门
     */
    @Schema(description = "部门")
    private String dept;
    
    /**
     * 机构，记录的是sys_company的pkey
     */
    @Schema(description = "机构")
    private String org;
    
    /**
     * 建档员
     */
    @Schema(description = "建档员")
    @CreatedBy
    private Integer createdBy;
    
    /**
     * 建档时间
     */
    @Schema(description = "建档时间")
    @CreatedDate
    private Date createdTime;
    
    /**
     * 版本
     */
    @Schema(description = "版本")
    @Column(nullable = false, columnDefinition = "smallint(6)")
    private Integer rowVension;
    
    @Schema(description = "市场相关配置信息")
    @OneToOne(targetEntity = SysFarmerConfig.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "pkey")
    private SysFarmerConfig config;
    
    @Schema(description = "市场相关配置信息")
    @OneToMany(targetEntity = SysFarmerMtype.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "farmer", updatable = false)
    private List<SysFarmerMtype> types;
    
    @Schema(description = "归属主键")
    private Integer ascription;
}