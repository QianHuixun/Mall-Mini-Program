package cn.tofocus.file.ueditor;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UeResult
{
    private String state;//: "SUCCESS"
    
    public UeResult(StateEnum s)
    {
        this.state = s.getName();
    }
}
