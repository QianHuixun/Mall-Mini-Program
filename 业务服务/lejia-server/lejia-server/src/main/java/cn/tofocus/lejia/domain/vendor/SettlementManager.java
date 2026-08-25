package cn.tofocus.lejia.domain.vendor;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;

import cn.tofocus.common.excel.ExcelHelper;
import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.common.util.CollectionUtil;
import cn.tofocus.common.util.PageUtil;
import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.core.data.NamedBean;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.page.PageParameter;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.vendor.BankInfo;
import cn.tofocus.lejia.bean.dto.vendor.ReportInfo;
import cn.tofocus.lejia.bean.dto.vendor.ReportOnList;
import cn.tofocus.lejia.bean.dto.vendor.SettlementInfo;
import cn.tofocus.lejia.bean.dto.vendor.SettlementLineOnList;
import cn.tofocus.lejia.bean.dto.vendor.SettlementProcess;
import cn.tofocus.lejia.bean.dto.vendor.VendorOrderInfo;
import cn.tofocus.lejia.bean.dto.vendor.VendorOrderOnList;
import cn.tofocus.lejia.bean.dto.vendor.VendorOrderSettleOnList;
import cn.tofocus.lejia.bean.dto.vendor.VendorSettleDateInfo;
import cn.tofocus.lejia.bean.entity.market.MktOrder;
import cn.tofocus.lejia.bean.entity.ns.MktNsPayLine;
import cn.tofocus.lejia.bean.entity.sys.SysFarmer;
import cn.tofocus.lejia.bean.entity.sys.SysFarmerConfig;
import cn.tofocus.lejia.bean.entity.vendor.MktSettlement;
import cn.tofocus.lejia.bean.entity.vendor.MktSettlementLine;
import cn.tofocus.lejia.bean.entity.vendor.MktSettlementProcess;
import cn.tofocus.lejia.bean.entity.vendor.MktVendor;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorOrder;
import cn.tofocus.lejia.bean.enums.CommissionType;
import cn.tofocus.lejia.bean.enums.ProcessNode;
import cn.tofocus.lejia.bean.enums.SettlementMethodType;
import cn.tofocus.lejia.bean.enums.SettlementType;
import cn.tofocus.lejia.bean.enums.v3.SettleSortType;
import cn.tofocus.lejia.bean.excel.ExportSettlementDoingLine;
import cn.tofocus.lejia.bean.excel.ExportSettlementLine;
import cn.tofocus.lejia.bean.excel.ExportVendorBill;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.market.MktOrderDao;
import cn.tofocus.lejia.dao.market.MktPayLineDao;
import cn.tofocus.lejia.dao.ns.MktNsPayLineDao;
import cn.tofocus.lejia.dao.sys.SysFarmerConfigDao;
import cn.tofocus.lejia.dao.sys.SysFarmerDao;
import cn.tofocus.lejia.dao.vendor.MktSettlementDao;
import cn.tofocus.lejia.dao.vendor.MktSettlementLineDao;
import cn.tofocus.lejia.dao.vendor.MktSettlementProcessDao;
import cn.tofocus.lejia.dao.vendor.MktVendorDao;
import cn.tofocus.lejia.dao.vendor.MktVendorOrderDao;
import cn.tofocus.lejia.exception.LejiaErrCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SettlementManager
{
    @Autowired
    private MktSettlementDao settlementDao;
    
    @Autowired
    private MktSettlementLineDao settlementLineDao;
    
    @Autowired
    private MktSettlementProcessDao settlementProcessDao;
    
    @Autowired
    private MktOrderDao orderDao;
    
    @Autowired
    private MktVendorOrderDao vendorOrderDao;
    
    @Autowired
    private MktVendorDao vendorDao;
    
    @Autowired
    private MktNsPayLineDao nsPayLineDao;
    
    @Autowired
    private SysFarmerDao sysFarmerDao;
    
    @Autowired
    private SysFarmerConfigDao sysFarmerConfigDao;
    
    @Autowired
    private MktPayLineDao payDao;
    
    @Autowired
    private ExcelHelper excelHelper;
    
    public ReportInfo getReport(int page, int pagesize, String startTime, String endTime, List<String> marketKeys,
        SettleSortType sortType, Boolean sort)
    {
        ReportInfo res = new ReportInfo();
        String marketPkey = CurrentSession.marketPkey();
        Integer ascription = CurrentSession.ascriptionPkey();
        if (!(Constant.Operation + ascription).equals(marketPkey))
        {
            marketKeys = new ArrayList<>();
            marketKeys.add(marketPkey);
        }
        checkTime(startTime, endTime, marketKeys);
        res.setTime(startTime + " - " + endTime);
        
        List<String> listCode;
        if (ascription.equals(1))
        {
            listCode = nsPayLineDao.listCode(startTime, endTime);
        }
        else
        {
            listCode = payDao.listCode(startTime, endTime);
        }
        if (listCode.isEmpty()) return res;
        List<Integer> listKey = orderDao.listKey(listCode, marketKeys);
        if (listKey.isEmpty()) return res;
        List<ReportOnList> reportList = vendorOrderDao.sumVendorPrice(listKey, ReportOnList.class);
        if (reportList.isEmpty()) return res;
        // 校验这段时间里 是否有数据结算  是否有采购订单未确认
        if (Boolean.FALSE.equals(
            checkOrder(DateUtil.atStartOfDay(startTime), DateUtil.atStartOfNextDay(endTime), marketKeys))) return res;
        List<Integer> vendorKeys = new ArrayList<>();
        Map<Integer, List<VendorOrderSettleOnList>> vos = getVOSVendor(listKey);
        for (ReportOnList r : reportList)
        {
            vendorKeys.add(r.getVendor());
            assemblyReportOnList(res, r, vos);
        }
        sortReport(reportList, sortType, sort);
        PageParameter parameter = new PageParameter(page, pagesize);
        PageResult<ReportOnList> lines = PageUtil.page(reportList, parameter);
        res.setLines(lines);
        return res;
    }
    
    @Transactional(rollbackFor = Exception.class)
    public synchronized boolean addReport(Date queryTime, String startTime, String endTime, String rem,
        List<String> marketKeys)
    {
        ReportInfo res = new ReportInfo();
        Integer ascription = CurrentSession.ascriptionPkey();
        String marketPkey = CurrentSession.marketPkey();
        if (!(Constant.Operation + ascription).equals(marketPkey))
        {
            marketKeys = new ArrayList<>();
            marketKeys.add(marketPkey);
        }
        checkTime(startTime, endTime, marketKeys);
        // 时间范围 应该order 表里取  然后根据code 读数据
        List<String> listCode;
        if (ascription.equals(1))
        {
            listCode = nsPayLineDao.listCode(startTime, endTime);
        }
        else
        {
            listCode = payDao.listCode(startTime, endTime);
        }
        List<Integer> listKey = orderDao.listKey(listCode, marketKeys);
        List<ReportOnList> reportList = vendorOrderDao.sumVendorPrice(listKey, ReportOnList.class);
        if (reportList.isEmpty())
        {
            log.info("vendorOrder查询出来没数据");
            return false;
        }
        Map<Integer, List<VendorOrderSettleOnList>> vos = getVOSVendor(listKey);
        // 校验这段时间里 是否有数据结算  是否有采购订单未确认
        if (Boolean.FALSE
            .equals(checkOrder(DateUtil.atStartOfDay(startTime), DateUtil.atStartOfNextDay(endTime), marketKeys)))
        {
            log.info("这段时间有采购订单未确认");
            return false;
        }
        
        Map<String, List<ReportOnList>> reportMap = new HashMap<>();
        for (ReportOnList r : reportList)
        {
            String market = r.getVendorInfo().getFarmer();
            String company = r.getVendorInfo().getCompany();
            String key = market + "___" + company;
            if (!reportMap.containsKey(key))
            {
                List<ReportOnList> va = new ArrayList<>();
                reportMap.put(key, va);
            }
            reportMap.get(key).add(r);
        }
        for (Map.Entry<String, List<ReportOnList>> entry : reportMap.entrySet())
        {
            List<Integer> vendorKeys = new ArrayList<>();
            List<ReportOnList> list = entry.getValue();
            List<MktSettlementLine> lines = new ArrayList<>();
            String key = entry.getKey();
            String[] split = key.split("___");
            String market = split[0];
            String company = split[1];
            for (ReportOnList r : list)
            {
                vendorKeys.add(r.getVendor());
                MktSettlementLine settlementLine = BeanUtil.beanFrom(MktSettlementLine.class, r.getVendorInfo());
                assemblyReportOnList(res, r, vos);
                settlementLine.setVendor(r.getVendor());
                settlementLine.setVendorName(r.getVendorInfo().getName());
                settlementLine.setBankuserIdentity(r.getVendorInfo().getZxIdentity());
                settlementLine.setCommission(r.getVendorInfo().getCommissionRate());
                settlementLine.setOrderAmt(r.getPurchaseAmt());
                settlementLine.setOrderCount(r.getPurchaseNum());
                settlementLine.setOrderCommission(r.getOrderComm());
                settlementLine.setAmt(r.getSettlementAmt());
                settlementLine.setType(SettlementType.SUCCESS);
                settlementLine.setAscription(ascription);
                lines.add(settlementLine);
            }
            MktSettlement settlement = BeanUtil.beanFrom(MktSettlement.class, res);
            settlement.setStartDate(startTime);
            settlement.setEndDate(endTime);
            settlement.setType(SettlementType.SUCCESS);
            settlement.setAscription(ascription);
            settlement.setFarmer(market);
            settlement.setCompany(company);
            settlementDao.add(settlement);
            lines.forEach(line -> line.setSettlementPkey(settlement.getPkey()));
            settlementLineDao.putAll(lines);
            lines.forEach(line -> {
                //结算查询记录
                MktSettlementProcess process = new MktSettlementProcess();
                process.setSettlementKey(line.getPkey());
                process.setProcessNode(ProcessNode.REPORT);
                process.setCreatedTime(queryTime);
                process.setAscription(ascription);
                settlementProcessDao.add(process);
                //结算申请记录
                MktSettlementProcess process2 = new MktSettlementProcess();
                process2.setSettlementKey(line.getPkey());
                process2.setProcessNode(ProcessNode.APPLY);
                JSONObject content = new JSONObject(true);
                content.put("商户名称", line.getVendorName());
                content.put("交易笔数", line.getOrderCount());
                content.put("佣金费率", line.getCommission().stripTrailingZeros().toPlainString() + "%");
                content.put("结算金额", line.getAmt().stripTrailingZeros().toPlainString() + "元");
                content.put("开户银行", line.getBankname());
                content.put("开户支行名称", line.getBankBranchName());
                content.put("开户人", line.getBankuser());
                content.put("开户人身份证号", line.getBankuserIdentity());
                content.put("银行卡号", line.getBankcard());
                content.put("银行卡绑定手机号", line.getBankuserMoblie());
                process2.setContent(content.toString());
                process2.setRem(rem);
                settlementProcessDao.add(process2);
            });
            //订单添加结算主键
            updVendorOrder(settlement.getPkey(), vendorKeys, listKey);
        }
        
        return true;
    }
    
    // 按照佣金费率进行计算 
    private void updVendorOrder(Integer settlementPkey, List<Integer> vendorKeys, List<Integer> keys)
    {
        Map<Integer, MktVendor> map = vendorDao.getZxVenodrMap(vendorKeys);
        List<MktVendorOrder> list = vendorOrderDao.getVendorOrderNotStart(keys);
        for (MktVendorOrder v : list)
        {
            v.setSettlementPkey(settlementPkey);
            v.setStatus(SettlementType.SUCCESS);
            if (map.containsKey(v.getVendor()))
            {
                MktVendor vendor = map.get(v.getVendor());
                BigDecimal totalPrice = v.getTotalPrice();
                // 佣金费率
                BigDecimal commissionRate = vendor.getCommissionRate();
                v.setCommissionRate(commissionRate);
                // 交易佣金数 = 总价 X 佣金费率（数据库没有记录百分号，所以要除100）
                BigDecimal comissions =
                    totalPrice.multiply(commissionRate).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_UP);
                v.setCommissions(comissions);
                // 采购金额 = 总价-交易佣金（结算金额小于0的设置为0）
                BigDecimal amt =
                    totalPrice.subtract(comissions).compareTo(new BigDecimal("0.00")) < 0 ? new BigDecimal("0.00")
                        : totalPrice.subtract(comissions);
                v.setAmt(amt);
            }
        }
        vendorOrderDao.updateAll(list);
    }
    
    /** <一句话功能简述>
     * <功能详细描述>
     * @param res
     * @param r
     * @param method
     * @return
     */
    
    private ReportOnList assemblyReportOnList(ReportInfo res, ReportOnList r,
        Map<Integer, List<VendorOrderSettleOnList>> vos)
    {
        //默认采购价
        BigDecimal settlementAmt = r.getPurchaseAmt();
        BigDecimal orderCommission = BigDecimal.ZERO;
        BigDecimal amt = BigDecimal.ZERO;
        if (vos.containsKey(r.getVendor()))
        {
            List<VendorOrderSettleOnList> list = vos.get(r.getVendor());
            for (VendorOrderSettleOnList vosBean : list)
            {
                orderCommission = orderCommission.add(vosBean.getCommissions());
                amt = amt.add(vosBean.getAmt());
            }
        }
        r.setOrderComm(orderCommission);
        r.setSettlementAmt(amt);
        
        res.setAmt(res.getAmt().add(r.getPurchaseAmt()));
        res.setAwaitAmt(res.getAwaitAmt().add(settlementAmt));
        res.setNum(res.getNum() + r.getPurchaseNum());
        res.setNumMerchant(res.getNumMerchant() + 1);
        return r;
    }
    
    // 判断是否是当前之前一天
    private void checkTime(String startDate, String endDate, List<String> marketKeys)
    {
        //判断开始时间和结束时间是否规范
        Date startTime = DateUtil.atStartOfDay(startDate);
        Date endTime = DateUtil.atStartOfDay(endDate);
        Date date = DateUtil.atStartOfToday();
        //开始日期不得大于结束日期
        if (startTime.compareTo(endTime) > 0) throw TofocusException.of(LejiaErrCode.TIME_ERROR);
        //日期只能D+1
        if (startTime.compareTo(date) >= 0 || endTime.compareTo(date) >= 0)
            throw TofocusException.of(LejiaErrCode.REPORT_TIME_ERROR);
        //查看日期是否存在结算记录
        MktSettlement settle = settlementDao.findByDate(startDate, endDate, marketKeys);
        if (settle != null) throw TofocusException.of(LejiaErrCode.SETTLE_ERROR);
        
        // 增加未确认的订单无法结算判断 2022-03-25
        Map<String, Long> map = orderDao.getNotPurchaseMap(CurrentSession.ascriptionPkey(), marketKeys);
        if (map != null)
        {
            String errorMsg = "";
            Calendar cal = Calendar.getInstance();
            cal.setTime(startTime);
            StringBuilder msg = new StringBuilder();
            while (cal.getTime().getTime() <= endTime.getTime())
            {
                if (map.containsKey(DateUtil.formatDate(cal.getTime(), "yyyy-MM-dd")))
                {
                    if (msg.length() > 0) msg.append("、");
                    msg.append(DateUtil.formatDate(cal.getTime(), "yyyy年MM月dd号"));
                }
                cal.add(Calendar.DAY_OF_MONTH, 1);
            }
            errorMsg = msg.toString();
            if (errorMsg.length() > 0)
            {
                throw TofocusException.of(LejiaErrCode.SETTLEMENT_DATE_ERROR, errorMsg + "存在未确定的订单无法结算");
            }
        }
        
    }
    
    public List<NamedBean> settlementList(String marketPkey, SettlementType type)
    {
        List<NamedBean> beans = new ArrayList<>();
        List<MktSettlement> list = settlementDao.findByMaket(marketPkey, type);
        list.forEach(x -> {
            String startTime = x.getStartDate().replace("-", "/");
            String endTime = x.getEndDate().replace("-", "/");
            beans.add(new NamedBean(x.getPkey(), startTime + "-" + endTime));
        });
        return beans;
    }
    
    public SettlementInfo queryLine(int page, int pagesize, List<String> marketKeys, String startTime, String endTime,
        SettleSortType sortType, Boolean sort)
    {
        SettlementInfo info = new SettlementInfo();
        String marketPkey = CurrentSession.marketPkey();
        Integer ascription = CurrentSession.ascriptionPkey();
        if (!(Constant.Operation + ascription).equals(marketPkey))
        {
            marketKeys = new ArrayList<>();
            marketKeys.add(marketPkey);
        }
        List<MktSettlement> settlements = settlementDao.findByPkeys(startTime, endTime, marketKeys, ascription);
        if (settlements.isEmpty()) return info;
        List<Integer> keys = new ArrayList<>();
        Map<Integer, MktSettlement> map = new HashMap<>();
        settlements.forEach(s -> {
            keys.add(s.getPkey());
            map.put(s.getPkey(), s);
        });
        List<SettlementLineOnList> lines = settlementLineDao.findByLineV4(keys, ascription, SettlementLineOnList.class);
        for (SettlementLineOnList line : lines)
        {
            Integer sk = line.getSettlementPkey();
            if (map.containsKey(sk))
            {
                MktSettlement settlement = map.get(sk);
                line.setTime(settlement.getStartDate() + "-" + settlement.getEndDate());
                line.setStartTime(settlement.getStartDate());
                line.setEndTime(settlement.getEndDate());
            }
            info.setNum(info.getNum() + line.getOrderCount());
            info.setAmt(info.getAmt().add(line.getOrderAmt()));
            info.setAwaitAmt(info.getAwaitAmt().add(line.getAmt()));
            SysFarmer farmer = sysFarmerDao.get(line.getFarmer());
            if (farmer != null) line.setMarketName(farmer.getName());
        }
        PageParameter parameter = new PageParameter(page, pagesize);
        sortLineExcel(lines, sortType, sort);
        PageResult<SettlementLineOnList> pageLines = PageUtil.page(lines, parameter);
        info.setNumMerchant(lines.size());
        info.setLines(pageLines);
        return info;
    }
    
    public List<SettlementProcess> process(Long linePkey)
    {
        List<SettlementProcess> process = settlementProcessDao.findByProcess(linePkey, SettlementProcess.class);
        return process;
    }
    
    public void export(List<String> marketKeys, String startTime, String endTime, SettleSortType sortType, Boolean sort,
        OutputStream outputStream)
    {
        try
        {
            ReportInfo res = new ReportInfo();
            Integer ascription = CurrentSession.ascriptionPkey();
            List<String> listCode;
            if (ascription.equals(1))
            {
                listCode = nsPayLineDao.listCode(startTime, endTime);
            }
            else
            {
                listCode = payDao.listCode(startTime, endTime);
            }
            if (listCode.isEmpty()) return;
            List<Integer> listKey = orderDao.listKey(listCode, marketKeys);
            
            List<ReportOnList> reportList = vendorOrderDao.sumVendorPrice(ReportOnList.class, listKey);
            
            List<ExportSettlementLine> lines = new ArrayList<>();
            Map<Integer, List<VendorOrderSettleOnList>> vos = getVOSVendor(listKey);
            int i = 1;
            for (ReportOnList r : reportList)
            {
                ExportSettlementLine settlementLine = BeanUtil.beanFrom(ExportSettlementLine.class, r.getVendorInfo());
                assemblyReportOnList(res, r, vos);
                settlementLine.setRank(String.valueOf(i));
                i++;
                settlementLine.setVendorName(r.getVendorInfo().getName());
                BigDecimal commissionRate = r.getVendorInfo().getCommissionRate();
                if (commissionRate == null) commissionRate = BigDecimal.ZERO;
                settlementLine.setCommission(commissionRate + "%");
                settlementLine.setCommissionRate(commissionRate);
                settlementLine.setOrderAmt(r.getPurchaseAmt());
                settlementLine.setOrderCount(r.getPurchaseNum());
                settlementLine.setOrderCommission(r.getOrderComm());
                settlementLine.setAmt(r.getSettlementAmt());
                lines.add(settlementLine);
            }
            sortReportExcel(lines, sortType, sort);
            String[] titles = new String[2];
            titles[0] = "商户结算";
            titles[1] = "报表日期: " + startTime + " - " + endTime + "            合计:   总交易笔数: " + res.getNum()
                + "笔      总交易金额: " + res.getAwaitAmtStr() + "元      待结算金额: " + res.getAwaitAmtStr() + "元";
            excelHelper.exportExcel(lines, "商户结算", outputStream, ExportSettlementLine.class, titles);
            outputStream.flush();
            outputStream.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (outputStream != null) try
            {
                outputStream.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            
        }
    }
    
    public void exportLine(List<String> marketKeys, String startTime, String endTime, SettleSortType sortType,
        Boolean sort, OutputStream outputStream)
    {
        try
        {
            SettlementInfo info = queryLine(0, 10000, marketKeys, startTime, endTime, sortType, sort);
            Class<?> excelModel = ExportSettlementDoingLine.class;
            String t1 = "已结算采购笔数： ";
            String t2 = "已结算采购金额： ";
            String[] titles = new String[2];
            titles[0] = "商户结算";
            titles[1] = "合计: " + t1 + info.getNum() + "笔       " + t2 + info.getAmtStr() + "元 ";
            List<ExportSettlementDoingLine> lines = new ArrayList<>();
            if (info.getLines() != null && !info.getLines().isEmpty())
            {
                int i = 1;
                for (SettlementLineOnList sl : info.getLines().getContent())
                {
                    ExportSettlementDoingLine line = BeanUtil.beanFrom(ExportSettlementDoingLine.class, sl);
                    line.setCreatedTime(DateUtil.formatDate(sl.getCreatedTime()));
                    line.setCommission(sl.getCommission().toString() + "%");
                    line.setRank(String.valueOf(i));
                    i++;
                    lines.add(line);
                }
                excelHelper.exportExcel(lines, "商户结算", outputStream, excelModel, titles);
            }
            else
                excelHelper.exportExcel(lines, "商户结算", outputStream, excelModel, titles);
            outputStream.flush();
            outputStream.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (outputStream != null) try
            {
                outputStream.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            
        }
    }
    
    public VendorOrderInfo check(int page, int pagesize, String startDate, String endDate, String startSettlementDate,
        String endSettlementDate, String startVendorTime, String endVendorTime, String vendorName, String booth,
        String code, List<SettlementType> status, String marketPkey, Integer ascription)
    {
        VendorOrderInfo info = new VendorOrderInfo();
        List<Integer> vendor = new ArrayList<>();
        List<Integer> orderPkeys = new ArrayList<>();
        if (StringUtils.isNotBlank(code))
        {
            List<MktOrder> list = orderDao.select().like("code", code).exec();
            if (list == null || list.isEmpty())
            {
                info.setLines(PageUtil.page(new ArrayList<>(), PageParameter.of(page, pagesize)));
                return info;
            }
            list.forEach(e -> orderPkeys.add(e.getPkey()));
        }
        if (StringUtils.isNotBlank(vendorName) || StringUtils.isNotBlank(booth))
        {
            List<Integer> byNameAndBooth = vendorDao.byNameAndBooth(vendorName, booth, marketPkey, ascription);
            if (byNameAndBooth == null || byNameAndBooth.isEmpty())
            {
                info.setLines(PageUtil.page(new ArrayList<>(), PageParameter.of(page, pagesize)));
                return info;
            }
            vendor.addAll(byNameAndBooth);
        }
        List<VendorOrderOnList> list = vendorOrderDao.findByLine(startDate,
            endDate,
            startSettlementDate,
            endSettlementDate,
            startVendorTime,
            endVendorTime,
            vendor,
            orderPkeys,
            status,
            marketPkey,
            ascription,
            VendorOrderOnList.class);
        Map<Integer, Integer> repeat = new HashMap<>();
        list.forEach(x -> {
            if (!repeat.containsKey(x.getOrderPkey()))
            {
                repeat.put(x.getOrderPkey(), 1);
                info.setOrderNum(info.getOrderNum() + 1);
            }
            info.setAmt(info.getAmt().add(x.getTotalPrice()));
            info.setTotalAmt(info.getTotalAmt().add(x.getAmt()));
            info.setGoodsTotalPrice(info.getGoodsTotalPrice().add(x.getGoodsTotalPrice()));
        });
        info.setNum(list.size());
        PageResult<VendorOrderOnList> pageLines = PageUtil.page(list, PageParameter.of(page, pagesize));
        for (VendorOrderOnList vo : pageLines.getContent())
        {
            //            if(vo.getTotalPrice().compareTo(vo.getAmt()) < 0)
            //                vo.setTotalPrice(vo.getAmt());
            BigDecimal subtract = null;
            if (vo.getProcureRefundAmt() != null)
            {
                subtract = vo.getTotalPrice().subtract(vo.getProcureRefundAmt());
            }
            boolean f = subtract != null && subtract.compareTo(BigDecimal.ZERO) == 0;
            if (vo.getPackingCharge() != null && !f)
            {
                vo.setAmt(vo.getAmt().add(vo.getPackingCharge()));
            }
            vo.setNeedAmt(vo.getAmt());
            if (vo.getCommissions() != null) vo.setNeedAmt(vo.getNeedAmt().add(vo.getCommissions()));
//            if (vo.getDiscountRefundAmt() != null) vo.setRefundAmt(vo.getRefundAmt().add(vo.getDiscountRefundAmt()));
            if (CommissionType.MERCHANT.equals(vo.getCommissionType()) && vo.getPayComm() != null)
                vo.setAmt(vo.getAmt().subtract(vo.getPayComm()));
            if(!CommissionType.MERCHANT.equals(vo.getCommissionType()))
                vo.setPayComm(BigDecimal.ZERO);
        }
        if (marketPkey != null)
        {
            SysFarmerConfig farmerConfig = sysFarmerConfigDao.get(marketPkey);
            if (farmerConfig != null && farmerConfig.getSettlementMethod() != null
                && SettlementMethodType.PURCHASE_SETTLEMENT.equals(farmerConfig.getSettlementMethod()))
            {
                for (VendorOrderOnList vo : pageLines.getContent())
                {
                    if (vo.getCommissionRate().compareTo(BigDecimal.ZERO) < 0) vo.setCommissionRate(null);
                }
            }
        }
        info.setLines(pageLines);
        return info;
    }
    
    public List<VendorSettleDateInfo> getDate(List<String> marketKeys)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        String marketPkey = CurrentSession.marketPkey();
        if (!(Constant.Operation + ascription).equals(marketPkey))
        {
            marketKeys = new ArrayList<>();
            marketKeys.add(marketPkey);
        }
        List<MktSettlement> exec = settlementDao.select()
            .notEq("type", SettlementType.AWAIT_CONFIRM)
            .notEq("type", SettlementType.NOT_START)
            .in("farmer", marketKeys)
            .eq("ascription", ascription)
            .sort("startDate", false)
            .exec();
        List<VendorSettleDateInfo> res = new ArrayList<>();
        exec.forEach(e -> {
            VendorSettleDateInfo info = new VendorSettleDateInfo();
            info.setStart(DateUtil.formatDateStr(e.getStartDate(), "yyyy-MM-dd").getTime());
            info.setEnd(DateUtil.formatDateStr(e.getEndDate(), "yyyy-MM-dd").getTime());
            info.setColour(false);
            res.add(info);
        });
        List<VendorSettleDateInfo> list = orderDao.getNotPurchase(ascription, marketKeys);
        res.addAll(list);
        return res;
    }
    
    public void exportVendorBill(String startDate, String endDate, String startSettlementDate, String endSettlementDate,
        String startVendorTime, String endVendorTime, String vendorName, String booth, String code,
        List<SettlementType> status, String marketPkey, Integer ascription, ServletOutputStream outputStream)
    {
        try
        {
            VendorOrderInfo info = check(0,
                50000,
                startDate,
                endDate,
                startSettlementDate,
                endSettlementDate,
                startVendorTime,
                endVendorTime,
                vendorName,
                booth,
                code,
                status,
                marketPkey,
                ascription);
            
            String[] titles = new String[2];
            if (info.getLines() == null || info.getLines().getContent() == null)
            {
                titles[0] = "商户对账单";
                titles[1] = "            合计:   总订单数： " + info.getOrderNum() + "笔     总采购数：" + info.getNum()
                    + "件     商品总价： " + info.getGoodsTotalPrice() + "元     总采购金额数： " + info.getAmt() + "元     总结算金额： "
                    + info.getTotalAmt() + "元";
                excelHelper.exportExcel(new ArrayList<>(), "商户结算", outputStream, ExportVendorBill.class, titles);
                outputStream.flush();
                outputStream.close();
                return;
            }
            titles[0] = "商户对账单";
            titles[1] = "            合计:   总订单数： " + info.getOrderNum() + "笔     总采购数：" + info.getNum() + "件     商品总价： "
                + info.getGoodsTotalPrice() + "元     总采购金额数： " + info.getAmt() + "元     总结算金额： " + info.getTotalAmt()
                + "元";
            List<ExportVendorBill> lines = new ArrayList<>();
            for(VendorOrderOnList vo : info.getLines().getContent())
            {
                ExportVendorBill beanFrom = BeanUtil.beanFrom(ExportVendorBill.class, vo);
                beanFrom.setEndDate(DateUtil.formatDate(vo.getEndDate()));
                lines.add(beanFrom);
            }
//            List<ExportVendorBill> lines = BeanUtil.beanListFrom(ExportVendorBill.class, info.getLines().getContent());
            excelHelper.exportExcel(lines, "商户结算", outputStream, ExportVendorBill.class, titles);
            outputStream.flush();
            outputStream.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (outputStream != null) try
            {
                outputStream.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            
        }
    }
    
    //    public void exportVendorBill(String startDate, String endDate,
    //        String startSettlementDate, String endSettlementDate, String startVendorTime, String endVendorTime,
    //        String vendorName, String booth, String code, List<SettlementType> status, String marketPkey, Integer ascription, ServletOutputStream outputStream)
    //    {
    //        try
    //        {
    //            VendorOrderInfo info = new VendorOrderInfo();
    //            List<Integer> vendor = new ArrayList<>();
    //            List<Integer> orderPkeys = new ArrayList<>();
    //            if(StringUtils.isNotBlank(code))
    //            {
    //                List<MktOrder> list = orderDao.select().like("code", code).exec();
    //                if(list == null || list.isEmpty())
    //                {
    //                    String[] titles = new String[2];
    //                    titles[0] = "商户对账单";
    //                    titles[1] = "            合计:   总订单数： " + info.getOrderNum() + "笔     总采购数：" 
    //                        + info.getNum() + "件     商品总价： " + info.getGoodsTotalPrice() + "元     总采购金额数： " 
    //                        + info.getAmt() + "元     总结算金额： " + info.getTotalAmt() + "元";
    //                    excelHelper.exportExcel(new ArrayList<>(), "商户结算", outputStream, ExportVendorBill.class, titles);
    //                    outputStream.flush();
    //                    outputStream.close();
    //                    return;
    //                }
    //                list.forEach(e -> orderPkeys.add(e.getPkey()));
    //            }
    //            if(StringUtils.isNotBlank(vendorName) || StringUtils.isNotBlank(booth))
    //            {
    //                List<Integer> byNameAndBooth = vendorDao.byNameAndBooth(vendorName, booth, marketPkey, ascription);
    //                if(byNameAndBooth == null  || byNameAndBooth.isEmpty())
    //                {
    //                    String[] titles = new String[2];
    //                    titles[0] = "商户对账单";
    //                    titles[1] = "            合计:   总订单数： " + info.getOrderNum() + "笔     总采购数：" 
    //                        + info.getNum() + "件     商品总价： " + info.getGoodsTotalPrice() + "元     总采购金额数： " 
    //                        + info.getAmt() + "元     总结算金额： " + info.getTotalAmt() + "元";
    //                    excelHelper.exportExcel(new ArrayList<>(), "商户结算", outputStream, ExportVendorBill.class, titles);
    //                    outputStream.flush();
    //                    outputStream.close();
    //                    return;
    //                }
    //                vendor.addAll(byNameAndBooth);
    //            }
    //            List<BankInfo> vendors = vendorDao.findByMarket(marketPkey, vendor, BankInfo.class);
    //            Map<Integer, BankInfo> map =
    //                vendors.stream().collect(Collectors.toMap(BankInfo::getPkey, Function.identity()));
    //            List<VendorOrderOnList> list = vendorOrderDao.findByLine(
    //                startDate,
    //                endDate,
    //                startSettlementDate,
    //                endSettlementDate,
    //                startVendorTime,
    //                endVendorTime,
    //                vendor,
    //                orderPkeys,
    //                status,
    //                marketPkey,
    //                ascription,
    //                VendorOrderOnList.class);
    //            List<ExportVendorBill> lines = new ArrayList<>();
    //            Map<Integer,Integer> repeat = new HashMap<>();
    //            list.forEach(x -> {
    //                ExportVendorBill evb = BeanUtil.beanFrom(ExportVendorBill.class, x);
    //                evb.setStartDate(DateUtil.formatDate(x.getStartDate()));
    //                evb.setEndDate(DateUtil.formatDate(x.getEndDate()));
    //                evb.setOriginallyAmt(evb.getAmt());
    //                if(x.getPackingCharge() != null)
    //                    evb.setOriginallyAmt(evb.getOriginallyAmt().add(x.getPackingCharge()));
    //                lines.add(evb);
    //                if(!repeat.containsKey(x.getOrderPkey()))
    //                {
    //                    repeat.put(x.getOrderPkey(), 1);
    //                    info.setOrderNum(info.getOrderNum() + 1);
    //                }
    //                info.setAmt(info.getAmt().add(x.getTotalPrice()));
    //            });
    //            info.setNum(list.size());
    //            String[] titles = new String[2];
    //            titles[0] = "商户对账单";
    //            titles[1] = "            合计:   总订单数： " + info.getOrderNum() + "笔     总采购数：" 
    //                + info.getNum() + "件     商品总价： " + info.getGoodsTotalPrice() + "元     总采购金额数： " 
    //                + info.getAmt() + "元     总结算金额： " + info.getTotalAmt() + "元";
    ////            List<ExportVendorBill> lines = new ArrayList<>();
////                BeanUtil.beanListFrom(ExportVendorBill.class, list);
    //            if(marketPkey != null)
    //            {
    //                SysFarmerConfig farmerConfig = sysFarmerConfigDao.get(marketPkey);
    //                if(farmerConfig != null && farmerConfig.getSettlementMethod() != null 
    //                    && SettlementMethodType.PURCHASE_SETTLEMENT.equals(farmerConfig.getSettlementMethod()))
    //                {
    //                    for(VendorOrderOnList vo : list)
    //                    {
    //                        if(vo.getCommissionRate().compareTo(BigDecimal.ZERO) == 0)
    //                            vo.setCommissionRate(null);
    //                    }
    //                }
    //            }
    //            lines.forEach(x -> {
    //                BankInfo bankInfo = map.get(x.getVendor());
    //                if (bankInfo != null) BeanUtils.copyProperties(bankInfo, x);
    //            });
    //            excelHelper.exportExcel(lines, "商户结算", outputStream, ExportVendorBill.class, titles);
    //            outputStream.flush();
    //            outputStream.close();
    //        }
    //        catch (Exception e)
    //        {
    //            e.printStackTrace();
    //        }
    //        finally
    //        {
    //            if (outputStream != null) try
    //            {
    //                outputStream.close();
    //            }
    //            catch (IOException e)
    //            {
    //                e.printStackTrace();
    //            }
    //            
    //        }
    //    }
    
    public BankInfo getBankInfo(Integer vendorPkey)
    {
        MktVendor vendor = vendorDao.get(vendorPkey);
        BankInfo info = BeanUtil.beanFrom(BankInfo.class, vendor);
        return info;
    }
    
    private Boolean checkOrder(Date startDate, Date endDate, List<String> marketKeys)
    {
        Boolean checkFarTimeConfirm = orderDao.checkFarTimeConfirm(startDate, endDate, marketKeys);
        if (!checkFarTimeConfirm) throw TofocusException.of(LejiaErrCode.SETTLEMENT_ORDER_ERROR);
        List<Integer> keyList = orderDao.querKeyList(startDate, endDate, CurrentSession.ascriptionPkey(), marketKeys);
        if (keyList.isEmpty()) return false;
        checkFarTimeConfirm = vendorOrderDao.checkFarTimeConfirm(keyList);
        if (!checkFarTimeConfirm) throw TofocusException.of(LejiaErrCode.SETTLEMENT_ORDER_ERROR);
        return true;
    }
    
    // 修改采购订单佣金分成
    public void runVendorOrderCommissionRate(Date date)
    {
        List<MktVendor> exec =
            vendorDao.select().eq("settlementMethod", SettlementMethodType.COMMISSION_SETTLEMENT).exec();
        if (exec.isEmpty()) return;
        Map<Integer, BigDecimal> map = new HashMap<>();
        exec.forEach(e -> {
            map.put(e.getPkey(), e.getCommissionRate());
        });
        List<Integer> keyList = CollectionUtil.keyList(exec);
        List<MktVendorOrder> orderList =
            vendorOrderDao.select().in("vendor", keyList.toArray()).gt("createdTime", date).exec();
        List<MktSettlementLine> settleLines = settlementLineDao.select().in("vendor", keyList.toArray()).exec();
        Map<Integer, BigDecimal> settleMap = new HashMap<>();
        settleLines.forEach(e -> {
            settleMap.put(e.getVendor(), e.getCommission());
        });
        for (MktVendorOrder v : orderList)
        {
            Integer vendor = v.getVendor();
            if (SettlementType.SUCCESS.equals(v.getStatus()))
            {
                if (settleMap.containsKey(vendor)) v.setCommissionRate(settleMap.get(vendor));
            }
            else
            {
                if (map.containsKey(vendor)) v.setCommissionRate(map.get(vendor));
            }
        }
        vendorOrderDao.updateAll(orderList);
    }
    
    private Map<Integer, List<VendorOrderSettleOnList>> getVOSVendor(List<Integer> listKey)
    {
        List<MktVendorOrder> data = vendorOrderDao.getVendorOrderNotStart(listKey);
        List<Integer> keys = new ArrayList<>();
        data.forEach(e -> keys.add(e.getVendor()));
        Map<Integer, MktVendor> map = vendorDao.getMapVendor(keys);
        List<VendorOrderSettleOnList> list = BeanUtil.beanListFrom(VendorOrderSettleOnList.class, data);
        Map<Integer, MktOrder> orderMap = orderDao.getMap(listKey);
        List<String> ts = new ArrayList<>();
        orderMap.values().forEach(e -> {
            String code = e.getCode();
            ts.add(code.substring(0, code.length() - 1));
        });
        List<MktNsPayLine> nsLines = nsPayLineDao.select().in("outTradeNo", ts).exec();
        Map<String, String> zpMap = new HashMap<>();
        nsLines.forEach(e -> zpMap.put(e.getOutTradeNo(), e.getTransactionId()));
        for (VendorOrderSettleOnList o : list)
        {
            if (map.containsKey(o.getVendor())) o.setZxUserId(map.get(o.getVendor()).getZxUserId());
            if (orderMap.containsKey(o.getOrderPkey()))
            {
                MktOrder mktOrder = orderMap.get(o.getOrderPkey());
                String code = mktOrder.getCode();
                o.setCode(code.substring(0, code.length() - 1));
                o.setAmtn(mktOrder.getAmtn());
                o.setCardAmt(mktOrder.getCardAmt());
            }
            if (zpMap.containsKey(o.getCode())) o.setTransactionId(zpMap.get(o.getCode()));
        }
        Map<Integer, List<VendorOrderSettleOnList>> res = new HashMap<>();
        list.forEach(e -> {
            Integer vendor = e.getVendor();
            if (!res.containsKey(vendor))
            {
                List<VendorOrderSettleOnList> value = new ArrayList<>();
                res.put(vendor, value);
            }
            res.get(vendor).add(e);
        });
        return res;
    }
    
    private void sortReport(List<ReportOnList> reportList, SettleSortType sortType, Boolean sort)
    {
        if (sortType != null)
        {
            switch (sortType)
            {
                case ORDERCOUNT_SORT:
                    Collections.sort(reportList, new Comparator<ReportOnList>()
                    {
                        @Override
                        public int compare(ReportOnList o1, ReportOnList o2)
                        {
                            if (Boolean.TRUE.equals(sort))
                                return o1.getPurchaseNum() - o2.getPurchaseNum();
                            else
                                return o2.getPurchaseNum() - o1.getPurchaseNum();
                        }
                    });
                    
                    break;
                case ORDERAMT_SORT:
                    Collections.sort(reportList, new Comparator<ReportOnList>()
                    {
                        @Override
                        public int compare(ReportOnList o1, ReportOnList o2)
                        {
                            if (Boolean.TRUE.equals(sort))
                                return o1.getPurchaseAmt().compareTo(o2.getPurchaseAmt());
                            else
                                return o2.getPurchaseAmt().compareTo(o1.getPurchaseAmt());
                        }
                    });
                    break;
                case COMMISSION_SORT:
                    Collections.sort(reportList, new Comparator<ReportOnList>()
                    {
                        @Override
                        public int compare(ReportOnList o1, ReportOnList o2)
                        {
                            if (Boolean.TRUE.equals(sort))
                                return o1.getVendorInfo()
                                    .getCommissionRate()
                                    .compareTo(o2.getVendorInfo().getCommissionRate());
                            else
                                return o2.getVendorInfo()
                                    .getCommissionRate()
                                    .compareTo(o1.getVendorInfo().getCommissionRate());
                        }
                    });
                    break;
                case ORDERCOMMISSION_SORT:
                    Collections.sort(reportList, new Comparator<ReportOnList>()
                    {
                        @Override
                        public int compare(ReportOnList o1, ReportOnList o2)
                        {
                            if (Boolean.TRUE.equals(sort))
                                return o1.getOrderComm().compareTo(o2.getOrderComm());
                            else
                                return o2.getOrderComm().compareTo(o1.getOrderComm());
                        }
                    });
                    break;
                case AMT:
                    Collections.sort(reportList, new Comparator<ReportOnList>()
                    {
                        @Override
                        public int compare(ReportOnList o1, ReportOnList o2)
                        {
                            if (Boolean.TRUE.equals(sort))
                                return o1.getSettlementAmt().compareTo(o2.getSettlementAmt());
                            else
                                return o2.getSettlementAmt().compareTo(o1.getSettlementAmt());
                        }
                    });
                    break;
                
                default:
                    break;
            }
        }
    }
    
    private void sortReportExcel(List<ExportSettlementLine> reportList, SettleSortType sortType, Boolean sort)
    {
        if (sortType != null)
        {
            switch (sortType)
            {
                case ORDERCOUNT_SORT:
                    Collections.sort(reportList, new Comparator<ExportSettlementLine>()
                    {
                        @Override
                        public int compare(ExportSettlementLine o1, ExportSettlementLine o2)
                        {
                            if (Boolean.TRUE.equals(sort))
                                return o1.getOrderCount() - o2.getOrderCount();
                            else
                                return o2.getOrderCount() - o1.getOrderCount();
                        }
                    });
                    
                    break;
                case ORDERAMT_SORT:
                    Collections.sort(reportList, new Comparator<ExportSettlementLine>()
                    {
                        @Override
                        public int compare(ExportSettlementLine o1, ExportSettlementLine o2)
                        {
                            if (Boolean.TRUE.equals(sort))
                                return o1.getOrderAmt().compareTo(o2.getOrderAmt());
                            else
                                return o2.getOrderAmt().compareTo(o1.getOrderAmt());
                        }
                    });
                    break;
                case COMMISSION_SORT:
                    Collections.sort(reportList, new Comparator<ExportSettlementLine>()
                    {
                        @Override
                        public int compare(ExportSettlementLine o1, ExportSettlementLine o2)
                        {
                            if (Boolean.TRUE.equals(sort))
                                return o1.getCommissionRate().compareTo(o2.getCommissionRate());
                            else
                                return o2.getCommissionRate().compareTo(o1.getCommissionRate());
                        }
                    });
                    break;
                case ORDERCOMMISSION_SORT:
                    Collections.sort(reportList, new Comparator<ExportSettlementLine>()
                    {
                        @Override
                        public int compare(ExportSettlementLine o1, ExportSettlementLine o2)
                        {
                            if (Boolean.TRUE.equals(sort))
                                return o1.getOrderCommission().compareTo(o2.getOrderCommission());
                            else
                                return o2.getOrderCommission().compareTo(o1.getOrderCommission());
                        }
                    });
                    break;
                case AMT:
                    Collections.sort(reportList, new Comparator<ExportSettlementLine>()
                    {
                        @Override
                        public int compare(ExportSettlementLine o1, ExportSettlementLine o2)
                        {
                            if (Boolean.TRUE.equals(sort))
                                return o1.getAmt().compareTo(o2.getAmt());
                            else
                                return o2.getAmt().compareTo(o1.getAmt());
                        }
                    });
                    break;
                
                default:
                    break;
            }
        }
    }
    
    private void sortLineExcel(List<SettlementLineOnList> reportList, SettleSortType sortType, Boolean sort)
    {
        if (sortType != null)
        {
            switch (sortType)
            {
                case ORDERCOUNT_SORT:
                    Collections.sort(reportList, new Comparator<SettlementLineOnList>()
                    {
                        @Override
                        public int compare(SettlementLineOnList o1, SettlementLineOnList o2)
                        {
                            if (Boolean.TRUE.equals(sort))
                                return o1.getOrderCount() - o2.getOrderCount();
                            else
                                return o2.getOrderCount() - o1.getOrderCount();
                        }
                    });
                    
                    break;
                case ORDERAMT_SORT:
                    Collections.sort(reportList, new Comparator<SettlementLineOnList>()
                    {
                        @Override
                        public int compare(SettlementLineOnList o1, SettlementLineOnList o2)
                        {
                            if (Boolean.TRUE.equals(sort))
                                return o1.getOrderAmt().compareTo(o2.getOrderAmt());
                            else
                                return o2.getOrderAmt().compareTo(o1.getOrderAmt());
                        }
                    });
                    break;
                case COMMISSION_SORT:
                    Collections.sort(reportList, new Comparator<SettlementLineOnList>()
                    {
                        @Override
                        public int compare(SettlementLineOnList o1, SettlementLineOnList o2)
                        {
                            if (Boolean.TRUE.equals(sort))
                                return o1.getCommission().compareTo(o2.getCommission());
                            else
                                return o2.getCommission().compareTo(o1.getCommission());
                        }
                    });
                    break;
                case ORDERCOMMISSION_SORT:
                    Collections.sort(reportList, new Comparator<SettlementLineOnList>()
                    {
                        @Override
                        public int compare(SettlementLineOnList o1, SettlementLineOnList o2)
                        {
                            if (Boolean.TRUE.equals(sort))
                                return o1.getOrderCommission().compareTo(o2.getOrderCommission());
                            else
                                return o2.getOrderCommission().compareTo(o1.getOrderCommission());
                        }
                    });
                    break;
                case AMT:
                    Collections.sort(reportList, new Comparator<SettlementLineOnList>()
                    {
                        @Override
                        public int compare(SettlementLineOnList o1, SettlementLineOnList o2)
                        {
                            if (Boolean.TRUE.equals(sort))
                                return o1.getAmt().compareTo(o2.getAmt());
                            else
                                return o2.getAmt().compareTo(o1.getAmt());
                        }
                    });
                    break;
                
                default:
                    break;
            }
        }
    }
    
}
