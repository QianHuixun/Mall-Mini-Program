package cn.tofocus.lejia.zx.beanV2;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

import java.util.List;

@Data
@XStreamAlias("DATA")
public class T21000037ResponseData {
    private String RSP_CODE;
    private String RSP_MSG;
    private String REQ_SSN;
    private String LIST_COUNT;//列表条数
    private String SIGN_INFO;
    @XStreamAlias("LIST")
    private List<T21000037ResponseDataList> list;
}
