package tw.com.leadtek.nhiwidget.dto;

import io.swagger.annotations.ApiModelProperty;


public class AdditionalContent2ListPl {
    @ApiModelProperty(value="支付標準代碼", example="nhi001", required=true, position=2)
    private String nhi_no;
    @ApiModelProperty(value="醫令類別清單", required=true, position=3)
    private java.util.List<String> cpoe;
    
    public String getNhi_no() {
        return nhi_no.replaceAll("\'", "\'\'");
    }
    public void setNhi_no(String nhi_no) {
        this.nhi_no = nhi_no;
    }
    public java.util.List<String> getCpoe() {
        return cpoe;
    }
    public void setCpoe(java.util.List<String> cpoe) {
        this.cpoe = cpoe;
    }
}

