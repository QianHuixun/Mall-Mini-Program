package cn.tofocus.core.enums;

import lombok.Getter;

@Getter
public enum ModelStatus implements IBaseDbEnum
{
    // @formatter:off
    OnLine(0, "上线"),
    OffLine(1, "下线"),
    Disabled(2, "停用");
    // @formatter:on

    private final int index;
    
    private final String name;
 
    private ModelStatus(int index,String name)
    {
        this.name = name;
        this.index = index;
    }
    
}
