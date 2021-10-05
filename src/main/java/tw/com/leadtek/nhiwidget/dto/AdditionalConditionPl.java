package tw.com.leadtek.nhiwidget.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

//PlanIcdNoPl, PlanLessNdayPl, PlanMoreTimesPl

@ApiModel(value = "總額外點數條件參數")
public class AdditionalConditionPl {
    @ApiModelProperty(value="是否啟用(1|0)", example="1", required=true, position=1)
    private int active;
    @ApiModelProperty(value="年度", example="110", required=true, position=2)
    private int syear;
    @ApiModelProperty(value="生效日(timestamp)", example="1625932800000", required=true, position=3)
    private String start_date;
    @ApiModelProperty(value="失效日(timestamp)", example="1627488000000", required=true, position=4)
    private String end_date;
    //-----門診
    @ApiModelProperty(value="門診案件分類", required=false, position=5)
    private AdditionalContent1Pl outpatient_1;
    @ApiModelProperty(value="門診醫令類別", required=false, position=6)
    private java.util.List<AdditionalContent2Pl> outpatient_2;
    @ApiModelProperty(value="門診支付代碼項目", required=false, position=7)
    private java.util.List<AdditionalContent3Pl> outpatient_3;
    @ApiModelProperty(value="門診醫令數量*醫令單價", required=false, position=8)
    private java.util.List<AdditionalContent4Pl> outpatient_4;
    @ApiModelProperty(value="門診主診斷", required=false, position=9)
    private java.util.List<AdditionalContent5Pl> outpatient_5;
    @ApiModelProperty(value="門診照護計畫", required=false, position=10)
    private java.util.List<AdditionalContent6Pl> outpatient_6;
    @ApiModelProperty(value="門診試辦計畫", required=false, position=11)
    private java.util.List<AdditionalContent7Pl> outpatient_7;
    //-----住院
    @ApiModelProperty(value="住院案件分類", required=false, position=12)
    private AdditionalContent1Pl inpatient_1;
    @ApiModelProperty(value="住院醫令類別", required=false, position=13)
    private java.util.List<AdditionalContent2Pl> inpatient_2;
    @ApiModelProperty(value="住院支付代碼項目", required=false, position=14)
    private java.util.List<AdditionalContent3Pl> inpatient_3;
    @ApiModelProperty(value="住院照護計畫", required=false, position=15)
    private java.util.List<AdditionalContent6Pl> inpatient_6;
    public int getActive() {
        return active;
    }
    public void setActive(int active) {
        this.active = active;
    }
    public int getSyear() {
        return syear;
    }
    public void setSyear(int syear) {
        this.syear = syear;
    }
    public String getStart_date() {
        return start_date;
    }
    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }
    public String getEnd_date() {
        return end_date;
    }
    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }
    public AdditionalContent1Pl getOutpatient_1() {
        return outpatient_1;
    }
    public void setOutpatient_1(AdditionalContent1Pl outpatient_1) {
        this.outpatient_1 = outpatient_1;
    }
    public java.util.List<AdditionalContent2Pl> getOutpatient_2() {
        return outpatient_2;
    }
    public void setOutpatient_2(java.util.List<AdditionalContent2Pl> outpatient_2) {
        this.outpatient_2 = outpatient_2;
    }
    public java.util.List<AdditionalContent3Pl> getOutpatient_3() {
        return outpatient_3;
    }
    public void setOutpatient_3(java.util.List<AdditionalContent3Pl> outpatient_3) {
        this.outpatient_3 = outpatient_3;
    }
    public java.util.List<AdditionalContent4Pl> getOutpatient_4() {
        return outpatient_4;
    }
    public void setOutpatient_4(java.util.List<AdditionalContent4Pl> outpatient_4) {
        this.outpatient_4 = outpatient_4;
    }
    public java.util.List<AdditionalContent5Pl> getOutpatient_5() {
        return outpatient_5;
    }
    public void setOutpatient_5(java.util.List<AdditionalContent5Pl> outpatient_5) {
        this.outpatient_5 = outpatient_5;
    }
    public java.util.List<AdditionalContent6Pl> getOutpatient_6() {
        return outpatient_6;
    }
    public void setOutpatient_6(java.util.List<AdditionalContent6Pl> outpatient_6) {
        this.outpatient_6 = outpatient_6;
    }
    public java.util.List<AdditionalContent7Pl> getOutpatient_7() {
        return outpatient_7;
    }
    public void setOutpatient_7(java.util.List<AdditionalContent7Pl> outpatient_7) {
        this.outpatient_7 = outpatient_7;
    }
    public AdditionalContent1Pl getInpatient_1() {
        return inpatient_1;
    }
    public void setInpatient_1(AdditionalContent1Pl inpatient_1) {
        this.inpatient_1 = inpatient_1;
    }
    public java.util.List<AdditionalContent2Pl> getInpatient_2() {
        return inpatient_2;
    }
    public void setInpatient_2(java.util.List<AdditionalContent2Pl> inpatient_2) {
        this.inpatient_2 = inpatient_2;
    }
    public java.util.List<AdditionalContent3Pl> getInpatient_3() {
        return inpatient_3;
    }
    public void setInpatient_3(java.util.List<AdditionalContent3Pl> inpatient_3) {
        this.inpatient_3 = inpatient_3;
    }
    public java.util.List<AdditionalContent6Pl> getInpatient_6() {
        return inpatient_6;
    }
    public void setInpatient_6(java.util.List<AdditionalContent6Pl> inpatient_6) {
        this.inpatient_6 = inpatient_6;
    }

}



