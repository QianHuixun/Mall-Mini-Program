package cn.tofocus.lejia.bean.entity.goods;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.tofocus.common.cachemap.bean.HasPkey;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
*  mkt_space_kc
* @author zdw 2022-01-27
*/

@Entity
@Data
@Table(name = "mkt_space_kc")
public class MktSpaceKc implements HasPkey<Integer>
{
    @Id
    @Schema(description = "pkey", required = true)
    private Integer pkey;
    
    @Schema(description = "kc_num", required = true)
    private Integer kcNum;
    
    @Schema(description = "归属主键")
    private Integer ascription;
}