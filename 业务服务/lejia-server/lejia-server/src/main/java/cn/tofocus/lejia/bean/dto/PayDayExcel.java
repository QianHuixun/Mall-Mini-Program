package cn.tofocus.lejia.bean.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.alibaba.excel.annotation.write.style.HeadStyle;
import com.alibaba.excel.enums.BooleanEnum;

import lombok.Data;

/**
 * 对账中心 每日数据
 * @author Administrator
 *
 */
@Data
@ColumnWidth(15)
@HeadFontStyle(fontHeightInPoints = 15, bold = BooleanEnum.FALSE)
@HeadStyle(fillForegroundColor = 1)
public class PayDayExcel 
{
	@ExcelProperty(value = "日期")
	private String day;

	@ExcelProperty(value = "收入")
	private String amt;
	
	@ExcelProperty(value = "收入笔数")
	private String amtNum;
	
	@ExcelProperty(value = "手续费")
	private String handlingFee;
	
	@ExcelProperty(value = "支出")
	private String expenditure;
	
	@ExcelProperty(value = "支出笔数")
	private String expenditureNum;
	
	@ExcelProperty(value = "收益")
	private String income;

}
