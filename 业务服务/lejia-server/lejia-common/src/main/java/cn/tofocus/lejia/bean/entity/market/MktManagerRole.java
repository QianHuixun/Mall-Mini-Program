package cn.tofocus.lejia.bean.entity.market;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.lejia.bean.enums.ManagerRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.CreatedDate;

import java.util.Date;

/**
 * @author czy
 * @version [版本号, 2024/8/12]
 */
@Data
@Entity
@Schema(description = "管理员角色表")
@Table(name = "mkt_manager_role")
@FieldNameConstants(innerTypeName = "F")
public class MktManagerRole implements HasPkey<String>
{
    @Id
    @Column
    @Schema(description = "主键")
    private String pkey;
    
    @Column
    @Schema(description = "管理员主键")
    private Integer manager;
    
    @Column(columnDefinition = "tinyint")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    @Schema(description = "角色")
    private ManagerRole role;

    @Schema(description = "市场")
    @Column(length = 40, nullable = false)
    private String farmer;

    @Schema(description = "公司")
    @Column(length = 40, nullable = false)
    private String company;

    @Schema(description = "建档时间")
    @Column(nullable = false)
    @CreatedDate
    private Date createdTime;

    @Schema(description = "归属主键")
    @Column(nullable = false)
    private Integer ascription;
    
    public String makePkey(Integer manager, ManagerRole role)
    {
        return manager + "_" + role.getIndex();
    }
    
    public void setPkey(String pkey)
    {
        this.pkey = pkey;
    }
    
    public void setPkey(Integer manager, ManagerRole role)
    {
        setManager(manager);
        setRole(role);
        setPkey(makePkey(manager, role));
    }
}
