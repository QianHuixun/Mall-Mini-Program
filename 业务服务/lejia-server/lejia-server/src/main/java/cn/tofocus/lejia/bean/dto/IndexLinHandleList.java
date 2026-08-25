package cn.tofocus.lejia.bean.dto;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class IndexLinHandleList
{
    private String market;
    
    private List<Map<String, Object>> list;
}
