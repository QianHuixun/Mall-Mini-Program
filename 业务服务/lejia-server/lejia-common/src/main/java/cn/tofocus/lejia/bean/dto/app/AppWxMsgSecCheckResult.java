package cn.tofocus.lejia.bean.dto.app;

import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
public class AppWxMsgSecCheckResult
{
    @Schema(description = "错误码")
    private Integer errcode;
    
    @Schema(description = "错误信息")
    private String errmsg;
    
    @Schema(description = "唯一请求标识，标记单次请求")
    @JSONField(name = "trace_id")
    private String traceId;
    
    @Schema(description = "综合结果")
    private CheckResult result;
    
    @Schema(description = "详细检测结果")
    private List<CheckDetail> detail;
    
    public boolean isSuccess()
    {
        return errcode == 0;
    }
    
    public boolean isPass()
    {
        if (result == null)
            return false;
        return result.isPass();
    }
    
    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class CheckDetail extends CheckResult
    {
        @Schema(description = "策略类型")
        private String strategy;
        
        @Schema(description = "错误码，仅当该值为0时，该项结果有效")
        private Integer errcode;
        
        @Schema(description = "命中的自定义关键词")
        private String keyword;
        
        @Schema(description = "0-100，代表置信度，越高代表越有可能属于当前返回的标签（label）")
        private Integer prob;
        
        public boolean isSuccess()
        {
            return errcode == 0;
        }
    }
    
    @Data
    public static class CheckResult
    {
        /**
         * 有risky、pass、review三种值
         */
        @Schema(description = "建议")
        private String suggest;
        
        /**
         * 100 正常；10001 广告；20001 时政；20002 色情；20003 辱骂；20006 违法犯罪；20008 欺诈；20012 低俗；20013 版权；21000 其他
         */
        @Schema(description = "命中标签枚举值")
        private Integer label;
        
        public boolean isPass()
        {
            return "pass".equals(getSuggest());
        }
    }
}
