package cn.tofocus.lejia.bean.dto.market;

import java.util.Date;
import java.util.List;

import cn.tofocus.db.dto.JoinProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MktOrderGoodsCommentInfo
{
    @Schema(description = "主键")
    private Integer pkey;

    @Schema(description = "订单主键")
    private Integer orderPkey;

    @Schema(description = "评价用户")
    private Integer member;
    
    @Schema(description = "用户手机号")
    @JoinProperty(dataQuery = "mktMemberDao", from = "member", propertyName = "mobile")
    private String memberMobile;
    
    @Schema(description = "订单号")
    @JoinProperty(dataQuery = "mktOrderDao", from = "orderPkey", propertyName = "code")
    private String orderCode;
    
    @Schema(description = "商品名称")
    private String goodsName;
    
    @Schema(description = "评分")
    private Integer score;
    
    @Schema(description = "内容")
    private String content;
    
    @Schema(description = "图片")
    private List<String> photo;
    
    @Schema(description = "回复内容")
    private String replyContent;
    
    @Schema(description = "回复时间")
    private Date replyTime;
    
    @Schema(description = "建档时间")
    private Date createdTime;
}
