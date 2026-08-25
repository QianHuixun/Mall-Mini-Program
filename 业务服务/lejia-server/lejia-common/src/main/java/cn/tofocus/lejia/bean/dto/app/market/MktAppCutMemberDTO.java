package cn.tofocus.lejia.bean.dto.app.market;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MktAppCutMemberDTO 
{
	@JsonIgnore
    private Integer memberPkey;
    private String memberName;
    @Schema(description = "砍价金额")
    private BigDecimal cutAmt;
    private String photo;
}
