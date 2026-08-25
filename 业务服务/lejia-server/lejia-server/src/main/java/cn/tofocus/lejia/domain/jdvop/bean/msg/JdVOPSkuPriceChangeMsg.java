package cn.tofocus.lejia.domain.jdvop.bean.msg;

import lombok.Data;

/**
 * 商品价格变更
 * {"skuId" : 商品编号 }
 */
@Data
public class JdVOPSkuPriceChangeMsg
{
    private Long skuId;
}
