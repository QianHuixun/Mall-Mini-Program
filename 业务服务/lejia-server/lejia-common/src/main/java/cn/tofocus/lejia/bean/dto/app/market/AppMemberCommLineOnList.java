package cn.tofocus.lejia.bean.dto.app.market;

import cn.tofocus.lejia.bean.enums.CommSourceType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class AppMemberCommLineOnList {

    /**
     * pkey
     */
    @Schema(description = "pkey")
    private Integer pkey;

    /**
     * 用户
     */
    @Schema(description = "用户")
    private Integer member;

    /**
     * 借贷标志 借(-)/贷(+)
     */
    @Schema(description = "借贷标志 借(-)/贷(+)")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    private Boolean direct;

    /**
     * 佣金值
     */
    @Schema(description = "佣金值")
    private BigDecimal comms;

    /**
     * 积分来源 购买+/消费-/手动+-
     */
    @Schema(description = "积分来源 购买+/消费-/手动+-")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    private CommSourceType source;

    /**
     * 来源单据
     */
    @Schema(description = "来源单据")
    private String formId;

    /**
     * 建档时间
     */
    @Schema(description = "建档时间")
    private Date createdTime;
}
