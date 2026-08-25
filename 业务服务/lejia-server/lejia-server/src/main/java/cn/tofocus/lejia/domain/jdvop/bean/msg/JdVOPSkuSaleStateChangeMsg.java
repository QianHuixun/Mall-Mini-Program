package cn.tofocus.lejia.domain.jdvop.bean.msg;

import lombok.Data;

/**
 * 商品可售状态
 * {"skuId": 商品编号}
 */
@Data
public class JdVOPSkuSaleStateChangeMsg
{
    private Long skuId;
}
