package cn.tofocus.lejia.bean.dto.market.jd;

import cn.tofocus.db.dto.JoinProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class JdOrderFullOnPage extends JdOrderOnPage
{
    @Schema(description = "京东订单号")
    @JoinProperty(dataQuery = "jdOrderCorrelationDao", from = "code", referencedName = "orderCode", propertyName = "jdCode")
    private Long jdOrderId;
}
