package cn.tofocus.lejia.utils;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import cn.tofocus.common.util.StringUtil;

/**
 * 
 *  
 * 工具类
 * 
 * @author  Administrator
 * @version  [版本号, 2021年12月2日]
 */
public class LejiaUtils
{
    /**
     * 按照指定小时分割时间段
     * @param dateType 类型 M/D/H/N -->每月/每天/每小时/每分钟
     * @param dBegin开始时间
     * @param dEnd结束时间
     * @param time 指定小时(如：1、2、3、4)
     * @return
     */
    public static List<String> findDates(String dateType, Date dBegin, Date dEnd, int time)
        throws Exception
    {
        List<String> listDate = new ArrayList<>();
        listDate.add(new SimpleDateFormat("HH:mm").format(dBegin));
        Calendar calBegin = Calendar.getInstance();
        calBegin.setTime(dBegin);
        Calendar calEnd = Calendar.getInstance();
        calEnd.setTime(dEnd);
        while (calEnd.after(calBegin))
        {
            if ("H".equals(dateType))
            {
                calBegin.add(Calendar.HOUR, time);
            }
            if ("M".equals(dateType))
            {
                calBegin.add(Calendar.MONTH, time);
            }
            if ("D".equals(dateType))
            {
                calBegin.add(Calendar.DATE, time);
            }
            if ("N".equals(dateType))
            {
                calBegin.add(Calendar.MINUTE, time);
            }
            if (calEnd.after(calBegin) || calEnd.equals(calBegin))
            {
                listDate.add(new SimpleDateFormat("HH:mm").format(calBegin.getTime()));
            }
            else
            {
                
            }
            
        }
        return listDate;
    }
    
    public static String getNewTime(String datetime, String addMinutes)
    
    {
        String retval = null;
        
        try
        {
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
            
            Date originalDate = formatter.parse(datetime);
            
            Calendar newTime = Calendar.getInstance();
            newTime.setTime(originalDate);
            newTime.add(Calendar.MINUTE, Integer.parseInt(addMinutes));//日期加n分
            
            Date newDate = newTime.getTime();
            retval = formatter.format(newDate);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return retval;
    }
    
    public static String getCurrHourTime(Date date)
    {
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.MINUTE, 0);
        ca.set(Calendar.SECOND, 0);
        date = ca.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sdf.format(date);
    }
    
    public static String getRandomString(int length)
    {
        
        try
        {
            Random random = SecureRandom.getInstanceStrong();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < length; i++)
            {
                int number = random.nextInt(10);
                sb.append(number);
            }
            return sb.toString();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
        
    }
    
    
    public static String getNewRandomString(int len)
    {
        int rs = (int) ((Math.random() * 9 + 1) * Math.pow(10, len - 1));
        return String.valueOf(rs); 
    
    }
    
    public static String getNextHourTime(String time)
    {
        if (StringUtil.isBlank(time) || time.length() < 5) return null;
        String minStr = time.substring(3);
        
        Integer min = Integer.valueOf(minStr);
        min = 30 - min;
        if (min <= 0)
        {
            Integer b = Integer.valueOf(time.substring(0, 2));
            if (b == 23)
            {
                b = 0;
            }
            else
            {
                b += 1;
            }
            return String.format("%02d", b) + ":" + "00";
            
        }
        else
            return time.substring(0, 3) + "30";
    }
    
    public static List<String> getListTime(Date day, String yytb, String yyte)
        throws Exception
    {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        String strm = yytb.substring(3);
        String end = yyte.substring(3);
        
        Integer m = Integer.valueOf(strm);
        m = 30 - m;
        if (m <= 0)
        {
            Integer b = Integer.valueOf(yytb.substring(0, 2));
            if (b == 23)
            {
                b = 0;
            }
            else
            {
                b += 1;
            }
            yytb = String.format("%02d", b) + ":" + "00";
            
        }
        else
            yytb = yytb.substring(0, 3) + "30";
        
        m = Integer.valueOf(end);
        m = m - 30;
        if (m >= 0)
            yyte = yyte.substring(0, 3) + "30";
        else
            yyte = yyte.substring(0, 3) + "00";
        
        Date tb = format.parse(yytb);
        Date te = format.parse(yyte);
        
        List<String> rs = LejiaUtils.findDates("N", tb, te, 30);
        String s1 = sdf.format(day);
        List<String> lv = new ArrayList<>();
        for (int i = 0; i < rs.size() - 1; i++)
        {
            String s = s1 + " " + rs.get(i) + "~" + rs.get(i + 1);
            lv.add(s);
        }
        
        return lv;
        
    }
    
    public static List<String> getNextListTime(Date day, String yytb, String yyte)
        throws Exception
    {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        String strm = yytb.substring(3);
        String end = yyte.substring(3);
        
        Integer m = Integer.valueOf(strm);
        m = 30 - m;
        if (m < 0)
        {
            Integer b = Integer.valueOf(yytb.substring(0, 2));
            if (b == 23)
            {
                b = 0;
            }
            else
            {
                b += 1;
            }
            yytb = String.format("%02d", b) + ":" + "00";
            
        }
        else
            yytb = yytb.substring(0, 3) + "30";
        
        m = Integer.valueOf(end);
        m = m - 30;
        if (m >= 0)
            yyte = yyte.substring(0, 3) + "30";
        else
            yyte = yyte.substring(0, 3) + "00";
        
        Date tb = format.parse(yytb);
        Date te = format.parse(yyte);
        
        List<String> rs = LejiaUtils.findDates("N", tb, te, 30);
        String s1 = sdf.format(day);
        List<String> lv = new ArrayList<>();
        for (int i = 0; i < rs.size() - 1; i++)
        {
            String s = s1 + " " + rs.get(i) + "~" + rs.get(i + 1);
            lv.add(s);
        }
        
        return lv;
        
    }
    
}
