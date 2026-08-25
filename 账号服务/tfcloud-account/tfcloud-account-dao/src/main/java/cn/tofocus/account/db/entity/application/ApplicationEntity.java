package cn.tofocus.account.db.entity.application;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.core.enums.AppGrantType;
import cn.tofocus.core.enums.AppTypeEnum;
import cn.tofocus.db.IndexInRedis;
import cn.tofocus.db.Name;
import cn.tofocus.db.SetConverter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

@Entity
@Table(name = "sys_application")
@Data
@ToString
@EqualsAndHashCode
@Schema(description = "应用")
@FieldNameConstants(innerTypeName = "F")
public class ApplicationEntity implements HasPkey<String>
{
    @Id
    @Column(length = 40)
    private String pkey;
    
    //应用名称
    @Column(length = 100, name = "appname")
    @Name
    @Schema(description = "名称")
    private String name;

    //应用类型
    @Schema(description = "应用类型")
    private AppTypeEnum apptype;

    //授权类型
    @Schema(description = "授权类型")
    private AppGrantType grantType;
    
    //应用安全码
    @Schema(description = "应用安全码")
    @Column(length = 40)
    private String secret;

    @Schema(description = "授权登录Url")
    @Convert(converter = SetConverter.class)
    @Column(length = 1000)
    private Set<String> uri;
    
    @IndexInRedis
    @Schema(description = "所属域")
    private String domainid;

    @Schema(description = "是否需要验证码")
    private Boolean needCaptcha;
    
    public ApplicationEntity()
    {
        
    }

    public ApplicationEntity(ApplicationEntity app)
    {
        this.setPkey(app.getPkey());
        this.setDomainid(app.getDomainid());
        this.setName(app.getName());
        this.setApptype(app.getApptype());
        this.setGrantType(app.getGrantType());
        this.setSecret(app.getSecret());
        this.setUri(app.getUri());
    }
}
