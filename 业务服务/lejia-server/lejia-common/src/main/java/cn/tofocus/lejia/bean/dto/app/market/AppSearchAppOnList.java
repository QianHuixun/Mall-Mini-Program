package cn.tofocus.lejia.bean.dto.app.market;

import cn.tofocus.lejia.bean.entity.market.MktSearch;
import cn.tofocus.lejia.bean.entity.market.MktSearchHot;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class AppSearchAppOnList
{

    /**
     * 用户搜索历史
     */
    @Schema(description = "用户搜索历史")
    private List<MktSearch> lines;

    /**
     * 热门搜索
     */
    @Schema(description = "热门搜索")
    private List<MktSearchHot> hotLines;

}
