package cn.tofocus.lejia.bean.entity.market;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
*  app访问记录
* @author zdw 2020-08-17
*/

@Entity
@Data
@Table(name="mkt_access_log")
public class MktAccessLog implements HasPkey<Integer> {
   


    /**
     * pkey
     */
    @Id
    @AutoRedisID(domain = "zyysc", sequence="mkt_access_log")
	@Schema(description = "pkey", required = true)
    private Integer pkey;

    @Schema(description = "用户主键", required = false)
    private Integer member;
    
	@Schema(description = "openid", required = false)
    private String openid;

	@Schema(description = "建档时间", required = true)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd" , timezone = "GMT+8")
    private Date accessTime;
	
	/**
     * 市场
     */
    @Schema(description = "市场")
    private String farmer;

    /**
     * 公司
     */
    @Schema(description = "公司")
    private String company;

    @Schema(description = "归属主键")
    private Integer ascription;

}