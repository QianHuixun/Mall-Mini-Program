package cn.tofocus.lejia.bean.dto.goods;

import java.util.ArrayList;
import java.util.List;

import cn.tofocus.common.util.StringUtil;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.lejia.bean.dto.market.GoodsSellingPointDTO;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsSellingPoint;
import cn.tofocus.lejia.exception.LejiaErrCode;

public interface ExcelHasSellingPoints
{
    int NAME_LENGTH_MAX = 6;
    
    int CONTENT_LENGTH_MAX = 6;
    
    String getSellingPointName1();
    
    void setSellingPointName1(String name);
    
    String getSellingPointContent1();
    
    void setSellingPointContent1(String content);
    
    String getSellingPointName2();
    
    void setSellingPointName2(String name);
    
    String getSellingPointContent2();
    
    void setSellingPointContent2(String content);
    
    String getSellingPointName3();
    
    void setSellingPointName3(String name);
    
    String getSellingPointContent3();
    
    void setSellingPointContent3(String content);
    
    String getSellingPointName4();
    
    void setSellingPointName4(String name);
    
    String getSellingPointContent4();
    
    void setSellingPointContent4(String content);
    
    default void setSellingPoints(List<GoodsSellingPointDTO> sellingPoints)
    {
        if (sellingPoints == null)
            return;
        if (!sellingPoints.isEmpty())
        {
            GoodsSellingPointDTO sellingPoint1 = sellingPoints.get(0);
            setSellingPointName1(sellingPoint1.getName());
            setSellingPointContent1(sellingPoint1.getContent());
        }
        if (sellingPoints.size() > 1)
        {
            GoodsSellingPointDTO sellingPoint2 = sellingPoints.get(1);
            setSellingPointName2(sellingPoint2.getName());
            setSellingPointContent2(sellingPoint2.getContent());
        }
        if (sellingPoints.size() > 2)
        {
            GoodsSellingPointDTO sellingPoint3 = sellingPoints.get(2);
            setSellingPointName3(sellingPoint3.getName());
            setSellingPointContent3(sellingPoint3.getContent());
        }
        if (sellingPoints.size() > 3)
        {
            GoodsSellingPointDTO sellingPoint4 = sellingPoints.get(3);
            setSellingPointName4(sellingPoint4.getName());
            setSellingPointContent4(sellingPoint4.getContent());
        }
    }
    
    default boolean isSellingPoint1NotBlank()
    {
        return StringUtil.isNotBlank(getSellingPointName1()) && StringUtil.isNotBlank(getSellingPointContent1());
    }
    
    default boolean isSellingPoint2NotBlank()
    {
        return StringUtil.isNotBlank(getSellingPointName2()) && StringUtil.isNotBlank(getSellingPointContent2());
    }
    
    default boolean isSellingPoint3NotBlank()
    {
        return StringUtil.isNotBlank(getSellingPointName3()) && StringUtil.isNotBlank(getSellingPointContent3());
    }
    
    default boolean isSellingPoint4NotBlank()
    {
        return StringUtil.isNotBlank(getSellingPointName4()) && StringUtil.isNotBlank(getSellingPointContent4());
    }
    
    default List<MktGoodsSellingPoint> convertSellingPoints2List()
    {
        List<MktGoodsSellingPoint> list = new ArrayList<>();
        if (isSellingPoint1NotBlank())
        {
            MktGoodsSellingPoint sellingPoint = new MktGoodsSellingPoint();
            sellingPoint.setName(getSellingPointName1());
            sellingPoint.setContent(getSellingPointContent1());
            list.add(sellingPoint);
        }
        if (isSellingPoint2NotBlank())
        {
            MktGoodsSellingPoint sellingPoint = new MktGoodsSellingPoint();
            sellingPoint.setName(getSellingPointName2());
            sellingPoint.setContent(getSellingPointContent2());
            list.add(sellingPoint);
        }
        if (isSellingPoint3NotBlank())
        {
            MktGoodsSellingPoint sellingPoint = new MktGoodsSellingPoint();
            sellingPoint.setName(getSellingPointName3());
            sellingPoint.setContent(getSellingPointContent3());
            list.add(sellingPoint);
        }
        if (isSellingPoint4NotBlank())
        {
            MktGoodsSellingPoint sellingPoint = new MktGoodsSellingPoint();
            sellingPoint.setName(getSellingPointName4());
            sellingPoint.setContent(getSellingPointContent4());
            list.add(sellingPoint);
        }
        return list;
    }
    
    default void validExcelHasSellingPoints()
    {
        if ((StringUtil.isBlank(getSellingPointName1()) && StringUtil.isNotBlank(getSellingPointContent1()))
            || (StringUtil.isNotBlank(getSellingPointName1()) && StringUtil.isBlank(getSellingPointContent1())))
            throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "卖点1名称和内容仅允许同时为空或同时有值");
        if ((StringUtil.isBlank(getSellingPointName2()) && StringUtil.isNotBlank(getSellingPointContent2()))
            || (StringUtil.isNotBlank(getSellingPointName2()) && StringUtil.isBlank(getSellingPointContent2())))
            throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "卖点2名称和内容仅允许同时为空或同时有值");
        if ((StringUtil.isBlank(getSellingPointName3()) && StringUtil.isNotBlank(getSellingPointContent3()))
            || (StringUtil.isNotBlank(getSellingPointName3()) && StringUtil.isBlank(getSellingPointContent3())))
            throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "卖点3名称和内容仅允许同时为空或同时有值");
        if ((StringUtil.isBlank(getSellingPointName4()) && StringUtil.isNotBlank(getSellingPointContent4()))
            || (StringUtil.isNotBlank(getSellingPointName4()) && StringUtil.isBlank(getSellingPointContent4())))
            throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "卖点4名称和内容仅允许同时为空或同时有值");
        if (getSellingPointName1() != null && getSellingPointName1().length() > NAME_LENGTH_MAX)
            throw TofocusException.of(LejiaErrCode.PARAM_VALUE_ERR, "卖点1名称不允许超过" + NAME_LENGTH_MAX + "个字");
        if (getSellingPointContent1() != null && getSellingPointContent1().length() > CONTENT_LENGTH_MAX)
            throw TofocusException.of(LejiaErrCode.PARAM_VALUE_ERR, "卖点1内容不允许超过" + CONTENT_LENGTH_MAX + "个字");
        if (getSellingPointName2() != null && getSellingPointName2().length() > NAME_LENGTH_MAX)
            throw TofocusException.of(LejiaErrCode.PARAM_VALUE_ERR, "卖点2名称不允许超过" + NAME_LENGTH_MAX + "个字");
        if (getSellingPointContent2() != null && getSellingPointContent2().length() > CONTENT_LENGTH_MAX)
            throw TofocusException.of(LejiaErrCode.PARAM_VALUE_ERR, "卖点2内容不允许超过" + CONTENT_LENGTH_MAX + "个字");
        if (getSellingPointName3() != null && getSellingPointName3().length() > NAME_LENGTH_MAX)
            throw TofocusException.of(LejiaErrCode.PARAM_VALUE_ERR, "卖点3名称不允许超过" + NAME_LENGTH_MAX + "个字");
        if (getSellingPointContent3() != null && getSellingPointContent3().length() > CONTENT_LENGTH_MAX)
            throw TofocusException.of(LejiaErrCode.PARAM_VALUE_ERR, "卖点3内容不允许超过" + CONTENT_LENGTH_MAX + "个字");
        if (getSellingPointName4() != null && getSellingPointName4().length() > NAME_LENGTH_MAX)
            throw TofocusException.of(LejiaErrCode.PARAM_VALUE_ERR, "卖点4名称不允许超过" + NAME_LENGTH_MAX + "个字");
        if (getSellingPointContent4() != null && getSellingPointContent4().length() > CONTENT_LENGTH_MAX)
            throw TofocusException.of(LejiaErrCode.PARAM_VALUE_ERR, "卖点4内容不允许超过" + CONTENT_LENGTH_MAX + "个字");
    }
}
