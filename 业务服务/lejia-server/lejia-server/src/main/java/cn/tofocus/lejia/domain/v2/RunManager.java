package cn.tofocus.lejia.domain.v2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsSpace;
import cn.tofocus.lejia.bean.entity.goods.MktSpaceKc;
import cn.tofocus.lejia.bean.entity.market.MktCard;
import cn.tofocus.lejia.bean.entity.member.MktMemberCard;
import cn.tofocus.lejia.bean.entity.sys.SysFarmer;
import cn.tofocus.lejia.bean.entity.sys.SysFarmerMtype;
import cn.tofocus.lejia.bean.entity.vendor.MktVendor;
import cn.tofocus.lejia.bean.enums.MType;
import cn.tofocus.lejia.bean.enums.v2.VendorZxStatus;
import cn.tofocus.lejia.cache.SpaceKcCache;
import cn.tofocus.lejia.dao.goods.MktGoodsSpaceDao;
import cn.tofocus.lejia.dao.goods.MktSpaceKcDao;
import cn.tofocus.lejia.dao.market.MktCardDao;
import cn.tofocus.lejia.dao.market.MktMemberCardDao;
import cn.tofocus.lejia.dao.sys.SysFarmerDao;
import cn.tofocus.lejia.dao.sys.SysFarmerMtypeDao;
import cn.tofocus.lejia.dao.vendor.MktVendorDao;

@Component
public class RunManager
{
    @Autowired
    private MktVendorDao vendorDao;
    
    @Autowired
    private MktGoodsSpaceDao goodsSpaceDao;
    
    @Autowired
    private MktCardDao cardDao;
    
    @Autowired
    private MktMemberCardDao memberCardDao;
    
    @Autowired
    private SysFarmerDao sysFarmerDao;
    
    @Autowired
    private SysFarmerMtypeDao farmerMtypeDao;
    
    @Autowired
    private SpaceKcCache spaceKcCache;
    
    @Autowired
    private MktSpaceKcDao spaceKcDao;
    
    public String runMemberCardAndSpaceNum()
    {
        String res = "";
        String runKc = runKc();
        String runMemCard = runMemCard();
        String runFarmer = runFarmer();
        String runVendor = runVendor();
        res = runKc + "     " + runMemCard + "    " + runFarmer + "     " + runVendor;
        return res;
    }
    
    /*
     * 2.5.1上线
     */
    public String runKc()
    {
        long k1 = System.currentTimeMillis();
        String res = "";
        List<MktGoodsSpace> list = goodsSpaceDao.findAll();
        List<MktSpaceKc> add = BeanUtil.beanListFrom(MktSpaceKc.class, list);
        spaceKcDao.addAll(add);
        for (MktSpaceKc sk : add)
        {
            spaceKcCache.set(String.valueOf(sk.getPkey()), Long.valueOf(sk.getKcNum()));
        }
        long k2 = System.currentTimeMillis();
        res = "库存缓存处理 " + add.size() + "条,耗时: " + (k2 - k1);
        return res;
    }
    
    /*
     * 2.5.1上线
     */
    public String runMemCard()
    {
        String res = "";
        List<MktCard> list = cardDao.findAll();
        List<MktMemberCard> mcList = memberCardDao.findAll();
        Map<Integer, MktCard> map = new HashMap<>();
        list.forEach(e -> {
            map.put(e.getPkey(), e);
        });
        for (MktMemberCard mc : mcList)
        {
            if (map.containsKey(mc.getCard()))
            {
                MktCard card = map.get(mc.getCard());
                mc.setUserFarmer(card.getUserFarmer());
                mc.setUserGoods(card.getUserGoods());
                mc.setUserType(card.getUserType());
                mc.setLimitCost(card.getLimitCost());
            }
        }
        memberCardDao.updateAll(mcList);
        res = "memberCard 更新了 " + mcList.size() + " 条数据";
        return res;
    }
    
    /*
     * 2.5.1上线
     */
    public String runFarmer()
    {
        String res = "";
        List<MType> types = new ArrayList<>();
        types.add(MType.MARKET_GOODS);
        types.add(MType.CUT_GOODS);
        types.add(MType.COLLAGE_GOODS);
        types.add(MType.PRESALE_GOODS);
        List<SysFarmer> list = sysFarmerDao.findAll();
        List<SysFarmerMtype> mtypeAdd = new ArrayList<>();
        for (SysFarmer sf : list)
        {
            
            for (MType m : types)
            {
                SysFarmerMtype mt = new SysFarmerMtype();
                mt.setFarmer(sf.getPkey());
                mt.setMType(m);
                mt.setDelivery(true);
                mt.setPickup(true);
                mtypeAdd.add(mt);
            }
        }
        farmerMtypeDao.addAll(mtypeAdd);
        res = "市场配送表新增 " + mtypeAdd.size() + "条数据,涉及市场: " + list.size();
        return res;
    }
    
    /*
     * 2.5.1上线
     */
    public String runVendor()
    {
        String res = "";
        List<MktVendor> list = vendorDao.findAll();
        for (MktVendor v : list)
        {
            if (v.getIsClear() == null)
                v.setZxStatus(VendorZxStatus.NOT_AUDIT);
            else if (v.getIsClear())
                v.setZxStatus(VendorZxStatus.AUDIT_SUCCESS);
            else
            {
                v.setZxStatus(VendorZxStatus.AUDIT_FAILURE);
            }
            
        }
        vendorDao.updateAll(list);
        res = "商户审核状态修改完成";
        return res;
    }
}
