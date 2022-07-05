package tw.com.leadtek.nhiwidget.payload.report;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.payload.BaseResponse;

@ApiModel("資料庫統計匯出條件")
public class DatabaseCalculateExportFactor extends BaseResponse implements Serializable{
	
	private static final long serialVersionUID = -4489747940716711033L;

	//DRG案件數分佈佔率與定額、實際點數條件
    @ApiModelProperty(value="DRG項目列出", example="false", required=false)
    private Boolean isShowDRGList;
    
    @ApiModelProperty(value="顯示區間，若為多筆資料，用空格隔開", example="A B1 B2 C", required=false)
    private String sections;
    
    @ApiModelProperty(value="指定DRG代碼，若為多筆資料，用空格隔開", example="", required=false)
    private String drgCodes;
    
    //申報分配佔率與點數、金額 條件
    @ApiModelProperty(value="健保狀態(單選) 健保(含勞保) or 健保(不含勞保)", example="false", required=false)
    private Boolean withLaborProtection;
    
    @ApiModelProperty(value="費用分類 多選用空格隔開", example="", required=false)
    private String classFee;
    
    //醫令項目與執行量
    @ApiModelProperty(value="費用申報狀態(可複選) 多選用空格隔開", example="自費 健保", required=false)
    private String feeApply;
    
    //自費項目清單
    @ApiModelProperty(value="自費分項列出", example="false", required=false)
    private Boolean isShowSelfFeeList;
    
    //案件狀態與各別數量(可複選)
    @ApiModelProperty(value="就醫清單列出", example="false", required=false)
    private Boolean isShowPhysicalList;
    
    @ApiModelProperty(value="顯示案件狀態，若為多筆資料，用空格隔開", example="無須變更 評估不調整 優化完成", required=false)
    private String caseStatus;
    
    //共用條件
    @ApiModelProperty(value="日期類型: 0=年月帶入，1=日期區間", example="0", required=false)
    private String dateType;
    
    @ApiModelProperty(value="西元年，若為多筆資料，用空格隔開，dateType=0時必填", example="2021 2022", required=false)
    private String year;
    
    @ApiModelProperty(value="月份，若為多筆資料，用空格隔開，dateType=0時必填", example="1 2 3", required=false)
    private String month;
    
    @ApiModelProperty(value="起始日，格式為yyyy-MM-dd，dateType=1時必填", example="2020-06-01", required=false)
    private String betweenSDate;
    
    @ApiModelProperty(value="迄日，格式為yyyy-MM-dd，dateType=1時必填", example="2020-06-30", required=false)
    private String betweenEDate;
    
    @ApiModelProperty(value="就醫類別，若為多筆資料，用空格隔開，為all totalop op em ip", example="", required=false)
    private String dataFormats;
    
    @ApiModelProperty(value="科別，若為多筆資料，用空格隔開，05 06", example="", required=false)
    private String funcTypes;
    
    @ApiModelProperty(value="醫護姓名，若為多筆資料，用空格隔開，R A", example="", required=false)
    private String medNames;
    
    @ApiModelProperty(value="病歷編號，若為多筆資料，用空格隔開", example="", required=false)
    private String icdcms;
    
    @ApiModelProperty(value="就醫紀錄編號，若為多筆資料，用空格隔開", example="", required=false)
    private String medLogCodes;
    
    @ApiModelProperty(value="單筆申報點數(最小)", example="1", required=false)
    private Integer applMin;
    
    @ApiModelProperty(value="單筆申報點數(最大)", example="1", required=false)
    private Integer applMax;
    
    @ApiModelProperty(value="不分區ICD碼，若為多筆資料，用空格隔開", example="")
    private String icdAll;
    
    @ApiModelProperty(value="支付標準代碼", example="", required=false)
    private String payCode;
    
    @ApiModelProperty(value="院內碼", example="", required=false)
    private String inhCode;
    
    @ApiModelProperty(value="上個月同條件相比", example="false", required=false)
    private Boolean isLastM;
    
    @ApiModelProperty(value="去年同期時段相比", example="false", required=false)
    private Boolean isLastY;

    @ApiModelProperty(value="報表名稱", example="案件狀態與各別數量(可複選)",required=false)
    private String exportName;

	public Boolean getIsShowDRGList() {
		return isShowDRGList;
	}

	public void setIsShowDRGList(Boolean isShowDRGList) {
		this.isShowDRGList = isShowDRGList;
	}

	public String getSections() {
		return sections;
	}

	public void setSections(String sections) {
		this.sections = sections;
	}

	public String getDrgCodes() {
		return drgCodes;
	}

	public void setDrgCodes(String drgCodes) {
		this.drgCodes = drgCodes;
	}

	public Boolean getWithLaborProtection() {
		return withLaborProtection;
	}

	public void setWithLaborProtection(Boolean withLaborProtection) {
		this.withLaborProtection = withLaborProtection;
	}

	public String getClassFee() {
		return classFee;
	}

	public void setClassFee(String classFee) {
		this.classFee = classFee;
	}

	public String getFeeApply() {
		return feeApply;
	}

	public void setFeeApply(String feeApply) {
		this.feeApply = feeApply;
	}

	public Boolean getIsShowSelfFeeList() {
		return isShowSelfFeeList;
	}

	public void setIsShowSelfFeeList(Boolean isShowSelfFeeList) {
		this.isShowSelfFeeList = isShowSelfFeeList;
	}

	public Boolean getIsShowPhysicalList() {
		return isShowPhysicalList;
	}

	public void setIsShowPhysicalList(Boolean isShowPhysicalList) {
		this.isShowPhysicalList = isShowPhysicalList;
	}

	public String getCaseStatus() {
		return caseStatus;
	}

	public void setCaseStatus(String caseStatus) {
		this.caseStatus = caseStatus;
	}

	public String getDateType() {
		return dateType;
	}

	public void setDateType(String dateType) {
		this.dateType = dateType;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getBetweenSDate() {
		return betweenSDate;
	}

	public void setBetweenSDate(String betweenSDate) {
		this.betweenSDate = betweenSDate;
	}

	public String getBetweenEDate() {
		return betweenEDate;
	}

	public void setBetweenEDate(String betweenEDate) {
		this.betweenEDate = betweenEDate;
	}

	public String getDataFormats() {
		return dataFormats;
	}

	public void setDataFormats(String dataFormats) {
		this.dataFormats = dataFormats;
	}

	public String getFuncTypes() {
		return funcTypes;
	}

	public void setFuncTypes(String funcTypes) {
		this.funcTypes = funcTypes;
	}

	public String getMedNames() {
		return medNames;
	}

	public void setMedNames(String medNames) {
		this.medNames = medNames;
	}

	public String getIcdcms() {
		return icdcms;
	}

	public void setIcdcms(String icdcms) {
		this.icdcms = icdcms;
	}

	public String getMedLogCodes() {
		return medLogCodes;
	}

	public void setMedLogCodes(String medLogCodes) {
		this.medLogCodes = medLogCodes;
	}

	public Integer getApplMin() {
		return applMin;
	}

	public void setApplMin(Integer applMin) {
		this.applMin = applMin;
	}

	public Integer getApplMax() {
		return applMax;
	}

	public void setApplMax(Integer applMax) {
		this.applMax = applMax;
	}

	public String getIcdAll() {
		return icdAll;
	}

	public void setIcdAll(String icdAll) {
		this.icdAll = icdAll;
	}

	public String getPayCode() {
		return payCode;
	}

	public void setPayCode(String payCode) {
		this.payCode = payCode;
	}

	public String getInhCode() {
		return inhCode;
	}

	public void setInhCode(String inhCode) {
		this.inhCode = inhCode;
	}

	public Boolean getIsLastM() {
		return isLastM;
	}

	public void setIsLastM(Boolean isLastM) {
		this.isLastM = isLastM;
	}

	public Boolean getIsLastY() {
		return isLastY;
	}

	public void setIsLastY(Boolean isLastY) {
		this.isLastY = isLastY;
	}

	public String getExportName() {
		return exportName;
	}

	public void setExportName(String exportName) {
		this.exportName = exportName;
	}
    
    
}
