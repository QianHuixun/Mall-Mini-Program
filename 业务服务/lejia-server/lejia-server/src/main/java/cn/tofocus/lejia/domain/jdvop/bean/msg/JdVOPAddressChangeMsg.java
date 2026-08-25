package cn.tofocus.lejia.domain.jdvop.bean.msg;

import lombok.Data;

/**
 * 京东地址变更消息
 * {"areaId":"京东地址编码","areaName":"京东地址名称","parentId":"父京东ID编码","areaLevel":“地址等级(行政级别：国家(1)、省(2)、市(3)、县(4)、镇(5))”,"operateType":”操作类型(插入数据为1，更新时为2，删除时为3)}
 */
@Data
public class JdVOPAddressChangeMsg
{
    private Long areaId;
    
    private String areaName;
    
    private Long parentId;
    
    private Integer areaLevel;
    
    // 操作类型(插入数据为1，更新时为2，删除时为3)
    private Integer operateType;
}
