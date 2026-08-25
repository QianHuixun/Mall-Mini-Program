package cn.tofocus.domain.user;

import java.io.Serializable;

import javax.persistence.Column;

import lombok.Data;

@Data
public class UserContentKey implements Serializable
{
    /**
     * 注释内容
     */
    private static final long serialVersionUID = 1L;

    //用户id
    @Column(length = 40)
    private String ownerid;
    
    //顺序号
    private int seqid;
}
