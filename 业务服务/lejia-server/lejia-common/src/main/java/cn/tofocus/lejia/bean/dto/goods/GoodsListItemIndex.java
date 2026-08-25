package cn.tofocus.lejia.bean.dto.goods;

import cn.tofocus.common.cachemap.bean.HasPkey;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GoodsListItemIndex implements HasPkey<String>
{
    @Schema(description = "分类")
    private int gtype;
    
    @Schema(description = "商品库")
    private int goodsMain;
    
    @Schema(description = "起始位置")
    private int start;
    
    @Schema(description = "总数量")
    private int size;
    
    public GoodsListItemIndex(int gtype, int goodsMain, int start)
    {
        super();
        this.gtype = gtype;
        this.goodsMain = goodsMain;
        this.start = start;
    }
    
    @Override
    public String getPkey()
    {
        return gtype + ":" + goodsMain;
    }
    
    @Override
    public void setPkey(String pkey)
    {
        String[] s = pkey.split(":");
        gtype = Integer.valueOf(s[0]);
        goodsMain = Integer.valueOf(s[1]);
    }
    
}
