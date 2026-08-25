package cn.tofocus.lejia.domain.market.keruyun;

import cn.tofocus.lejia.bean.entity.market.MktKryOrder;
import cn.tofocus.lejia.bean.entity.market.MktKryVendor;
import cn.tofocus.lejia.bean.entity.member.MktMember;
import cn.tofocus.lejia.bean.enums.KryStatus;
import cn.tofocus.lejia.bean.enums.SourceType;
import cn.tofocus.lejia.dao.market.MktKryOrderDao;
import cn.tofocus.lejia.dao.market.MktKryVendorDao;
import cn.tofocus.lejia.dao.market.MktMemberDao;
import cn.tofocus.lejia.domain.market.MemberPointManager;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class KeruyunManager
{
    
    @Autowired
    private MktKryVendorDao mktKryVendorDao;
    
    @Autowired
    private MktKryOrderDao mktKryOrderDao;
    
    @Autowired
    private MemberPointManager mpManager;
    
    @Autowired
    private MktMemberDao memberDao;
    
    private void listOrder(long shopIdenty, String token, int pageNo, List<MktKryOrder> orderList)
        throws Exception
    {
        JSONObject obj = KeruyunTool.loadOrderList(shopIdenty, token, pageNo);
        if (obj == null) return;
        JSONArray items = obj.getJSONArray("items");
        int size = items.size();
        Long[] ids = new Long[size];
        MktKryOrder[] orders = new MktKryOrder[size];
        for (int i = 0; i < size; i++)
        {
            JSONObject item = items.getJSONObject(i);
            MktKryOrder order = new MktKryOrder();
            order.setUuid(shopIdenty);
            order.setOrderId(item.getLong("orderId"));
            order.setCode(item.getString("tradeNo"));
            if (item.getInteger("tradeType").intValue() == 1 && item.getInteger("tradeStatus").intValue() == 4)
                order.setStatus(KryStatus.KRY_COMPLETED);
            else
                order.setStatus(KryStatus.KRY_OTHER);
            order.setSource(item.getString("sourceName"));
            order.setReceivedAmount(item.getLong("receivedAmount"));
            order.setCustRealPay(item.getLong("custRealPay"));
            order.setTradeAmount(item.getLong("tradeAmount"));
            order.setPrivilegeAmount(item.getLong("privilegeAmount"));
            order.setOrderTime(new Date(item.getLong("orderTime")));
            ids[i] = order.getOrderId();
            orders[i] = order;
        }
        JSONArray res = KeruyunTool.loadOrder(shopIdenty, ids, token);
        if (res != null) for (int i = 0; i < size; i++)
        {
            JSONObject lineObj = res.getJSONObject(0);
            JSONArray tradeCustomerInfos = lineObj.getJSONArray("tradeCustomerInfos");
            if (tradeCustomerInfos.size() > 0)
            {
                orders[i].setMobile(tradeCustomerInfos.getJSONObject(0).getString("customerPhone"));
                orders[i].setCustmerName(tradeCustomerInfos.getJSONObject(0).getString("customerName"));
                orders[i].setCustomerId(tradeCustomerInfos.getJSONObject(0).getLong("customerId"));
                orders[i].setMemberId(tradeCustomerInfos.getJSONObject(0).getLong("memberId"));
            }
            orderList.add(orders[i]);
        }
        int rows = (obj.getInteger("totalRows") - 1) / 20 + 1;
        if (rows > pageNo) listOrder(shopIdenty, token, pageNo + 1, orderList);
    }
    
    /*
     * 跑批导入订单
     */
    public void runOrderDay(Integer ascription)
    {
        try
        {
            List<MktKryVendor> list =
                mktKryVendorDao.select().eq("ascription", ascription).eq("enabled", true).eq("idDel", false).exec();
            List<MktKryOrder> orderList = new ArrayList<>();
            for (MktKryVendor vendor : list)
            {
                System.out.println("客如云插入数据：" + vendor.getUuid());
                listOrder(vendor.getUuid(), vendor.getToken(), 1, orderList);
            }
            System.out.println("客如云插入数据：" + orderList.size() + "条");
            mktKryOrderDao.addAll(orderList);
        }
        catch (Exception e)
        {
            // TODO: handle exception
            e.printStackTrace();
        }
    }
    
    /*
     * 客如云会员积分跑批
     */
    public void runPointDay(Integer ascription){
		List<MktKryOrder> list = mktKryOrderDao.select().eq("ascription", ascription).eq("status", KryStatus.KRY_COMPLETED).isNotNull("mobile").exec();
		for(MktKryOrder line:list){
			MktMember member = memberDao.selectOne().eq("ascription", ascription).eq("mobile", line.getMobile()).exec();
			if(member == null)
				continue;
			mpManager.updPointForAmt(member.getPkey(), new BigDecimal(String.valueOf(line.getCustRealPay()/100)), true, SourceType.POINTS_CONSUMPTION,
					line.getCode(), "客如云" + line.getUuid(), ascription, null);
		}
	}
}
