package cn.tofocus.lejia.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cn.tofocus.lejia.bean.dto.config.AscriptionGoodsZoneConfig;
import cn.tofocus.lejia.bean.entity.member.*;
import cn.tofocus.lejia.bean.enums.MsdOperationType;
import cn.tofocus.lejia.bean.enums.member.TagType;
import cn.tofocus.lejia.dao.market.*;
import cn.tofocus.lejia.dao.sys.SysDynamicAttributeDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.common.util.StringUtil;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.bean.dto.app.market.MktAppMemberDetailsDTO;
import cn.tofocus.lejia.bean.dto.msd.MsdCateringMemberInfo;
import cn.tofocus.lejia.bean.dto.msd.MsdCateringMemberOnList;
import cn.tofocus.lejia.bean.entity.sys.SysFarmer;
import cn.tofocus.lejia.dao.sys.SysFarmerDao;
import cn.tofocus.lejia.domain.market.MemberManager;
import cn.tofocus.lejia.domain.market.MktMemberMsdManager;
import cn.tofocus.lejia.exception.LejiaErrCode;
import lombok.extern.slf4j.Slf4j;

import static org.springframework.amqp.rabbit.config.ListenerContainerFactoryBean.Type.direct;

/**
 * 民生豆业务
 * 第三方报餐系统对接
 * @author  yx
 * @version  [版本号, 2025年11月27日]
 */
@Slf4j
@Component
public class MsdCateringManager
{
    /**
     * 是否对接第三方报餐系统的会员
     */
    @Value("${catering.enabled:false}")
    private boolean enabled;
    
    @Value("${catering.ascription:22}")
    private Integer ascription;
    
    @Value("${catering.apiurl:https://bc.350c.com}")
    private String apiurl;
    
    @Value("${catering.apitoken:cp08yQa48K8EutGWmo0g5MtEgzllKtxu}")
    private String apitoken;
    
    @Autowired
    private SysFarmerDao farmerDao;
    
    @Autowired
    private MktMemberDao memberDao;
    
    @Autowired
    private MktTagDao tagDao;
    
    @Autowired
    private MktMemberTagDao memberTagDao;
    
    @Autowired
    private MktMemberMsdDao memberMsdDao;

    @Autowired
    private MktMemberMsdLineDao memberMsdLineDao;
    
    @Autowired
    private SysDynamicAttributeDao dynamicAttributeDao;
    
    @Autowired
    private MemberManager memberManager;

    @Transactional(rollbackFor = Exception.class)
    public void syncMemberTask()
    {
        SysFarmer farmer = farmerDao.selectOne()
            .notEq(SysFarmer.F.pkey, (Constant.Operation + ascription))
            .eq(SysFarmer.F.ascription, ascription)
            .eq(SysFarmer.F.enabled, true)
            .eq(SysFarmer.F.idDel, false)
            .exec();
        if (farmer == null)
            throw TofocusException.of(LejiaErrCode.MARKET_INEXISTENCE, "当前配置运营端下没有运营中的市场");
        List<MsdCateringMemberOnList> list = ListCateringMembers();
        if (list == null)
            return;
        for (MsdCateringMemberOnList item : list)
        {
            // 如果手机号没有会员，新增一个会员
            MktMember member = memberDao.getMobile(item.getMobile(), ascription);
            if (member == null)
            {
                log.info("手机号（{}）还没有注册会员，新增一条空会员", item.getMobile());
                MktAppMemberDetailsDTO entity = new MktAppMemberDetailsDTO();
                entity.setMobile(item.getMobile());
                entity.setName(item.getName());
                entity.setLastFarmer(farmer.getPkey());
                member = memberManager.insMember(entity, ascription);
            }
            // 如果查不到标签，新建一个
            MktTag tag = tagDao.getByName(item.getSorttxt(), ascription);
            if (tag == null)
            {
                tag = new MktTag();
                tag.setType(TagType.MSD);
                tag.setName(item.getSorttxt());
                tag.setAscription(ascription);
                tag.setIdDel(false);
                tag = tagDao.add(tag);
            }
            // 查询民生豆账号
            MktMemberMsd oldBean = memberMsdDao.get(member.getPkey(), ascription);
            MktMemberMsd bean = null;
            if (oldBean == null)
            {
                bean = new MktMemberMsd();
                bean.setPkey(member.getPkey());
                bean.setTag(tag.getPkey());
                bean.setBalance(BigDecimal.ZERO);
                bean.setAscription(ascription);
                bean = memberMsdDao.add(bean);
            }
            else
            {
                bean = BeanUtil.beanFrom(MktMemberMsd.class, oldBean);
                bean.setTag(tag.getPkey());
            }
            if (oldBean == null || !Objects.equals(oldBean.getTag(), bean.getTag()))
            {
                if (oldBean != null)
                {
                    // 民生豆账户改标签
                    memberMsdDao.updateTag(bean.getPkey(), ascription, bean.getTag());
                    // 用户去掉原标签
                    MktMemberTag memberOldTag =
                        memberTagDao.get(MktMemberTag.makePkey(bean.getPkey(), oldBean.getTag()));
                    if (memberOldTag != null)
                        memberTagDao.remove(memberOldTag);
                }
                // 如果该member还没有该标签，加上
                MktMemberTag memberTag = memberTagDao.get(MktMemberTag.makePkey(bean.getPkey(), bean.getTag()));
                if (memberTag == null)
                {
                    memberTag = new MktMemberTag();
                    memberTag.setPkey(bean.getPkey(), bean.getTag());
                    memberTag.setAscription(ascription);
                    memberTagDao.put(memberTag);
                }
                if (tag.getType() != TagType.MSD)
                {
                    tagDao.updateType(tag.getPkey(), TagType.MSD);
                }
            }
        }
    }
    
    public BigDecimal getMemberBalance(String mobile)
    {
        try
        {
            MsdCateringMemberInfo info = getCateringMemberInfo(mobile);
            if (info != null)
            {
                return info.getMoney();
            }
            return BigDecimal.ZERO;
        }
        catch (Exception e)
        {
            log.error("获取余额报错，返回余额0，mobile：{}", mobile, e);
            return BigDecimal.ZERO;
        }
    }
    
    public void consume(Integer memberPkey, BigDecimal amt, String orderCode)
    {
        MktMember member = memberDao.get(memberPkey);
        if (member == null)
            throw TofocusException.of(LejiaErrCode.MEMBER_NOT_ERROR);
        
        MktMemberMsd account = memberMsdDao.get(memberPkey, member.getAscription());
        
        MsdOperationType operationType = MsdOperationType.CONSUME;
        
        MktMemberMsdLine old = memberMsdLineDao.selectOne()
            .eq(MktMemberMsdLine.F.formId, orderCode)
            .eq(MktMemberMsdLine.F.operationType, operationType)
            .exec();
        if (old != null)
            throw TofocusException.of(LejiaErrCode.WRONG_FORMID);
        
        AscriptionGoodsZoneConfig config = dynamicAttributeDao.getFarmerAttribute(AscriptionGoodsZoneConfig.class,
            member.getAscription(),
            Constant.Operation + ascription);
        if (config == null)
            config = new AscriptionGoodsZoneConfig();
        String remark = config.getIntegralMsdDisplayName() + "消费";
        
        BigDecimal balance = cateringMemberConsume(member.getMobile(), amt, orderCode, remark);
        
        MktMemberMsdLine line = new MktMemberMsdLine();
        line.setMember(memberPkey);
        if (account != null)
            line.setTag(account.getTag());
        line.setDirect(false);
        line.setAmt(amt);
        line.setOperationType(operationType);
        line.setRemark(orderCode);
        line.setFormId(orderCode);
        line.setAscription(ascription);
        line.setBalance(balance);
        memberMsdLineDao.add(line);
    }
    
    //{"code":200,"data":{"total":3,"code":200,"message":"获取成功","items":[{"sorttxt":"默认部门","mtype":0,"money":"100.00","addtime":"2025-11-25 15:58","idcard":"174758979274","name":"邵","mobile":"18927282170","id":6448,"sort":235,"mtypetxt":"内部"},{"sorttxt":"默认部门","mtype":0,"money":"0.00","addtime":"2025-11-27 10:43","idcard":"233751169387","name":"王映昕","mobile":"17695540624","id":6473,"sort":235,"mtypetxt":"内部"},{"sorttxt":"默认部门","mtype":0,"money":"0.00","addtime":"2025-11-27 13:06","idcard":"780259279273","name":"商城测试","mobile":"13906640800","id":6475,"sort":235,"mtypetxt":"内部"}]}}
    private List<MsdCateringMemberOnList> ListCateringMembers()
    {
        if (!enabled)
            return null;
        
        List<MsdCateringMemberOnList> list = new ArrayList<>();
        int page = 1;
        int pagesize = 200;
        Integer total = 0;
        do
        {
            JSONObject param = new JSONObject();
            param.put("limit", pagesize);
            param.put("page", page);
            log.info("第三方报餐系统会员列表，参数：{}", param);
            JSONObject result = post("/api/member_list.html", param);
            log.info("第三方报餐系统会员列表，结果：{}", result);
            
            if (result.getInteger("code").equals(200))
            {
                JSONObject data = result.getJSONObject("data");
                if (data.getInteger("code").equals(200))
                {
                    total = data.getInteger("total");
                    JSONArray items = data.getJSONArray("items");
                    list.addAll(items.toJavaList(MsdCateringMemberOnList.class));
                }
                else
                {
                    String msg = data.getString("message");
                    throw TofocusException.of(LejiaErrCode.REQUEST_OUT_ERROR,
                        StringUtil.isNotBlank(msg) ? msg : "第三方报餐系统会员列表失败");
                }
            }
            page++;
        }
        while (list.size() < total);
        return list;
    }
    
    //{"code":200,"data":{"code":200,"data":{"sorttxt":"默认部门","ismanager":0,"mobile":"13906640800","sort":235,"mtypetxt":"内部","mtype":0,"money":"200.00","addtime":"2025-11-27 13:06","idcard":"780259279273","name":"商城测试","id":6475,"status_text":"正常","status":1},"message":"获取成功"}}
    private MsdCateringMemberInfo getCateringMemberInfo(String mobile)
    {
        if (!enabled)
            return null;
        
        JSONObject param = new JSONObject();
        param.put("mobile", mobile);
        
        log.info("第三方报餐系统会员详情，参数：{}", param);
        JSONObject result = post("/api/member_info.html", param);
        log.info("第三方报餐系统会员详情，结果：{}", result);
        
        if (result.getInteger("code").equals(200))
        {
            JSONObject data = result.getJSONObject("data");
            if (data.getInteger("code").equals(200))
            {
                return data.getObject("data", MsdCateringMemberInfo.class);
            }
            else
            {
                String msg = data.getString("message");
                throw TofocusException.of(LejiaErrCode.REQUEST_OUT_ERROR,
                    StringUtil.isNotBlank(msg) ? msg : "第三方报餐系统会员详情失败");
            }
        }
        else
        {
            throw TofocusException.of(LejiaErrCode.REQUEST_OUT_ERROR, "第三方报餐系统会员详情失败");
        }
    }
    
    //{"code":200,"data":{"code":200,"data":{"txt":"API操作：测试 订单号：911006227911022","money":199,"id":6475},"message":"操作成功"}}
    private BigDecimal cateringMemberConsume(String mobile, BigDecimal amt, String orderCode, String remark)
    {
        if (!enabled)
            return null;
        
        JSONObject param = new JSONObject();
        param.put("mobile", mobile);
        param.put("amount", amt.toPlainString());
        param.put("txt", remark);
        param.put("orderid", orderCode);
        
        log.info("第三方报餐系统会员消费，参数：{}", param);
        JSONObject result = post("/api/member_moneydo.html", param);
        log.info("第三方报餐系统会员消费，结果：{}", result);
        
        if (result.getInteger("code").equals(200))
        {
            JSONObject data = result.getJSONObject("data");
            if (data.getInteger("code").equals(200))
            {
                return data.getBigDecimal("money");
            }
            else
            {
                String msg = data.getString("message");
                throw TofocusException.of(LejiaErrCode.REQUEST_OUT_ERROR,
                    StringUtil.isNotBlank(msg) ? msg : "第三方报餐系统会员消费失败");
            }
        }
        else
        {
            throw TofocusException.of(LejiaErrCode.REQUEST_OUT_ERROR, "第三方报餐系统会员消费失败");
        }
    }
    
    private JSONObject post(String url, JSONObject param)
    {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", apitoken);
        
        HttpEntity<String> requestEntity = new HttpEntity<>(JsonUtil.toString(param), headers);
        
        // 发送请求
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response =
            restTemplate.exchange(apiurl + url, HttpMethod.POST, requestEntity, String.class);
        
        return JSONObject.parseObject(response.getBody());
    }
    
    public static void main(String[] args)
    {
        System.out.println("START");
        JSONObject param = new JSONObject();
        param.put("limit", "200");
        param.put("page", "1");
        //param.put("mobile", "13906640800");
        //param.put("amount", "1.00");
        //param.put("txt", "测试");
        //param.put("orderid", "911006227911022");
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "cp08yQa48K8EutGWmo0g5MtEgzllKtxu");
        
        HttpEntity<String> requestEntity = new HttpEntity<>(JsonUtil.toString(param), headers);
        
        // 发送请求
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate
            .exchange("https://bc.350c.com/api/member_list.html", HttpMethod.POST, requestEntity, String.class);
        
        JSONObject results = JSONObject.parseObject(response.getBody());
        System.out.println(results);
    }
    
}
