package cn.tofocus.lejia.bean.dto.msd;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MsdCateringMemberOnList
{
    private Integer id;
    
    private String name;
    
    private String mobile;

    private String idcard;
    
    private Integer mtype;
    
    private String mtypetxt;
    
    private Integer sort;
    
    private String sorttxt;
    
    // 元
    private BigDecimal money;
    
    // yyyy-MM-dd hh:mm
    private String addtime;
}
