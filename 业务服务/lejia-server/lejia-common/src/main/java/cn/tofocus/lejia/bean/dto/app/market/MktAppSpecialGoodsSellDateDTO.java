package cn.tofocus.lejia.bean.dto.app.market;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
public class MktAppSpecialGoodsSellDateDTO {

    /**
     * date
     */
    @Schema(description = "date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "M.d" , timezone = "GMT+8")
    private Date date;

    /**
     * 状态
     */
    @Schema(description = "状态")
    private String state;

    /**
     * 日期
     */
    @Schema(description = "日期")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd" , timezone = "GMT+8")
    private Date timeDate;

}
