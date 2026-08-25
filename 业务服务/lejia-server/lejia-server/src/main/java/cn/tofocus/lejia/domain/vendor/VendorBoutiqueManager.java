package cn.tofocus.lejia.domain.vendor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.common.util.PageUtil;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.page.PageParameter;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.app.vendor.AppVendorBoutiquerGoodsInfo;
import cn.tofocus.lejia.bean.dto.app.vendor.AppVendorBoutiquerIndexInfo;
import cn.tofocus.lejia.bean.dto.market.DropDTO;
import cn.tofocus.lejia.bean.dto.vendor.VendorBoutiqueInfo;
import cn.tofocus.lejia.bean.dto.vendor.VendorBoutiqueOnPage;
import cn.tofocus.lejia.bean.entity.goods.MktGoods;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsSpace;
import cn.tofocus.lejia.bean.entity.market.MktGtype;
import cn.tofocus.lejia.bean.entity.member.MktMember;
import cn.tofocus.lejia.bean.entity.vendor.MktVendor;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorBoutique;
import cn.tofocus.lejia.bean.enums.ShowType;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.dao.goods.MktGoodsDao;
import cn.tofocus.lejia.dao.goods.MktGoodsSpaceDao;
import cn.tofocus.lejia.dao.market.MktGtypeDao;
import cn.tofocus.lejia.dao.vendor.MktVendorBoutiqueDao;
import cn.tofocus.lejia.dao.vendor.MktVendorDao;
import cn.tofocus.lejia.exception.LejiaErrCode;

@Component
public class VendorBoutiqueManager
{
    @Autowired
    private MktVendorBoutiqueDao vendorBoutiqueDao;
    
    @Autowired
    private MktVendorDao vendorDao;
    
    @Autowired
    private MktGoodsDao goodsDao;
    
    @Autowired
    private MktGoodsSpaceDao goodsSpaceDao;
    
    @Autowired
    private MktGtypeDao mktGtypeDao;
    
    public PageResult<VendorBoutiqueOnPage> queryVendorBoutique(int page, int pagesize, String vendorName,
        String displayName, Boolean enabled)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        String marketPkey = CurrentSession.marketPkey();
        List<Integer> vendorKeys = new ArrayList<>();
        List<MktVendor> exec = vendorDao.select().like("name", vendorName).like("displayName", displayName)
            .eq("ascription", ascription)
            .eq("farmer", marketPkey)
            .exec();
        if ((exec == null || exec.isEmpty())
            && (StringUtils.isNotBlank(vendorName) || StringUtils.isNotBlank(displayName)))
            return PageUtil.page(new ArrayList<>(), PageParameter.of(page, pagesize));
        exec.forEach(e -> vendorKeys.add(e.getPkey()));
        PageResult<VendorBoutiqueOnPage> res = vendorBoutiqueDao.selectPage()
            .page(page)
            .pagesize(pagesize)
            .eq("ascription", ascription)
            .in("vendor", vendorKeys)
            .eq("farmer", marketPkey)
            .eq("enabled", enabled)
            .execDto(VendorBoutiqueOnPage.class);
        for (VendorBoutiqueOnPage dto : res.getContent())
        {
            String label = dto.getLabel();
            if (StringUtils.isNotBlank(label))
            {
                String[] split = label.split(",");
                dto.setLabels(Arrays.asList(split));
            }
            MktVendor mktVendor = vendorDao.get(dto.getVendor());
            dto.setName(mktVendor.getName());
            dto.setDisplayName(mktVendor.getDisplayName());
        }
        return res;
    }
    
    public Integer addVendorBoutique(VendorBoutiqueInfo info)
    {
        MktVendorBoutique vb = vendorBoutiqueDao.byVendorAndFarmer(info.getVendor(), CurrentSession.marketPkey());
        if(vb != null)
            throw TofocusException.of(LejiaErrCode.VENDORBOUTIQUE_ONE_ERROR);
        MktVendorBoutique bean = BeanUtil.beanFrom(MktVendorBoutique.class, info);
        bean.setLabel(StringUtils.join(info.getLabels(), ","));
        
        bean.setEnabled(false);
        bean.setFarmer(CurrentSession.marketPkey());
        bean.setCompany(CurrentSession.companyPkey());
        if (bean.getSort() == null) bean.setSort(0);
        bean.setAscription(CurrentSession.ascriptionPkey());
        MktVendorBoutique add = vendorBoutiqueDao.add(bean);
        return add.getPkey();
    }
    
    public Boolean updVendorBoutique(VendorBoutiqueInfo info)
    {
        MktVendorBoutique vb = vendorBoutiqueDao.byVendorAndFarmerNotPkey(info.getVendor(), CurrentSession.marketPkey(), info.getPkey());
        if(vb != null)
            throw TofocusException.of(LejiaErrCode.VENDORBOUTIQUE_ONE_ERROR);
        MktVendorBoutique bean = vendorBoutiqueDao.get(info.getPkey());
        BeanUtils.copyProperties(info, bean, "labels");
        bean.setLabel(StringUtils.join(info.getLabels(), ","));
        vendorBoutiqueDao.update(bean);
        return true;
    }
    
    public Boolean enabledVendorBoutique(Integer pkey, Boolean enabled)
    {
        MktVendorBoutique bean = vendorBoutiqueDao.get(pkey);
        if(enabled)
        {
            MktVendor mktVendor = vendorDao.get(bean.getVendor());
            if(mktVendor != null && !mktVendor.getEnabled())
                throw TofocusException.of(LejiaErrCode.VENDORBOUTIQUE_ENABLED_TRUE_ERROR);
        }
        bean.setEnabled(enabled);
        vendorBoutiqueDao.update(bean);
        return true;
    }
    
    public Boolean deVendorBoutique(Integer pkey)
    {
        MktVendorBoutique bean = vendorBoutiqueDao.get(pkey);
        if (Boolean.TRUE.equals(bean.getEnabled())) throw TofocusException.of(LejiaErrCode.ENABLED_TRUE_NOT_DEL);
        return vendorBoutiqueDao.remove(bean);
    }
    
    public List<DropDTO> listGoodsDrop(Integer vendor)
    {
        return goodsDao.select()
            .eq("vendor", vendor)
            .eq("enabled", true)
            .eq("idDel", false)
            .sort("createdTime")
            .sort("pkey")
            .execDto(DropDTO.class);
    }
    
    public PageResult<AppVendorBoutiquerIndexInfo> queryAppVendorBoutique(Integer page, Integer pagesize)
    {
        String farmerPkey = MobileSession.farmerPkey();
        MktMember member = MobileSession.member();
        Integer tjv = null;
        Date tjvTime = null;
        if(member != null)
        {
            tjv = member.getTjv();
            tjvTime = member.getTjvTime();
        }
        Calendar cal = Calendar.getInstance();
        PageResult<AppVendorBoutiquerIndexInfo> res;
        if(tjv != null && tjvTime != null && cal.getTime().compareTo(tjvTime) > 0)
        {
            List<MktVendorBoutique> content = vendorBoutiqueDao.listVendorAndFarmer(tjv, farmerPkey);
            List<MktVendorBoutique> list = vendorBoutiqueDao.select()
                .notEq("vendor", tjv)
                .eq("enabled", true)
                .eq("farmer", farmerPkey).sort("sort", false).exec();
            content.addAll(list);
            res = PageUtil.page(BeanUtil.beanListFrom(AppVendorBoutiquerIndexInfo.class, content), PageParameter.of(page, pagesize));
        }
        else
        {
            res = vendorBoutiqueDao.selectPage().page(page).pagesize(pagesize)
                .eq("enabled", true)
                .eq("farmer", farmerPkey)
                .sort("sort", false)
                .execDto(AppVendorBoutiquerIndexInfo.class);
        }
        
        Map<Integer, String> typePkeyNames =
            mktGtypeDao.listGtype(MobileSession.farmerPkey(), MobileSession.appid()).stream().collect(Collectors.toMap(MktGtype::getPkey, MktGtype::getName));
        for(AppVendorBoutiquerIndexInfo vb : res.getContent())
        {
            MktVendor mktVendor = vendorDao.get(vb.getVendor());
            vb.setVendorName(mktVendor.getDisplayName());
            vb.setBooth(mktVendor.getBooth());
            vb.setBusinessScopesName(assembleBusinessScopesName(mktVendor.getBusinessScope(), typePkeyNames));
            String label = vb.getLabel();
            if (StringUtils.isNotBlank(label))
            {
                String[] split = label.split(",");
                vb.setLabels(Arrays.asList(split));
            }
            if(ShowType.SHOW_GOODS.equals(vb.getShowType1()))
            {
                AppVendorBoutiquerGoodsInfo goods1 = assembleGoods(vb.getShowContent1());
                vb.setGoods1(goods1);
            }
            if(ShowType.SHOW_GOODS.equals(vb.getShowType2()))
            {
                AppVendorBoutiquerGoodsInfo goods2 = assembleGoods(vb.getShowContent2());
                vb.setGoods2(goods2);
            }
        }
        return res;
    }
    
    private String assembleBusinessScopesName(String businessScope, Map<Integer, String> typePkeyNames)
    {
        // 经营范围
        List<Integer> realBusinessScope = new ArrayList<>();
        // 经营范围中文
        StringBuilder builder = new StringBuilder();
        if (StringUtils.isNotBlank(businessScope))
        {
            String[] scopes = businessScope.split(",");
            for (int i = 0; i < scopes.length; i++)
            {
                int scopePkey = 0;
                String name = "";
                
                if (i > 0)
                {
                    name += ",";
                }
                
                try
                {
                    scopePkey = Integer.parseInt(scopes[i]);
                    realBusinessScope.add(scopePkey);
                }
                // 转换失败处理
                catch (NumberFormatException e)
                {
                    name += "未知";
                }
                // 一级范围名称
                String v = typePkeyNames.get(scopePkey);
                if (Objects.nonNull(v))
                {
                    name += v;
                }
                else
                {
                    name += "未知";
                }
                builder.append(name);
            }
        }
        return builder.toString();
    }
    
    private AppVendorBoutiquerGoodsInfo assembleGoods(String pkey)
    {
        AppVendorBoutiquerGoodsInfo dto = new AppVendorBoutiquerGoodsInfo();
        if(StringUtils.isBlank(pkey))
            return dto;
        MktGoods mktGoods = goodsDao.get(Integer.valueOf(pkey));
        if(mktGoods == null)
            return dto;
        dto.setPkey(mktGoods.getPkey());
        dto.setTitle(mktGoods.getTitle());
        dto.setPrice(mktGoods.getPrice());
        dto.setWrapperPhoto("");
        List<String> photo1 = mktGoods.getPhoto1();
        if(photo1 != null && !photo1.isEmpty())
            dto.setWrapperPhoto(photo1.get(0));
        MktGoodsSpace goodsSpace = goodsSpaceDao.selectOne()
            .eq("goods", mktGoods.getPkey())
            .eq("price", mktGoods.getPrice())
            .exec();
        dto.setPriceOld(goodsSpace.getPriceOld());
        return dto;
    }
}
