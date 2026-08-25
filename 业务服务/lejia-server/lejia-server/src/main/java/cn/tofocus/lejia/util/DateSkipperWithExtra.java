package cn.tofocus.lejia.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.MonthDay;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.alibaba.fastjson.JSONObject;

import cn.tofocus.common.util.HttpRequestBuilder1;
import cn.tofocus.common.util.HttpUtil;
import cn.tofocus.core.json.JsonUtil;

public class DateSkipperWithExtra
{
    public static void main(String[] args) {
//        LocalDate startDate = LocalDate.of(2025, 1, 1); // 开始日期
//        LocalDate endDate = LocalDate.of(2025, 12, 31); // 结束日期
//        LocalDate currentDate = startDate;
// 
//        while (!currentDate.isAfter(endDate)) {
//            if (isWeekendOrHoliday(currentDate)) 
//            {
//                System.out.println("节假日: " + currentDate);
//            }
//            currentDate = currentDate.plusDays(1);
//        }
        
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("User-Agent", "");
//        String exec = HttpUtil.forString("https://timor.tech/api/holiday/year/2025")
//             .get()
//             .headers(headers)
//             .exec();
//        System.out.println("object: " + exec);
        String s = "{\"code\":0,\"holiday\":{\"01-01\":{\"holiday\":true,\"name\":\"元旦\",\"wage\":3,\"date\":\"2025-01-01\",\"rest\":29},\"01-26\":{\"holiday\":false,\"name\":\"春节前补班\",\"wage\":1,\"after\":false,\"target\":\"春节\",\"date\":\"2025-01-26\",\"rest\":11},\"01-28\":{\"holiday\":true,\"name\":\"除夕\",\"wage\":2,\"date\":\"2025-01-28\",\"rest\":13},\"01-29\":{\"holiday\":true,\"name\":\"初一\",\"wage\":3,\"date\":\"2025-01-29\",\"rest\":1},\"01-30\":{\"holiday\":true,\"name\":\"初二\",\"wage\":3,\"date\":\"2025-01-30\",\"rest\":1},\"01-31\":{\"holiday\":true,\"name\":\"初三\",\"wage\":3,\"date\":\"2025-01-31\",\"rest\":1},\"02-01\":{\"holiday\":true,\"name\":\"初四\",\"wage\":2,\"date\":\"2025-02-01\",\"rest\":1},\"02-02\":{\"holiday\":true,\"name\":\"初五\",\"wage\":2,\"date\":\"2025-02-02\",\"rest\":1},\"02-03\":{\"holiday\":true,\"name\":\"初六\",\"wage\":2,\"date\":\"2025-02-03\",\"rest\":1},\"02-04\":{\"holiday\":true,\"name\":\"初七\",\"wage\":2,\"date\":\"2025-02-04\",\"rest\":1},\"02-08\":{\"holiday\":false,\"name\":\"春节后补班\",\"wage\":1,\"target\":\"春节\",\"after\":true,\"date\":\"2025-02-08\",\"rest\":2},\"04-04\":{\"holiday\":true,\"name\":\"清明节\",\"wage\":3,\"date\":\"2025-04-04\",\"rest\":8},\"04-05\":{\"holiday\":true,\"name\":\"清明节\",\"wage\":2,\"date\":\"2025-04-05\",\"rest\":1},\"04-06\":{\"holiday\":true,\"name\":\"清明节\",\"wage\":2,\"date\":\"2025-04-06\",\"rest\":1},\"04-27\":{\"holiday\":false,\"name\":\"劳动节前补班\",\"wage\":1,\"target\":\"劳动节\",\"after\":false,\"date\":\"2025-04-27\",\"rest\":20},\"05-01\":{\"holiday\":true,\"name\":\"劳动节\",\"wage\":3,\"date\":\"2025-05-01\",\"rest\":24},\"05-02\":{\"holiday\":true,\"name\":\"劳动节\",\"wage\":2,\"date\":\"2025-05-02\",\"rest\":1},\"05-03\":{\"holiday\":true,\"name\":\"劳动节\",\"wage\":3,\"date\":\"2025-05-03\",\"rest\":1},\"05-04\":{\"holiday\":true,\"name\":\"劳动节\",\"wage\":3,\"date\":\"2025-05-04\",\"rest\":1},\"05-05\":{\"holiday\":true,\"name\":\"劳动节\",\"wage\":3,\"date\":\"2025-05-05\",\"rest\":1},\"05-31\":{\"holiday\":true,\"name\":\"端午节\",\"wage\":3,\"date\":\"2025-05-31\",\"rest\":4},\"06-01\":{\"holiday\":true,\"name\":\"端午节\",\"wage\":2,\"date\":\"2025-06-01\",\"rest\":1},\"06-02\":{\"holiday\":true,\"name\":\"端午节\",\"wage\":2,\"date\":\"2025-06-02\",\"rest\":1},\"09-28\":{\"holiday\":false,\"name\":\"国庆节前补班\",\"after\":false,\"wage\":1,\"target\":\"国庆节\",\"date\":\"2025-09-28\",\"rest\":74},\"10-01\":{\"holiday\":true,\"name\":\"国庆节\",\"wage\":3,\"date\":\"2025-10-01\",\"rest\":77},\"10-02\":{\"holiday\":true,\"name\":\"国庆节\",\"wage\":3,\"date\":\"2025-10-02\",\"rest\":1},\"10-03\":{\"holiday\":true,\"name\":\"国庆节\",\"wage\":3,\"date\":\"2025-10-03\",\"rest\":1},\"10-04\":{\"holiday\":true,\"name\":\"国庆节\",\"wage\":2,\"date\":\"2025-10-04\",\"rest\":1},\"10-05\":{\"holiday\":true,\"name\":\"国庆节\",\"wage\":2,\"date\":\"2025-10-05\",\"rest\":1},\"10-06\":{\"holiday\":true,\"name\":\"中秋节\",\"wage\":2,\"date\":\"2025-10-06\",\"rest\":1},\"10-07\":{\"holiday\":true,\"name\":\"国庆节\",\"wage\":2,\"date\":\"2025-10-07\",\"rest\":1},\"10-08\":{\"holiday\":true,\"name\":\"国庆节\",\"wage\":2,\"date\":\"2025-10-08\",\"rest\":1},\"10-11\":{\"holiday\":false,\"after\":true,\"wage\":1,\"name\":\"国庆节后补班\",\"target\":\"国庆节\",\"date\":\"2025-10-11\"}}}";
        JSONObject jo = JSONObject.parseObject(s);
        Map<String,JSONObject> map = new HashMap<>();
        JSONObject jsonObject = jo.getJSONObject("holiday");
        System.out.println("jsonObject: " + JsonUtil.toString(jsonObject.keySet(), true));
    }
 
    private static Boolean isWeekendOrHoliday(LocalDate date) {
        if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) 
        {
            // 是周末
            return true; 
        }
        return false;
    }
    
}
