package cn.tofocus.lejia.bean.dto.app;

import cn.tofocus.lejia.bean.enums.SourceType;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
public class AppUsePointsRecordOnList {
    /**
     * pkey
     */
    @Schema(description = "pkey")
    private Integer pkey;

    @Schema(description = "支付会员")
    private Integer member;

    @Schema(description = "支付会员姓名")
    private String memberName;

    /**
     * 积分
     */
    @Schema(description = "积分")
    private Integer points;

    /**
     * 购买+/消费-/活动+/手动+-
     */
    @Schema(description = "积分来源")
    private SourceType source;

    /**
     * 支付时间
     */
    @Schema(description = "支付时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date createdTime;
}
