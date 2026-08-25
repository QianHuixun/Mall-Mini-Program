package cn.tofocus.lejia;

import static cn.tofocus.core.query.exp.ExpUtil.f;
import static cn.tofocus.core.query.exp.ExpUtil.substring;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.Entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import cn.tofocus.common.excel.ExcelUtil;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.DBUtil;
import cn.tofocus.lejia.bean.dto.sys.ManagerOnPage;
import cn.tofocus.lejia.bean.entity.market.MktOrder;
import cn.tofocus.lejia.bean.entity.market.MktOrderDesc;
import cn.tofocus.lejia.bean.entity.member.MktMemberCard;
import cn.tofocus.lejia.bean.enums.CardStatus;
import cn.tofocus.lejia.bean.enums.OrderStatus;
import cn.tofocus.lejia.bean.enums.OrderType;
import cn.tofocus.lejia.dao.goods.MktGoodsDao;
import cn.tofocus.lejia.dao.goods.MktGoodsMainDao;
import cn.tofocus.lejia.dao.market.MktMemberCardDao;
import cn.tofocus.lejia.dao.market.MktOrderCodeDao;
import cn.tofocus.lejia.dao.market.MktOrderDao;
import cn.tofocus.lejia.dao.vendor.MktVendorOrderDao;
import cn.tofocus.lejia.domain.GoodListQueryer;
import cn.tofocus.lejia.domain.GoodsBoxManager;
import cn.tofocus.lejia.domain.GtypeV4Manager;
import cn.tofocus.lejia.domain.ManagerRoleManager;
import cn.tofocus.lejia.domain.market.VendorOrderManager;
import cn.tofocus.lejia.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
public class MysqlTest
{
    @Autowired
    private MktVendorOrderDao vendorOrderDao;
    
    @Autowired
    private MktOrderCodeDao orderCodeDao;
    
    @Autowired
    private VendorOrderManager vendorOrderManager;
    
    @Autowired
    private GtypeV4Manager gtypeV4Manager;
    
    @Autowired
    private MktOrderDao orderDao;
    
    @Autowired
    private MktGoodsMainDao goodsMainDao;
    
    @Autowired
    private MktGoodsDao goodsDao;
    
    @Autowired
    private GoodsBoxManager goodsBoxManager;
    
    @Autowired
    private GoodListQueryer goodListQueryer;
    
    @Autowired
    private ManagerRoleManager managerRoleManager;
    
    @Autowired
    private MktMemberCardDao memberCardDao;
    
    
    @Test
    public void test1()
    {
//        List<MktVendorOrder> list = vendorOrderDao.select().eq("length(pkey)", "1").exec();
//        log.info("list: " + JsonUtil.toString(list, true));
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -3);
//        List<MktOrder> list3 = orderDao.joinSelect()
//            .as("pkey")
//        .in("orderType", OrderType.INTEGRAL_ORDER, OrderType.INTEGRAL_PRESALE_ORDER)
//        .in("status", OrderStatus.SHIPPED_ORDER, OrderStatus.ARRIVED_ORDER)
//        .eq("farmer", Constant.Operation + 13)
//        .join(MktOrderDesc.class, "pkey", "pkey")
//            .isNotNull("fhTime")
//            .le("fhTime", calendar.getTime())
//        .endJoin()
//        .exec(MktOrder.class);
        List<OrderType> otList = new ArrayList<>();
        otList.add(OrderType.MARKET_ORDER);
        otList.add(OrderType.SHARE_ORDER);
        otList.add(OrderType.CUT_ORDER);
        otList.add(OrderType.COLLAGE_ORDER);
        otList.add(OrderType.PRESALE_ORDER);
        List<MktOrder> list1 = orderDao.select()
            .in("orderType", otList)
            .in("status", OrderStatus.SHIPPED_ORDER, OrderStatus.ARRIVED_ORDER)
            .start("farmer", "zy_mkt_")
            .notEq("farmer", Constant.Operation + 1)
            .exec();
        
        System.out.println("list1: " + JsonUtil.toString(list1, true));
    }
    
    @Test
    public void test2()
    {
//        MktOrderCode oc = new MktOrderCode();
//        oc.setOrderPkey(1);
//        oc.setCode("23655114");
//        MktOrderCode add = orderCodeDao.add(oc);
//        List<MktOrderCode> list = orderCodeDao.findAll();
//        log.info("list: " + JsonUtil.toString(list, true));
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 6);
        String time = DateUtil.formatDate(calendar.getTime(), "yyyy-MM-dd");
        List<MktMemberCard> exec =
            memberCardDao.select().eq("status", CardStatus.UNUSED).eq(substring(f("endDate"), 1, 10), time).exec();
        log.info("exec: " + JsonUtil.toString(exec, true));
    }
    
    @Test
    public void test3()
    {
//        vendorOrderManager.runVendorOrderConfirm();
        long k0 = System.currentTimeMillis();
//        gtypeV4Manager.oldDataMigrate();
//        gtypeV4Manager.changeGtype(); 
//        gtypeV4Manager.updGtypeAndTwoAndThreeSort();
        long k1 = System.currentTimeMillis();
        System.out.println("耗时: " + (k1 - k0) / 1000);
//        goodListQueryer.resetAll(null, GoodsSortType.PRICE);
//        goodListQueryer.resetAll(null, GoodsSortType.SALED);
        goodListQueryer.resetAll(null, null); 
        long k2 = System.currentTimeMillis();
        System.out.println("耗时: " + (k2 - k1) / 1000);
    }
    
    @Test
    public void test4()
    {
//        appGoodsV4Manager2.queryAppGtypeGoodsSQL(0, 10, 15931, GoodsSortType.PRICE, false, "zy_mkt_0023", null);
//        appGoodsV4Manager2.queryAppGoodsMainGoodsSQL(0, 10, 10602, GoodsSortType.PRICE, false);
//        Calendar cal = Calendar.getInstance();
//        cal.set(2024, 1, 2);
//        System.out.println("1111:  " + DateUtil.formatDate(cal.getTime()));
//        Integer orderPrintMaxNum = orderDao.getOrderPrintMaxNum("zy_mkt_0017", DateUtil.atStartOfDay(cal.getTime()));
//        System.out.println("orderPrintMaxNum: " + orderPrintMaxNum);
//        String time = "2024-03-05 00:15:00";
//        List<MktVendorOrder> vendorOrderList = vendorOrderDao.listCertainDayBefore(time);
//        System.out.println("vendorOrderList: " + JsonUtil.toString(vendorOrderList, true));
//        managerRoleManager.oldDataHandle();
        PageResult<ManagerOnPage> query = managerRoleManager.query(0, 10, null, null);
        System.out.println("query: " + JsonUtil.toString(query.getContent(), true));
    }
    @Autowired
    private ManagerRoleManager manager;
    @Test
    public void test5()
    {
        // 商城管理员老数据处理
//        manager.oldDataHandle();
    }
    
    @Test
    public void test6()
    {
        //找到所有实体
        List<Class<?>> entityClassList = DBUtil.findClass("cn.tofocus.lejia.bean.entity", Entity.class);
        entityClassList.addAll(DBUtil.findClass("cn.tofocus.lejia.bean.entity", Entity.class));
        ExcelUtil.exportTableDoc(entityClassList, "D://zyysc.xlsx", "zyysc");
    }
}
