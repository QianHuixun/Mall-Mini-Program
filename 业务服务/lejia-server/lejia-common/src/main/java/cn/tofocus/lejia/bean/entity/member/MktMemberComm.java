package cn.tofocus.lejia.bean.entity.member;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import lombok.Data;

/**
*  mkt_member_comm
* @author lai 2020-06-15
*/

@Entity
@Data
@Table(name="mkt_member_comm")
public class MktMemberComm implements HasPkey<Integer> {
   

    @Id
    @AutoRedisID(domain = "zyysc", sequence="mkt_member_comm")
    /**
    * pkey
    */
	@Schema(description = "pkey")
    private Integer pkey;

    /**
    * 佣金值
    */
	@Schema(description = "佣金值")
    private BigDecimal comms;

    /**
    * 锁定佣金
    */
	@Schema(description = "锁定佣金")
    private BigDecimal lockComms;

    /**
    * 最后更新时间
    */
	@Schema(description = "最后更新时间")
    private Date updateTime;

    @Schema(description = "归属主键")
    private Integer ascription;

}