package cn.tofocus.lejia.bean.dto.app.market;

import cn.tofocus.db.dto.JoinEnum;
import cn.tofocus.lejia.bean.enums.AdvertPosition;
import cn.tofocus.lejia.bean.enums.LinkType;
import cn.tofocus.lejia.bean.enums.LocationType;
import cn.tofocus.lejia.bean.enums.MemberVisibleRange;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.persistence.Column;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Type;

import java.util.Date;
import java.util.List;

@Data
public class AppAdvertOnList {
    /**
     * pkey
     */
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

    /**
     * 图片
     */
    @Schema(description = "图片", required = true)
    private String photo;
    public String getPhoto()
    {
        if(StringUtils.isNotBlank(photo))
            return photo + "&thumb=big";
        return photo;
    }

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

    /**
     * 商品名称
     */
    @Schema(description = "商品名称", hidden = true)
    private String goodsName = "";
    
    @Schema(description = "分类名称")
    private String objKeyName;

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
    
    @Schema(description = "分类跳转,true:可以跳转  false:不可")
    private Boolean jump = true;

    /**
     * 市场
     */
    @Schema(description = "市场", hidden = true)
    private String farmer;

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
