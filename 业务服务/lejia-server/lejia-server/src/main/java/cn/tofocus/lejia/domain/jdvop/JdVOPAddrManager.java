package cn.tofocus.lejia.domain.jdvop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.jd.open.api.sdk.domain.vopdz.ConvertAddressOpenProvider.response.convertFourAreaByDetailStr.QueryAreaFourIdOpenResp;
import com.jd.open.api.sdk.domain.vopdz.QueryAddressOpenProvider.response.queryJdAreaIdList.AreaInfoBaseResp;
import com.jd.open.api.sdk.request.vopdz.VopAddressConvertFourAreaByDetailStrRequest;
import com.jd.open.api.sdk.request.vopdz.VopAddressConvertFourAreaByLatLngRequest;
import com.jd.open.api.sdk.request.vopdz.VopAddressQueryJdAreaIdListRequest;
import com.jd.open.api.sdk.request.vopdz.VopAddressVerifyAreaFourIdOpenReqRequest;
import com.jd.open.api.sdk.response.vopdz.VopAddressConvertFourAreaByDetailStrResponse;
import com.jd.open.api.sdk.response.vopdz.VopAddressConvertFourAreaByLatLngResponse;
import com.jd.open.api.sdk.response.vopdz.VopAddressQueryJdAreaIdListResponse;
import com.jd.open.api.sdk.response.vopdz.VopAddressVerifyAreaFourIdOpenReqResponse;

import cn.tofocus.common.util.CollectionUtil;
import cn.tofocus.common.util.StringUtil;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.lejia.bean.entity.jd.JdAddress;
import cn.tofocus.lejia.bean.entity.market.MktAddr;
import cn.tofocus.lejia.dao.jd.JdAddressDao;
import cn.tofocus.lejia.domain.jdvop.bean.JdVOPAreaInfo;
import cn.tofocus.lejia.exception.LejiaErrCode;
import lombok.extern.slf4j.Slf4j;

/**
 * 京东VOP - 地址接口
 */
@Slf4j
@Component
public class JdVOPAddrManager extends BaseJdVOPManager
{
    @Autowired
    private JdAddressDao jdAddressDao;
    
    public void syncJdAddrTask()
    {
        Integer firstLevel = 1;
        Integer secondLevel = 2;
        Integer thirdLevel = 3;
        Integer fourthLevel = 4;
        Map<Long, JdAddress> beanMap = new HashMap<>();
        List<AreaInfoBaseResp> firstList = queryJdAreaIdList(firstLevel, null);
        int firstNum = 0;
        for (AreaInfoBaseResp first : firstList)
        {
            JdAddress firstBean = new JdAddress();
            firstBean.setAreaId(first.getAreaId());
            firstBean.setAreaName(first.getAreaName());
            firstBean.setAreaLevel(firstLevel);
            firstBean.setParent(null);
            beanMap.put(firstBean.getAreaId(), firstBean);
            
            List<AreaInfoBaseResp> secondList = queryJdAreaIdList(secondLevel, first.getAreaId());
            for (AreaInfoBaseResp second : secondList)
            {
                JdAddress secondBean = new JdAddress();
                secondBean.setAreaId(second.getAreaId());
                secondBean.setAreaName(second.getAreaName());
                secondBean.setAreaLevel(secondLevel);
                secondBean.setParent(first.getAreaId());
                beanMap.put(secondBean.getAreaId(), secondBean);
                
                List<AreaInfoBaseResp> thirdList = queryJdAreaIdList(thirdLevel, second.getAreaId());
                for (AreaInfoBaseResp third : thirdList)
                {
                    JdAddress thirdBean = new JdAddress();
                    thirdBean.setAreaId(third.getAreaId());
                    thirdBean.setAreaName(third.getAreaName());
                    thirdBean.setAreaLevel(thirdLevel);
                    thirdBean.setParent(second.getAreaId());
                    beanMap.put(thirdBean.getAreaId(), thirdBean);
                    
                    List<AreaInfoBaseResp> fourthList = queryJdAreaIdList(fourthLevel, third.getAreaId());
                    for (AreaInfoBaseResp fourth : fourthList)
                    {
                        JdAddress fourthBean = new JdAddress();
                        fourthBean.setAreaId(fourth.getAreaId());
                        fourthBean.setAreaName(fourth.getAreaName());
                        fourthBean.setAreaLevel(fourthLevel);
                        fourthBean.setParent(third.getAreaId());
                        beanMap.put(fourthBean.getAreaId(), fourthBean);
                    }
                }
            }
            firstNum++;
            log.info("[京东VOP]地址同步-[{}]的子类目查询完成，查询进度{}/{}", first.getAreaName(), firstNum, firstList.size());
        }
        log.info("[京东VOP]地址同步-查询完成");
        // 查询数据库内areaId
        List<Long> oldIds = jdAddressDao.select().execDto(JdAddress.F.areaId, Long.class);
        List<Long> newIds = Lists.newArrayListWithCapacity(beanMap.size());
        newIds.addAll(beanMap.keySet());
        // 新areaId删除数据库内areaId，结果为新增数据
        newIds.removeAll(oldIds);
        // 数据库内areaId删除map内key，结果为删除数据
        oldIds.removeAll(beanMap.keySet());
        // 删除数据
        if (!oldIds.isEmpty())
        {
            jdAddressDao.removeAllById(oldIds);
            log.info("[京东VOP]地址同步-删除弃用数据完成");
        }
        else
        {
            log.info("[京东VOP]地址同步-没有删除弃用数据");
        }
        // 新增数据
        List<JdAddress> beans = Lists.newArrayListWithCapacity(newIds.size());
        for (Long newId : newIds)
        {
            beans.add(beanMap.get(newId));
        }
        int batchSize = 1000; // 每批处理条数
        int totalSize = beans.size();
        for (int i = 0; i < totalSize; i += batchSize)
        {
            int end = Math.min(i + batchSize, totalSize);
            List<JdAddress> toAdd = beans.subList(i, end);
            jdAddressDao.addAll(toAdd);
            log.info("[京东VOP]地址同步-新增同步新数据，进度{}/{}", end, totalSize);
        }
        log.info("[京东VOP]地址同步已完成");
    }
    
    /**
     * 由系统地址转换为京东VOP地址
     * @param addr
     * @return
     */
    public JdVOPAreaInfo convert2AreaInfo(MktAddr addr)
    {
        return convert2AreaInfo(addr.getPro(), addr.getCity(), addr.getArea(), addr.getTown());
    }
    
    /**
     * 由系统地址转换为京东VOP地址
     * @param pro 省
     * @param city 市
     * @param area 区
     * @param town 街道
     * @return
     */
    public JdVOPAreaInfo convert2AreaInfo(String pro, String city, String area, String town)
    {
        if (StringUtil.isBlank(town) || StringUtil.isBlank(area) || StringUtil.isBlank(city) || StringUtil.isBlank(pro))
            throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "请选择正确的四级地址");
        List<JdAddress> list = jdAddressDao.listByName(town);
        if (CollectionUtil.isEmpty(list))
            throw TofocusException.of(LejiaErrCode.DATA_INEXISTENCE, "找不到对应京东地址");
        JdVOPAreaInfo areaInfo = new JdVOPAreaInfo();
        JdAddress jdAddr = null;
        if (list.size() > 1)
        {
            for (JdAddress item : list)
            {
                JdAddress parent = getParent(item);
                if (area.equals(parent.getAreaName()) || area.equals(parent.getClientName()))
                {
                    jdAddr = item;
                }
            }
        }
        else
        {
            jdAddr = list.get(0);
        }
        if (jdAddr == null)
            throw TofocusException.of(LejiaErrCode.DATA_INEXISTENCE, "找不到对应京东地址");
        JdAddress jdCounty;
        if (jdAddr.getAreaLevel() == 3)
        {
            areaInfo.setTownId(0L);
            jdCounty = jdAddr;
        }
        else
        {
            areaInfo.setTownId(jdAddr.getAreaId());
            jdCounty = getParent(jdAddr);
        }
        areaInfo.setCountyId(jdCounty.getAreaId());
        JdAddress jdCity = getParent(jdCounty);
        areaInfo.setCityId(jdCity.getAreaId());
        JdAddress jdPro = getParent(jdCity);
        areaInfo.setProvinceId(jdPro.getAreaId());
        return areaInfo;
    }
    
    private JdAddress getParent(JdAddress child)
    {
        JdAddress parent = jdAddressDao.get(child.getParent());
        if (parent == null)
        {
            log.error("[京东VOP]京东地址({})的父节点({})找不到", child.getAreaId(), child.getParent());
            throw TofocusException.of(LejiaErrCode.DATA_INEXISTENCE, "找不到对应京东地址");
        }
        return parent;
    }
    
    /**
     * 查询四级地址ID列表
     * @param areaLevel 区域级别
     * @param jdAreaId 区域级别为1时可不传区域ID，非1时必传;此id来源于本接口返回值,分级查询
     * @return 区域信息列表
     */
    public List<AreaInfoBaseResp> queryJdAreaIdList(Integer areaLevel, Long jdAreaId)
    {
        try
        {
            VopAddressQueryJdAreaIdListRequest request = new VopAddressQueryJdAreaIdListRequest();
            request.setAreaLevel(areaLevel);
            request.setJdAreaId(jdAreaId);
            VopAddressQueryJdAreaIdListResponse response = jdClient().execute(request);
            if (!response.getOpenRpcResult().getSuccess())
            {
                printResponseFailedLog(log, request, response);
                throw TofocusException.of(LejiaErrCode.REMOTE_REQUEST_ERROR,
                    wrapVopErrMsg(response.getOpenRpcResult().getResultMessage()));
            }
            if (response.getOpenRpcResult().getResult() == null
                || response.getOpenRpcResult().getResult().getAreaInfoList() == null)
                return new ArrayList<>();
            return response.getOpenRpcResult().getResult().getAreaInfoList();
        }
        catch (TofocusException te)
        {
            throw te;
        }
        catch (Exception e)
        {
            String operate = "查询四级地址ID列表";
            String errMsg = operate + "请求异常";
            log.error("[京东VOP]{}", errMsg, e);
            throw TofocusException.of(LejiaErrCode.REQUEST_OUT_ERROR, errMsg);
        }
    }
    
    /**
     * 验证四级地址ID有效性
     * @return 校验结果
     */
    public Boolean verifyAreaFourIdOpenReq(Long provinceId, Long cityId, Long townId, Long countyId)
    {
        try
        {
            VopAddressVerifyAreaFourIdOpenReqRequest request = new VopAddressVerifyAreaFourIdOpenReqRequest();
            request.setProvinceId(provinceId);
            request.setCityId(cityId);
            request.setCountyId(countyId);
            request.setTownId(townId);
            VopAddressVerifyAreaFourIdOpenReqResponse response = jdClient().execute(request);
            if (!response.getOpenRpcResult().getSuccess())
            {
                printResponseFailedLog(log, request, response);
                throw TofocusException.of(LejiaErrCode.REMOTE_REQUEST_ERROR,
                    wrapVopErrMsg(response.getOpenRpcResult().getResultMessage()));
            }
            return response.getOpenRpcResult().getResult();
        }
        catch (TofocusException te)
        {
            throw te;
        }
        catch (Exception e)
        {
            String operate = "验证四级地址ID有效性";
            String errMsg = operate + "请求异常";
            log.error("[京东VOP]{}", errMsg, e);
            throw TofocusException.of(LejiaErrCode.REQUEST_OUT_ERROR, errMsg);
        }
    }
    
    /**
     * 地址详情转换京东地址编码
     * @param addressDetailStr 详细地址字符串
     * @return 响应结果
     */
    public QueryAreaFourIdOpenResp convertFourAreaByDetailStr(String addressDetailStr)
    {
        try
        {
            VopAddressConvertFourAreaByDetailStrRequest request = new VopAddressConvertFourAreaByDetailStrRequest();
            request.setAddressDetailStr(addressDetailStr);
            VopAddressConvertFourAreaByDetailStrResponse response = jdClient().execute(request);
            if (!response.getOpenRpcResult().getSuccess())
            {
                printResponseFailedLog(log, request, response);
                throw TofocusException.of(LejiaErrCode.REMOTE_REQUEST_ERROR,
                    wrapVopErrMsg(response.getOpenRpcResult().getResultMessage()));
            }
            return response.getOpenRpcResult().getResult();
        }
        catch (TofocusException te)
        {
            throw te;
        }
        catch (Exception e)
        {
            String operate = "地址详情转换京东地址编码";
            String errMsg = operate + "请求异常";
            log.error("[京东VOP]{}", errMsg, e);
            throw TofocusException.of(LejiaErrCode.REQUEST_OUT_ERROR, errMsg);
        }
    }
    
    /**
     * 通过经纬度转换为四级地址
     * @return 响应结果
     */
    public com.jd.open.api.sdk.domain.vopdz.ConvertAddressOpenProvider.response.convertFourAreaByLatLng.QueryAreaFourIdOpenResp convertFourAreaByLatLng(
        double longitude, double latitude)
    {
        try
        {
            VopAddressConvertFourAreaByLatLngRequest request = new VopAddressConvertFourAreaByLatLngRequest();
            request.setLongitude(longitude);
            request.setLatitude(latitude);
            VopAddressConvertFourAreaByLatLngResponse response = jdClient().execute(request);
            if (!response.getOpenRpcResult().getSuccess())
            {
                printResponseFailedLog(log, request, response);
                throw TofocusException.of(LejiaErrCode.REMOTE_REQUEST_ERROR,
                    wrapVopErrMsg(response.getOpenRpcResult().getResultMessage()));
            }
            return response.getOpenRpcResult().getResult();
        }
        catch (TofocusException te)
        {
            throw te;
        }
        catch (Exception e)
        {
            String operate = "通过经纬度转换为四级地址";
            String errMsg = operate + "请求异常";
            log.error("[京东VOP]{}", errMsg, e);
            throw TofocusException.of(LejiaErrCode.REQUEST_OUT_ERROR, errMsg);
        }
    }
}
