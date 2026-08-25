package cn.tofocus.lejia.bean.dto.msd;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.Data;

@Data
public class MsdCateringMemberInfo extends MsdCateringMemberOnList
{
    
    private Integer ismanager;
    
    private Integer status;
    
    @JSONField(name = "status_text")
    private String statusTxt;
}
