package cn.tofocus.lejia.api.v1.market;

import cn.tofocus.common.excel.ExcelHelper;
import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.core.Result;
import cn.tofocus.core.data.TreeModel;
import cn.tofocus.core.log.LogApi;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.MktSupplyExcel;
import cn.tofocus.lejia.bean.dto.market.*;
import cn.tofocus.lejia.bean.enums.MType;
import cn.tofocus.lejia.domain.market.SupplyManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

@RequestMapping("/v1/market/supply")
@Controller
@Slf4j
public class SupplyApiImpl implements SupplyApi
{
	/**
	 * 本模块manager类
	 */
	@Resource
    private SupplyManager manager;

	@Resource
	private ExcelHelper excelHelper;

	/**
	 * 商品供应库列表查询
	 * @param param 		请求参数
	 * @return				结果
	 */
	@Override
	@ResponseBody
	public Result<PageResult<MktSupplyOnList>> pageList(MktSupplyParamDTO param)
	{
		return new Result<>(manager.pageList(param));
	}

	/**
	 * 获取运营端-派单配置
	 * @return 结果
	 */
	@Override
	@ResponseBody
	public Result<SupplySendConfDTO> getConf()
	{
		return new Result<>(manager.getConf());
	}

	/**
	 * 修改运营端-派单配置
	 * @param upd 参数
	 * @return 结果
	 */
	@Override
	@ResponseBody
	@LogApi(operation = "修改运营端-派单配置", format = "派单配置：{upd.isOperation}",
		resultFormat = "派单配置（false-市场自定义，true-统一配置；" +
			"统一配置是人工还是自动（false-人工，true-自动）")
	public Result<Boolean> updSendConf(SupplySendConfDTO upd)
	{
		return new Result<>(manager.updSendConf(upd));
	}

	/**
	 * 当前市场能否增删改商品供应库
	 * @return  结果
	 */
	@Override
	@ResponseBody
	public Result<Boolean> isManipulation()
	{
		return new Result<>(manager.isManipulation());
	}

	/**
	 * 商品供应库数据删除（不支持删除整条商品信息）
	 * @param pkeys		主键列表
	 * @return			结果
	 */
	@Override
	@ResponseBody
	@LogApi(operation = "商品供应库数据删除", format = "商品供应库数据删除：{pkeys}")
	public Result<Boolean> del(List<Integer> pkeys)
	{
		return new Result<>(manager.del(pkeys));
	}

	/**
	 * 根据商品删除商品供应库信息
	 * @param goodPkeys		商品主键
	 * @return				结果
	 */
	@Override
	@ResponseBody
	@LogApi(operation = "根据商品删除商品供应库信息", format = "根据商品删除商品供应库信息：{goodPkeys}")
	public Result<Boolean> delByGoods(List<Integer> goodPkeys)
	{
		return new Result<>(manager.delByGoods(goodPkeys));
	}

	/**
	 * 导出商品供应库数据
	 * @param param	    请求参数
	 * @param response	原生响应对象
	 */
	@Override
	@LogApi(operation = "导出商品供应库数据", format = "导出商品供应库数据")
	public void exportSupply(MktSupplyParamDTO param, HttpServletResponse response)
	{
		OutputStream out = null;
		try
		{
			// 设置响应信息
			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf8");
			// 前端UrlDecode解码即可
			response.setHeader("Content-Disposition",
				"attachment; filename=" + java.net.URLEncoder.encode("商品供应库清单", "UTF-8") + ".xlsx");
			response.addHeader("Pragma", "no-cache");
			response.addHeader("Cache-Control", "no-cache");
			out = response.getOutputStream();

			// 复用分页查询逻辑，设置参数
			param.setPage(0);
			// 2^16次方 = 65536条
			Double aDouble = Math.pow(2, 16);
			int aInt = aDouble.intValue();
			param.setPagesize(aInt);
			// 获取数据列表
			PageResult<MktSupplyOnList> result = manager.pageList(param);
			// 数据DTO列表 -> Excel列表
			List<MktSupplyExcel> list = new ArrayList<>();
			for (MktSupplyOnList dto : result.getContent())
			{
				// 一对多detail的数据，每条拆分成一条
				for (MktSupplyPageDetail detail : dto.getDetails())
				{
					// 复制外层数据
					MktSupplyExcel excel = BeanUtil.beanFrom(MktSupplyExcel.class, dto);
					// 设置detail的单条数据
					BeanUtils.copyProperties(detail, excel);
					excel.setPurchasingPrice(detail.getPurchasingPrice().toString());
					excel.setEnabled(detail.getEnabled() ? "是" : "否");
					excel.setSort(detail.getSort().toString());

					// 商户是否存在
					Boolean isExist = detail.getIsExist();
					if (!isExist){
						excel.setVendorName(excel.getVendorName() + "（商户不存在）");
					}

					log.info("导出商品供应库数据：{}", excel);
					list.add(excel);
				}
			}
			excelHelper.exportExcel(list, "Sheet1", out, MktSupplyExcel.class, null);
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
	 * 商品供应库——商品树形列表
	 * @param marketPkey  市场pkey
	 * @return		   	  结果
	 */
	@Override
	@ResponseBody
	public Result<List<TreeModel<Integer, MktSupplyGoodsInfo>>> goodsList(MType mType, String marketPkey)
	{
		return new Result<>(manager.goodsList(mType, marketPkey));
	}

	/**
	 * 商品供应库——规格列表
	 * @return		   结果
	 */
	@Override
	@ResponseBody
	public Result<List<MktSupplySpaceInfo>> spaceList(Integer goodsPkey)
	{
		return new Result<>(manager.spaceList(goodsPkey));
	}

	/**
	 * 商品供应库——供应商列表
	 * @param marketPkey 市场pkey
	 * @return		     结果
	 */
	@Override
	@ResponseBody
	public Result<List<MktSupplyVendorInfo>> vendorList(String marketPkey)
	{
		return new Result<>(manager.vendorList(marketPkey));
	}

	/**
	 * 商品供应库——商品明细
	 * @param marketPkey    市场pkey
	 * @param goodsPkey		商品pkey
	 * @return				结果
	 */
	@Override
	@ResponseBody
	public Result<MktSupplyInfo> detail(String marketPkey, Integer goodsPkey)
	{
		return new Result<>(manager.detail(marketPkey, goodsPkey));
	}

	/**
	 * 商品供应库——新增
	 * @param mktSupplyInfo	商品供应库——单项信息
	 * @return				结果
	 */
	@Override
	@ResponseBody
	@LogApi(operation = "商品供应库——新增", format = "商品供应库——新增：{mktSupplyInfo.goodsName}")
	public Result<Boolean> insert(MktSupplyInfo mktSupplyInfo)
	{
		return new Result<>(manager.insert(mktSupplyInfo, true));
	}

	/**
	 * 商品供应库——更新
	 * @param mktSupplyInfo	商品供应库——单项信息
	 * @return				结果
	 */
	@Override
	@ResponseBody
	@LogApi(operation = "商品供应库——更新", format = "商品供应库——更新：{mktSupplyInfo.goodsPkey}")
	public Result<Boolean> update(MktSupplyInfo mktSupplyInfo)
	{
		return new Result<>(manager.update(mktSupplyInfo, true));
	}

	/**
	 * 商品供应库启用/停用
	 * @param pkey	商品供应库单项数据pkey
	 * @return		是否成功
	 */
	@Override
	@ResponseBody
	@LogApi(operation = "商品供应库启用/停用", format = "商品供应库启用/停用：{pkey}")
	public Result<Boolean> enable(Integer pkey)
	{
		return new Result<>(manager.enable(pkey));
	}

	/**
	 * @return 商品供应库-运营端是否开启统一配置
	 */
	@Override
	@ResponseBody
	public Result<Boolean> isGoodSupply()
	{
		return new Result<>(manager.isGoodSupply());
	}

	/**
	 * @return 商品供应库-是否系统自动派单
	 */
	@Override
	@ResponseBody
	public Result<Boolean> isGoodPurchaseDeploy()
	{
		return new Result<>(manager.isGoodPurchaseDeploy());
	}

	/**
	 * 导入商品供应库
	 * @param myfile   模板文件
	 */
//	@Override
//	@PostMapping(value = "/import")
//	@LogApi(operation = "导入商品供应库", format = "导入商品供应库")
//	public void importExcel(MultipartFile myfile, HttpServletResponse response)
//	{
//		manager.importExcel(myfile, response);
//	}

//	@Resource
//  	private VendorOrderManager vendorOrderManager;
//
//	@Operation(summary = "测试自动采购", tags = ApiTags.marketGoodsSupply)
//	@PostMapping(value = "/autoPurchase")
//	@ResponseBody
//	public Result<Boolean> autoPurchase(int orderPkey){
//	  Boolean res = vendorOrderManager.autoPurchase(orderPkey);
//	  return new Result<>(res);
//	}
//
//	@Operation(summary = "测试重置商品供应库顺序", tags = ApiTags.marketGoodsSupply)
//	@PostMapping(value = "/resetSupplyOrder")
//	@ResponseBody
//	public Result<Boolean> resetSupplyOrder(){
//		Boolean res = vendorOrderManager.resetSupplyOrder();
//		return new Result<>(res);
//	}
}
