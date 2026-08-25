package cn.tofocus.lejia.zx.bean;

import java.math.BigDecimal;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class T21000029ResponseROW
{
    private List<ROW> ROW;
    
    @Data
    public static class ROW
    {
        private String USER_NAME;
        
        private String TRANS_DT;
        
        private String TRANS_TM;
        
        private String TRANS_TYPE;
        
        private String REQ_JRN;
        
        private String MCHNT_ORDER_ID;
        
        private String MCHNT_ORDER_SUB_ID;
        
        private String REGISTER_SSN;
        
        private BigDecimal TRANS_AMT;
        
        @Schema(description = "资金方向 C- 账户入金  D- 账户出金")
        private String C_D_FLAG;
        
        private BigDecimal CUR_AMT;
        
        private String GOAC;
        
        private String OANM;
        
        private String DIGEST;
    }
}
