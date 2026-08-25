//package cn.tofocus.lejia.bean.entity.member;
//
//import java.math.BigDecimal;
//import java.util.Date;
//
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.Id;
//import javax.persistence.Table;
//
//import cn.tofocus.common.cachemap.bean.HasPkey;
//import io.swagger.v3.oas.annotations.media.Schema;
//import lombok.Data;
//import lombok.experimental.FieldNameConstants;
//import org.springframework.data.annotation.LastModifiedDate;
//
///**
// * @author czy
// * @version [版本号, 2025/8/20]
// */
//@Data
//@Entity
//@Schema(description = "会员京东账户")
//@Table(name = "mkt_member_jd")
//@FieldNameConstants(innerTypeName = "F")
//public class MktMemberJd implements HasPkey<Integer>
//{
//    @Id
//    @Column
//    @Schema(description = "会员主键")
//    private Integer pkey;
//
//    @Column
//    @Schema(description = "标签主键")
//    private Integer tag;
//
//    @Schema(description = "余额")
//    @Column(precision = 11, scale = 2)
//    private BigDecimal balance;
//
//    @Schema(description = "最后更新时间")
//    @Column
//    @LastModifiedDate
//    private Date updatedTime;
//    
//    @Schema(description = "归属主键")
//    @Column
//    private Integer ascription;
//}
