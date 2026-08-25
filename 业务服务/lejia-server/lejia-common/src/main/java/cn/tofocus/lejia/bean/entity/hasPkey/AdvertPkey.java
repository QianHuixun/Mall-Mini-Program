package cn.tofocus.lejia.bean.entity.hasPkey;

import java.io.Serializable;

import javax.persistence.Id;

import lombok.Data;

@Data
public class AdvertPkey implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    @Id
    private Integer advertKey;
    
    @Id
    private Integer id;

    public AdvertPkey(Integer advertKey, Integer id)
    {
        super();
        this.advertKey = advertKey;
        this.id = id;
    }

    public AdvertPkey()
    {
        super();
    }
    
    
}
