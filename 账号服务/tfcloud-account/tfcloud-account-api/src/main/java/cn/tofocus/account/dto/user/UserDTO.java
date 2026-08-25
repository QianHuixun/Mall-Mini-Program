package cn.tofocus.account.dto.user;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import cn.tofocus.account.dto.user.role.RoleInstDTO;
import cn.tofocus.core.enums.SexEnum;
import cn.tofocus.db.dto.JoinDTO;
import cn.tofocus.db.dto.UserName;
import lombok.Data;

@Data
public class UserDTO implements Serializable
{
    /**
     * 注释内容
     */
    private static final long serialVersionUID = 1L;

    private Long pkey;
    
    private String userid;
    
    private String nickname;
    
    private SexEnum sex;
    
    private boolean actived;
    
    private String bindPhone;
    
    @JoinDTO(referencedName = "ownerid", dataQuery = "roleInstanceDao", type = RoleInstDTO.class, cascade = true)
    private List<RoleInstDTO> roles;
    
    private Date createdTime;
    
    private Long createdBy;
    
    @UserName(from = "createdBy")
    private String createdByName;
    
    public String getRoleNames()
    {
        StringBuilder sb = new StringBuilder();
        if(roles != null)
        {
            for(int i = 0; i<roles.size(); i++)
            {
                sb.append(roles.get(i).getRolename());
                if(i < roles.size() - 1)
                {
                    sb.append(',');
                }
            }
        }
        return sb.toString();
    }
}
