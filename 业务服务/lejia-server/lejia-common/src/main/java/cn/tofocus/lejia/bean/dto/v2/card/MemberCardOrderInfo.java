package cn.tofocus.lejia.bean.dto.v2.card;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MemberCardOrderInfo
{
    @Schema(description = "可用优惠券")
    private List<MemberCardV2OnList> available;
    
    @Schema(description = "不可用优惠券")
    private List<MemberCardV2OnList> notAvailable;
    
    @Schema(description = "可用优惠券数量")
    public Integer getCardNum()
    {
        if (available == null)
            return 0;
        return available.size();
    }
    
    @Schema(description = "不可用优惠券数量")
    public Integer getCardNotNum()
    {
        if (notAvailable == null)
            return 0;
        return notAvailable.size();
    }
}
