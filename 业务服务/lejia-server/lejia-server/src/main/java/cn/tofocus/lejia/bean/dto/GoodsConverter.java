package cn.tofocus.lejia.bean.dto;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.CellData;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

public class GoodsConverter implements Converter<Boolean>
{
    
    @Override
    public Class supportJavaTypeKey()
    {
        return Boolean.class;
    }
    
    @Override
    public CellDataTypeEnum supportExcelTypeKey()
    {
        return CellDataTypeEnum.STRING;
    }
    
    @Override
    public Boolean convertToJavaData(ReadCellData cellData, ExcelContentProperty contentProperty,
        GlobalConfiguration globalConfiguration)
        throws Exception
    {
        return "是".equals(cellData.getStringValue()) ? true : false;
    }
    
    @Override
    public WriteCellData convertToExcelData(Boolean value, ExcelContentProperty contentProperty,
        GlobalConfiguration globalConfiguration)
        throws Exception
    {
        return new WriteCellData<>(value == true ? "是" : "否");
    }
    
}
