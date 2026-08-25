package cn.tofocus.lejia.bean.dto.market;

import java.util.Date;

import org.springframework.data.annotation.CreatedDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.tofocus.db.dto.JoinDTO;
import cn.tofocus.lejia.bean.enums.IndexAdvertSubject;
import cn.tofocus.lejia.bean.enums.LinkType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
*  app弹窗广告
* @author zdw 2020-09-22
*/

@Data
public class MktIndexAdvertOnList
{
    
    /**
     * pkey
     */
    @Schema(description = "pkey", required = true)
    private Integer pkey;
    
    /**
    * 名称
    */
    @Schema(description = "名称", required = true)
    private String name;
    
    /**
    * 图片
    */
    @Schema(description = "图片", required = true)
    private String photo;
    
    /**
    * 活动对象 全部/年费会员/活跃会员/......
    */
    @Schema(description = "subject", required = true)
    private IndexAdvertSubject subject;
    
    private String subjectName;
    
    /**
    * 链接类型 无/链接/积分商城/会员办理
    */
    @Schema(description = "链接类型 无/链接/积分商城/会员办理", required = true)
    private LinkType urlType;
    
    /**
    * 对象
    */
    @Schema(description = "对象", required = false)
    private String objKey;
    
    @Schema(description = "商品名称", hidden = true)
    private String goodsName = "";

    @Schema(description = "卡券活动名称", hidden = true)
    private String activityName = "";
    
    /**
    * 启用日期
    */
    @Schema(description = "启用日期", required = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startDate;
    
    /**
    * 结束日期
    */
    @Schema(description = "结束日期", required = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endDate;
    
    @Schema(description = "市场")
    private String farmer;
    
    @JoinDTO(dataQuery = "sysFarmerDao", from = "farmer")
    private String farmerName;
    
    /**
    * 建档时间
    */
    @Schema(description = "建档时间", required = true)
    @CreatedDate
    private Date createdTime;
    
}