package cn.tofocus.lejia.zx.beanV2;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

@Data
@XStreamAlias("ROWS")
public class T21000037ResponseDataList {
    private String PAN;//卡号
    private String DEFAULT_FLAG;//默认卡标识
    private String STT;//卡状态
}
