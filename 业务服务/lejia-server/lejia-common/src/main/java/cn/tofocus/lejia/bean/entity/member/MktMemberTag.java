package cn.tofocus.lejia.bean.entity.member;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;

import cn.tofocus.common.cachemap.bean.HasPkey;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

/**
 * @author czy
 * @version [版本号, 2024/8/12]
 */
@Data
@Entity
@Schema(description = "会员标签关联表")
@Table(name = "mkt_member_tag")
@FieldNameConstants(innerTypeName = "F")
public class MktMemberTag implements HasPkey<String>
{
    @Id
    @Column(length = 50)
    @Schema(description = "主键")
    private String pkey;
    
    @Column
    @Schema(description = "会员主键")
    private Integer member;
    
    @Column
    @Schema(description = "标签主键")
    private Integer tag;
    
    @Column
    @Schema(description = "建档时间")
    @CreatedDate
    private Date createdTime;
    
    @Column
    @Schema(description = "归属主键")
    private Integer ascription;
    
    public static String makePkey(Integer member, Integer tag)
    {
        return member + "_" + tag;
    }
    
    public void setPkey(String pkey)
    {
        this.pkey = pkey;
    }
    
    public void setPkey(Integer member, Integer tag)
    {
        setMember(member);
        setTag(tag);
        setPkey(makePkey(member, tag));
    }
}
