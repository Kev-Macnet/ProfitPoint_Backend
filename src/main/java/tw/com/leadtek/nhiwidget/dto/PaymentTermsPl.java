package tw.com.leadtek.nhiwidget.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


@ApiModel(description = "支付條件設定Master")
public class PaymentTermsPl {
    
    @ApiModelProperty(value="分類", example="門診診察費", required=true)
    private String category;
    
    @ApiModelProperty(value="院內碼",example="abc001", required=false)
    private String fee_no;
    @ApiModelProperty(value="院內碼名稱", example="掛號費", required=false)
    private String fee_name;
    
    @ApiModelProperty(value="支付標準代碼", example="abc001", required=false)
    private String nhi_no;
    @ApiModelProperty(value="支付標準代碼名稱", example="掛號費", required=false)
    private String nhi_name;

    @ApiModelProperty(value="生效日(timestamp)", example="1625932800000", required=true)
    private long start_date;
    @ApiModelProperty(value="失效日(timestamp)", example="1627488000000", required=true)
    private long end_date;
    
    @ApiModelProperty(value="醫院層級(1醫學中心/2區域醫院/3地方醫院/4基層診所)", example="2", required=true)
    private int hospital_type;
    @ApiModelProperty(value="就醫方式(門急)(1|0)", example="1", required=true)
    private int outpatient_type;
    @ApiModelProperty(value="就醫方式(住院)(1|0)", example="0", required=true)
    private int hospitalized_type;
    
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public String getFee_no() {
        return fee_no;
    }
    public void setFee_no(String fee_no) {
        this.fee_no = fee_no;
    }
    public String getFee_name() {
        return fee_name;
    }
    public void setFee_name(String fee_name) {
        this.fee_name = fee_name;
    }
    public String getNhi_no() {
        return nhi_no;
    }
    public void setNhi_no(String nhi_no) {
        this.nhi_no = nhi_no;
    }
    public String getNhi_name() {
        return nhi_name;
    }
    public void setNhi_name(String nhi_name) {
        this.nhi_name = nhi_name;
    }
    
    public long getStart_date() {
        return start_date;
    }
    public void setStart_date(long start_date) {
        this.start_date = start_date;
    }
    public long getEnd_date() {
        return end_date;
    }
    public void setEnd_date(long end_date) {
        this.end_date = end_date;
    }
    public int getHospital_type() {
        return hospital_type;
    }
    public void setHospital_type(int hospital_type) {
        this.hospital_type = hospital_type;
    }
    public int getOutpatient_type() {
        return outpatient_type;
    }
    public void setOutpatient_type(int outpatient_type) {
        this.outpatient_type = outpatient_type;
    }
    public int getHospitalized_type() {
        return hospitalized_type;
    }
    public void setHospitalized_type(int hospitalized_type) {
        this.hospitalized_type = hospitalized_type;
    }

    
}


