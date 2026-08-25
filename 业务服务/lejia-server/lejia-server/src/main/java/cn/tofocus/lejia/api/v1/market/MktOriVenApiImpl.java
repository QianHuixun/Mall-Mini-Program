package cn.tofocus.lejia.api.v1.market;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.core.Result;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.log.LogApi;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.market.MktOriVenOnList;
import cn.tofocus.lejia.bean.entity.market.MktOriVen;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.domain.market.OriVenManager;
import cn.tofocus.lejia.exception.WsaleErrCode;
import cn.tofocus.lejia.util.ExcelUtils;
import cn.tofocus.lejia.util.FileUtil;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;

@Slf4j

@RequestMapping("/v1/market/oriven")
@RestController
public class MktOriVenApiImpl implements MktOriVenApi
{
	@Autowired
	private OriVenManager oriVenManager;
	
	@Override
	@LogApi(operation = "新增溯源信息", format = "新增溯源信息,溯源商户:{entity.merchant}", resultFormat = "")
	public Result<MktOriVenOnList> insOriVen(MktOriVenOnList entity) {
		return new Result<>(oriVenManager.insOriVen(entity));
	}

	@Override
	public Result<MktOriVenOnList> getOriVen(Integer pkey) {
		return new Result<>(oriVenManager.getOriVen(pkey));
	}

	@Override
	public Result<PageResult<MktOriVenOnList>> queryOriVen(int page, int pagesize, String merchant, String goods,
			String vendor) {
		return new Result<>(oriVenManager.queryOriVen(page, pagesize, merchant, goods, vendor, true));
	}

	@Override
	@LogApi(operation = "修改溯源信息", format = "修改溯源信息 溯源商户:{entity.merchant}", resultFormat = "")
	public Result<MktOriVenOnList> updOriVen(MktOriVenOnList entity) {
		return new Result<>(oriVenManager.updOriVen(entity));
	}

	@Override
	@LogApi(operation = "删除溯源信息", format = "删除溯源信息")
	public Result<Boolean> delOriVen(Integer pkey) {
		return new Result<>(oriVenManager.delOriVen(pkey));
	}

	@Override
	@LogApi(operation = "导入溯源信息excel", format = "导入溯源信息")
	public Result<Boolean> importExcel(MultipartFile myfile) {
		List<List<Object>> lists;
		List<MktOriVen> otList = new ArrayList<>();
		String farmer = CurrentSession.marketPkey();
		String company = CurrentSession.companyPkey();
		try {
			lists = ExcelUtils.getUserListByExcel(myfile.getInputStream(), myfile.getOriginalFilename());
	        
	        for (int i = 0; i < lists.size(); i++) {
	        	Iterator<Object> iterator = lists.get(i).iterator();
	        	MktOriVen mot = new MktOriVen();
        		log.info("object: {}", lists.get(i));
        		if(iterator.hasNext()) {
					String str = iterator.next().toString();
					if(StringUtils.isBlank(str))
						continue;
					mot.setMerchant(str);
				}
        		else
        			continue;
        		if(iterator.hasNext()) {
					String trim = iterator.next().toString().trim();
					if(StringUtils.isBlank(trim))
						continue;
					mot.setGoods(trim);
				}
        		else
        			continue;
        		if(iterator.hasNext()) {
					Object next = iterator.next();
					if(next != null)
						mot.setVendor(next.toString());
				}
        		if(iterator.hasNext())
        			mot.setOriDate(DateUtil.formatDateStr(iterator.next().toString(), "yyyy-MM-dd"));
        		mot.setCompany(company);
    			mot.setFarmer(farmer);
    			mot.setAscription(CurrentSession.ascriptionPkey());
    			mot.setRowVension(1);
        		otList.add(mot);
	        }
		} catch (Exception e) {
			throw TofocusException.of(WsaleErrCode.EXCEL_PROBLEM);
		}
		// 将数据存入数据库
		Boolean importExcel = oriVenManager.importExcel(otList);
		return new Result<>(importExcel);
	}

	@Operation(summary = "溯源信息模板下载", tags = ApiTags.custOriVen)
	@GetMapping(value = "/down/template")
	public Result<Boolean> downTemplate(HttpServletRequest request,HttpServletResponse response)
	{
		FileUtil.buildExcelDocument("sy.xlsx", "溯源信息模板.xlsx", "/data/tofocus/server/zyysc", request, response);
		return new Result<>(true);
	}
	
	@GetMapping("/down/img")
	@LogApi(operation = "溯源信息二维码", format = "下载卡券二维码")	
	public Result<BufferedImage> downExcel(Integer pkey,HttpServletRequest request,HttpServletResponse response) {
		BufferedImage img = null;
		if(pkey == null)
			throw TofocusException.of(WsaleErrCode.CAN_NOT_BE_EMPTY,"pkey");
//		PageResult<MktOriVenOnList> pageResult = oriVenManager.queryOriVen(0, 10000, null, null, null);
		try {
			img = FileUtil.createImage("pages/showyeGroup/tranceSource/index" , 500, 500);
		} catch (Exception e) {
			e.printStackTrace();
		}
//		FileUtil.buildExcelDocument("溯源二维码", img, request, response);
		return new Result<>(img);
	}

}
