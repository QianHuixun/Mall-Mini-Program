package cn.tofocus.lejia.domain.jdvop.bean.msg;

import lombok.Data;

/**
 * 商品信息变更
 * {"skuId" : 商品编号 }
 */
@Data
public class JdVOPSkuChangeMsg
{
    private Long skuId;
}
