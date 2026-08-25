package cn.tofocus.lejia.bean.enums.v5;

import cn.tofocus.core.enums.IBaseDbEnum;
import lombok.Getter;

@Getter
public enum MerchantStatus implements IBaseDbEnum
{
    STOP(0, "停用"), NORMAL(1, "正常"), STALL(2, "退摊");
    
    private final int index;
    
    private final String name;
    
    private MerchantStatus(int index, String name)
    {
        this.name = name;
        this.index = index;
    }
}