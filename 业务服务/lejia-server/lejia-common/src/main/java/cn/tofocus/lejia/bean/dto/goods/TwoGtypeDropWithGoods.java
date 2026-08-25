package cn.tofocus.lejia.bean.dto.goods;

import java.util.List;

import cn.tofocus.core.data.KeyName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TwoGtypeDropWithGoods extends KeyName<Integer>
{
    @Schema(description = "二级分类")
    private List<SecondGtype> children;
    
    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class SecondGtype extends KeyName<Integer>
    {
        @Schema(description = "商品")
        private List<KeyName<Integer>> children;
    }
}
