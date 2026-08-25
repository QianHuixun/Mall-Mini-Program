package cn.tofocus.lejia.bean.dto;

import java.util.Date;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.alibaba.excel.annotation.write.style.HeadStyle;
import com.alibaba.excel.enums.BooleanEnum;

import cn.tofocus.db.excel.ErrMsgModel;
import lombok.Data;

@Data
@ColumnWidth(15)
@HeadFontStyle(fontHeightInPoints = 15, bold = BooleanEnum.FALSE)
@HeadStyle(fillForegroundColor = 1)
public class MktOriVenExcel extends ErrMsgModel
{

	/**
	 * 检测商户
	 */
	@ExcelProperty("检测商户")
	private String merchant;
	
	/**
	 * 检测商品 MktGoods
	 */
	@ExcelProperty("检测商品")
	private String goods;

	/**
	 * 检测项目
	 */
	@ExcelProperty("检测项目")
	private String entry;

	/**
	 * 检测结果
	 */
	@ExcelProperty("检测结果")
	private Boolean testResult;

	/**
	 * 检测日期
	 */
	@ExcelProperty("检测日期")
	private Date testDate;

}
