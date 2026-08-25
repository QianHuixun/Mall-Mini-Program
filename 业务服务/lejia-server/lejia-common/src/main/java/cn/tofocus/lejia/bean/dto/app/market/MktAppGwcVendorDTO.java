package cn.tofocus.lejia.bean.dto.app.market;

import java.util.List;

import lombok.Data;

@Data
public class MktAppGwcVendorDTO
{
    private String verdorName;
    
    private String verdorMobile;
    
    private String verdorAddr;
    
    private String booth;
    
    private List<MktAppGwcDTO> list2;
    
}
