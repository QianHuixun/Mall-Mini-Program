package cn.tofocus.lejia.bean.entity.jd;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.lejia.bean.enums.jd.OrderCorrelationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Entity
@Data
@Table(name = "jd_order_correlation")
@FieldNameConstants(innerTypeName = "F")
public class JdOrderCorrelation implements HasPkey<Integer>
{
    @Id
    @Schema(description = "mkt_order的主键")
    private Integer pkey;
    
    @Column(columnDefinition = "tinyint")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    @Schema(description = "订单状态,是否作废")
    private OrderCorrelationStatus status;
    
    @Schema(description = "mkt_order的编号")
    private String orderCode;
    
    @Schema(description = "京东的订单编号")
    private Long jdCode;
    
    @Schema(description = "京东拆掉父类主键(该值为空代表没有拆单)")
    private Long parentOrder;
    
    @Schema(description = "建档时间")
    @CreatedDate
    private Date createdTime;
}
