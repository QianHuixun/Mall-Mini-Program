package cn.tofocus.lejia.excel.listener;

import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.*;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import cn.tofocus.common.util.CollectionUtil;
import cn.tofocus.core.exception.SysErrCode;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;

import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.db.excel.ErrMsgModel;
import cn.tofocus.lejia.excel.MktMemberMsdExcel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ImportMemberMsdListener extends AnalysisEventListener<MktMemberMsdExcel>
{
    
    private ExcelWriterBuilder errWriterBuilder;
    
    private List<MktMemberMsdExcel> errList;
    
    private List<MktMemberMsdExcel> list;
    
    private final Class<MktMemberMsdExcel> valueClass = MktMemberMsdExcel.class;
    
    private int count;
    
    private int errCount;
    
    private boolean commitOnErr = true;
    
    private Map<String, Integer> memberMobileMap;
    
    private Map<String, Integer> tagNameMap;

    private Set<String> existMobiles;

    private Set<String> repeatMobiles;
    
    private Validator validator;
    
    public ImportMemberMsdListener(List<MktMemberMsdExcel> list, Map<String, Integer> memberMobileMap,
        Map<String, Integer> tagNameMap, Set<String> repeatMobiles, OutputStream errOutputStream)
    {
        super();
        this.list = list;
        this.memberMobileMap = memberMobileMap;
        this.tagNameMap = tagNameMap;
        this.existMobiles = new HashSet<>();
        this.repeatMobiles = repeatMobiles;
        if (errOutputStream != null)
        {
            errWriterBuilder = EasyExcel.write(errOutputStream, MktMemberMsdExcel.class);
            errList = new ArrayList<>();
        }
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }
    
    @Override
    public void invoke(MktMemberMsdExcel data, AnalysisContext context)
    {
        try
        {
            Set<ConstraintViolation<MktMemberMsdExcel>> set = validator.validate(data);
            // javax校验
            if (CollectionUtil.isNotEmpty(set))
            {
                ConstraintViolation<MktMemberMsdExcel> cv = set.iterator().next();
                Field declaredField = data.getClass().getDeclaredField(cv.getPropertyPath().toString());
                ExcelProperty annotation = declaredField.getAnnotation(ExcelProperty.class);
                throw TofocusException.of(SysErrCode.REQUIRED_PRARAM_ERROR, cv.getMessage());
            }
            // 表内手机号重复校验，仅记录，不报错
            if (existMobiles.contains(data.getMobile()))
                repeatMobiles.add(data.getMobile());
            else
                existMobiles.add(data.getMobile());
            data.setMember(memberMobileMap.get(data.getMobile()));
            data.setTag(tagNameMap.get(data.getTagName()));
            list.add(data);
            count++;
        }
        catch (Exception e)
        {
            log.error(e.getMessage(), e);
            String errmsg;
            if (e instanceof TofocusException)
                errmsg = e.getMessage();
            else
                errmsg = e.getClass().getSimpleName() + ":" + e.getMessage();
            log.error("导入{}发生错误：数据[{}]，原因[{}]", valueClass.getSimpleName(), data, errmsg);
            if (errWriterBuilder != null)
            {
                if (data instanceof ErrMsgModel)
                {
                    (data).setErrMsg(errmsg);
                }
                errList.add(data);
            }
            errCount++;
        }
    }
    
    @Override
    public void doAfterAllAnalysed(AnalysisContext context)
    {
        if (errWriterBuilder != null && errCount > 0)
        {
            ExcelWriter errWriter = errWriterBuilder.build();
            WriteSheet errSheet = EasyExcel.writerSheet("错误数据").build();
            errWriter.write(errList, errSheet);
            errWriter.finish();
        }
        if (commitOnErr)
            log.info("导入{}完成，共导入{}条记录，{}条错误记录被忽略", valueClass.getSimpleName(), count, errCount);
        else if (errCount == 0)
            log.info("导入{}完成，共导入{}条记录", valueClass.getSimpleName(), count);
        else
            log.info("导入{}因有错误数据中止，共{}条错误记录", valueClass.getSimpleName(), errCount);
        
    }
}
