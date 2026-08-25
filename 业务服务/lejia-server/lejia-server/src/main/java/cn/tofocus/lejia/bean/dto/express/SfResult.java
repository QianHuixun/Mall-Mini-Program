package cn.tofocus.lejia.bean.dto.express;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SfResult<T>
{
    // 返回成功或失败信息(ok/fail)
    @Schema(description = "状态码")
    private String succ;
    
    // 提示信息
    @Schema(description = "信息")
    private String msg;
    
    @Schema(description = "数据")
    private T result;
    
    @Schema(description = "接口调用是否成功")
    public boolean isSuccess()
    {
        return "ok".equals(this.succ);
    }
}
