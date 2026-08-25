package cn.tofocus.lejia.bean.dto.pay.ns;


import java.io.Serializable;

/**
 * 交易关闭
 * 
 * 
 */
public class WXBillGetRequestBean implements Serializable {

    /**
     * @Fields serialVersionUID:对象说明
     */
    private static final long serialVersionUID = -6550181778591862295L;

    private String merchId;
    private String billDate;
    private String billType;
    private String tarType;
    public String getMerchId() {
        return merchId;
    }
    public void setMerchId(String merchId) {
        this.merchId = merchId;
    }
    public String getBillDate() {
        return billDate;
    }
    public void setBillDate(String billDate) {
        this.billDate = billDate;
    }
    public String getBillType() {
        return billType;
    }
    public void setBillType(String billType) {
        this.billType = billType;
    }
    public String getTarType() {
        return tarType;
    }
    public void setTarType(String tarType) {
        this.tarType = tarType;
    }
    

}