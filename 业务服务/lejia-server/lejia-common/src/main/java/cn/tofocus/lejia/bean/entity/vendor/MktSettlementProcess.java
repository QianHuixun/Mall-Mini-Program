package cn.tofocus.lejia.bean.entity.vendor;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import cn.tofocus.lejia.bean.enums.ProcessNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
*  结算流程
* @author zdw 2021-12-07
*/

@Entity
@Data
@Table(name = "mkt_settlement_process")
public class MktSettlementProcess implements HasPkey<Long>
{
    
    @Id
    @Column
    @AutoRedisID(domain = "zyysc", sequence = "mkt_settlement_process")
    @Schema(description = "pkey", required = true)
    private Long pkey;
    
    @Schema(description = "结算明细主键", required = true)
    private Long settlementKey;
    
    @Schema(description = "流程节点", required = false)
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    private ProcessNode processNode;
    
    @Schema(description = "content")
    @Column(length = 500)
    private String content;
    
    @Schema(description = "rem", required = false)
    @Column(length = 200)
    private String rem;
    
    @Schema(description = "建档时间", required = true)
    @CreatedDate
    private Date createdTime;
    
    @Schema(description = "建档员", required = true)
    @CreatedBy
    private Integer createdBy;
    
    @Schema(description = "归属主键")
    private Integer ascription;
}