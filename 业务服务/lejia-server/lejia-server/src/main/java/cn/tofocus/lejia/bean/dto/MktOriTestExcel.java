package cn.tofocus.lejia.bean.dto;

import java.util.Date;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.alibaba.excel.annotation.write.style.HeadStyle;
import com.alibaba.excel.enums.BooleanEnum;
import com.fasterxml.jackson.annotation.JsonFormat;

import cn.tofocus.db.excel.ErrMsgModel;
import lombok.Data;

@Data
@ColumnWidth(15)
@HeadFontStyle(fontHeightInPoints = 15, bold = BooleanEnum.FALSE)
@HeadStyle(fillForegroundColor = 1)
public class MktOriTestExcel extends ErrMsgModel
{

	/**
	 * 溯源商户
	 */
	@ExcelProperty("溯源商户")
	private String merchant;
	
	/**
	 * 溯源商品
	 */
	@ExcelProperty("溯源商品")
	private String goods;
	
	
	/**
	 * 供应商
	 */
	@ExcelProperty("供应商")
	private String vendor;

	/**
	 * 进货日期
	 */
	@ExcelProperty("进货日期")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd" , timezone = "GMT+8")
	private Date oriDate;
	


}
