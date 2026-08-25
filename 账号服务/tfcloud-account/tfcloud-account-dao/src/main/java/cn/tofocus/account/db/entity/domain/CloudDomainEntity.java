package cn.tofocus.account.db.entity.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.Name;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 
 * 域<br>
 * 
 * @author  wyw
 * @version  [版本号, 2019年11月19日]
 */
@Entity
@Table(name = "sys_domain")
@Data
@Schema(description = "")
public class CloudDomainEntity implements HasPkey<String>
{
    @Id
    private String pkey;
    
    @Name
    @Schema(description = "名称")
    private String name;
}
