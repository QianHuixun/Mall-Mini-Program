package cn.tofocus.lejia.api.v1.market;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.core.Result;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.log.LogApi;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.market.MktOriTestOnList;
import cn.tofocus.lejia.bean.entity.market.MktOriTest;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.domain.market.OriTestManager;
import cn.tofocus.lejia.exception.WsaleErrCode;
import cn.tofocus.lejia.util.ExcelUtils;
import cn.tofocus.lejia.util.FileUtil;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;

@Slf4j

@RequestMapping("/v1/market/oritest")
@RestController
public class MktOriTestApiImpl implements MktOriTestApi
{
	@Autowired
	private OriTestManager oriTestManager;
	
	@Override
	@LogApi(operation = "新增检测信息", format = "新增检测信息检测商户:{entity.merchant}", resultFormat = "")
	public Result<MktOriTestOnList> insOriTest(MktOriTestOnList entity) {
		return new Result<>(oriTestManager.insOriTest(entity));
	}

	@Override
	public Result<MktOriTestOnList> getOriTest(Integer pkey) {
		return new Result<>(oriTestManager.getOriTest(pkey));
	}

	@Override
	public Result<PageResult<MktOriTestOnList>> queryOriTest(int page, int pagesize, String merchant, 
			Date startDate, Date endDate, String goods, String entry, Boolean testResult) {
		return new Result<>(oriTestManager.queryOriTest(page, pagesize, merchant, startDate, endDate, goods, entry, testResult, true));
	}

	@Override
	@LogApi(operation = "修改检测信息", format = "修改检测信息检测商户:{merchant}")
	public Result<MktOriTestOnList> updOriTest(Integer pkey, String merchant, String goods, String entry,
			Boolean testResult) {
		return new Result<>(oriTestManager.updOriTest(pkey, merchant, goods, entry, testResult));
	}

	@Override
	@LogApi(operation = "删除检测信息", format = "删除检测信息")
	public Result<Boolean> delOriTest(Integer pkey) {
		return new Result<>(oriTestManager.delOriTest(pkey));
	}
	
	@LogApi(operation = "导入检测信息excel")
	@PostMapping(value = "/importexcel")
	public Result<Boolean> importExcel(MultipartFile myfile, HttpServletResponse response)
	{
		List<List<Object>> lists;
		List<MktOriTest> otList = new ArrayList<>();
		String farmer = CurrentSession.marketPkey();
		String company = CurrentSession.companyPkey();
		try {
			lists = ExcelUtils.getUserListByExcel(myfile.getInputStream(), myfile.getOriginalFilename());
	        
	        for (int i = 0; i < lists.size(); i++) {
	        	MktOriTest mot = new MktOriTest();
        		Object object = lists.get(i);
        		log.info("object: {}", object);
        		
        		if(StringUtils.isBlank(lists.get(i).get(0).toString()) ||
        				StringUtils.isBlank(lists.get(i).get(1).toString()) ||
        				StringUtils.isBlank(lists.get(i).get(2).toString()) ||
        				StringUtils.isBlank(lists.get(i).get(3).toString()) ||
        				StringUtils.isBlank(lists.get(i).get(4).toString()))
            			continue;
        		
        		mot.setMerchant(lists.get(i).get(0).toString());
        		mot.setGoods(lists.get(i).get(1).toString().trim());
        		mot.setEntry(lists.get(i).get(2).toString());
        		String l3 = lists.get(i).get(3).toString().trim().toLowerCase();
        		if("合格".equals(l3) || "true".equals(l3))
        			mot.setTestResult(true);
        		else
        			mot.setTestResult(false);
        		mot.setTestDate(DateUtil.formatDateStr(lists.get(i).get(4).toString(), "yyyy-MM-dd"));
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
		Boolean importExcel = oriTestManager.importExcel(otList);
		return new Result<>(importExcel);
	}
	
	
	@Operation(summary = "检测信息模板下载", tags = "检测信息")
	@GetMapping(value = "/down/template")
	public Result<Boolean> downTemplate(HttpServletRequest request, HttpServletResponse response)
	{
		FileUtil.buildExcelDocument("jc.xlsx", "检查信息模板.xlsx", "/data/tofocus/server/zyysc", request, response);
		return new Result<>(true);
	}
	
	@GetMapping("/down")
	public Result<Boolean> downExcel(HttpServletRequest request,HttpServletResponse response) {
		BufferedImage img = null;
		try {
			img = FileUtil.createImage("http://www.baidu.com", 500, 500);
		} catch (Exception e) {
			e.printStackTrace();
		}
		FileUtil.buildExcelDocument("gg", img, request, response);
		return new Result<>(true);
	}

	
}
