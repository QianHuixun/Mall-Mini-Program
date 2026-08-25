package cn.tofocus.lejia.api.v1.market;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.common.excel.ExcelHelper;
import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.core.Result;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.log.LogApi;
import cn.tofocus.db.redis.lock.RedisLockTemplate;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.EnumNameDTO;
import cn.tofocus.lejia.bean.dto.PkeyNameDTO;
import cn.tofocus.lejia.bean.dto.VendorOrderReportExcel;
import cn.tofocus.lejia.bean.dto.app.market.MktVendorGoodsDTO;
import cn.tofocus.lejia.bean.dto.app.market.MktVendorGoodsPriceDTO;
import cn.tofocus.lejia.bean.dto.order.MktVendorOrderDTO;
import cn.tofocus.lejia.bean.dto.order.MktVendorOrderExcelLong;
import cn.tofocus.lejia.bean.dto.order.MktVendorOrderExcelShort;
import cn.tofocus.lejia.bean.dto.order.MktVendorOrderMainDTO;
import cn.tofocus.lejia.bean.dto.order.MktVendorOrderParamDTO;
import cn.tofocus.lejia.bean.dto.order.MktVendorParamDTO;
import cn.tofocus.lejia.bean.dto.order.RevokeDTO;
import cn.tofocus.lejia.bean.dto.order.RevokeExcelLong;
import cn.tofocus.lejia.bean.dto.order.RevokeExcelShort;
import cn.tofocus.lejia.bean.dto.order.RevokeMainDTO;
import cn.tofocus.lejia.bean.dto.order.SettlementDTO;
import cn.tofocus.lejia.bean.dto.order.SettlementDetailDTO;
import cn.tofocus.lejia.bean.dto.order.SettlementExcelLShort;
import cn.tofocus.lejia.bean.dto.order.SettlementExcelLong;
import cn.tofocus.lejia.bean.dto.order.SettlementMainDTO;
import cn.tofocus.lejia.bean.dto.order.VendorOrderInfo;
import cn.tofocus.lejia.bean.dto.order.VendorOrderReport;
import cn.tofocus.lejia.bean.entity.refund.MktOrderRefundLine;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorOrder;
import cn.tofocus.lejia.bean.enums.DataEnums;
import cn.tofocus.lejia.bean.enums.PurchaseStatus;
import cn.tofocus.lejia.bean.enums.SettlementMethodType;
import cn.tofocus.lejia.bean.enums.SettlementType;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.refund.MktOrderRefundLineDao;
import cn.tofocus.lejia.dao.sys.SysFarmerConfigDao;
import cn.tofocus.lejia.dao.vendor.MktVendorOrderDao;
import cn.tofocus.lejia.domain.market.VendorOrderManager;
import cn.tofocus.lejia.exception.LejiaErrCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.extern.slf4j.Slf4j;

@RequestMapping("/v1/market/vendorOrder")
@RestController
@Slf4j
public class MktVendorOrderApiImpl implements MktVendorOrderApi
{
    
    @Autowired
    private VendorOrderManager manager;
    
    @Resource
    private ExcelHelper excelHelper;
    
    @Autowired
    private MktOrderRefundLineDao orderRefundLineDao;
    
    /**
     * sys_farmer_config表
     */
    @Resource
    private SysFarmerConfigDao sysFarmerConfigDao;
    
    /**
     * 格式：2021-08-12 10:28:39
     */
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Override
    public Result<List<MktVendorOrderDTO>> loadOrder(Integer pkey)
    {
        return new Result<>(manager.queryOrder(pkey));
    }
    
    @Override
    public Result<List<MktVendorGoodsDTO>> loadVendor(Integer pkey)
    {
        return new Result<>(manager.loadVendor(pkey));
    }
    
    @Override
    public Result<List<MktVendorGoodsPriceDTO>> loadVendorV2(Integer pkey)
    {
        return new Result<>(manager.loadVendorV2(pkey));
    }
    
    @Override
    public Result<Boolean> checkOrder(MktVendorOrderMainDTO info)
    {
//        manager.checkOrder(info.getOrderPkey(), info.getList());
        manager.checkOrderUnlimited(info.getOrderPkey(), info.getList());
        return new Result<>(true);
    }
    
    @Autowired
    private MktVendorOrderDao vendorOrderDao;
    
    @Autowired
    private RedisLockTemplate rlock;
    
    @Override
    public Result<Boolean> againPurchase(MktVendorOrderDTO info)
    {
        Boolean res = false;
        String lockKey = info.getPkey().toString();
        try
        {
            rlock.lock("againPurchase-vendorOrder", lockKey);
            MktVendorOrder vendorOrder = vendorOrderDao.get(info.getPkey());
            if (vendorOrder == null) return  new Result<>(res);
            PurchaseStatus purchaseStatus = vendorOrder.getPurchaseStatus();
            if (purchaseStatus != null && purchaseStatus.getIndex() > 2)
            {
                throw TofocusException.of(LejiaErrCode.PURCHASESTATUS_ERROR);
            }
            MktOrderRefundLine orderLinePkey = orderRefundLineDao.getOrderLinePkey(vendorOrder.getOrderLinePkey());
            if(orderLinePkey != null)
                throw TofocusException.of(LejiaErrCode.VENDOR_ORDER_REFUND_ERROR);
            res = manager.againPurchase(info, vendorOrder);
        }
        finally
        {
            rlock.unlock("againPurchase-vendorOrder", lockKey);
        }
        return new Result<>(res);
    }
    
    @Override
    public Result<Boolean> confirmPurchase(List<Integer> pkeys)
    {
        return new Result<>(manager.confirmPurchase(pkeys));
    }
    
    @Override
    public Result<Boolean> confirmPurchaseRun()
    {
        manager.confirmPurchaseRun();
        return new Result<>(true);
    }
    
    @Override
    public Result<VendorOrderInfo> clistPurchase(Integer pkey)
    {
        return new Result<>(manager.clistPurchase(pkey));
    }
    
    /**
     * 商户对账分页数据
     */
    @Override
    public Result<MktVendorOrderMainDTO> queryOrder(int page, int pagesize, List<Integer> vendor, String startDate,
        String endDate, List<SettlementType> status)
    {
        return new Result<>(
            manager.queryVendorOrder(null, page, pagesize, vendor, startDate, endDate, status, true, true, CurrentSession.ascriptionPkey()));
    }
    
    /**
     * 商户列表
     * @return  结果
     */
    @Override
    public Result<List<PkeyNameDTO>> vendorList()
    {
        return new Result<>(manager.vendorList());
    }
    
    /**
     * 结算状态枚举列表
     * @return     结果
     */
    @Override
    public Result<List<EnumNameDTO>> statusList()
    {
        return new Result<>(manager.statusList());
    }
    
    /**
     * 采购方式
     * @return     结果
     */
    @Override
    public Result<EnumNameDTO> settlementMethod(String pkey)
    {
        return new Result<>(manager.settlementMethod(pkey));
    }
    
    /**
     * 导出商户对账列表
     */
    @Override
    @LogApi(operation = "导出商户对账列表", format = "导出商户对账列表")
    public void export(List<Integer> pkeys, List<Integer> vendor, String startDate, String endDate,
        List<SettlementType> status, HttpServletResponse response)
    {
        OutputStream out = null;
        try
        {
            out = getStream(response, "商户对账列表");
            
            // 复用分页查询逻辑，设置参数
            // 2^16次方 = 65536
            Double aDouble = Math.pow(2, 16);
            int aInt = aDouble.intValue();
            // 获取数据列表（升序）
            MktVendorOrderMainDTO result =
                manager.queryVendorOrder(pkeys, 0, aInt, vendor, startDate, endDate, status, false, true, CurrentSession.ascriptionPkey());
            List<MktVendorOrderDTO> content = result.getPageList().getContent();
            
            // 数据DTO列表 -> Excel列表
            SettlementMethodType type = sysFarmerConfigDao.getFarmerSettle(CurrentSession.marketPkey());
            if (Objects.nonNull(type) && SettlementMethodType.COMMISSION_SETTLEMENT.equals(type))
            {
                // 佣金结算
                List<MktVendorOrderExcelLong> excels = new ArrayList<>();
                content.forEach(dto -> {
                    MktVendorOrderExcelLong excel = BeanUtil.beanFrom(MktVendorOrderExcelLong.class, dto);
                    
                    excel.setNum(dto.getNum().toString());
                    excel.setPrice(dto.getPrice().toString());
                    excel.setTotalPrice(dto.getTotalPrice() == null ? "" : dto.getTotalPrice().toString());
                    excel.setCommissionRate(
                        dto.getCommissionRate() == null ? "" : dto.getCommissionRate().toString() + "%");
                    excel.setCommissions(dto.getCommissions().toString());
                    excel.setAmt(dto.getAmt().toString());
                    
                    LocalDateTime localDateTime =
                        dto.getCreatedTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                    excel.setCreatedTime(formatter.format(localDateTime));
                    
                    log.info("导出商户对账列表数据：{}", excel);
                    excels.add(excel);
                });
                // 设置合计数据
                StringBuilder builder = new StringBuilder();
                builder.append("合计：总订单数：")
                    .append(result.getOrderCount())
                    .append("笔     总采购数：")
                    .append(result.getPurchaseCount())
                    .append("笔     总采购金额数：")
                    .append(result.getAmt())
                    .append("元");
                String[] titles = {builder.toString()};
                excelHelper.exportExcel(excels, "Sheet1", out, MktVendorOrderExcelLong.class, titles);
            }
            else
            {
                // 采购价结算
                List<MktVendorOrderExcelShort> excels = new ArrayList<>();
                content.forEach(dto -> {
                    MktVendorOrderExcelShort excel = BeanUtil.beanFrom(MktVendorOrderExcelShort.class, dto);
                    
                    excel.setNum(dto.getNum().toString());
                    excel.setPrice(dto.getPrice().toString());
                    // 兼容老数据，总价即为原来的采购价
                    excel.setTotalPrice(
                        dto.getTotalPrice() == null ? dto.getAmt().toString() : dto.getTotalPrice().toString());
                    
                    LocalDateTime localDateTime =
                        dto.getCreatedTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                    excel.setCreatedTime(formatter.format(localDateTime));
                    
                    log.info("导出商户对账列表数据：{}", excel);
                    excels.add(excel);
                });
                // 设置合计数据
                StringBuilder builder = new StringBuilder();
                builder.append("合计：总订单数：")
                    .append(result.getOrderCount())
                    .append("笔     总采购数：")
                    .append(result.getPurchaseCount())
                    .append("笔     总采购金额数：")
                    .append(result.getAmt())
                    .append("元");
                String[] titles = {builder.toString()};
                excelHelper.exportExcel(excels, "Sheet1", out, MktVendorOrderExcelShort.class, titles);
            }
            out.flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (out != null)
            {
                try
                {
                    out.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * 设置OutputStream
     * @param response 响应体
     * @param name     excel名称
     * @return         OutputStream
     * @throws IOException 异常
     */
    private OutputStream getStream(HttpServletResponse response, String name)
        throws IOException
    {
        // 设置响应信息
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf8");
        // 前端UrlDecode解码即可
        response.setHeader("Content-Disposition",
            "attachment; filename=" + java.net.URLEncoder.encode(name, "UTF-8") + ".xlsx");
        response.addHeader("Pragma", "no-cache");
        response.addHeader("Cache-Control", "no-cache");
        return response.getOutputStream();
    }
    
    /**
     * 商户结算分页数据
     * @param param 参数
     * @return      结果
     */
    @Override
    public Result<SettlementMainDTO> settlementList(MktVendorOrderParamDTO param)
    {
        param.setCreateTimeSort(true);
        return new Result<>(manager.settlementList(param));
    }
    
    /**
     * 选中的商户结算详情
     * @param pkeys     选中的数据主键
     * @return          结果
     */
    @Override
    public Result<SettlementDetailDTO> settlementDetail(List<Integer> pkeys)
    {
        return new Result<>(manager.settlementDetail(pkeys));
    }
    
    /**
     * 商户结算
     * @param pkeys             选中的数据主键
     * @param settlementRemark  结算备注
     * @return                  是否成功
     */
    @Override
    public Result<Boolean> settlement(List<Integer> pkeys, String settlementRemark)
    {
        return new Result<>(manager.settlement(pkeys, settlementRemark));
    }
    
    /**
     * 导出商户结算列表
     * @param param    参数
     * @param response 原生响应对象
     */
    @Override
    public void settlementExport(MktVendorOrderParamDTO param, HttpServletResponse response)
    {
        OutputStream out = null;
        try
        {
            out = getStream(response, "导出商户结算列表");
            
            // 复用分页查询逻辑，设置参数
            // 2^16次方 = 65536
            Double aDouble = Math.pow(2, 16);
            int aInt = aDouble.intValue();
            param.setPage(0);
            param.setPagesize(aInt);
            param.setCreateTimeSort(false);
            // 获取数据列表（升序）
            SettlementMainDTO result = manager.settlementList(param);
            List<SettlementDTO> content = result.getPageList().getContent();
            
            // 数据DTO列表 -> Excel列表
            SettlementMethodType type = sysFarmerConfigDao.getFarmerSettle(CurrentSession.marketPkey());
            if (Objects.nonNull(type) && SettlementMethodType.COMMISSION_SETTLEMENT.equals(type))
            {
                // 佣金结算
                List<SettlementExcelLong> excels = new ArrayList<>();
                content.forEach(dto -> {
                    SettlementExcelLong excel = BeanUtil.beanFrom(SettlementExcelLong.class, dto);
                    // 采购日期
                    excel.setCreatedTime(dto.getCreatedTime());
                    excel.setTradeCount(dto.getTradeCount().toString());
                    excel.setTradePrice(dto.getTradePrice().toString());
                    
                    String commissionRate =
                        Objects.nonNull(dto.getCommissionRate()) ? dto.getCommissionRate().toString() + "%" : "";
                    excel.setCommissionRate(commissionRate);
                    excel.setCommissions(dto.getCommissions().toString());
                    excel.setAmt(dto.getAmt().toString());
                    log.info("导出商户结算列表数据：{}", excel);
                    excels.add(excel);
                });
                // 设置合计数据
                StringBuilder builder = new StringBuilder();
                builder.append("合计：总采购笔数：")
                    .append(result.getPurchaseCount())
                    .append("笔     总采购金额：")
                    .append(result.getPurchaseAmt())
                    .append("元     已结算采购笔数：")
                    .append(result.getAlreadycount())
                    .append("笔     已结算采购金额：")
                    .append(result.getAlreadyAmt())
                    .append("元     未结算采购笔数：")
                    .append(result.getAwaitCount())
                    .append("笔     未结算采购金额：")
                    .append(result.getAwaitAmt())
                    .append("元");
                String[] titles = {builder.toString()};
                excelHelper.exportExcel(excels, "Sheet1", out, SettlementExcelLong.class, titles);
            }
            else
            {
                // 采购价结算
                List<SettlementExcelLShort> excels = new ArrayList<>();
                content.forEach(dto -> {
                    SettlementExcelLShort excel = BeanUtil.beanFrom(SettlementExcelLShort.class, dto);
                    
                    // 采购日期
                    excel.setCreatedTime(dto.getCreatedTime());
                    excel.setTradeCount(dto.getTradeCount().toString());
                    excel.setTradePrice(dto.getTradePrice().toString());
                    log.info("导出商户结算列表数据：{}", excel);
                    excels.add(excel);
                });
                // 设置合计数据
                StringBuilder builder = new StringBuilder();
                builder.append("合计：总交易笔数：")
                    .append(result.getPurchaseCount())
                    .append("笔     总交易金额：")
                    .append(result.getPurchaseAmt())
                    .append("元     已结算交易笔数：")
                    .append(result.getAlreadycount())
                    .append("笔     已结算交易金额：")
                    .append(result.getAlreadyAmt())
                    .append("元     未结算交易笔数：")
                    .append(result.getAwaitCount())
                    .append("笔     未结算交易金额：")
                    .append(result.getAwaitAmt())
                    .append("元");
                String[] titles = {builder.toString()};
                excelHelper.exportExcel(excels, "Sheet1", out, SettlementExcelLShort.class, titles);
            }
            out.flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (out != null)
            {
                try
                {
                    out.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * 撤销记录分页数据
     * @param param  参数
     * @return       结果
     */
    @Override
    public Result<RevokeMainDTO> revokeList(MktVendorParamDTO param)
    {
        // 创建时间倒序
        param.setCreateTimeSort(true);
        return new Result<>(manager.revokeList(param));
    }
    
    /**
     * 导出撤销记录数据
     * @param param     参数
     * @param response  原生响应对象
     */
    @Override
    public void revokeExport(MktVendorParamDTO param, HttpServletResponse response)
    {
        OutputStream out = null;
        try
        {
            out = getStream(response, "撤销记录数据");
            
            // 复用分页查询逻辑，设置参数
            // 2^16次方 = 65536
            Double aDouble = Math.pow(2, 16);
            int aInt = aDouble.intValue();
            param.setPage(0);
            param.setPagesize(aInt);
            // 获取数据列表（升序）
            param.setCreateTimeSort(false);
            RevokeMainDTO result = manager.revokeList(param);
            List<RevokeDTO> content = result.getPageList().getContent();
            
            // 数据DTO列表 -> Excel列表
            SettlementMethodType type = sysFarmerConfigDao.getFarmerSettle(CurrentSession.marketPkey());
            if (Objects.nonNull(type) && SettlementMethodType.COMMISSION_SETTLEMENT.equals(type))
            {
                // 佣金结算
                List<RevokeExcelShort> excels = new ArrayList<>();
                content.forEach(dto -> {
                    RevokeExcelShort excel = BeanUtil.beanFrom(RevokeExcelShort.class, dto);
                    excel.setNum(dto.getNum().toString());
                    excel.setGoodsPrice(dto.getGoodsPrice().toString());
                    excel.setCreatedTime(DateUtil.formatDate(dto.getCreatedTime()));
                    excel.setRevokeTime(DateUtil.formatDate(dto.getCreatedTime()));
                    log.info("导出撤销记录数据：{}", excel);
                    excels.add(excel);
                });
                // 设置合计数据
                StringBuilder builder = new StringBuilder();
                builder.append("合计：总订单数：")
                    .append(result.getOrderCount())
                    .append("笔     总采购数：")
                    .append(result.getPurchaseCount())
                    .append("笔     总采购金额数：")
                    .append(result.getPurchaseAmt())
                    .append("元");
                String[] titles = {builder.toString()};
                excelHelper.exportExcel(excels, "Sheet1", out, RevokeExcelShort.class, titles);
            }
            else
            {
                // 采购价结算
                List<RevokeExcelLong> excels = new ArrayList<>();
                content.forEach(dto -> {
                    RevokeExcelLong excel = BeanUtil.beanFrom(RevokeExcelLong.class, dto);
                    
                    excel.setNum(dto.getNum().toString());
                    excel.setGoodsPrice(dto.getGoodsPrice().toString());
                    excel.setPrice(dto.getPrice().toString());
                    excel.setTotalPrice(dto.getTotalPrice() == null ? "" : dto.getTotalPrice().toString());
                    excel.setCreatedTime(DateUtil.formatDate(dto.getCreatedTime()));
                    excel.setAmt(dto.getAmt().toString());
                    excel.setRevokeTime(DateUtil.formatDate(dto.getCreatedTime()));
                    
                    log.info("导出撤销记录数据：{}", excel);
                    excels.add(excel);
                });
                // 设置合计数据
                StringBuilder builder = new StringBuilder();
                builder.append("合计：总订单数：")
                    .append(result.getOrderCount())
                    .append("笔     总采购数：")
                    .append(result.getPurchaseCount())
                    .append("笔     总采购金额数：")
                    .append(result.getPurchaseAmt())
                    .append("元");
                String[] titles = {builder.toString()};
                excelHelper.exportExcel(excels, "Sheet1", out, RevokeExcelLong.class, titles);
            }
            out.flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (out != null)
            {
                try
                {
                    out.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
    
    @Override
    public Result<VendorOrderReport> purchaseReport(int page, int pagesize, DataEnums dataEnums, String startDate,
        String endDate, List<Integer> vendorKeys, SettlementType status)
    {
        return new Result<>(
            manager.purchaseReport(page, pagesize, dataEnums, startDate, endDate, vendorKeys, status, "desc"));
    }
    
    @Operation(summary = "导出商户采购报表", tags = ApiTags.custVendorOrder)
    @PostMapping(value = "/export/purchase")
    public void exportPurchaseReport(
        @RequestParam(value = "dataEnums", required = false, defaultValue = "DAY") @Parameter(description = "时间类型") DataEnums dataEnums,
        @RequestParam(value = "startDate", required = false) @Parameter(description = "开始时间") String startDate,
        @RequestParam(value = "endDate", required = false) @Parameter(description = "结束时间") String endDate,
        @RequestParam(value = "vendorKeys", required = false) @Parameter(description = "商户主键") List<Integer> vendorKeys,
        @RequestParam(value = "status", required = false, defaultValue = "ALREADY_SETTLEMENT") SettlementType status,
        HttpServletResponse response)
    {
        // 升序
        VendorOrderReport purchaseReport =
            manager.purchaseReport(0, 100000, dataEnums, startDate, endDate, vendorKeys, status, "asc");
        List<VendorOrderReportExcel> list =
            BeanUtil.beanListFrom(VendorOrderReportExcel.class, purchaseReport.getLines().getContent());
        OutputStream out = null;
        try
        {
            out = getStream(response, "商户采购报表");
            String[] titles = new String[] {"商户采购报表", status.getName() + "合计   总采购笔数: "
                + purchaseReport.getPurchaseNum() + "笔    总采购金额: " + purchaseReport.getPurchaseAmt() + "元"};
            excelHelper.exportExcel(list, "Sheet1", out, VendorOrderReportExcel.class, titles);
            out.flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (out != null)
            {
                try
                {
                    out.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
    
}
