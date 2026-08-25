package cn.tofocus.lejia.app.v1.courier;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.common.data.datadealer.MobileDealer;
import cn.tofocus.core.Result;
import cn.tofocus.core.exception.SysErrCode;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.lejia.app.AppTags;
import cn.tofocus.lejia.domain.app.AppCourierManager;
import io.swagger.v3.oas.annotations.Operation;

@RequestMapping("/v1/app/courierlogin")
@RestController
public class AppCourierLoginApiImpl {

	@Autowired
	private AppCourierManager courierManger;

	/**
	 * 获取手机登陆验证码
	 * 
	 * @param phone
	 */
	@Operation(summary = "获取手机登陆验证码", tags = AppTags.mobileCourier)
	@PostMapping(value = "/captcha")
	public Result<Boolean> loginCaptcha(@RequestParam("phone") String phone) {
		Boolean result = courierManger.createCaptcha(checkPhone(phone));
		return new Result<>(result);
	}

	/**
	 * 验证手机号码
	 * <p/>
	 * <功能详细描述>
	 *
	 * @param phone
	 * @return
	 */
	private String checkPhone(String phone) {
		MobileDealer dealer = new MobileDealer();
		String result = dealer.convert(phone);
		if (result == null || result.length() == 0) {
			throw TofocusException.of(SysErrCode.PHONE_ERROR, phone);
		} else {
			return result;
		}
	}

	@Operation(summary = "骑手登录", tags = AppTags.mobileCourier)
	@PostMapping(value = "/login")
	public Result<Boolean> login(@RequestParam("phone") String phone, @RequestParam("captcha") String captcha,
			HttpServletRequest request) {
		String openid = request.getHeader("openid");
		courierManger.checkCaptcha(phone, captcha, openid);
		return new Result<>(true);
	}
	
	
	@Operation(summary = "登录状态查询", tags = AppTags.mobileCourier)
	@PostMapping(value = "/checkLogin")
	public Result<Boolean> checkLogin(HttpServletRequest request) {
		String openid = request.getHeader("openid");
		return new Result<>(courierManger.checkLogin(openid));
	}
	
	
}
