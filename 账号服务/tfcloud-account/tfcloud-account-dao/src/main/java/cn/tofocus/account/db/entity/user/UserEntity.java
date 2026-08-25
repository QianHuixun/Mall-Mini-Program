package cn.tofocus.account.db.entity.user;

import java.util.Collection;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.security.core.GrantedAuthority;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.core.enums.SexEnum;
import cn.tofocus.core.security.TofocusUser;
import cn.tofocus.db.AutoRedisID;
import cn.tofocus.db.IndexInRedis;
import cn.tofocus.db.Name;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

/**
 * 
 * 用户
 * <功能详细描述>
 * 
 * @author  wyw
 * @version  [版本号, 2018年8月15日]
 */
@Entity
@Data
@Table(name = "sys_user")
@ToString
@EqualsAndHashCode
@Schema(description = "用户")
@FieldNameConstants(innerTypeName = "F")
public class UserEntity implements HasPkey<Long>
{
    @Id
    @AutoRedisID(domain = "tfcloud", app = "acc", sequence = "user")
    private Long pkey;
    
    //第一次注册时自动生成用户名，以tf_开头，用户可修改一次，修改时必须字母开头，并且由字母和数字组成，可以全使用字母
    @Column(length = 40)
    @IndexInRedis(unique = true)
    @Schema(description = "用户Id")
    private String userid;
    
    //用户昵称，可修改
    @Column(length = 100)
    @Name
    @Schema(description = "昵称")
    private String nickname;
    
    //密码
    @Column(length = 100)
    @Schema(description = "密码")
    private String password;
    
    //性别
    @Schema(description = "性别")
    private SexEnum sex;
    
    //身份证
    @Column(length = 20)
    @Schema(description = "身份证")
    private String idcard;
    
    //出生日期
    @Schema(description = "出生日期")
    private Date birthday;
    
    //备注
    @Column(length = 200)
    @Schema(description = "备注")
    private String remark;

    @Schema(description = "是否启用")
    private boolean actived = true;

    @Schema(description = "从哪个域注册")
    private String registFromDomain;
    
    @Schema(description = "注册时间")
    private Date registtime;

    @Schema(description = "过期时间")
    private Date exprietime;
    
    //用户绑定的手机，纯数字，可用手机登录
    @Column(length = 20)
    @Schema(description = "绑定的手机")
    private String bindPhone;
    
    //用户绑定的邮箱，可用邮箱登录
    @Column(length = 50)
    @Schema(description = "绑定的邮箱")
    private String bindEmail;

    @CreatedDate
    @Column(name = "created_time", updatable = false)
    private Date createdTime;
    
    @LastModifiedDate
    @Column(name = "updated_time")
    private Date updatedTime;
    
    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private Long createdBy;
    
    @LastModifiedBy
    @Column(name = "updated_by")
    private Long updatedBy;
    
    public UserEntity(UserEntity user)
    {
        this.setActived(user.actived);
        this.setBindEmail(user.bindEmail);
        this.setBindPhone(user.bindPhone);
        this.setBirthday(user.birthday);
        this.setCreatedBy(user.getCreatedBy());
        this.setCreatedTime(user.getCreatedTime());
        this.setExprietime(user.getExprietime());
        this.setIdcard(user.idcard);
        this.setNickname(user.nickname);
        this.setPassword(user.password);
        this.setPkey(user.getPkey());
        this.setRegisttime(user.registtime);
        this.setRemark(user.remark);
        this.setSex(user.sex);
        this.setUpdatedBy(user.getUpdatedBy());
        this.setUpdatedTime(user.getUpdatedTime());
        this.setUserid(user.userid);
    }
    
    public UserEntity()
    {
    }
    
    public TofocusUser toTofocusUser(Collection<? extends GrantedAuthority> authoritieset)
    {
        boolean expired = false;
        if (getExprietime() != null)
        {
            expired = getExprietime().getTime() < System.currentTimeMillis();
        }
        TofocusUser u = new TofocusUser(getPkey(), getUserid(), getNickname(), getPassword(), isActived(), !expired,
            !expired, isActived(), authoritieset);
        u.setBindPhone(bindPhone);
        return u;
    }
}
