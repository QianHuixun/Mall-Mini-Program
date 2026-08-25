package cn.tofocus.account.db.entity.application;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.tofocus.common.cachemap.bean.HasPkey;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Entity
@Table(name = "sys_server_run_as_check")
@Data
@Schema(description = "server_run_as方式登录校验")
@FieldNameConstants(innerTypeName = "F")
public class AppLoginCheckEntity implements HasPkey<String>
{
    @Id
    @Schema(description = "应用")
    @Column(length = 40)
    private String pkey;

    @Column(length = 40)
    @Schema(description = "功能主键")
    private String funcKey;

    @Column(length = 40)
    @Schema(description = "所属域")
    private String domainid;
    
}
