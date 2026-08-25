package cn.tofocus.lejia.zx.beanV2;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import lombok.Data;

@Data
@XStreamAlias("ROOT")
public class T22000006Response
{
    private String CODE;//外联平台应答码
    
    private String MESSAGE;//外联平台应答码描述
    
    private T22000006ResponseData DATA;//业务响应数据
}
