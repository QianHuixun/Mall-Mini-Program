package cn.tofocus.lejia.domain;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.SelectBuilder;
import cn.tofocus.lejia.bean.dto.market.PayDayDTO;
import cn.tofocus.lejia.bean.dto.market.PayLineDTO;
import cn.tofocus.lejia.bean.entity.market.MktCommDraw;
import cn.tofocus.lejia.bean.entity.market.MktOrder;
import cn.tofocus.lejia.bean.entity.market.MktPayLine;
import cn.tofocus.lejia.bean.entity.member.MktMember;
import cn.tofocus.lejia.bean.entity.ns.MktNsPayLine;
import cn.tofocus.lejia.bean.enums.CommDrawStatus;
import cn.tofocus.lejia.bean.enums.OrderStatus;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.market.MktCommDrawDao;
import cn.tofocus.lejia.dao.market.MktMemberDao;
import cn.tofocus.lejia.dao.market.MktOrderDao;
import cn.tofocus.lejia.dao.market.MktPayLineDao;
import cn.tofocus.lejia.dao.ns.MktNsPayLineDao;
import lombok.extern.slf4j.Slf4j;

import static cn.tofocus.core.query.exp.ExpUtil.f;
import static cn.tofocus.core.query.exp.ExpUtil.substring;

@Slf4j
@Component
public class PayBillManager
{
    
    @Autowired
    private MktPayLineDao payLineDao;
    
    @Autowired
    private MktNsPayLineDao nsPayLineDao;
    
    @Autowired
    private MktCommDrawDao commDrawDao;
    
    @Autowired
    private MktOrderDao orderDao;
    
    @Autowired
    private MktMemberDao memberDao;
    
    public PageResult<PayLineDTO> queryPayLines(int page, int pagesize, Boolean buy, Boolean recharge, Boolean member,
        Boolean withdraw, String startTime, String endTime, Integer ascription)
    {
        PageResult<PayLineDTO> result = null;
        if(ascription.equals(1))
        {
            result = nsPayLineDao.queryPayLines(page, pagesize, buy, recharge, member, startTime, endTime);
            for (PayLineDTO p : result.getContent())
            {
                String payTime = DateUtil.formatDate(p.getCreatedTime());
                StringBuilder sd = new StringBuilder(payTime);
                sd.insert(4, "-");
                sd.insert(7, "-");
                sd.insert(10, " ");
                sd.insert(13, ":");
                sd.insert(16, ":");
                p.setPayTime(sd.toString());
                if (p.getOutTradeNo().startsWith("91")) p.setBuyType("购物");
                if (p.getOutTradeNo().startsWith("92")) p.setBuyType("会员办理");
                if (p.getOutTradeNo().startsWith("93")) p.setBuyType("充值");
                // 计算手续费 千分之六 微信
                BigDecimal scale =
                    new BigDecimal(p.getCashFee()).multiply(new BigDecimal("0.006")).setScale(6, BigDecimal.ROUND_HALF_UP);
                log.info("scale: {}", scale);
                p.setHandlingFee(scale.stripTrailingZeros().toString());
                p.setPayTypeN(p.getNoticeType() + "支付");
                p.setAmt(new BigDecimal(p.getCashFee()).setScale(2).toString());
                
                if ("SUCCESS".equals(p.getStatus()))
                    p.setStatus("支付成功");
                else
                    p.setStatus("支付失败");
            }
        }
        else
        {
            PageResult<MktPayLine> pageResult =
                payLineDao.queryPayLines(page, pagesize, buy, recharge, member, startTime, endTime, ascription);
            result = BeanUtil.beanPageFrom(PayLineDTO.class, pageResult);
            for (PayLineDTO p : result.getContent())
            {
                String payTime = p.getPayTime();
                StringBuilder sd = new StringBuilder(payTime);
                sd.insert(4, "-");
                sd.insert(7, "-");
                sd.insert(10, " ");
                sd.insert(13, ":");
                sd.insert(16, ":");
                p.setPayTime(sd.toString());
                if (p.getOrderNumber().startsWith("91")) p.setBuyType("购物");
                if (p.getOrderNumber().startsWith("92")) p.setBuyType("会员办理");
                if (p.getOrderNumber().startsWith("93")) p.setBuyType("充值");
                // 计算手续费 千分之六 微信
                BigDecimal scale =
                    new BigDecimal(p.getAmt()).divide(new BigDecimal("100")).multiply(new BigDecimal("0.006")).setScale(6, BigDecimal.ROUND_HALF_UP);
                log.info("scale: {}", scale);
                p.setHandlingFee(scale.stripTrailingZeros().toString());
                p.setPayTypeN(p.getPayType().getName() + "支付");
                p.setAmt(new BigDecimal(p.getAmt()).divide(new BigDecimal("100")).setScale(2).toString());
                
                if ("SUCCESS".equals(p.getStatus()))
                    p.setStatus("支付成功");
                else
                    p.setStatus("支付失败");
            }
        }
       
        // 提现
        if (withdraw && result.getTotalElements() < pagesize)
        {
            List<MktCommDraw> exec = commDrawDao.select()
                .eq("ascription", ascription)
                .eq("status", CommDrawStatus.COMMDRAW_SENT)
                .between(substring(f("checkTime"), 1, 10), startTime, endTime)
                .exec();
            List<PayLineDTO> content = new ArrayList<>();
            content.addAll(result.getContent());
            int max = (int)(pagesize - result.getTotalElements());
            if (exec.size() < max) max = exec.size();
            for (int i = 0; i < max; i++)
            {
                MktCommDraw cd = exec.get(i);
                PayLineDTO dto = new PayLineDTO();
                dto.setPayTime(DateUtil.formatDate(cd.getCheckTime()));
                dto.setBuyType("提现");
                dto.setStatus("支付成功");
                dto.setAmt(cd.getComms().setScale(2).toString());
                dto.setHandlingFee("-");
                dto.setPayTypeN("-");
                dto.setCode("-");
                dto.setOrderNumber("-");
                dto.setMember(cd.getMember());
                content.add(dto);
            }
            result.setContent(content);
            log.info("getTotalElements: {}", result.getTotalElements());
            result.setTotalElements(result.getTotalElements() + max);
        }
        
        for (PayLineDTO dto : result.getContent())
        {
            String orderNumber = dto.getOrderNumber();
            if (StringUtils.isNotBlank(orderNumber) && !orderNumber.equals("-"))
            {
                MktOrder order = orderDao.selectOne().like("code", dto.getOrderNumber()).exec();
                if (order != null)
                {
                    Integer merberPkey = order.getMember();
                    MktMember mktMember = memberDao.get(merberPkey);
                    if (mktMember != null)
                    {
                        dto.setMobile(mktMember.getMobile());
                        dto.setName(mktMember.getName());
                    }
                }
            }
            Integer merberPkey = dto.getMember();
            if (merberPkey != null)
            {
                MktMember mktMember = memberDao.get(merberPkey);
                if (mktMember != null)
                {
                    dto.setMobile(mktMember.getMobile());
                    dto.setName(mktMember.getName());
                }
            }
        }
        
        return result;
    }
    
    public Map<String, Object> queryPayDetailNumCount(Boolean buy, Boolean recharge, Boolean member, Boolean withdraw,
        String startTime, String endTime, Integer ascription)
    {
        PageResult<PayLineDTO> result = null;
        BigDecimal sum = BigDecimal.ZERO;
        if(ascription.equals(1))
        {
            result = nsPayLineDao.queryPayLines(0, 100000, buy, recharge, member, startTime, endTime);
            for (PayLineDTO p : result.getContent())
            {
                // 计算手续费 千分之六 微信
                BigDecimal scale =
                    new BigDecimal(p.getCashFee()).multiply(new BigDecimal("0.006")).setScale(6, BigDecimal.ROUND_HALF_UP);
                log.info("scale: {}", scale);
                sum =
                    sum.add(new BigDecimal(p.getCashFee()).setScale(4, BigDecimal.ROUND_HALF_UP))
                        .subtract(scale);
            }
        }
        else
        {
            PageResult<MktPayLine> pageResult =
                payLineDao.queryPayLines(0, 100000, buy, recharge, member, startTime, endTime, ascription);
            result = BeanUtil.beanPageFrom(PayLineDTO.class, pageResult);
            for (PayLineDTO p : result.getContent())
            {
                // 计算手续费 千分之六 微信
                BigDecimal scale =
                    new BigDecimal(p.getAmt()).divide(new BigDecimal("100")).multiply(new BigDecimal("0.006")).setScale(6, BigDecimal.ROUND_HALF_UP);
                log.info("scale: {}", scale);
                sum =
                    sum.add(new BigDecimal(p.getAmt()).divide(new BigDecimal("100")).setScale(4, BigDecimal.ROUND_HALF_UP))
                        .subtract(scale);
            }
        }
      
        List<PayLineDTO> content = new ArrayList<>();
        content.addAll(result.getContent());
        int num = content.size();
        // 提现
        if (withdraw)
        {
            List<MktCommDraw> exec = commDrawDao.select()
                .eq("status", CommDrawStatus.COMMDRAW_SENT)
                .eq("ascription", ascription)
                .between(substring(f("checkTime"), 1, 10), startTime, endTime)
                .exec();
            num = num + exec.size();
            for (MktCommDraw cd : exec)
                sum = sum.subtract(cd.getComms().setScale(4, BigDecimal.ROUND_HALF_UP));
        }
        Map<String, Object> map = new HashMap<>();
        map.put("amt", sum.toString());
        map.put("num", num);
        return map;
    }
    
    public List<PayDayDTO> queryPayLines(String month)
        throws Exception
    {
        Map<Integer, PayDayDTO> map = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        String formatDate = DateUtil.formatDate(calendar.getTime(), "yyyy-MM");
        int actualMaximum = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        if (month.equals(formatDate)) actualMaximum = Integer.valueOf(DateUtil.formatDate(calendar.getTime(), "dd"));
        for (int i = actualMaximum; i > 0; i--)
        {
            PayDayDTO dto = new PayDayDTO();
            dto.setAmt("0");
            dto.setAmtNum("0");
            dto.setHandlingFee("0");
            dto.setExpenditure("0");
            dto.setExpenditureNum("0");
            dto.setIncome("0");
            if (i < 10)
                dto.setDay("0" + i);
            else
                dto.setDay(i + "");
            map.put(i, dto);
        }
        getDayPayData(month.replace("-", "") + "01", month.replace("-", "") + actualMaximum, map, 0, CurrentSession.ascriptionPkey());
        getDayExpenditureData(month + "-01", month + "-" + actualMaximum, map, 0, CurrentSession.ascriptionPkey());
        List<PayDayDTO> result = new ArrayList<>(map.values());
        Collections.reverse(result);
        return result;
    }
    
    public List<PayDayDTO> queryPayLinesT(String startTime, String endTime, String companyPkey, String marketPkey)
    {
        List<String> orderNumList = new ArrayList<>();
        Integer ascription = CurrentSession.ascriptionPkey();
        SelectBuilder<Integer, MktOrder> builder = orderDao.select()
            .eq("ascription", ascription)
            .in("status",
                OrderStatus.DELIVERED_ORDER,
                OrderStatus.SHIPPED_ORDER,
                OrderStatus.WAIT_ARRIVAL_ORDER,
                OrderStatus.WAIT_WRITEOFF_ORDER,
                OrderStatus.ARRIVED_ORDER,
                OrderStatus.CONFIRM_ORDER);
        if (StringUtils.isNotBlank(companyPkey)) builder.eq("company", companyPkey);
        if (StringUtils.isNotBlank(marketPkey)) builder.eq("farmer", marketPkey);
        List<MktOrder> exec = builder.exec();
        for (MktOrder o : exec)
            orderNumList.add(o.getCode().substring(0, o.getCode().length() - 1));
        Map<String, PayDayDTO> map = new HashMap<>();
        List<String> days = getDays(startTime, endTime);
        for (String s : days)
        {
            PayDayDTO dto = new PayDayDTO();
            dto.setAmt("0");
            dto.setAmtNum("0");
            dto.setHandlingFee("0");
            dto.setExpenditure("0");
            dto.setExpenditureNum("0");
            dto.setIncome("0");
            dto.setDay(s);
            map.put(s.replace("-", ""), dto);
        }
        
        if (orderNumList.isEmpty())
        {
            List<PayDayDTO> result = new ArrayList<>();
            Collections.reverse(days);
            for (String key : days)
            {
                result.add(map.get(key.replace("-", "")));
            }
            return result;
        }
        getDayPayDataT(startTime, endTime, map, 0, orderNumList);
//        getDayPayDataT(startTime.replace("-", ""), endTime.replace("-", ""), map, 0, orderNumList);
        // 没有传公司和市场 才计算 支出 支出是针对 用户的 无法以公司和市场 进行划分
        if (StringUtils.isBlank(companyPkey) && StringUtils.isBlank(marketPkey))
            getDayExpenditureDataT(startTime, endTime, map, 0);
        List<PayDayDTO> result = new ArrayList<>();
        Collections.reverse(days);
        for (String key : days)
        {
            result.add(map.get(key.replace("-", "")));
        }
        return result;
    }
    
    public List<String> getDays(String startTime, String endTime)
    {
        List<String> days = new ArrayList<String>();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try
        {
            Date start = dateFormat.parse(startTime);
            Date end = dateFormat.parse(endTime);
            
            Calendar tempStart = Calendar.getInstance();
            tempStart.setTime(start);
            
            Calendar tempEnd = Calendar.getInstance();
            tempEnd.setTime(end);
            tempEnd.add(Calendar.DATE, +1);// 日期加1(包含结束)
            while (tempStart.before(tempEnd))
            {
                days.add(dateFormat.format(tempStart.getTime()));
                tempStart.add(Calendar.DAY_OF_YEAR, 1);
            }
            
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return days;
    }
    
    public List<PayDayDTO> queryMonthPay(String year)
    {
        Calendar calendar = Calendar.getInstance();
        String formatDate = DateUtil.formatDate(calendar.getTime(), "yyyy");
        int actualMaximum = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        if (year.equals(formatDate)) actualMaximum = Integer.valueOf(DateUtil.formatDate(calendar.getTime(), "MM"));
        Map<Integer, PayDayDTO> map = new HashMap<>();
        for (int i = actualMaximum; i > 0; i--)
        {
            PayDayDTO dto = new PayDayDTO();
            dto.setAmt("0");
            dto.setAmtNum("0");
            dto.setHandlingFee("0");
            dto.setExpenditure("0");
            dto.setExpenditureNum("0");
            dto.setIncome("0");
            if (i < 10)
                dto.setDay("0" + i);
            else
                dto.setDay(i + "");
            map.put(i, dto);
        }
        getDayPayData(year + "0101", year + "1231", map, 1, CurrentSession.ascriptionPkey());
        getDayExpenditureData(year + "-01-01", year + "-1231", map, 1, CurrentSession.ascriptionPkey());
        List<PayDayDTO> result = new ArrayList<>(map.values());
        Collections.reverse(result);
        return result;
    }
    
    public List<PayDayDTO> queryMonthPayT(String startTime, String endTime, String companyPkey, String marketPkey)
    {
        List<String> orderNumList = new ArrayList<>();
        Integer ascription = CurrentSession.ascriptionPkey();
        SelectBuilder<Integer, MktOrder> builder = orderDao.select()
            .eq("ascription", ascription)
            .notIn("status",
                OrderStatus.UNPAID_ORDER,
                OrderStatus.VOID_ORDER,
                OrderStatus.REFUNDED_ORDER,
                OrderStatus.REFUND_APPLICATION_ORDER);
        if (StringUtils.isNotBlank(companyPkey)) builder.eq("company", companyPkey);
        if (StringUtils.isNotBlank(marketPkey)) builder.eq("farmer", marketPkey);
        List<MktOrder> exec = builder.exec();
        for (MktOrder o : exec)
            orderNumList.add(o.getCode().substring(0, o.getCode().length() - 1));
        Map<String, PayDayDTO> map = new HashMap<>();
        List<String> months = getMonths(startTime, endTime);
        for (String s : months)
        {
            PayDayDTO dto = new PayDayDTO();
            dto.setAmt("0");
            dto.setAmtNum("0");
            dto.setHandlingFee("0");
            dto.setExpenditure("0");
            dto.setExpenditureNum("0");
            dto.setIncome("0");
            dto.setDay(s);
            map.put(s.replace("-", ""), dto);
        }
        
        getDayPayDataT(startTime, endTime, map, 1, orderNumList);
//        getDayPayDataT(startTime.replace("-", ""), endTime.replace("-", ""), map, 1, orderNumList);
        // 没有传公司和市场 才计算 支出 支出是针对 用户的 无法以公司和市场 进行划分
        if (StringUtils.isBlank(companyPkey) && StringUtils.isBlank(marketPkey))
            getDayExpenditureDataT(startTime, endTime, map, 1);
        List<PayDayDTO> result = new ArrayList<>();
        Collections.reverse(months);
        for (String key : months)
        {
            result.add(map.get(key.replace("-", "")));
        }
        return result;
    }
    
    private List<String> getMonths(String startTime, String endTime)
    {
        List<String> result = new ArrayList<String>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");//格式化为年月
        
        Calendar min = Calendar.getInstance();
        Calendar max = Calendar.getInstance();
        try
        {
            min.setTime(sdf.parse(startTime));
            min.set(min.get(Calendar.YEAR), min.get(Calendar.MONTH), 1);
            max.setTime(sdf.parse(endTime));
            max.set(max.get(Calendar.YEAR), max.get(Calendar.MONTH), 2);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        
        Calendar curr = min;
        while (curr.before(max))
        {
            result.add(sdf.format(curr.getTime()));
            curr.add(Calendar.MONTH, 1);
        }
        min = null;
        max = null;
        curr = null;
        return result;
    }
    
    // 收入 optM 0:根据日 来统计 1:根据月 来统计
    private void getDayPayDataT(String startTime, String endTime, Map<String, PayDayDTO> map, int optM,
        List<String> orderNumList)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        if("1".equals(ascription))
        {
            PageResult<MktNsPayLine> pageResult = nsPayLineDao.aggregationPay(true, true, true, startTime, endTime, optM, orderNumList);
            String pa = "yyyyMMdd";
            if (optM == 1) pa = "yyyyMM";
            for (MktNsPayLine p : pageResult.getContent())
            {
                String date = DateUtil.formatDate(p.getCreatedTime(), pa);
                PayDayDTO dto = map.get(date);
                BigDecimal amt = new BigDecimal(p.getCashFee());
                dto.setAmt(amt.toString());
                dto.setAmtNum(p.getPkey().toString());
                BigDecimal setScale = amt.multiply(new BigDecimal("0.006")).setScale(6, BigDecimal.ROUND_HALF_UP);
                dto.setHandlingFee(setScale.stripTrailingZeros().toString());
                dto.setIncome(amt.subtract(setScale).setScale(6, BigDecimal.ROUND_HALF_UP).stripTrailingZeros().toString());
            }
        }
        else
        {
            int e = 8;
            if (optM == 1) e = 6;
            PageResult<MktPayLine> pageResult =
                payLineDao.aggregationPay(true, true, true, startTime.replace("-", ""), endTime.replace("-", ""), optM, orderNumList, ascription);
            for (MktPayLine p : pageResult.getContent())
            {
                PayDayDTO dto = map.get(p.getPayTime().substring(0, e));
                BigDecimal amt = new BigDecimal(p.getAmt()).divide(new BigDecimal("100"));
                dto.setAmt(amt.toString());
                dto.setAmtNum(p.getPkey().toString());
                BigDecimal setScale = amt.multiply(new BigDecimal("0.006")).setScale(6, BigDecimal.ROUND_HALF_UP);
                dto.setHandlingFee(setScale.stripTrailingZeros().toString());
                dto.setIncome(amt.subtract(setScale).setScale(6, BigDecimal.ROUND_HALF_UP).stripTrailingZeros().toString());
            }
        }
    }
    
    // 支出 optM 0:根据日 来统计 1:根据月 来统计
    private void getDayExpenditureDataT(String startTime, String endTime, Map<String, PayDayDTO> map, int optM)
    {
        PageResult<MktCommDraw> pageResult = commDrawDao.aggregationCommDraw(startTime, endTime, optM, CurrentSession.ascriptionPkey());
        int s = 0;
        int e = 8;
        if (optM == 1)
        {
            s = 0;
            e = 6;
        }
        for (MktCommDraw c : pageResult.getContent())
        {
            //			int i = Integer.valueOf(c.getRemark().substring(s, e)).intValue();
            PayDayDTO dto = map.get(c.getRemark().replace("-", "").substring(s, e));
            dto.setExpenditure(c.getComms().setScale(2).toString());
            dto.setExpenditureNum(c.getPkey().toString());
            dto.setIncome(new BigDecimal(dto.getIncome()).subtract(c.getComms()).toString());
        }
    }
    
    // 收入 optM 0:根据日 来统计 1:根据月 来统计
    private void getDayPayData(String startTime, String endTime, Map<Integer, PayDayDTO> map, int optM, Integer ascription)
    {
        PageResult<MktPayLine> pageResult = payLineDao.aggregationPay(true, true, true, startTime, endTime, optM, ascription);
        int s = 6;
        int e = 8;
        if (optM == 1)
        {
            s = 4;
            e = 6;
        }
        for (MktPayLine p : pageResult.getContent())
        {
            int i = Integer.valueOf(p.getPayTime().substring(s, e)).intValue();
            PayDayDTO dto = map.get(i);
            dto.setAmt(p.getAmt());
            dto.setAmtNum(p.getPkey().toString());
            BigDecimal setScale = new BigDecimal(p.getAmt()).multiply(new BigDecimal("0.006")).setScale(6, BigDecimal.ROUND_HALF_UP);
            dto.setHandlingFee(setScale.stripTrailingZeros().toString());
            dto.setIncome(new BigDecimal(p.getAmt()).subtract(setScale).setScale(6, BigDecimal.ROUND_HALF_UP).stripTrailingZeros().toString());
        }
    }
    
    // 支出 optM 0:根据日 来统计 1:根据月 来统计
    private void getDayExpenditureData(String startTime, String endTime, Map<Integer, PayDayDTO> map, int optM, Integer ascription)
    {
        PageResult<MktCommDraw> pageResult = commDrawDao.aggregationCommDraw(startTime, endTime, optM, ascription);
        int s = 8;
        int e = 10;
        if (optM == 1)
        {
            s = 5;
            e = 7;
        }
        for (MktCommDraw c : pageResult.getContent())
        {
            int i = Integer.valueOf(c.getRemark().substring(s, e)).intValue();
            PayDayDTO dto = map.get(i);
            dto.setExpenditure(c.getComms().setScale(2).toString());
            dto.setExpenditureNum(c.getPkey().toString());
            dto.setIncome(new BigDecimal(dto.getIncome()).subtract(c.getComms()).toString());
        }
    }
}
