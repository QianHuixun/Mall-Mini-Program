package cn.tofocus.lejia.bean.dto.market;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;

import org.hibernate.annotations.Type;

import cn.tofocus.db.ListConverter;
import cn.tofocus.db.dto.JoinEnum;
import cn.tofocus.lejia.bean.enums.AdvertPosition;
import cn.tofocus.lejia.bean.enums.AdvertType;
import cn.tofocus.lejia.bean.enums.LinkType;
import cn.tofocus.lejia.bean.enums.LocationType;
import cn.tofocus.lejia.bean.enums.MemberVisibleRange;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MktAdvertOnList
{
    @Schema(hidden = true)
    private Integer pkey;
    
    /**
    * 名称
    */
    @Schema(description = "名称", required = true)
    private String name;
    
    /**
    * 位置 1号/2号/3号/4号/5号
    */
    @Schema(description = "位置 1/2/3/4/5", required = true)
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    private AdvertPosition position;

    @Schema(description = "位置关联对象主键")
    private String positionObj;

    @Schema(description = "位置关联对象名称")
    private String positionObjName;
    
    /**
    * 图片
    */
    @Schema(description = "图片", required = true)
    private String photo;
    
    /**
    * 链接类型 无/链接/积分商城/会员办理
    */
    @Schema(description = "链接类型 无/链接/积分商城/会员办理/商品", required = true)
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    private LinkType urlType;
    
    /**
    * 对象
    */
    @Schema(description = "对象")
    private String objKey = "";
    
    @Schema(description = "商品名称", hidden = true)
    private String goodsName = "";
    
    @Schema(description = "分类名称")
    private String objKeyName = "";

    @Schema(description = "卡券活动名称", hidden = true)
    private String activityName = "";
    
    /**
     * 排序
     */
    @Schema(description = "排序", required = true)
    private Integer sort;
    
    /**
    * 启用标志
    */
    @Schema(description = "启用标志", required = true)
    private Boolean enabled;
    
    @Schema(description = "广告类型:专区、自有")
    private AdvertType type;
    
    /**
    * 市场
    */
    @Schema(description = "市场")
    private String farmer;
    
    private List<String> farmers;
    
    private List<String> farmersName;
    
    /**
    * 公司
    */
    @Schema(description = "公司", hidden = true)
    private String company;
    
    /**
    * 建档时间
    */
    @Schema(description = "建档时间", hidden = true)
    private Date createdTime;
    
    @Schema(description = "展示位置")
     private LocationType locationType;
    
    @JoinEnum(from="locationType")
    private String locationTypeName;
    
    @Schema(description = "标签")
    private List<Integer> targerKeys;

    
    @Schema(description = "用户可见范围")
    private MemberVisibleRange visibleRange;
    @Schema(description = "用户可见范围名字")
    @JoinEnum(from="visibleRange")
    private String visibleRangeName;
}
