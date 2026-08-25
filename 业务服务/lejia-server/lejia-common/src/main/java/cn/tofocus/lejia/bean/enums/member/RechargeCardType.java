package cn.tofocus.lejia.bean.enums.member;

import cn.tofocus.core.enums.IBaseDbEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * 充值卡密类型
 * @author czy
 * @version [版本号, 2026/5/27]
 */
public enum RechargeCardType implements IBaseDbEnum
{
    // @formatter:off
    NORMAL(1, "普通充值"),
    MSD(2, "热力豆充值"),
    ;
    // @formatter:on
    
    private final int index;
    
    private final String name;
    
    RechargeCardType(int index, String name)
    {
        this.name = name;
        this.index = index;
    }
    
    @Override
    public String getName()
    {
        return name;
    }
    
    @Override
    public int getIndex()
    {
        return index;
    }
    
    public static RechargeCardType fromIndex(Integer index)
    {
        return IBaseDbEnum.fromIndex(RechargeCardType.class, index);
    }

    public static Map<String, RechargeCardType> nameMap()
    {
        Map<String, RechargeCardType> map = new HashMap<String, RechargeCardType>();
        for (RechargeCardType item : RechargeCardType.values())
        {
            map.put(item.getName(), item);
        }
        return map;
    }
}
