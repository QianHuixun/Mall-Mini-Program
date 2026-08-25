package cn.tofocus.lejia.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.time.DateFormatUtils;

import cn.tofocus.common.util.CollectionUtil;
import cn.tofocus.common.util.StringUtil;
import cn.tofocus.common.util.date.NumberDateUnit;
import cn.tofocus.common.util.date.NumberItem;
import cn.tofocus.core.exception.SysErrCode;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.log.SimpleLog;

public class DateUtil
{
    private static DateUtil instance = new DateUtil();
    
    private String pattern32 = "^\\d{1,2}-\\d{1,2}-\\d{2}$";
    
    private Set<Character> timeunits;
    
    private Map<Character, Character> digitMap;
    
    private DateUtil()
    {
        timeunits = new HashSet<>();
        timeunits.add('星');
        timeunits.add('期');
        timeunits.add('年');
        timeunits.add('月');
        timeunits.add('日');
        timeunits.add('时');
        timeunits.add('点');
        timeunits.add('分');
        timeunits.add('秒');
        timeunits.add('上');
        timeunits.add('下');
        timeunits.add('午');
        timeunits.add('a');
        timeunits.add('p');
        timeunits.add('m');
        
        digitMap = new HashMap<>();
        digitMap.put('一', '1');
        digitMap.put('二', '2');
        digitMap.put('三', '3');
        digitMap.put('四', '4');
        digitMap.put('五', '5');
        digitMap.put('六', '6');
        digitMap.put('七', '7');
        digitMap.put('八', '8');
        digitMap.put('九', '9');
        digitMap.put('零', '0');
        digitMap.put('两', '2');
        digitMap.put('1', '1');
        digitMap.put('2', '2');
        digitMap.put('3', '3');
        digitMap.put('4', '4');
        digitMap.put('5', '5');
        digitMap.put('6', '6');
        digitMap.put('7', '7');
        digitMap.put('8', '8');
        digitMap.put('9', '9');
        digitMap.put('0', '0');
        
    }
    
    /**
     * 格式化时间
     * @param date
     * @param pattern
     * @return
     */
    public static String formatDate(Date date, String pattern)
    {
        if (null == date || null == pattern) return null;
        return DateFormatUtils.format(date, pattern);
    }
    
    /**
     * 格式化时间
     * @param date
     * @return
     */
    public static String formatDate(Date date)
    {
        if (null == date) return null;
        return DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss");
    }
    
    /**
     * 对任意时间格式的文本，尽量尝试着解析出时间
     * @param datestr
     * @param pattern
     * @return
     */
    public static Date formatDateStr(String datestr, String pattern)
    {
        if (null == datestr || null == pattern || datestr.length() == 0) return null;
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        Date da = null;
        try
        {
            da = format.parse(datestr);
        }
        catch (ParseException e)
        {
            throw TofocusException.of(SysErrCode.REQUIRED_PRARAM_ERROR, e, "日期数据解析失败：%s %s", pattern, datestr);
        }
        return da;
    }
    
    public static Date formatDateStr(String datestr)
        throws Exception
    {
        if (null == datestr) return null;
        Date trydate = null;
        try
        {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            trydate = format.parse(datestr);
        }
        catch (Exception e)
        {
        }
        if (trydate != null) return trydate;
        
        try
        {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            trydate = format.parse(datestr);
        }
        catch (Exception e)
        {
        }
        if (trydate != null) return trydate;
        
        try
        {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            trydate = format.parse(datestr);
        }
        catch (Exception e)
        {
        }
        if (trydate != null) return trydate;
        
        //格式意义不明，例如”06-10-15“，很难知道这个是”2006年10月15日“，还是”2015年6月10日“，还是”2015年10月6日“?
        //当遇到类似06-10月-15格式的时间时，统一按日-月-年意义的格式去做转换。
        if (Pattern.matches(instance.pattern32, datestr))
        {
            try
            {
                String[] splites = datestr.split("-");
                int d1 = Integer.valueOf(splites[0]);
                int d2 = Integer.valueOf(splites[1]);
                int d3 = Integer.valueOf(splites[2]);
                if (d1 <= 12)
                {
                    if (d2 <= 12)
                    {
                        if (d3 <= 12)
                        {
                            trydate = DateUtil.formatDateStr(datestr, "dd-MM-yy");//此格式优先
                        }
                        else if (d3 <= 31)
                        {
                            trydate = DateUtil.formatDateStr(datestr, "yy-MM-dd");
                        }
                    }
                    else if (d2 <= 31)
                    {
                        if (d3 <= 12)
                        {
                            trydate = DateUtil.formatDateStr(datestr, "MM-dd-yy");
                        }
                        else if (d3 <= 31)
                        {
                            trydate = DateUtil.formatDateStr(datestr, "MM-dd-yy");
                            
                        }
                    }
                }
                else
                {
                    if (d2 <= 12)
                    {
                        if (d3 <= 12)
                        {
                            trydate = DateUtil.formatDateStr(datestr, "dd-MM-yy");//此格式优先
                        }
                        else if (d3 <= 31)
                        {
                            trydate = DateUtil.formatDateStr(datestr, "yy-MM-dd");
                        }
                    }
                }
            }
            catch (Exception e)
            {
            }
            if (trydate != null) return trydate;
        }
        else if (Pattern.matches("^\\d{1,2}-\\d{1,2}月-\\d{2}$", datestr))
        {
            try
            {
                trydate = DateUtil.formatDateStr(datestr, "dd-MM月-yy");
            }
            catch (Exception e)
            {
            }
            if (trydate != null) return trydate;
        }
        
        String dateString = null;
        Date d = null;
        Boolean isPM = null;
        try
        {
            //有中文单位的数字
            ArrayList<NumberItem> years = new ArrayList<>();
            ArrayList<NumberItem> months = new ArrayList<>();
            ArrayList<NumberItem> days = new ArrayList<>();
            ArrayList<NumberItem> hours = new ArrayList<>();
            ArrayList<NumberItem> minutes = new ArrayList<>();
            ArrayList<NumberItem> seconds = new ArrayList<>();
            ArrayList<NumberItem> weeks = new ArrayList<>();
            ArrayList<NumberItem> others = new ArrayList<>(); //无单位数字
            //数字
            ArrayList<NumberItem> numbers = new ArrayList<>();
            //分割
            ArrayList<String> spans = new ArrayList<>();
            dateString = StringUtil.full2HalfChange2(datestr).trim().toLowerCase();
            int numberindex = 0;
            int index = 0;
            int tenindex = -1000;
            StringBuilder numberBuf = new StringBuilder();
            StringBuilder spanBuf = new StringBuilder();
            //分离数字和非数字
            while (index < dateString.length())
            {
                while (index < dateString.length())
                {
                    char c = dateString.charAt(index);
                    if (isDigit(c))
                    {
                        break;
                    }
                    else if (c == '十')
                    {
                        numberBuf.append('1');
                        tenindex = index;
                        index++;
                        break;
                    }
                    else
                    {
                        spanBuf.append(c);
                        index++;
                    }
                }
                spans.add(spanBuf.toString());
                while (index < dateString.length())
                {
                    char c = dateString.charAt(index);
                    if (isDigit(c))
                    {
                        numberBuf.append(convertDigit(c));
                        index++;
                    }
                    else if (c == '十')
                    {
                        tenindex = index;
                        index++;
                    }
                    else
                    {
                        if (tenindex + 1 == index)
                        {
                            numberBuf.append('0');
                        }
                        break;
                    }
                }
                if (numberBuf.length() > 0)
                {
                    String content = numberBuf.toString();
                    NumberItem n = new NumberItem(numberindex, content);
                    numbers.add(n);
                    numberindex++;
                }
                tenindex = -1000;
                numberBuf = new StringBuilder();
                spanBuf = new StringBuilder();
            }
            //去除多余字符
            ArrayList<String> tspans = new ArrayList<>();
            for (int i = 0; i < spans.size(); i++)
            {
                int idx = 0;
                StringBuilder sb = new StringBuilder();
                while (idx < spans.get(i).length())
                {
                    char c = spans.get(i).charAt(idx);
                    if (instance.timeunits.contains(c)) sb.append(c);
                    idx++;
                }
                tspans.add(sb.toString());
            }
            spans = tspans;
            //提取明确的时间
            for (int i = 0; i < numbers.size(); i++)
            {
                NumberItem n = numbers.get(i);
                String prespan = "";
                String afterspan = "";
                if (i < spans.size()) prespan = spans.get(i);
                if (i + 1 < spans.size()) afterspan = spans.get(i + 1);
                if ((prespan.endsWith("下午") || prespan.endsWith("pm"))
                    || (afterspan.endsWith("下午") || afterspan.endsWith("pm")))
                    isPM = true;
                else if ((prespan.endsWith("上午") || prespan.endsWith("am"))
                    || (afterspan.endsWith("上午") || afterspan.endsWith("am"))) isPM = false;
                if (afterspan.startsWith("年"))
                {
                    n.setUnit(NumberDateUnit.年);
                    years.add(n);
                }
                else if (afterspan.startsWith("月"))
                {
                    n.setUnit(NumberDateUnit.月);
                    months.add(n);
                }
                else if (afterspan.startsWith("日"))
                {
                    n.setUnit(NumberDateUnit.日);
                    days.add(n);
                }
                else if (afterspan.startsWith("时"))
                {
                    n.setUnit(NumberDateUnit.时);
                    hours.add(n);
                }
                else if (afterspan.startsWith("点"))
                {
                    n.setUnit(NumberDateUnit.时);
                    hours.add(n);
                }
                else if (afterspan.startsWith("分"))
                {
                    n.setUnit(NumberDateUnit.分);
                    minutes.add(n);
                }
                else if (afterspan.startsWith("秒"))
                {
                    n.setUnit(NumberDateUnit.秒);
                    seconds.add(n);
                }
                else if (prespan.endsWith("星期"))
                {
                    n.setUnit(NumberDateUnit.星期);
                    weeks.add(n);
                }
                else
                {
                    others.add(n);
                }
            }
            
            //组合时间
            if (years.isEmpty())
            {
                others = guessYMDorder(others);
                String smonth = null;
                String sday = null;
                String shour = null;
                String sminute = null;
                String ssecond = null;
                if (!months.isEmpty()) smonth = months.get(0).getContent();
                if (!days.isEmpty()) sday = days.get(0).getContent();
                if (!hours.isEmpty()) shour = hours.get(0).getContent();
                if (!minutes.isEmpty()) sminute = minutes.get(0).getContent();
                if (!seconds.isEmpty()) ssecond = seconds.get(0).getContent();
                d = ParseOtherDate(others, -1, 6, isPM, null, smonth, sday, shour, sminute, ssecond);
                if (d != null) return d;
            }
            else
            {
                for (int i1 = 0; i1 < years.size(); i1++)
                {
                    NumberItem year = years.get(i1);
                    String syear = year.getContent();
                    if (months.isEmpty())
                    {
                        d = ParseOtherDate(others, year.getIndex(), 1, isPM, syear, "01", "01", "00", "00", "00");
                        if (d != null) return d;
                    }
                    else
                    {
                        for (int i2 = 0; i2 < months.size(); i2++)
                        {
                            NumberItem month = months.get(i2);
                            String smonth = month.getContent();
                            if (days.isEmpty())
                            {
                                d = ParseOtherDate(others,
                                    month.getIndex(),
                                    2,
                                    isPM,
                                    syear,
                                    smonth,
                                    "01",
                                    "00",
                                    "00",
                                    "00");
                                if (d != null) return d;
                            }
                            else
                            {
                                for (int i3 = 0; i3 < days.size(); i3++)
                                {
                                    NumberItem day = days.get(i3);
                                    String sday = day.getContent();
                                    if (hours.isEmpty())
                                    {
                                        d = ParseOtherDate(others,
                                            day.getIndex(),
                                            3,
                                            isPM,
                                            syear,
                                            smonth,
                                            sday,
                                            "00",
                                            "00",
                                            "00");
                                        if (d != null) return d;
                                    }
                                    else
                                    {
                                        for (int i4 = 0; i4 < hours.size(); i4++)
                                        {
                                            NumberItem hour = hours.get(i4);
                                            String shour = null;
                                            int h = Integer.parseInt(hour.getContent());
                                            if (isPM == null)
                                            {
                                                shour = hour.getContent();
                                            }
                                            else if (isPM)
                                            {
                                                if (h != 12)
                                                    shour = String.valueOf(h + 12);
                                                else
                                                    shour = hour.getContent();
                                            }
                                            else
                                            {
                                                if (h == 12)
                                                    shour = "0";
                                                else
                                                    shour = hour.getContent();
                                            }
                                            if (minutes.isEmpty())
                                            {
                                                d = ParseOtherDate(others,
                                                    hour.getIndex(),
                                                    4,
                                                    isPM,
                                                    syear,
                                                    smonth,
                                                    sday,
                                                    shour,
                                                    "00",
                                                    "00");
                                                if (d != null) return d;
                                            }
                                            else
                                            {
                                                for (int i5 = 0; i5 < minutes.size(); i5++)
                                                {
                                                    NumberItem minute = minutes.get(i5);
                                                    String sminute = minute.getContent();
                                                    if (seconds.isEmpty())
                                                    {
                                                        d = ParseOtherDate(others,
                                                            minute.getIndex(),
                                                            5,
                                                            isPM,
                                                            syear,
                                                            smonth,
                                                            sday,
                                                            shour,
                                                            sminute,
                                                            "00");
                                                        if (d != null) return d;
                                                    }
                                                    else
                                                    {
                                                        for (int i6 = 0; i6 < seconds.size(); i6++)
                                                        {
                                                            NumberItem second = seconds.get(i6);
                                                            String ssecond = second.getContent();
                                                            d = ParseDate(syear, smonth, sday, shour, sminute, ssecond);
                                                            if (d != null) return d;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (d == null)
                throw TofocusException.of(SysErrCode.REQUIRED_PRARAM_ERROR, "时间类型转换错误");
            else
                return d;
        }
        catch (Exception e)
        {
            //            SimpleLog.outException(null, "日期数据解析失败, datestr=" + datestr, e);
            throw e;
        }
    }
    
    private static Date ParseOtherDate(ArrayList<NumberItem> others, int afterindex, int inisize, Boolean isPM,
        String... snumber)
    {
        if (others.isEmpty())
        {
            return ParseDate(snumber);
        }
        else
        {
            String[] s = new String[6];
            for (int i = 0; i < inisize; i++)
            {
                s[i] = snumber[i];
            }
            for (NumberItem number : others)
            {
                number.reset();
                while (!number.isguessfinished())
                {
                    number.guess(afterindex, s, isPM);
                    if (s[5] != null)
                    {
                        Date d = ParseDate(s);
                        if (d != null) return d;
                        s = new String[6];
                        for (int i = 0; i < inisize; i++)
                        {
                            s[i] = snumber[i];
                        }
                    }
                }
            }
            return ParseDate(s);
        }
    }
    
    //猜想年月日顺序
    private static ArrayList<NumberItem> guessYMDorder(ArrayList<NumberItem> list)
    {
        ArrayList<NumberItem> result = new ArrayList<>();
        if (list.isEmpty())
            return list;
        else if (list.size() == 1)
            return list;
        else if (list.size() == 2)
        {
            if (list.get(0).getContent().length() < list.get(1).getContent().length())
            {
                result.add(list.get(1));
                result.add(list.get(0));
            }
        }
        else
        {
            if (list.get(0).getContent().length() == 4)
            {
                result.add(list.get(0));
                int n2 = Integer.parseInt(list.get(1).getContent());
                int n3 = Integer.parseInt(list.get(2).getContent());
                if (n2 > 12 && n3 <= 12)
                {
                    result.add(list.get(2));
                    result.add(list.get(1));
                }
                else
                {
                    result.add(list.get(1));
                    result.add(list.get(2));
                }
            }
            else if (list.get(0).getContent().length() > 4)
            {
                return list;
            }
            else
            {
                int n2 = Integer.parseInt(list.get(1).getContent());
                int n3 = Integer.parseInt(list.get(2).getContent());
                if (n2 > 12)
                {
                    result.add(list.get(2));
                    result.add(list.get(0));
                    result.add(list.get(1));
                }
                else if (n3 > 31)
                {
                    result.add(list.get(2));
                    result.add(list.get(1));
                    result.add(list.get(0));
                }
                else
                {
                    result.add(list.get(0));
                    result.add(list.get(1));
                    result.add(list.get(2));
                }
            }
            for (int i = 3; i < list.size(); i++)
            {
                result.add(list.get(i));
            }
        }
        return result;
    }
    
    private static Date ParseDate(String... numberItems)
    {
        if (numberItems.length < 6) return null;
        for (int i = 0; i < numberItems.length; i++)
        {
            if (numberItems[i] == null)
            {
                if (i == 0)
                    return null;
                else if (i < 3)
                    numberItems[i] = "01";
                else
                    numberItems[i] = "00";
            }
            //                return null;
        }
        if (numberItems[0].length() == 1) numberItems[0] = "0" + numberItems[0];
        StringBuffer sb = new StringBuffer().append(numberItems[0]).append("-");
        sb.append(numberItems[1]).append("-");
        sb.append(numberItems[2]).append(" ");
        sb.append(numberItems[3]).append(":");
        sb.append(numberItems[4]).append(":");
        sb.append(numberItems[5]);
        Date d = null;
        if (numberItems[0].length() <= 2)
            d = ParseDate(sb.toString(), "yy-MM-dd HH:mm:ss");
        else
            d = ParseDate(sb.toString(), "yyyy-MM-dd HH:mm:ss");
        return d;
    }
    
    private static Date ParseDate(String s, String pattern)
    {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        format.setLenient(false);
        Date da = null;
        try
        {
            da = format.parse(s);
        }
        catch (ParseException e)
        {
            format = new SimpleDateFormat(pattern + "z");
            try
            {
                da = format.parse(s + "CST");
            }
            catch (ParseException e1)
            {
                SimpleLog.outException(null, "日期数据解析失败, datestr=" + s, e);
            }
        }
        return da;
    }
    
    public static boolean isDigit(char c)
    {
        return instance.digitMap.containsKey(c);
    }
    
    public static String convertDigit(char c)
    {
        return instance.digitMap.get(c).toString();
    }
    
    /**
     * 比较时间大小，如果返回值{@literal >}0表示d1{@literal >}d2,返回值 = 0 表示 d1 = d2 
     * @param d1
     * @param d2
     * @return
     */
    public static long compareDate(Date d1, Date d2)
    {
        if (d1 == null || d2 == null) return 0;
        
        return (d1.getTime() - d2.getTime());
    }
    
    /**
     * 比较日期大小，是否同一天，如果返回值{@literal >}0表示d1{@literal >}d2,返回值 = 0 表示 d1 = d2
     * @param date1
     * @param date2
     * @return
     */
    public static Boolean isSameDate(Date date1, Date date2)
    {
        if (date1 == null || date2 == null)
        {
            return false;
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        
        boolean isSameYear = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
        boolean isSameMonth = isSameYear && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
        boolean isSameDate = isSameMonth && cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
        
        return isSameDate;
    }
    
    /**
     * 根据Calendar里的field，获取时间对应的值
     * @param d
     * @param field :field的值包括: cal.HOUR; cal.DAY_OF_MONTH; cal.WEEK_OF_YEAR; 
     * @return
     */
    public static int getTimeFieldValue(Date d, int field)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        return cal.get(field);
    }
    
    public static String strDateTimeNow()
    {
        return formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
    }
    
    public static long parseTimeStrToSec(String timeStr)
    {
        
        if (StringUtil.isEmpty(timeStr))
        {
            return 0;
        }
        if (timeStr.matches("^(\\d{1,2})[:时](\\d{1,2})[:分](\\d{1,2})[秒]?$"))
        {
            Pattern pattern = Pattern.compile("^(\\d{1,2})[:时](\\d{1,2})[:分](\\d{1,2})[秒]?$");
            Matcher matcher = pattern.matcher(timeStr);
            long time = 0;
            if (matcher.find())
            {
                time += new Integer(matcher.group(1)) * 60 * 60;
                time += new Integer(matcher.group(2)) * 60;
                time += new Integer(matcher.group(3));
                return time;
            }
        }
        if (timeStr.matches("^\\d+[s秒]?$"))
        {
            if (timeStr.contains("s") || timeStr.contains("秒"))
            {
                return Long.parseLong(timeStr.substring(0, timeStr.length() - 1));
            }
            else
            {
                return Long.parseLong(timeStr);
            }
        }
        
        if (timeStr.matches("^\\d+[m分]$") || timeStr.matches("^\\d+分钟$"))
        {
            return Long.parseLong(timeStr.substring(0, timeStr.length() - 1)) * 60;
        }
        
        if (timeStr.matches("^(\\d+)分(\\d+)秒$"))
        {
            long min = Long.parseLong(timeStr.substring(0, timeStr.indexOf("分")));
            long sec = Long.parseLong(timeStr.substring(timeStr.indexOf("分") + 1, timeStr.indexOf("秒")));
            return min * 60 + sec;
        }
        
        if (timeStr.matches("^\\d+(时|小时)(\\d+)分$"))
        {
            long h = Long.parseLong(timeStr.substring(0, timeStr.indexOf("时")).replaceAll("\\D", ""));
            long min = Long.parseLong(timeStr.substring(timeStr.indexOf("时") + 1, timeStr.indexOf("分")));
            return h * 3600 + min * 60;
        }
        
        if (timeStr.matches("^\\d+(时|小时)(\\d+)分(\\d+)秒$"))
        {
            long h = Long.parseLong(timeStr.substring(0, timeStr.indexOf("时")).replaceAll("\\D", ""));
            long min = Long.parseLong(timeStr.substring(timeStr.indexOf("时") + 1, timeStr.indexOf("分")));
            long sec = Long.parseLong(timeStr.substring(timeStr.indexOf("分") + 1, timeStr.indexOf("秒")));
            return h * 3600 + min * 60 + sec;
        }
        
        return 0;
    }
    
    /**
     * 猜测时间的秒数
     * @param time
     * @return
     */
    public static long parseTimeSecond(String time)
    {
        long result = 0;
        try
        {
            if (time == null) return result;
            if (Pattern.matches("^\\d{1,2}:\\d{1,2}:\\d{1,2}$", time))
            {
                int hour = Integer.valueOf(time.substring(0, time.indexOf(":")));
                int minite = Integer.valueOf(time.substring(time.indexOf(":") + 1, time.lastIndexOf(":")));
                int sec = Integer.valueOf(time.substring(time.lastIndexOf(":") + 1));
                
                result = hour * 60L * 60 + minite * 60 + sec;
                return result;
            }
            ArrayList<NumberItem> hours = new ArrayList<>();
            ArrayList<NumberItem> minutes = new ArrayList<>();
            ArrayList<NumberItem> seconds = new ArrayList<>();
            ArrayList<NumberItem> others = new ArrayList<>(); //无单位数字
            //数字
            ArrayList<NumberItem> numbers = new ArrayList<>();
            //分割
            ArrayList<String> spans = new ArrayList<>();
            String dateString = StringUtil.full2HalfChange2(time).trim().toLowerCase();
            int numberindex = 0;
            int index = 0;
            int tenindex = -1000;
            StringBuffer numberBuf = new StringBuffer();
            StringBuffer spanBuf = new StringBuffer();
            //分离数字和非数字
            while (index < dateString.length())
            {
                while (index < dateString.length())
                {
                    char c = dateString.charAt(index);
                    if (DateUtil.isDigit(c))
                    {
                        break;
                    }
                    else if (c == '十')
                    {
                        numberBuf.append('1');
                        tenindex = index;
                        index++;
                        break;
                    }
                    else
                    {
                        spanBuf.append(c);
                        index++;
                    }
                }
                spans.add(spanBuf.toString());
                while (index < dateString.length())
                {
                    char c = dateString.charAt(index);
                    if (DateUtil.isDigit(c))
                    {
                        numberBuf.append(DateUtil.convertDigit(c));
                        index++;
                    }
                    else if (c == '十')
                    {
                        tenindex = index;
                        index++;
                    }
                    else
                    {
                        if (tenindex + 1 == index)
                        {
                            numberBuf.append('0');
                        }
                        break;
                    }
                }
                if (numberBuf.length() > 0)
                {
                    String content = numberBuf.toString();
                    NumberItem n = new NumberItem(numberindex, content);
                    numbers.add(n);
                    numberindex++;
                }
                tenindex = -1000;
                numberBuf = new StringBuffer();
                spanBuf = new StringBuffer();
            }
            //去除多余字符
            ArrayList<String> tspans = new ArrayList<>();
            for (int i = 0; i < spans.size(); i++)
            {
                int idx = 0;
                StringBuffer sb = new StringBuffer();
                while (idx < spans.get(i).length())
                {
                    char c = spans.get(i).charAt(idx);
                    if (instance.timeunits.contains(c)) sb.append(c);
                    idx++;
                }
                tspans.add(sb.toString());
            }
            spans = tspans;
            //提取明确的时间
            for (int i = 0; i < spans.size(); i++)
            {
                String span = spans.get(i);
                if (i > 0)
                {
                    if (span.startsWith("时"))
                    {
                        numbers.get(i - 1).setUnit(NumberDateUnit.时);
                        hours.add(numbers.get(i - 1));
                    }
                    else if (span.startsWith("点"))
                    {
                        numbers.get(i - 1).setUnit(NumberDateUnit.时);
                        hours.add(numbers.get(i - 1));
                    }
                    else if (span.startsWith("分"))
                    {
                        numbers.get(i - 1).setUnit(NumberDateUnit.分);
                        minutes.add(numbers.get(i - 1));
                    }
                    else if (span.startsWith("秒"))
                    {
                        numbers.get(i - 1).setUnit(NumberDateUnit.秒);
                        seconds.add(numbers.get(i - 1));
                    }
                    else
                    {
                        others.add(numbers.get(i - 1));
                    }
                }
            }
            if (CollectionUtil.isNotEmpty(hours))
            {
                for (NumberItem it : hours)
                {
                    int h = Integer.valueOf(it.getContent());
                    result += h * 60 * 60;
                }
            }
            if (CollectionUtil.isNotEmpty(minutes))
            {
                for (NumberItem it : minutes)
                {
                    int m = Integer.valueOf(it.getContent());
                    result += m * 60;
                }
            }
            if (CollectionUtil.isNotEmpty(seconds))
            {
                for (NumberItem it : seconds)
                {
                    int s = Integer.valueOf(it.getContent());
                    result += s;
                }
            }
        }
        catch (Exception e)
        {
            result = 0;
        }
        return result;
    }
    
    /**
     * 返回一天的0点
     * @return
     */
    public static Date atStartOfDay(String day)
    {
        if (StringUtil.isEmpty(day)) return null;
        LocalDate localDate = LocalDate.parse(day);
        return localDate2Date(localDate);
    }
    
    /**
     * 返回一天的0点
     * @return
     */
    public static Date atStartOfDay(Date day)
    {
        if (day == null) return null;
        LocalDate localDate = LocalDate.parse(formatDate(day, "yyyy-MM-dd"));
        return localDate2Date(localDate);
    }
    
    /**
     * 返回一天的23:59:59
     * @return
     */
    public static Date atEndOfDay(String day)
    {
        if (StringUtil.isEmpty(day)) return null;
        LocalDateTime localDateTime = LocalDateTime.of(LocalDate.parse(day), LocalTime.of(23, 59, 59));
        return localDateTime2Date(localDateTime);
    }
    
    /**
     * 返回一天的23:59:59
     * @return
     */
    public static Date atEndOfDay(Date day)
    {
        if (day == null) return null;
        LocalDateTime localDateTime =
            LocalDateTime.of(LocalDate.parse(formatDate(day, "yyyy-MM-dd")), LocalTime.of(23, 59, 59));
        return localDateTime2Date(localDateTime);
    }
    
    /**
     * 返回第二天的0点 
     * @return
     */
    public static Date atStartOfNextDay(String day)
    {
        if (StringUtil.isEmpty(day)) return null;
        LocalDate localDate = LocalDate.parse(day).plusDays(1);
        return localDate2Date(localDate);
    }
    
    /**
     * 返回第二天的0点 
     * @return
     */
    public static Date atStartOfNextDay(Date day)
    {
        if (day == null) return null;
        LocalDate localDate = LocalDate.parse(formatDate(day, "yyyy-MM-dd")).plusDays(1);
        return localDate2Date(localDate);
    }
    
    /**
     * 返回下个月1号的0点 
     * @return
     */
    public static Date atStartOfNextMonth(String day)
    {
        if (StringUtil.isEmpty(day)) return null;
        LocalDate localDate = LocalDate.parse(day).withDayOfMonth(1).plusMonths(1);
        return localDate2Date(localDate);
    }
    
    /**
     * 返回下个月1号的0点
     * @return
     */
    public static Date atStartOfNextMonth()
    {
        LocalDate localDate = LocalDate.now().withDayOfMonth(1).plusMonths(1);
        return localDate2Date(localDate);
    }
    
    /**
     * 返回今天之后几天的0点 
     * @return
     */
    public static Date atStartOfDayAfterToday(long daysToAdd)
    {
        LocalDate localDate = LocalDate.now().plusDays(daysToAdd);
        return localDate2Date(localDate);
    }
    
    /**
     * 返回今天的0点 
     * @return
     */
    public static Date atStartOfToday()
    {
        LocalDate localDate = LocalDate.now();
        return localDate2Date(localDate);
    }
    
    /**
     * 返回今天的23:59:59 
     * @return
     */
    public static Date atEndOfToday()
    {
        LocalDateTime localDateTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(23, 59, 59));
        return localDateTime2Date(localDateTime);
    }
    
    /**
     * 返回明天的0点 
     * @return
     */
    public static Date atStartOfTomorrow()
    {
        LocalDate localDate = LocalDate.now().plusDays(1);
        return localDate2Date(localDate);
    }
    
    /**
     * 返回昨天的0点 
     * @return
     */
    public static Date atStartOfYesterday()
    {
        LocalDate localDate = LocalDate.now().plusDays(-1);
        return localDate2Date(localDate);
    }
    
    /**
     * 返回7天前的0点 
     * @return
     */
    public static Date atStartOfLast7Day()
    {
        LocalDate localDate = LocalDate.now().minusDays(6);
        return localDate2Date(localDate);
    }
    
    /**
     * 返回本周的0点 
     * @return
     */
    public static Date atStartOfThisWeek()
    {
        LocalDate localDate = LocalDate.now().with(ChronoField.DAY_OF_WEEK, 1);
        return localDate2Date(localDate);
    }
    
    /**
     * 返回指定时间所在周的0点
     * @return
     */
    public static Date atStartOfThisWeek(String day)
    {
        if (StringUtil.isEmpty(day)) return null;
        LocalDate localDate = LocalDate.parse(day).with(ChronoField.DAY_OF_WEEK, 1);
        return localDate2Date(localDate);
    }
    
    /**
     * 返回本年的0点
     * @return
     */
    public static Date atStartOfThisYear()
    {
        LocalDate localDate = LocalDate.now().with(ChronoField.DAY_OF_YEAR, 1);
        return localDate2Date(localDate);
    }
    
    /**
     * 返回指定时间所在年的0点
     * @return
     */
    public static Date atStartOfThisYear(String day)
    {
        if (StringUtil.isEmpty(day)) return null;
        LocalDate localDate = LocalDate.parse(day).with(ChronoField.DAY_OF_YEAR, 1);
        return localDate2Date(localDate);
    }
    
    /**
     * 返回指定时间所在年的0点
     * @return
     */
    public static Date atStartOfYear(int year)
    {
        LocalDate localDate = LocalDate.of(year, 1, 1);
        return localDate2Date(localDate);
    }
    
    /**
     * 返回指定时间所在年的的最后一天的23:59:59
     * @return
     */
    public static Date atEndOfYear(String day)
    {
        if (StringUtil.isEmpty(day)) return null;
        LocalDate localDate = LocalDate.parse(day).with(ChronoField.DAY_OF_YEAR, 1).plusYears(1).minusDays(1);
        LocalDateTime localDateTime = LocalDateTime.of(localDate, LocalTime.of(23, 59, 59));
        return localDateTime2Date(localDateTime);
    }
    
    /**
     * 返回今年的的最后一天的23:59:59
     * @return
     */
    public static Date atEndOfThisYear()
    {
        LocalDate localDate = LocalDate.now().with(ChronoField.DAY_OF_YEAR, 1).plusYears(1).minusDays(1);
        LocalDateTime localDateTime = LocalDateTime.of(localDate, LocalTime.of(23, 59, 59));
        return localDateTime2Date(localDateTime);
    }
    
    /**
     * 返回指定时间所在年的的最后一天的23:59:59
     * @return
     */
    public static Date atEndOfYear(int year)
    {
        LocalDate localDate = LocalDate.of(year, 1, 1).plusYears(1).minusDays(1);
        LocalDateTime localDateTime = LocalDateTime.of(localDate, LocalTime.of(23, 59, 59));
        return localDateTime2Date(localDateTime);
    }
    
    /**
     * 返回本月的0点
     * @return
     */
    public static Date atStartOfThisMonth()
    {
        LocalDate localDate = LocalDate.now().with(ChronoField.DAY_OF_MONTH, 1);
        return localDate2Date(localDate);
    }
    
    /**
     * 返回指定时间所在月的0点
     * @return
     */
    public static Date atStartOfThisMonth(String day)
    {
        if (StringUtil.isEmpty(day)) return null;
        LocalDate localDate = LocalDate.parse(day).with(ChronoField.DAY_OF_MONTH, 1);
        return localDate2Date(localDate);
    }
    
    /**
     * 返回指定时间所在月的0点
     * @return
     */
    public static Date atStartOfMonth(int year, int month)
    {
        LocalDate localDate = LocalDate.of(year, month, 1);
        return localDate2Date(localDate);
    }
    
    /**
     * 返回指定时间所在月的最后一天的23:59:59
     * @return
     */
    public static Date atEndOfMonth(String day)
    {
        if (StringUtil.isEmpty(day)) return null;
        LocalDate localDate = LocalDate.parse(day).withDayOfMonth(1).plusMonths(1).minusDays(1);
        LocalDateTime localDateTime = LocalDateTime.of(localDate, LocalTime.of(23, 59, 59));
        return localDateTime2Date(localDateTime);
    }
    
    /**
     * 返回指定时间所在月的最后一天的23:59:59
     * @return
     */
    public static Date atEndOfMonth(int year, int month)
    {
        LocalDate localDate = LocalDate.of(year, month, 1).plusMonths(1).minusDays(1);
        LocalDateTime localDateTime = LocalDateTime.of(localDate, LocalTime.of(23, 59, 59));
        return localDateTime2Date(localDateTime);
    }
    
    /**
     * 返回下周的0点
     * @return
     */
    public static Date atStartOfNextWeek()
    {
        LocalDate localDate = LocalDate.now().with(ChronoField.DAY_OF_WEEK, 1).plusWeeks(1);
        return localDate2Date(localDate);
    }
    
    public static Date localDate2Date(LocalDate localDate)
    {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
        Date date = Date.from(instant);
        return date;
    }
    
    public static Date localDateTime2Date(LocalDateTime localDateTime)
    {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        Date date = Date.from(instant);
        return date;
    }
    
    public static LocalDate date2LocalDate(Date date)
    {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = date.toInstant();
        LocalDate localDate = instant.atZone(zone).toLocalDate();
        return localDate;
    }
    
    public static LocalDateTime date2LocalDateTime(Date date)
    {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = date.toInstant();
        LocalDateTime localDateTime = instant.atZone(zone).toLocalDateTime();
        return localDateTime;
    }
    
    public static Date trimMillisecon(Date date)
    {
        if (date == null)
            return null;
        else
        {
            long l = date.getTime();
            return new Date(l / 1000 * 1000);
        }
    }
    
    public static Date trimMillisecon(long time)
    {
        return new Date(time / 1000 * 1000);
    }
    
    public static long timeInterval(Date date1, Date date2, ChronoUnit unit)
    {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant1 = date1.toInstant();
        LocalDateTime localDateTime1 = instant1.atZone(zone).toLocalDateTime();
        Instant instant2 = date2.toInstant();
        LocalDateTime localDateTime2 = instant2.atZone(zone).toLocalDateTime();
        return localDateTime1.until(localDateTime2, unit);
    }
}
