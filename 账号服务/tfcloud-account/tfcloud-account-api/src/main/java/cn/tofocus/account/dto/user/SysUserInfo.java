package cn.tofocus.account.dto.user;

import java.util.Date;

import javax.validation.constraints.Size;

import cn.tofocus.core.enums.SexEnum;
import cn.tofocus.domain.user.User;
import lombok.Data;

@Data
public class SysUserInfo
{
    private Long pkey;

    @Size(max = 40)
    private String userid;

    @Size(max = 100)
    private String nickname;

    @Size(max = 100)
    private String password;
    
    private SexEnum sex;

    @Size(max = 20)
    private String idcard;
    
    private Date birthday;

    @Size(max = 200)
    private String remark;
    
    private boolean actived = true;

    private Date registtime;
    
    private Date exprietime;

    @Size(max = 20)
    private String bindPhone;

    @Size(max = 50)
    private String bindEmail;
    
    public SysUserInfo()
    {
        
    }
}
