package cn.tofocus.lejia.bean.dto.express.notify;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SfNotifyResult
{
    // OK-成功，ERR-失败；失败会重推两次，共推送三次，全部返回ERR则不再推送。
    @Schema(description = "接收推送信息状态")
    private String status;
    
    public static SfNotifyResult ok()
    {
        SfNotifyResult result = new SfNotifyResult();
        result.setStatus("OK");
        return result;
    }
    
    public static SfNotifyResult error()
    {
        SfNotifyResult result = new SfNotifyResult();
        result.setStatus("ERR");
        return result;
    }
}
