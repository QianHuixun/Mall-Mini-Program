package cn.tofocus.lejia.bean.dto.v3;

import java.util.List;

import lombok.Data;

@Data
public class ProblemAppOnList
{
    private Integer pkey;
    
    private String name;
    
    private List<ProblemOnInfo> content;
}
