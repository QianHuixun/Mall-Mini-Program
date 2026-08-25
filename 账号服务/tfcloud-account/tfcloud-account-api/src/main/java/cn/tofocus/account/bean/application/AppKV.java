package cn.tofocus.account.bean.application;

import cn.tofocus.core.data.StrKeyName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class AppKV extends StrKeyName
{
    /**
     * 注释内容
     */
    private static final long serialVersionUID = 1L;

    private String domainid;
    
    private Boolean needCaptcha;

    public AppKV(String pkey, String name, String domainid, Boolean needCaptcha)
    {
        super(pkey, name);
        this.domainid = domainid;
        this.needCaptcha = needCaptcha;
    }
}
