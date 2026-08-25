package cn.tofocus.lejia.domain.vendor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.backoff.Sleeper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import static cn.tofocus.core.query.exp.ExpUtil.f;
import static cn.tofocus.core.query.exp.ExpUtil.substring;
import com.alibaba.excel.util.DateUtils;
import com.alibaba.fastjson.JSONObject;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.lejia.bean.dto.vendor.ReportOnList;
import cn.tofocus.lejia.bean.dto.vendor.VendorOrderSettleOnList;
import cn.tofocus.lejia.bean.entity.market.MktOrder;
import cn.tofocus.lejia.bean.entity.market.MktZxPayLine;
import cn.tofocus.lejia.bean.entity.vendor.MktSettlement;
import cn.tofocus.lejia.bean.entity.vendor.MktSettlementLine;
import cn.tofocus.lejia.bean.entity.vendor.MktSettlementLineDay;
import cn.tofocus.lejia.bean.entity.vendor.MktSettlementProcess;
import cn.tofocus.lejia.bean.entity.vendor.MktSettlementTotal;
import cn.tofocus.lejia.bean.entity.vendor.MktVendor;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorOrder;
import cn.tofocus.lejia.bean.entity.zx.ZxFileRecord;
import cn.tofocus.lejia.bean.enums.ProcessNode;
import cn.tofocus.lejia.bean.enums.SettlementType;
import cn.tofocus.lejia.bean.enums.ZxFileStatus;
import cn.tofocus.lejia.dao.market.MktOrderDao;
import cn.tofocus.lejia.dao.market.MktZxPayLineDao;
import cn.tofocus.lejia.dao.vendor.MktSettlementDao;
import cn.tofocus.lejia.dao.vendor.MktSettlementLineDao;
import cn.tofocus.lejia.dao.vendor.MktSettlementLineDayDao;
import cn.tofocus.lejia.dao.vendor.MktSettlementProcessDao;
import cn.tofocus.lejia.dao.vendor.MktSettlementTotalDao;
import cn.tofocus.lejia.dao.vendor.MktVendorDao;
import cn.tofocus.lejia.dao.vendor.MktVendorOrderDao;
import cn.tofocus.lejia.dao.zx.ZxFileRecordDao;
import cn.tofocus.lejia.domain.app.AppZxEqManager;
import cn.tofocus.lejia.domain.app.AppZxFileManager;
import cn.tofocus.lejia.domain.app.AppZxFileManager2;
import cn.tofocus.lejia.utils.DateUtil;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ZxTaskManager
{
    
    @Autowired
    private MktSettlementDao settlementDao;
    
    @Autowired
    private MktSettlementLineDao settlementLineDao;
    
    @Autowired
    private MktSettlementTotalDao settlementTotalDao;
    
    @Autowired
    private MktSettlementLineDayDao settlementLineDayDao;
    
    @Autowired
    private ZxFileRecordDao zxFileRecordDao;
    
    @Autowired
    private AppZxEqManager appZxEqManager;
    
    @Autowired
    private MktVendorOrderDao vendorOrderDao;
    
    @Autowired
    private MktSettlementProcessDao settlementProcessDao;
    
    @Autowired
    private MktZxPayLineDao zxPayLineDao;
    
    @Autowired
    private MktOrderDao orderDao;
    
    @Autowired
    private AppZxFileManager2 zxFileManager;
    
    @Autowired
    private MktVendorDao vendorDao;
    
    public void runFileUpload()
    {
        /**
         * 查询昨天结算,或者昨天重新结算的数据
         * 生成文件.并上传银行
         */
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_WEEK, -1);
        List<MktSettlement> exec = settlementDao.findByDate(cal.getTime());
        if (exec.isEmpty())
        {
            log.info("今日没有需要结算的数据!");
            return;
        }
        // 循环生成文件
        for (MktSettlement s : exec)
        {
            Date sd = DateUtil.formatDateStr(s.getStartDate(), "yyyy-MM-dd");
            Date ed = DateUtil.formatDateStr(s.getEndDate(), "yyyy-MM-dd");
            cal.setTime(sd);
            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(ed);
            cal2.add(Calendar.DAY_OF_WEEK, 1);
            while (true)
            {
                if (cal.getTime().compareTo(cal2.getTime()) == 0) break;
                List<MktSettlementLineDay> settleList = settlementLineDayDao.select()
                    .eq("settlementPkey", s.getPkey())
                    .eq("settlementDate", cal.getTime())
                    .eq("type", SettlementType.DOING)
                    .exec();
                if (settleList.isEmpty())
                {
                    cal.add(Calendar.DAY_OF_WEEK, 1);
                    continue;
                }
                List<MktSettlementLineDay> settleList2 = new ArrayList<>();
                List<MktSettlementLineDay> settleList3 = new ArrayList<>();
                for (MktSettlementLineDay sl : settleList)
                {
                    Boolean selfMention = sl.getSelfMention();
                    if (selfMention != null && !selfMention)
                    {
                        settleList2.add(sl);
                    }
                    if(selfMention == null)
                        settleList3.add(sl);
                }
                if (!settleList2.isEmpty())
                {
                    // 有提现不成功,单独处理提现失败的数据
                    withdrawSettle(settleList2, null);
                }
                if(!settleList3.isEmpty())
                {
//                    MktSettlementTotal total = settlementTotalDao.getTotal(cal.getTime());
                    // 新接口
                    String format = DateUtils.format(cal.getTime(), "yyyy-MM-dd");
                    List<Integer> listKey = orderDao.listKey(zxPayLineDao.listCode(format, format), null);
                    Integer count = 0;
                    Map<String, List<VendorOrderSettleOnList>> data = getVOS(listKey);
//                    List<VendorOrderSettleOnList> data = getVOSList(listKey);
                    count = data.size();
//                    for(List<VendorOrderSettleOnList> v : data.values())
//                        count = count + v.size();
                    log.info("今日({})需要结算的数据, {}条", DateUtil.formatDate(cal.getTime()), count);
                    appZxEqManager.runFileUpload(
                        data,
                        String.valueOf(count),
                        cal.getTime(),
                        String.valueOf(s.getPkey())
                        );
                }
                cal.add(Calendar.DAY_OF_WEEK, 1);
            }
        }
    }
    
    // 获取组合文件的数据
    private Map<String, List<VendorOrderSettleOnList>> getVOS(List<Integer> listKey)
    {
        List<MktVendorOrder> data = vendorOrderDao.getVendorOrderNotStart(listKey);
        List<Integer> keys = new ArrayList<>();
        data.forEach(e ->  keys.add(e.getVendor()));
        Map<Integer, MktVendor> map = vendorDao.getMapVendor(keys);
        List<VendorOrderSettleOnList> list = BeanUtil.beanListFrom(VendorOrderSettleOnList.class, data);
        Map<Integer, MktOrder> orderMap = orderDao.getMap(listKey);
        List<String> ts = new ArrayList<>();
        orderMap.values().forEach(e -> {
            String code = e.getCode();
            ts.add(code.substring(0, code.length() - 1));
        });
        List<MktZxPayLine> zpLines = zxPayLineDao.select().in("out_trade_no", ts).exec();
        Map<String,String> zpMap = new HashMap<>();
        zpLines.forEach(e -> zpMap.put(e.getOut_trade_no(), e.getTransaction_id()));
        for(VendorOrderSettleOnList o : list)
        {
            if(map.containsKey(o.getVendor()))
                o.setZxUserId(map.get(o.getVendor()).getZxUserId());
            if(orderMap.containsKey(o.getOrderPkey()))
            {
                MktOrder mktOrder = orderMap.get(o.getOrderPkey());
                String code = mktOrder.getCode();
                o.setCode(code.substring(0, code.length() - 1));
                o.setAmtn(mktOrder.getAmtn());
                o.setCardAmt(mktOrder.getCardAmt());
            }
            if(zpMap.containsKey(o.getCode()))
                o.setTransactionId(zpMap.get(o.getCode()));
        }
        Map<String, List<VendorOrderSettleOnList>> res = new HashMap<>();
        list.forEach(e -> {
            String code = e.getCode();
            if(!res.containsKey(code))
            {
                List<VendorOrderSettleOnList> value = new ArrayList<>();
                res.put(code, value);
            }
            res.get(code).add(e);
        });
        return res;
    }
    
    private List<VendorOrderSettleOnList> getVOSList(List<Integer> listKey)
    {
        List<MktVendorOrder> data = vendorOrderDao.getVendorOrderNotStart(listKey);
        List<Integer> keys = new ArrayList<>();
        data.forEach(e ->  keys.add(e.getVendor()));
        Map<Integer, MktVendor> map = vendorDao.getMapVendor(keys);
        List<VendorOrderSettleOnList> res = BeanUtil.beanListFrom(VendorOrderSettleOnList.class, data);
        Map<Integer, MktOrder> orderMap = orderDao.getMap(listKey);
        List<String> ts = new ArrayList<>();
        orderMap.values().forEach(e -> {
            String code = e.getCode();
            ts.add(code.substring(0, code.length() - 1));
        });
//        List<MktZxPayLine> zpLines = zxPayLineDao.select().in("out_trade_no", ts).exec();
//        Map<String,String> zpMap = new HashMap<>();
//        zpLines.forEach(e -> zpMap.put(e.getOut_trade_no(), e.getTransaction_id()));
        for(VendorOrderSettleOnList o : res)
        {
            if(map.containsKey(o.getVendor()))
                o.setZxUserId(map.get(o.getVendor()).getZxUserId());
            if(orderMap.containsKey(o.getOrderPkey()))
            {
                MktOrder mktOrder = orderMap.get(o.getOrderPkey());
                String code = mktOrder.getCode();
                o.setCode(code.substring(0, code.length() - 1));
                o.setAmtn(mktOrder.getAmtn());
                o.setCardAmt(mktOrder.getCardAmt());
            }
//            if(zpMap.containsKey(o.getCode()))
//                o.setTransactionId(zpMap.get(o.getCode()));
        }
        return res;
    }
    
    // 只生成文件
    public void runFileUploadTest(Date date)
    {
        /**
         * 查询昨天结算,或者昨天重新结算的数据
         * 生成文件.并上传银行
         */
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        List<MktSettlement> exec = settlementDao.findByDateTest(date);
        if (exec.isEmpty())
        {
            log.info("今日没有需要结算的数据!");
            return;
        }
        // 循环生成文件
        for (MktSettlement s : exec)
        {
            Date sd = DateUtil.formatDateStr(s.getStartDate(), "yyyy-MM-dd");
            Date ed = DateUtil.formatDateStr(s.getEndDate(), "yyyy-MM-dd");
            cal.setTime(sd);
            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(ed);
            cal2.add(Calendar.DAY_OF_WEEK, 1);
            while (true)
            {
                if (cal.getTime().compareTo(cal2.getTime()) == 0) break;
                
                List<MktSettlementLineDay> settleList = settlementLineDayDao.select()
                    .eq("settlementPkey", s.getPkey())
                    .eq("settlementDate", cal.getTime())
                    .eq("type", SettlementType.DOING)
                    .exec();
                if (settleList.isEmpty())
                {
                    cal.add(Calendar.DAY_OF_WEEK, 1);
                    continue;
                }
                List<MktSettlementLineDay> settleList2 = new ArrayList<>();
                List<MktSettlementLineDay> settleList3 = new ArrayList<>();
                for (MktSettlementLineDay sl : settleList)
                {
                    Boolean selfMention = sl.getSelfMention();
                    if (selfMention != null && !selfMention)
                    {
                        settleList2.add(sl);
                    }
                    if(selfMention == null)
                        settleList3.add(sl);
                }
                if (!settleList2.isEmpty())
                {
                    // 有提现不成功,单独处理提现失败的数据
                    withdrawSettle(settleList2, null);
                }
                if(!settleList3.isEmpty())
                {
                    String format = DateUtils.format(cal.getTime(), "yyyy-MM-dd");
                    List<Integer> listKey = orderDao.listKey(zxPayLineDao.listCode(format, format), null);
                    Map<String, List<VendorOrderSettleOnList>> data = getVOS(listKey);
//                    List<VendorOrderSettleOnList> data = getVOSList(listKey);
//                    MktSettlementTotal total = settlementTotalDao.getTotal(cal.getTime());
                    zxFileManager.runFile(
                        data,
                        cal.getTime(),
                        String.valueOf(s.getPkey())
                        );
                }
                cal.add(Calendar.DAY_OF_WEEK, 1);
            }
        }
    }
    
    
    // 生成指定日期文件并上传
    public void runFileUploadTest2(Date date, String settleKey, Boolean flag)
    {
        String time = DateUtil.formatDate(date, "yyyy-MM-dd");        
        List<Integer> listKey = orderDao.listKey(zxPayLineDao.listCode(time, time), null);
        List<MktSettlementLineDay> lineDays = new ArrayList<>();
        List<ReportOnList> reportList = vendorOrderDao.sumVendorPrice(ReportOnList.class, listKey);
        for (ReportOnList r : reportList)
        {
            MktSettlementLineDay settlementLine = BeanUtil.beanFrom(MktSettlementLineDay.class, r.getVendorInfo());
            assemblyReportOnList(r);
            settlementLine.setSettlementDate(date);
            settlementLine.setSettlementPkey(Integer.valueOf(settleKey));
            settlementLine.setVendor(r.getVendor());
            settlementLine.setVendorName(r.getVendorInfo().getName());
            settlementLine.setBankuserIdentity(r.getVendorInfo().getZxIdentity());
            settlementLine.setCommission(r.getVendorInfo().getCommissionRate());
            settlementLine.setOrderAmt(r.getPurchaseAmt());
            settlementLine.setOrderCount(r.getPurchaseNum());
            settlementLine.setOrderCommission(r.getOrderComm());
            settlementLine.setAmt(r.getSettlementAmt());
            settlementLine.setType(SettlementType.DOING);
            lineDays.add(settlementLine);
        }
        List<MktSettlementLineDay> settleList = settlementLineDayDao.addAll(lineDays);
        System.out.println("settleList: " + settleList.size());
        MktSettlementTotal total = settlementTotalDao.getTotal(date);
        if (flag)
        {
//            ZxFileRecord record = appZxEqManager.runFileUpload(data, date, settleKey, total);
        }
        else
        {
//            zxFileManager.runFile(settleList, date, settleKey, total);
        }
    }
    
    private ReportOnList assemblyReportOnList(ReportOnList r)
    {
        BigDecimal settlementAmt = BigDecimal.ZERO;
        //默认采购价
        settlementAmt = r.getPurchaseAmt();
        
        BigDecimal rate =
            r.getVendorInfo().getCommissionRate() == null ? BigDecimal.ZERO : r.getVendorInfo().getCommissionRate();
        settlementAmt = BigDecimal.valueOf((1 - rate.doubleValue() / 100))
            .multiply(r.getPurchaseAmt())
            .setScale(2, BigDecimal.ROUND_HALF_UP);
        
        r.setOrderComm(r.getPurchaseAmt().subtract(settlementAmt));
        r.setSettlementAmt(settlementAmt);
        return r;
    }
    
    public String runQueryFile()
    {
        List<ZxFileRecord> exec = zxFileRecordDao.select().eq("status", ZxFileStatus.UPLOAD_SYCCESS).exec();
        String res = "没有可查询的文件";
        if (exec.isEmpty()) return res;
        for (int i = 0; i < exec.size(); i++)
        {
            if (i > 0)
            {
                try
                {
                    Thread.sleep(5 * 60 * 1000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
            ZxFileRecord file = exec.get(i);
            res = appZxEqManager.getFileStatus2(file.getName(), file);
            log.info("查询文件名称({})   状态: {}", file.getName(), res);
        }
        
        zxFileRecordDao.updateAll(exec);
        return res;
    }
    
    @Transactional
    public void runWithdraw()
    {
        // 查询今天上传 已经清分成功的文件
        List<ZxFileRecord> records = zxFileRecordDao.select()
            .eq("status", ZxFileStatus.FINISH)
            .eq(substring(f("uploadDate"), 1, 10), DateUtil.formatDate(new Date(), "yyyy-MM-dd"))
            .exec();
        if (records == null || records.isEmpty())
        {
            log.info("今天没有需要提现的数据");
            return;
        }
        for (ZxFileRecord record : records)
        {
            String settlementKey = record.getSettlementKey();
            if (StringUtils.isBlank(settlementKey)) continue;
            String name = record.getName();
            name = name.substring(22, 30);
            List<MktSettlementLineDay> settleList = settlementLineDayDao.select()
                .eq("settlementPkey", settlementKey)
                .eq("settlementDate", DateUtil.formatDateStr(name, "yyyyMMdd"))
                .eq("type", SettlementType.DOING)
                .exec();
            withdrawSettle(settleList, record);
        }
    }
    
    @Transactional
    public void runFailFile()
    {
        // 结算失败后的处理 
        List<ZxFileRecord> records = zxFileRecordDao.select()
            .in("status", ZxFileStatus.SEPARATE, ZxFileStatus.FAIL, ZxFileStatus.ABNORMAL)
//            .eq(substring(f("uploadDate"), 1, 10), DateUtil.formatDate(new Date(), "yyyy-MM-dd"))
            .exec();
        if (records == null || records.isEmpty()) return;
        String rem = "异常,需联系管理员解决";
        List<String> keyList = new ArrayList<>();
        for (ZxFileRecord fr : records)
        {
            if (StringUtils.isBlank(fr.getSettlementKey())) continue;
            keyList.add(fr.getSettlementKey());
            String str = fr.getName();
            str = str.substring(22, 30);
            List<MktSettlementLineDay> settleDayList = settlementLineDayDao.select()
                .eq("settlementPkey", fr.getSettlementKey())
                .eq("settlementDate", DateUtil.formatDateStr(str, "yyyyMMdd"))
                .eq("type", SettlementType.DOING)
                .exec();
            for (MktSettlementLineDay sl : settleDayList)
            {
                sl.setType(SettlementType.FAIL);
                sl.setRem(rem);
            }
            settlementLineDayDao.updateAll(settleDayList);
            // 修改每天总记录表的状态
            List<MktSettlementTotal> totalList =
                //                settlementTotalDao.getTimeTotal(date);
                settlementTotalDao.select().eq("settlementDate", DateUtil.formatDateStr(str, "yyyyMMdd")).exec();
            for (MktSettlementTotal t : totalList)
            {
                t.setType(SettlementType.FAIL);
            }
            settlementTotalDao.updateAll(totalList);
        }
        if (keyList.isEmpty()) return;
        
        // 修改每个商户 状态
        List<MktSettlementProcess> spList = new ArrayList<>();
        List<MktSettlementLine> settleList =
            settlementLineDao.select().in("settlementPkey", keyList.toArray()).eq("type", SettlementType.DOING).exec();
        for (MktSettlementLine sl : settleList)
        {
            sl.setType(SettlementType.FAIL);
            sl.setRem(rem);
            // 记录异常明细
            MktSettlementProcess sp = new MktSettlementProcess();
            sp.setSettlementKey(sl.getPkey());
            sp.setProcessNode(ProcessNode.FAIL);
            sp.setRem(rem);
            spList.add(sp);
        }
        settlementProcessDao.addAll(spList);
        settlementLineDao.updateAll(settleList);
        
        // 修改总报表 状态
        List<MktSettlement> exec = settlementDao.select().eq("type", SettlementType.DOING).in("pkey", keyList).exec();
        for (MktSettlement s : exec)
        {
            s.setType(SettlementType.FAIL);
            updVendorOrder(s.getPkey(), s.getType());
        }
        settlementDao.updateAll(exec);
    }
    
    @Transactional
    public void runSuccessFile()
    {
        // 最后跑批 跑这个  失败的状态全部已经处理好 剩下的全是成功的
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_WEEK, -1);
        List<MktSettlement> list = settlementDao.findByDate(cal.getTime());
        for (MktSettlement s : list)
        {
            s.setType(SettlementType.SUCCESS);
            updVendorOrder(s.getPkey(), s.getType());
        }
        settlementDao.updateAll(list);
        List<MktSettlementLine> lines = settlementLineDao.findByDate(cal.getTime());
        List<MktSettlementProcess> spList = new ArrayList<>();
        for (MktSettlementLine sl : lines)
        {
            sl.setType(SettlementType.SUCCESS);
            
            MktSettlementProcess sp = new MktSettlementProcess();
            sp.setSettlementKey(sl.getPkey());
            sp.setProcessNode(ProcessNode.SUCCESS);
            JSONObject content = new JSONObject(true);
            content.put("结算时间", DateUtil.formatDate(new Date()));
            sp.setContent(content.toString());
            spList.add(sp);
        }
        settlementLineDao.updateAll(lines);
        settlementProcessDao.addAll(spList);
        
        List<MktSettlementTotal> totalList = settlementTotalDao.getTimeTotal(cal.getTime());
        for (MktSettlementTotal t : totalList)
        {
            t.setType(SettlementType.SUCCESS);
        }
        settlementTotalDao.updateAll(totalList);
    }
    
    private void withdrawSettle(List<MktSettlementLineDay> settleList, ZxFileRecord record)
    {
        int fileFlag = 0;
        List<Integer> keyList = new ArrayList<>();
        List<Date> dateList = new ArrayList<>();
        for (MktSettlementLineDay sl : settleList)
        {
            Integer settlementPkey = sl.getSettlementPkey();
            Boolean flag = 
                appZxEqManager.runWithdraw(sl.getZxUserId(), sl.getBankcard(), sl.getBankuser(), sl.getAmt());
            if (flag)
            {
                sl.setType(SettlementType.SUCCESS);
                sl.setSelfMention(true);
            }
            else
            {
                keyList.add(settlementPkey);
                dateList.add(sl.getSettlementDate());
                sl.setType(SettlementType.FAIL);
                sl.setSelfMention(false);
                sl.setRem("提现失败");
                fileFlag += 1;
            }
        }
        // 提现成功 修改
        if (fileFlag == 0 && record != null)
        {
            record.setStatus(ZxFileStatus.WITHDRAW_FINISH);
            zxFileRecordDao.update(record);
        }
        settlementLineDayDao.updateAll(settleList);
        // 提现失败,修改对应的主表和合计明细
        if (!keyList.isEmpty())
        {
            keyList = keyList.stream().distinct().collect(Collectors.toList());
            List<MktSettlement> list = settlementDao.select().in("pkey", keyList.toArray()).exec();
            for (MktSettlement s : list)
            {
                s.setType(SettlementType.FAIL);
            }
            settlementDao.updateAll(list);
            List<MktSettlementLine> lineList =
                settlementLineDao.select().in("settlementPkey", keyList.toArray()).exec();
            List<MktSettlementProcess> spList = new ArrayList<>();
            String rem = "提现失败";
            for (MktSettlementLine line : lineList)
            {
                line.setType(SettlementType.FAIL);
                line.setRem(rem);
                MktSettlementProcess sp = new MktSettlementProcess();
                sp.setSettlementKey(line.getPkey());
                sp.setProcessNode(ProcessNode.FAIL);
                sp.setRem(rem);
                JSONObject content = new JSONObject(true);
                content.put("时间", DateUtil.formatDate(new Date()));
                content.put("失败说明", rem);
                sp.setContent(content.toString());
                
                spList.add(sp);
            }
            settlementLineDao.updateAll(lineList);
            settlementProcessDao.addAll(spList);
            
            List<MktSettlementTotal> totalList =
                settlementTotalDao.select().in("settlementDate", dateList).eq("type", SettlementType.DOING).exec();
            for (MktSettlementTotal t : totalList)
            {
                t.setType(SettlementType.FAIL);
            }
            settlementTotalDao.updateAll(totalList);
        }
        
    }
    
    private void updVendorOrder(Integer settleKey, SettlementType type)
    {
        List<MktVendorOrder> exec = vendorOrderDao.select().eq("settlementPkey", settleKey).exec();
        for (MktVendorOrder vo : exec)
        {
            vo.setStatus(type);
        }
        vendorOrderDao.updateAll(exec);
    }
    
}
