package tw.com.leadtek.nhiwidget.dto;

import io.swagger.annotations.ApiModelProperty;

public class AdditionalContent6Pl {
    @ApiModelProperty(value="1.啟用/0.關閉", example="1", required=true, position=1)
    private int enable;
    @ApiModelProperty(value="支付標準代碼", example="nhi001", required=true, position=2)
    private String nhi_no;
    @ApiModelProperty(value="案件分類", required=true, position=3)
    private java.util.List<String> category;
    @ApiModelProperty(value="醫令類別清單", required=true, position=4)
    private java.util.List<String> cpoe;
    @ApiModelProperty(value="照護計畫清單", required=true, position=5)
    private java.util.List<String> plan;
    
    public int getEnable() {
        return enable;
    }
    public void setEnable(int enable) {
        this.enable = enable;
    }
    public String getNhi_no() {
        return nhi_no;
    }
    public void setNhi_no(String nhi_no) {
        this.nhi_no = nhi_no;
    }
    public java.util.List<String> getCategory() {
        return category;
    }
    public void setCategory(java.util.List<String> category) {
        this.category = category;
    }
    public java.util.List<String> getCpoe() {
        return cpoe;
    }
    public void setCpoe(java.util.List<String> cpoe) {
        this.cpoe = cpoe;
    }
    public java.util.List<String> getPlan() {
        return plan;
    }
    public void setPlan(java.util.List<String> plan) {
        this.plan = plan;
    }
    
    
}

