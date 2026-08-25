package cn.tofocus.lejia.bean.dto.goods;

import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.alibaba.excel.annotation.write.style.HeadStyle;
import com.alibaba.excel.enums.BooleanEnum;

import cn.tofocus.db.excel.ErrMsgModel;

@ColumnWidth(15)
@HeadFontStyle(fontHeightInPoints = 12, bold = BooleanEnum.FALSE)
@HeadStyle(fillForegroundColor = 1)
public class MktTypeErrExcel extends ErrMsgModel
{
    
}
