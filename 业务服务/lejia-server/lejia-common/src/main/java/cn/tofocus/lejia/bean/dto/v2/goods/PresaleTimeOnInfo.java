package cn.tofocus.lejia.bean.dto.v2.goods;

import lombok.Data;

@Data
public class PresaleTimeOnInfo
{
    private Long startTime;
    
    private Long endTime;
    
    public String getStartHms()
    {
        if(startTime != null)
            return getTime(startTime);
        return "";
    }
    
    public String getEndHms()
    {
        if(endTime != null)
            return getTime(endTime);
        return "";
    }
    
    private String getTime(Long time) {
        String timeStr="";
        if (time==null){
            return null;
        }
        long hour = (time / (60 * 60 * 1000));  
        long min = ((time / (60 * 1000)) - hour * 60);  
        long sec = (time/1000-hour*60*60-min*60);  
        String h = String.valueOf(hour);
        String m = String.valueOf(min);
        String s = String.valueOf(sec);
        if(hour < 10)
            h = "0" + h;
        if(min < 10)
            m = "0" + m;
        if(sec < 10)
            s = "0" + s;
        timeStr = h + ":" + m + ":" + s;
        return timeStr;
    }
}
