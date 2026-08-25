package cn.tofocus.lejia.bean.entity.sys;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.db.AutoRedisID;
import cn.tofocus.lejia.exception.WsaleErrCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
*  sys_user_role
* @author lai 2020-06-15
*/

@Entity
@Data
@Table(name="sys_user")
public class SysUser implements HasPkey<Integer> {
   

	/**
	 * pkey
	 */
    @Id
    @AutoRedisID(domain = "zyysc", sequence="sys_user_role")
	@Schema(description = "pkey")
    private Integer pkey;

    @Schema(description = "手机号码")
    private String mobile;
    
    @Schema(description = "昵称")
    private String nickname;
    
    @Schema(description = "角色")
    private String roleKey;
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

    /**
    * 版本
    */
	@Schema(description = "版本")
	@Column(nullable = false, columnDefinition = "smallint(6)")
    private Integer rowVension;

	public void checkBelongCompany(String companyPkey)
    {
        if(company!= null && companyPkey != null && !company.equals(companyPkey))
        {
            throw TofocusException.of(WsaleErrCode.USER_NOT_BELONG_COMPANY);
        }
    }

    public void checkBelongMarket(String marketPkey)
    {
        if(farmer != null && marketPkey != null && !farmer.equals(marketPkey))
        {
            throw TofocusException.of(WsaleErrCode.USER_NOT_BELONG_MARKET);
        }
    }

    @Schema(description = "归属主键")
    private Integer ascription;
}