package cn.tofocus.lejia.bean.dto.market;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.tofocus.db.dto.JoinDTO;
import cn.tofocus.lejia.bean.enums.LevelType;
import cn.tofocus.lejia.bean.enums.MemberStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MktMemberOnList
{
    /**
     * pkey
     */
    @Id
    @Schema(description = "pkey")
    private Integer pkey;
    
    /**
     * 名称
     */
    @Schema(description = "名称")
    private String name;
    
    @Schema(description = "状态 正常/注销中/已注销")
    private MemberStatus status;
    
    @Schema(description = "状态")
    public String getStatusName()
    {
        if (status != null) return status.getName();
        return "";
    }

    /**
     * 性别
     */
    @Schema(description = "性别")
    private Integer sex;

    @Schema(description = "活跃度")
    private String activity;

    /**
     * 会员到期日期
     */
    @Schema(description = "会员到期日期")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endDate;

    /**
     * 手机
     */
    @Schema(description = "手机")
    private String mobile;

    @Schema(description = "地址")
    private String area;

    /**
     * 等级
     */
    @Schema(description = "等级")
    private LevelType level;

    private String levelName;

    /**
    * 头像
    */
    @Schema(description = "头像")
    private String photo;

    /**
     * 建档时间
     */
    @Schema(description = "建档时间")
    private Date createdTime;

    /**
     * 积分值
     */
    @Schema(description = "积分值")
    private Integer points;

    /**
     * 余额
     */
    @Schema(description = "余额")
    private BigDecimal balance;

    @Schema(description = "提现银行卡")
    private String custCard;

    @Schema(description = "提现银行卡用户名")
    private String custName;

    @Schema(description = "提现银行卡 开户行")
    private String accountBank;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "剩余优惠券")
    private Integer remainingCard;

    @Schema(description = "可用卡券笔数")
    private Integer cardNum;

    @Schema(description = "最近消费时间")
    private Date lastConsumeTime;

    @Schema(description = "最近消费市场")
    private String lastConsumeFarmer;

    @Schema(description = "最近消费市场名称")
    @JoinDTO(dataQuery = "sysFarmerDao", from = "lastConsumeFarmer")
    private String lastConsumeFarmerName;

    @Schema(description = "累计消费金额")
    private BigDecimal consumeAmt;

    @Schema(description = "累计消费笔数")
    private Long consumeCount;
    
    @Schema(description = "用户来源")
    private String source;
    
    @Schema(description = "标签")
    private List<String> tagNames;
}
