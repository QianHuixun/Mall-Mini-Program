package cn.tofocus.core.enums;

import lombok.Getter;

/**
 * 
 * 收单通道
 * <p/>
 * 
 * @author  wyw
 * @version  [版本号, 2018年9月11日]
 */
@Getter
public enum ReceiptLoadEnum implements IBaseDbEnum
{
    // @formatter:off
    POS(1, "POS"),
    LINE(2, "线上"),
    QRCODE(3, "二维码"),
    OTHER(4, "其他");
    // @formatter:on

    private final int index;
    
    private final String name;
 
    private ReceiptLoadEnum(int index,String name)
    {
        this.name = name;
        this.index = index;
    }
}
