package cn.tofocus.lejia.api.v1.vendor;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.market.DropDTO;
import cn.tofocus.lejia.bean.dto.vendor.VendorBoutiqueInfo;
import cn.tofocus.lejia.bean.dto.vendor.VendorBoutiqueOnPage;
import cn.tofocus.lejia.domain.vendor.VendorBoutiqueManager;

@RequestMapping("/v1/vendor/boutique")
@RestController
public class VendorBoutiqueApiImpl implements VendorBoutiqueApi
{
    @Autowired
    private VendorBoutiqueManager manager;
    
    @Override
    public Result<PageResult<VendorBoutiqueOnPage>> queryVendorBoutique(int page, int pagesize, String vendorName,
        String displayName, Boolean enabled)
    {
        return new Result<>(manager.queryVendorBoutique(page, pagesize, vendorName, displayName, enabled));
    }

    @Override
    public Result<Integer> addVendorBoutique(@Valid VendorBoutiqueInfo info)
    {
        return new Result<>(manager.addVendorBoutique(info));
    }

    @Override
    public Result<Boolean> updVendorBoutique(@Valid VendorBoutiqueInfo info)
    {
        return new Result<>(manager.updVendorBoutique(info));
    }

    @Override
    public Result<Boolean> enabledVendorBoutique(Integer pkey, Boolean enabled)
    {
        return new Result<>(manager.enabledVendorBoutique(pkey, enabled));
    }

    @Override
    public Result<Boolean> deVendorBoutique(Integer pkey)
    {
        return new Result<>(manager.deVendorBoutique(pkey));
    }

    @Override
    public Result<List<DropDTO>> listGoodsDrop(Integer vendor)
    {
        return new Result<>(manager.listGoodsDrop(vendor));
    }
    
}
