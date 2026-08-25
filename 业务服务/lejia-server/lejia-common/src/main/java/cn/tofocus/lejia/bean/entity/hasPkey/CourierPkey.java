package cn.tofocus.lejia.bean.entity.hasPkey;

import java.io.Serializable;

import javax.persistence.Id;

import lombok.Data;

@Data
public class CourierPkey implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    @Id
    private String market;
    
    @Id
    private Integer id;

    public CourierPkey(String market, Integer id)
    {
        super();
        this.market = market;
        this.id = id;
    }

    public CourierPkey()
    {
        super();
    }
    
    
}
