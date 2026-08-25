package cn.tofocus.lejia.bean.dto.market;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class MktOrdinaryGoodsDTO 
{
	/**
	 * pkey
	 */
    private Integer pkey;
    
    private String name;
    
    private List<Map<String,Object>> spaces;
}
