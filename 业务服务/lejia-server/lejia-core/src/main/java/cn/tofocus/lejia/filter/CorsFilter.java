//package cn.tofocus.lejia.filter;
//
//import java.io.IOException;
//
//import javax.servlet.Filter;
//import javax.servlet.FilterChain;
//import javax.servlet.FilterConfig;
//import javax.servlet.ServletException;
//import javax.servlet.ServletRequest;
//import javax.servlet.ServletResponse;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.springframework.stereotype.Component;
//
//@Component
//public class CorsFilter implements Filter{
//
//	@Override
//	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
//			throws IOException, ServletException {
//		HttpServletRequest request = (HttpServletRequest) req;  
//		String header = request.getHeader("Origin");
//		HttpServletResponse response = (HttpServletResponse) res;  
//		if (header != null || !"".equals(header)) {
//			response.setHeader("Access-Control-Allow-Origin", header);
//		}else {
//			response.setHeader("Access-Control-Allow-Origin", "*");
//		}
//		response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE,PUT");
//		response.setHeader("Access-Control-Max-Age", "3600");
//		response.setHeader("Access-Control-Allow-Headers", "Authorization,X-Requested-With,Content-Type");
//		response.setHeader("Access-Control-Allow-Credentials","true");
//        chain.doFilter(req, res);  
//	}
//	
//	public void init(FilterConfig filterConfig) {}  
//    public void destroy() {} 
//
//}
