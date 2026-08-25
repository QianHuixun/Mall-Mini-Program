package cn.tofocus.account.bean.application;

import java.util.Set;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.core.enums.AppGrantType;
import cn.tofocus.core.enums.AppTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode
public class ApplicationInfo implements HasPkey<String>
{
    @NotBlank
    @Size(max = 40)
    private String pkey;
    
    //应用名称
    @NotBlank
    @Size(max = 100)
    private String name;

    //应用类型
    @NotNull
    private AppTypeEnum apptype;

    //授权类型
    @NotNull
    private AppGrantType grantType;
    
    //应用安全码
    @NotBlank
    @Size(max = 40)
    private String secret;

    private Set<String> uri;

    @NotBlank
    @Size(max = 40)
    private String domainid;
    
    private Boolean needCaptcha;
    
}
