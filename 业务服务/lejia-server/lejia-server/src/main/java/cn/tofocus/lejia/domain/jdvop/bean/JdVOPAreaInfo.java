package cn.tofocus.lejia.domain.jdvop.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JdVOPAreaInfo
{
    private Long provinceId;
    
    private Long cityId;
    
    private Long countyId;
    
    private Long townId;
    
    public JdVOPAreaInfo(Long provinceId, Long cityId, Long countyId)
    {
        this.provinceId = provinceId;
        this.cityId = cityId;
        this.countyId = countyId;
        this.townId = 0L;
    }
}
