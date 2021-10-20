package tw.com.leadtek.nhiwidget.dto;

import io.swagger.annotations.ApiModelProperty;

public class AdditionalContent7ListPl {
    
    @ApiModelProperty(value="支付標準代碼", example="nhi001", required=true, position=2)
    private String nhi_no;
    @ApiModelProperty(value="照護計畫清單", required=true, position=3)
    private java.util.List<String> plan;
    @ApiModelProperty(value="試辦計畫清單", required=true, position=4)
    private java.util.List<String> trial;

    public String getNhi_no() {
        return nhi_no.replaceAll("\'", "\'\'");
    }
    public void setNhi_no(String nhi_no) {
        this.nhi_no = nhi_no;
    }
    public java.util.List<String> getPlan() {
        return plan;
    }
    public void setPlan(java.util.List<String> plan) {
        this.plan = plan;
    }
    public java.util.List<String> getTrial() {
        return trial;
    }
    public void setTrial(java.util.List<String> trial) {
        this.trial = trial;
    }

}

