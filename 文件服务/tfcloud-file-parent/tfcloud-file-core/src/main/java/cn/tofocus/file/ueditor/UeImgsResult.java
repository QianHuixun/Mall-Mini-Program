package cn.tofocus.file.ueditor;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UeImgsResult extends UeResult
{
    private List<Img> data;
    
    public UeImgsResult(StateEnum s)
    {
        super(s);
    }

    public void addUrl(String original, String title, String url)
    {
        if(data == null)
            data = new ArrayList<>();
        data.add(new Img(original, title, url));
    }
}
