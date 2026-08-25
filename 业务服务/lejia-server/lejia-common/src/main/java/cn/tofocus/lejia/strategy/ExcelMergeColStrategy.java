// ExcelMergeColStrategy.java
package cn.tofocus.lejia.strategy;

import java.lang.reflect.Field;
import java.util.*;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.write.merge.AbstractMergeStrategy;

import cn.tofocus.lejia.annotation.ExcelMergeCol;
import cn.tofocus.lejia.annotation.ExcelMergeColBase;
import lombok.extern.slf4j.Slf4j;

/**
 * 最简单的合并策略 - 在最后一行的最后一个单元格进行合并
 */
@Slf4j
public class ExcelMergeColStrategy extends AbstractMergeStrategy
{
    
    private final Class<?> excelClass;
    
    private final int dataStartRow;
    
    private final int dataSize;
    
    private int[] baseColumnIndexes = new int[0];
    
    private int[] mergeColumnIndexes = new int[0];
    
    private boolean hasMergeConfig = false;
    
    // 缓存行信息
    private Map<Integer, String> rowBaseValueMap = new HashMap<>();
    
    private Map<String, List<Integer>> groupMap = new HashMap<>();
    
    public ExcelMergeColStrategy(Class<?> excelClass, int dataStartRow, int dataSize)
    {
        this.excelClass = excelClass;
        this.dataStartRow = dataStartRow;
        this.dataSize = dataSize;
        initMergeConfig();
    }
    
    private void initMergeConfig()
    {
        //log.info("=== 初始化合并配置 ===");
        
        List<Field> fields = getAllFields(excelClass);
        
        List<Integer> baseCols = new ArrayList<>();
        List<Integer> mergeCols = new ArrayList<>();
        
        for (Field field : fields)
        {
            ExcelProperty excelProperty = field.getAnnotation(ExcelProperty.class);
            if (excelProperty == null)
            {
                continue;
            }
            
            int colIndex = excelProperty.index();
            if (colIndex < 0)
            {
                continue;
            }
            
            if (field.isAnnotationPresent(ExcelMergeColBase.class))
            {
                baseCols.add(colIndex);
                if (!mergeCols.contains(colIndex))
                {
                    mergeCols.add(colIndex);
                }
            }
            
            if (field.isAnnotationPresent(ExcelMergeCol.class) && !mergeCols.contains(colIndex))
            {
                mergeCols.add(colIndex);
            }
        }
        
        if (baseCols.isEmpty())
        {
            log.warn("没有找到基准列，将不进行合并");
            hasMergeConfig = false;
            return;
        }
        
        Collections.sort(baseCols);
        Collections.sort(mergeCols);
        
        baseColumnIndexes = baseCols.stream().mapToInt(Integer::intValue).toArray();
        mergeColumnIndexes = mergeCols.stream().mapToInt(Integer::intValue).toArray();
        hasMergeConfig = true;
        
        //log.info("基准列索引: " + Arrays.toString(baseColumnIndexes));
        //log.info("合并列索引: " + Arrays.toString(mergeColumnIndexes));
        //log.info("=== 合并配置初始化完成 ===\n");
    }
    
    @Override
    protected void merge(Sheet sheet, Cell cell, Head head, Integer relativeRowIndex)
    {
        if (!hasMergeConfig)
        {
            return;
        }
        
        int rowIndex = cell.getRowIndex();
        int colIndex = cell.getColumnIndex();
        
        if (rowIndex < dataStartRow)
        {
            return;
        }
        
        // 记录行的基准值
        if (colIndex == 0) // 只在第一列时记录（假设第一列是基准列）
        {
            String baseValue = getRowBaseValue(sheet, rowIndex);
            rowBaseValueMap.put(rowIndex, baseValue);
            groupMap.computeIfAbsent(baseValue, k -> new ArrayList<>()).add(rowIndex);
        }
        
        // 检查是否是最后一个单元格（最后一行的最后一列）
        int lastRowNum = dataStartRow + dataSize - 1;
        int lastColNum = getLastColumnIndex();
        
        if (rowIndex == lastRowNum && colIndex == lastColNum)
        {
            //log.info("检测到最后一行最后一列，开始合并");
            performMerge(sheet);
        }
    }
    
    /**
     * 执行合并操作
     */
    private void performMerge(Sheet sheet)
    {
        //log.info("执行合并操作...");
        for (Map.Entry<String, List<Integer>> entry : groupMap.entrySet())
        {
            List<Integer> rows = entry.getValue();
            if (rows.size() > 1)
            {
                int startRow = Collections.min(rows);
                int endRow = Collections.max(rows);
                
                // 对每个需要合并的列进行检查
                for (int colIndex : mergeColumnIndexes)
                {
                    if (!isMergedRegionExists(sheet, startRow, endRow, colIndex))
                    {
                        try
                        {
                            CellRangeAddress region = new CellRangeAddress(startRow, endRow, colIndex, colIndex);
                            sheet.addMergedRegion(region);
                            //System.out.printf("合并: 列 %d, 行 %d-%d%n", colIndex, startRow, endRow);
                        }
                        catch (Exception e)
                        {
                            // 忽略异常
                        }
                    }
                }
            }
        }
        //log.info("合并操作完成");
    }
    
    /**
     * 获取行的基准值
     */
    private String getRowBaseValue(Sheet sheet, int rowIndex)
    {
        StringBuilder keyBuilder = new StringBuilder();
        for (int colIndex : baseColumnIndexes)
        {
            Row row = sheet.getRow(rowIndex);
            String value = "";
            if (row != null)
            {
                Cell cell = row.getCell(colIndex);
                value = getCellStringValue(cell);
            }
            keyBuilder.append(value).append("|");
        }
        return keyBuilder.toString();
    }
    
    /**
     * 获取单元格字符串值
     */
    private String getCellStringValue(Cell cell)
    {
        if (cell == null)
        {
            return "";
        }
        
        switch (cell.getCellType())
        {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell))
                {
                    return cell.getDateCellValue().toString();
                }
                double numValue = cell.getNumericCellValue();
                if (numValue == Math.floor(numValue))
                {
                    return String.valueOf((int)numValue);
                }
                else
                {
                    return String.format("%.2f", numValue);
                }
            default:
                return "";
        }
    }
    
    /**
     * 检查列是否应该合并
     */
    private boolean shouldMergeColumn(Sheet sheet, int startRow, int endRow, int col)
    {
        if (startRow >= endRow)
        {
            return false;
        }
        
        String firstValue = getCellStringValue(sheet, startRow, col);
        for (int row = startRow + 1; row <= endRow; row++)
        {
            String currentValue = getCellStringValue(sheet, row, col);
            if (!Objects.equals(firstValue, currentValue))
            {
                return false;
            }
        }
        
        return true;
    }
    
    private String getCellStringValue(Sheet sheet, int row, int col)
    {
        Row rowObj = sheet.getRow(row);
        if (rowObj == null)
        {
            return "";
        }
        Cell cell = rowObj.getCell(col);
        return getCellStringValue(cell);
    }
    
    /**
     * 获取最后一列的索引
     */
    private int getLastColumnIndex()
    {
        int maxIndex = 0;
        for (int index : mergeColumnIndexes)
        {
            if (index > maxIndex)
            {
                maxIndex = index;
            }
        }
        return maxIndex;
    }
    
    /**
     * 检查合并区域是否已存在
     */
    private boolean isMergedRegionExists(Sheet sheet, int startRow, int endRow, int col)
    {
        for (int i = 0; i < sheet.getNumMergedRegions(); i++)
        {
            CellRangeAddress region = sheet.getMergedRegion(i);
            if (region.getFirstRow() == startRow && region.getLastRow() == endRow && region.getFirstColumn() == col
                && region.getLastColumn() == col)
            {
                return true;
            }
        }
        return false;
    }
    
    private List<Field> getAllFields(Class<?> clazz)
    {
        List<Field> fields = new ArrayList<>();
        Class<?> currentClass = clazz;
        while (currentClass != null && currentClass != Object.class)
        {
            fields.addAll(Arrays.asList(currentClass.getDeclaredFields()));
            currentClass = currentClass.getSuperclass();
        }
        return fields;
    }
}