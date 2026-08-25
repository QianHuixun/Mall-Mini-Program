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

@Entity
@Data
@Table(name="mkt_member_index_advert")
public class MktMemberIndexAdvert implements HasPkey<String> 
{
    @Id
    @Schema(description = "pkey")
    private String pkey;
    
    @Schema(description = "用户")
    @Column(name = "member_key")
    private Integer member;
    
    @Schema(description = "弹窗主键")
    private Integer indexAdvert;
    
    @Schema(description = "建档时间")
    @CreatedDate
    private Date createdTime;
}
