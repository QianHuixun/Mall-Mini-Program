package cn.tofocus.lejia.bean.enums;



import cn.tofocus.core.enums.IBaseDbEnum;

public enum LocationType implements IBaseDbEnum 
{
    LEFT(0, "左"), 
    UPPERRIGHT(1, "右上"), 
    CEZONTER(2, "中"), 
    LOWERRIGHT(3, "右下"),     
    ;


    private final int index;

    private final String name;

    private LocationType(int index, String name) {
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

   
}
