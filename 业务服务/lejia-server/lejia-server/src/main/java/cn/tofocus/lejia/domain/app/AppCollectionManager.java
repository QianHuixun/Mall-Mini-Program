package cn.tofocus.lejia.domain.app;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.app.market.AppCollectionDTO;
import cn.tofocus.lejia.bean.entity.goods.MktGoods;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsSpace;
import cn.tofocus.lejia.bean.entity.market.MktCollection;
import cn.tofocus.lejia.bean.entity.market.MktCookfd;
import cn.tofocus.lejia.bean.entity.sys.SysFarmer;
import cn.tofocus.lejia.bean.entity.vendor.MktVendor;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorFile;
import cn.tofocus.lejia.bean.enums.VendorFileType;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.dao.goods.MktGoodsDao;
import cn.tofocus.lejia.dao.goods.MktGoodsSpaceDao;
import cn.tofocus.lejia.dao.market.MktCollectionDao;
import cn.tofocus.lejia.dao.market.MktCookfdDao;
import cn.tofocus.lejia.dao.sys.SysFarmerDao;
import cn.tofocus.lejia.dao.vendor.MktVendorDao;
import cn.tofocus.lejia.dao.vendor.MktVendorFileDao;
import cn.tofocus.lejia.exception.WsaleErrCode;
import cn.tofocus.lejia.repository.market.MktCookfdRepository;

@Component
public class AppCollectionManager {
    @Autowired
    private MktCollectionDao collectionDao;
    @Autowired
    private SysFarmerDao farmerDao;
    @Autowired
    private MktCookfdDao cookfdDao;
    @Autowired
    private MktGoodsDao goodsDao;
    @Autowired
    private MktGoodsSpaceDao goodsSpaceDao;
    @Autowired
    private MktCookfdRepository cookfdRepository;
    
    @Autowired
    private MktVendorDao vendorDao;
    
    @Autowired
    private MktVendorFileDao vendorFileDao;
    


    @Transactional
    public Integer insCollection(Integer objKey, Integer ctype) {
        Integer memberPkey = MobileSession.memberPkey();
        String farmerPkey = MobileSession.farmerPkey();
        if(farmerPkey == null) throw TofocusException.of(WsaleErrCode.UNKOWN_FARMER);
        SysFarmer farmer = farmerDao.get(farmerPkey);
        if (farmer == null)
            throw TofocusException.of(WsaleErrCode.UNKOWN_MARKET);
        if(memberPkey == null)
            throw TofocusException.of(WsaleErrCode.MEMBER_NOT_LOGIN);
        MktCollection exec = collectionDao.selectOne()
                .eq("member", memberPkey)
                .eq("ctype", ctype)
                .eq("objKey", objKey)
                .exec();
        if (exec != null)
            throw TofocusException.of(WsaleErrCode.ALREADY_COLLECTION);
        MktCollection entity = new MktCollection();
        entity.setCompany(farmer.getOrg());
        entity.setFarmer(farmerPkey);
        entity.setAscription(MobileSession.appid());
        entity.setCtype(ctype);
        entity.setMember(memberPkey);
        entity.setObjKey(objKey);
        MktCollection add = collectionDao.add(entity);

        if (ctype == 0) {
            cookfdRepository.autoCollCount(objKey, 1);
        } else if (ctype == 1) {

        }
        return add.getPkey();
    }

    public PageResult<AppCollectionDTO> queryCollection(int page, int pagesize, Integer ctype) {
        Integer memberPkey = MobileSession.memberPkey();
        PageResult<MktCollection> pageResult = collectionDao.selectPage()
                .page(page)
                .pagesize(pagesize)
                .eq("member", memberPkey)
                .eq("ctype", ctype)
                .eq("ascription", MobileSession.appid())
                .sort("pkey", true)
                .exec();
        PageResult<AppCollectionDTO> result = BeanUtil.beanPageFrom(AppCollectionDTO.class, pageResult);
        for (AppCollectionDTO bean : result) {
            if (bean.getCtype() == 0) {
                MktCookfd cookfd = cookfdDao.selectOne().eq("idDel", false).eq("pkey", bean.getObjKey()).exec();
                if(cookfd != null)
                {
                    bean.setCollCount(cookfd.getCollCount());
                    bean.setName(cookfd.getName());
                    if (cookfd.getPhoto1().size() > 0)
                        bean.setPhoto(cookfd.getPhoto1().get(0));
                }
                else
                {
                    bean.setName("该菜谱已删除");
                }

            } else if(bean.getCtype() == 1) {
                MktGoods goods = goodsDao.get(bean.getObjKey());
                List<MktGoodsSpace> spaceList = goodsSpaceDao.select().eq("goods", goods.getPkey()).sort("price", false).exec();
                if (spaceList.size() > 0) {
//                Map<String, Object> map = goodsDao.getGoodsAllinfo(bean.getObjKey());
                    bean.setPrice(spaceList.get(0).getPrice());
                    if(goods.getMType().getIndex() == 1)
                    	bean.setPrice(spaceList.get(0).getPriceOld());
                    bean.setGoodsSpace(spaceList.get(0).getPkey());
                }
                bean.setPhoto(goods.getPhoto1() != null && goods.getPhoto1().size() > 0 ? goods.getPhoto1().get(0) : "");
                bean.setName(goods.getTitle());
                bean.setMType(goods.getMType());
            }
            else
            {
                MktVendor vendor = vendorDao.get(bean.getObjKey());
                if(vendor != null)
                {
                    bean.setName(vendor.getDisplayName());
                    bean.setBooth(vendor.getBooth());
                }
                Integer xsNum = goodsDao.getGoodsVendorXsNum(bean.getObjKey());
                bean.setXsNum(xsNum);
                MktVendorFile vendorFile = vendorFileDao.selectOne()
                .eq("vendorPkey", bean.getObjKey())
                .eq("type", VendorFileType.HEAD_ICON).exec();
                if(vendorFile != null)
                {
                    bean.setPhoto(vendorFile.getUrl());
                }
            }
        }
        return result;
    }

    @Transactional
    public Boolean delCollection(int pkey) {
        Integer memberPkey = MobileSession.memberPkey();
        MktCollection exec = collectionDao.selectOne().eq("pkey", pkey).eq("member", memberPkey).exec();
        boolean result = false;
        if (exec != null)
            result = collectionDao.removeById(pkey);

        if (exec.getCtype() == 0) {
            cookfdRepository.autoCollCount(exec.getObjKey(), -1);
        }
        return result;
    }

    public Integer chkCollection(Integer ctype, Integer objKey) {
        Integer memberPkey = MobileSession.memberPkey();
        if (memberPkey != null) {
            MktCollection collection = collectionDao.selectOne().eq("member", memberPkey).eq("ctype", ctype).eq("objKey", objKey).exec();
            if (collection != null)
                return collection.getPkey();
        }
        return 0;
    }
    
    public Map<String, Integer> getCtypeNum()
    {
        Integer memberPkey = MobileSession.memberPkey();
        Map<String, Long> map = collectionDao.aggregation()
        .eq("member", memberPkey)
        .execGroupByCount("ctype", "pkey");
        Map<String, Integer> res = new HashMap<>();
        Integer goodsNum = 0;
        Integer cookfdNum = 0;
        Integer boothNum = 0;
        if(map.containsKey("0"))
            cookfdNum = map.get("0").intValue();
        if(map.containsKey("1"))
            goodsNum = map.get("1").intValue();
        if(map.containsKey("2"))
            boothNum = map.get("2").intValue();
        res.put("goodsNum", goodsNum);
        res.put("cookfdNum", cookfdNum);
        res.put("boothNum", boothNum);
        return res;
    }
}





