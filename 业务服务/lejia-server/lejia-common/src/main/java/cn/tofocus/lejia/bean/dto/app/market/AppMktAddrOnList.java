package cn.tofocus.lejia.bean.dto.app.market;

import java.math.BigDecimal;

import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import cn.tofocus.db.AutoRedisID;
import cn.tofocus.lejia.bean.enums.AddrType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AppMktAddrOnList
{
    /**
     * pkey
     */
    @Id
    @AutoRedisID(domain = "zyysc", sequence = "mkt_addr")
    @Schema(description = "pkey", hidden = true)
    private Integer pkey;
    
    @Schema(description = "类型")
    private AddrType type;

    @Size(max = 40)
    @Schema(description = "省")
    private String pro;

    @Size(max = 40)
    @Schema(description = "市")
    private String city;

    @Size(max = 40)
    @Schema(description = "区")
    private String area;

    @Size(max = 40)
    @Schema(description = "街道")
    private String town;
    
    /**
    * 地址
    */
    @Schema(description = "地址")
    private String addr;
    
    /**
    * 详细地址
    */
    @Deprecated
    @Schema(description = "门牌号（弃用）")
    private String addrDetail;
    
    private String addrCode;
    
    /**
    * 收货人
    */
    @NotBlank(message = "请输入收货人")
    @Schema(description = "收货人")
    private String name;
    
    /**
    * 收货人手机
    */
    @NotBlank(message = "请输入手机号码")
    @Schema(description = "收货人手机")
    private String mobile;
    
    private String hideMobile;
    
    /**
    * 默认地址
    */
    @Schema(description = "默认地址")
    private Boolean defaultAddr;
    
    /**
    * 经度
    */
    @Schema(description = "经度")
    private BigDecimal longitude;
    
    /**
    * 纬度
    */
    @Schema(description = "纬度")
    private BigDecimal latitude;
    
}
