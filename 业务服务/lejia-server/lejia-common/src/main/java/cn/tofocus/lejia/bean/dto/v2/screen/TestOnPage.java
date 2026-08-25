package cn.tofocus.lejia.bean.dto.v2.screen;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.tofocus.db.dto.JoinDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class TestOnPage
{
    
    @Schema(description = "市场名称")
    @JoinDTO(dataQuery = "sysFarmerDao", from = "farmer")
    private String marketName;
    
    @Schema(description = "检测商品")
    private String goods;
    
    @Schema(description = "检测项目")
    private String entry;
    
    @Schema(description = "检测日期")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd" , timezone = "GMT+8")
    private Date testDate;
    
    @Schema(description = "检测结果")
    public String getTest()
    {
        if (testResult == null) return "合格";
        if (testResult)
            return "合格";
        else
            return "不合格";
    }
    
    @JsonIgnore
    private Boolean testResult;
    
    @JsonIgnore
    private String farmer;
}
