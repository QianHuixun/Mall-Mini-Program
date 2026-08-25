package cn.tofocus.lejia.domain.jdvop.bean.msg;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.Data;

/**
 * 商品池添加、删除消息
 * {"poolType": "recommend", "page_num":"商品池编号", "state":"1添加，2删除"}
 */
@Data
public class JdVOPSkuPoolAddDelMsg
{
    // p_skupool 用户的私有商品池； cate_pool 分类商品池 recommend 主推商品池；hot_sale 热销商品池； p_custom_skupool 用户的私有定制商品池；
    private String poolType;
    
    // 商品池编号
    @JSONField(name = "page_num")
    private String pageNum;
    
    // 1添加，2删除
    private Integer state;
}
