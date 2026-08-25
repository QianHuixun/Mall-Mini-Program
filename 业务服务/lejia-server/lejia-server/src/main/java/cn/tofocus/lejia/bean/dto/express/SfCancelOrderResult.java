package cn.tofocus.lejia.bean.dto.express;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SfCancelOrderResult
{
    @Schema(description = "客户订单号")
    private String orderid;

    // 1-客户订单号与顺丰运单不匹配；2-操作成功；
    private Integer code;

    public boolean isSuccess()
    {
        return this.code == 2;
    }
}
