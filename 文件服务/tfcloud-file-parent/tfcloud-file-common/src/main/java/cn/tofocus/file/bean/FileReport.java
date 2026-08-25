package cn.tofocus.file.bean;

import java.util.List;

import cn.tofocus.common.util.CollectionUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class FileReport
{
    private List<ReportItem> list;

    @Schema(description = "合计")
    public ReportItem getTotal()
    {
        ReportItem item = new ReportItem();
        if (CollectionUtil.isNotEmpty(list))
        {
            item.setName("合计");
            item.setCount(0);
            item.setSize(0L);
            for (ReportItem i : list)
            {
                item.setCount(item.getCount() + i.getCount());
                item.setSize(item.getSize() + i.getSize());
            }
        }
        return item;
    }
}
