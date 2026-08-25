package cn.tofocus.lejia.bean.dto.jd;

import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JdGoodsLowestBuySyncTask
{
    private Date startTime;
    
    private Integer totalNum;

    private Integer updNum;
    
    private boolean finished;

    private Date endTime;
    
    public static JdGoodsLowestBuySyncTask init(Date startTime, Integer totalNum)
    {
        JdGoodsLowestBuySyncTask task = new JdGoodsLowestBuySyncTask();
        task.setStartTime(startTime);
        task.setTotalNum(totalNum);
        task.setUpdNum(0);
        task.setFinished(false);
        return task;
    }
}
