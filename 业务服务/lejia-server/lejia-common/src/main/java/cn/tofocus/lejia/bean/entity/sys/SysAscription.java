package cn.tofocus.lejia.bean.entity.sys;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.file.FileUrl;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
*  归属表
* @author zdw 2022-06-16
*/

@Entity
@Data
@Table(name = "sys_ascription")
public class SysAscription implements HasPkey<Integer>
{
    @Id
    @Schema(description = "pkey")
    private Integer pkey;
    
    @Schema(description = "名称")
    private String name;
    
    @Schema(description = "运营者账户")
    private String account;
    
    @Schema(description = "登陆手机")
    private String mobile;
    
    @Schema(description = "图标")
    @FileUrl
    private String photo;
    
    @Schema(description = "微信消息模板id")
    private String templateid;
    
    @Schema(description = "支付appid")
    private String configAppid;
    
    @Schema(description = "支付mchid")
    private String configMchid;
    
    @Schema(description = "支付回调路径")
    private String configReurl;
    
    @Schema(description = "对应支付body-商品的描述")
    private String configAbname;
    
    @Schema(description = "对应支付attach-附加数据")
    private String configFullname;
    
    @Schema(description = "密码")
    private String configPassword;
    
    @Schema(description = "密钥")
    private String configKey;
    
    @Schema(description = "路径")
    private String configLocalpath;
    
    @Schema(description = "证书序列号")
    private String certificateSerialNo;
    
    @Schema(description = "运营端清分时间")
    private Integer zxQfSys;

    @Schema(description = "市场清分时间")
    private Integer zxQf;
    
    @Schema(description = "市场数量")
    private Integer marketNum;
    
    @Schema(description = "建档时间")
    @CreatedDate
    private Date createdTime;
}
