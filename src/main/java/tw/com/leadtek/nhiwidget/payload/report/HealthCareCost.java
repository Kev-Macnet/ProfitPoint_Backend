package tw.com.leadtek.nhiwidget.payload.report;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.dto.ClassCaseCountDto;
import tw.com.leadtek.nhiwidget.dto.ClassDoctorDto;
import tw.com.leadtek.nhiwidget.dto.ClassDoctorWeeklyDto;
import tw.com.leadtek.nhiwidget.dto.ClassDrugDotDto;
import tw.com.leadtek.nhiwidget.dto.ClassDrugFeeDto;
import tw.com.leadtek.nhiwidget.payload.BaseResponse;

@ApiModel("健保藥費概況")
public class HealthCareCost extends BaseResponse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5502090315999001723L;
	
	@ApiModelProperty(value="季度", required = true)
	private String season;

	@ApiModelProperty(value="門急診-住院病歷總點數", required = true)
	private String AllDot;
	
	@ApiModelProperty(value="門急診-住院案件數", required = true)
	private String AllCount;
	
	@ApiModelProperty(value="門急診-住院病歷總藥費", required = true)
	private String AllDrugFee;
	
	@ApiModelProperty(value="門急診-住院藥費佔率", required = true)
	private String AllRate;
	
	@ApiModelProperty(value="門急診-住院各科別藥品總點數(總藥費)", required = true)
	private List<ClassDrugDotDto> classAll;
	
	@ApiModelProperty(value="門急診-住院各科別各醫師藥品總點數(總藥費)", required = true)
	private List<ClassDoctorDto> classDoctorAll;
	
	@ApiModelProperty(value="門急診-住院各科別各醫師每週藥品總點數(總藥費)", required = true)
	private List<ClassDoctorWeeklyDto> classDoctorAllWeekly;
	
	@ApiModelProperty(value="門急診-住院各科別病歷總點數", required = true)
	private List<ClassDrugDotDto> classAll_TDot;
	
	@ApiModelProperty(value="門急診-住院各科別案件數", required = true)
	private List<ClassCaseCountDto> classAllCaseCount;
	
	@ApiModelProperty(value="門急診-住院各科別藥費佔率", required = true)
	private List<ClassDrugFeeDto> classAllFeeRate;
	
	@ApiModelProperty(value="門急診-住院各科別總藥費差額", required = true)
	private List<ClassDrugFeeDto> classAllFeeDiff;
	
	@ApiModelProperty(value="門急診-住院總藥費差額", required = true)
	private String AllFeeDiff;
	
	@ApiModelProperty(value="門急診病歷總點數", required = true)
	private String OP_Dot;
	
	@ApiModelProperty(value="門急診案件數", required = true)
	private String OPCount;
	
	@ApiModelProperty(value="門急診病歷總藥費", required = true)
	private String OP_DrugFee;
	
	@ApiModelProperty(value="門急診藥費佔率", required = true)
	private String OP_Rate;
	
	@ApiModelProperty(value="門急診各科別藥品總點數(總藥費)", required = true)
	private List<ClassDrugDotDto> classOP;
	
	@ApiModelProperty(value="門急診各科別病歷總點數", required = true)
	private List<ClassDrugDotDto> classOP_TDot;
	
	@ApiModelProperty(value="門急診各科別案件數", required = true)
	private List<ClassCaseCountDto> classOPCaseCount;
	
	@ApiModelProperty(value="門急診各科別藥費佔率", required = true)
	private List<ClassDrugFeeDto> classOPFeeRate;
	
	@ApiModelProperty(value="住院病歷總點數", required = true)
	private String IP_Dot;
	
	@ApiModelProperty(value="住院案件數", required = true)
	private String IPCount;
	
	@ApiModelProperty(value="住院病歷總藥費", required = true)
	private String IP_DrugFee;
	
	@ApiModelProperty(value="住院藥費佔率", required = true)
	private String IP_Rate;
	
	@ApiModelProperty(value="住院各科別藥品總點數(總藥費)", required = true)
	private List<ClassDrugDotDto> classIP;
	
	@ApiModelProperty(value="住院各科別病歷總點數", required = true)
	private List<ClassDrugDotDto> classIP_TDot;
	
	@ApiModelProperty(value="住院各科別案件數", required = true)
	private List<ClassCaseCountDto> classIPCaseCount;
	
	@ApiModelProperty(value="住院各科別藥費佔率", required = true)
	private List<ClassDrugFeeDto> classIPFeeRate;

	public String getSeason() {
		return season;
	}

	public void setSeason(String season) {
		this.season = season;
	}

	public String getAllDot() {
		return AllDot;
	}

	public void setAllDot(String allDot) {
		AllDot = allDot;
	}

	public String getAllCount() {
		return AllCount;
	}

	public void setAllCount(String allCount) {
		AllCount = allCount;
	}

	public String getAllDrugFee() {
		return AllDrugFee;
	}

	public void setAllDrugFee(String allDrugFee) {
		AllDrugFee = allDrugFee;
	}

	public String getAllRate() {
		return AllRate;
	}

	public void setAllRate(String allRate) {
		AllRate = allRate;
	}

	public List<ClassDrugDotDto> getClassAll() {
		return classAll;
	}

	public void setClassAll(List<ClassDrugDotDto> classAll) {
		this.classAll = classAll;
	}

	public List<ClassDoctorDto> getClassDoctorAll() {
		return classDoctorAll;
	}

	public void setClassDoctorAll(List<ClassDoctorDto> classDoctorAll) {
		this.classDoctorAll = classDoctorAll;
	}

	public List<ClassDoctorWeeklyDto> getClassDoctorAllWeekly() {
		return classDoctorAllWeekly;
	}

	public void setClassDoctorAllWeekly(List<ClassDoctorWeeklyDto> classDoctorAllWeekly) {
		this.classDoctorAllWeekly = classDoctorAllWeekly;
	}

	public List<ClassDrugDotDto> getClassAll_TDot() {
		return classAll_TDot;
	}

	public void setClassAll_TDot(List<ClassDrugDotDto> classAll_TDot) {
		this.classAll_TDot = classAll_TDot;
	}

	public List<ClassCaseCountDto> getClassAllCaseCount() {
		return classAllCaseCount;
	}

	public void setClassAllCaseCount(List<ClassCaseCountDto> classAllCaseCount) {
		this.classAllCaseCount = classAllCaseCount;
	}

	public List<ClassDrugFeeDto> getClassAllFeeRate() {
		return classAllFeeRate;
	}

	public void setClassAllFeeRate(List<ClassDrugFeeDto> classAllFeeRate) {
		this.classAllFeeRate = classAllFeeRate;
	}

	public List<ClassDrugFeeDto> getClassAllFeeDiff() {
		return classAllFeeDiff;
	}

	public void setClassAllFeeDiff(List<ClassDrugFeeDto> classAllFeeDiff) {
		this.classAllFeeDiff = classAllFeeDiff;
	}

	public String getAllFeeDiff() {
		return AllFeeDiff;
	}

	public void setAllFeeDiff(String allFeeDiff) {
		AllFeeDiff = allFeeDiff;
	}

	public String getOP_Dot() {
		return OP_Dot;
	}

	public void setOP_Dot(String oP_Dot) {
		OP_Dot = oP_Dot;
	}

	public String getOPCount() {
		return OPCount;
	}

	public void setOPCount(String oPCount) {
		OPCount = oPCount;
	}

	public String getOP_DrugFee() {
		return OP_DrugFee;
	}

	public void setOP_DrugFee(String oP_DrugFee) {
		OP_DrugFee = oP_DrugFee;
	}

	public String getOP_Rate() {
		return OP_Rate;
	}

	public void setOP_Rate(String oP_Rate) {
		OP_Rate = oP_Rate;
	}

	public List<ClassDrugDotDto> getClassOP() {
		return classOP;
	}

	public void setClassOP(List<ClassDrugDotDto> classOP) {
		this.classOP = classOP;
	}

	public List<ClassDrugDotDto> getClassOP_TDot() {
		return classOP_TDot;
	}

	public void setClassOP_TDot(List<ClassDrugDotDto> classOP_TDot) {
		this.classOP_TDot = classOP_TDot;
	}

	public List<ClassCaseCountDto> getClassOPCaseCount() {
		return classOPCaseCount;
	}

	public void setClassOPCaseCount(List<ClassCaseCountDto> classOPCaseCount) {
		this.classOPCaseCount = classOPCaseCount;
	}

	public List<ClassDrugFeeDto> getClassOPFeeRate() {
		return classOPFeeRate;
	}

	public void setClassOPFeeRate(List<ClassDrugFeeDto> classOPFeeRate) {
		this.classOPFeeRate = classOPFeeRate;
	}

	public String getIP_Dot() {
		return IP_Dot;
	}

	public void setIP_Dot(String iP_Dot) {
		IP_Dot = iP_Dot;
	}

	public String getIPCount() {
		return IPCount;
	}

	public void setIPCount(String iPCount) {
		IPCount = iPCount;
	}

	public String getIP_DrugFee() {
		return IP_DrugFee;
	}

	public void setIP_DrugFee(String iP_DrugFee) {
		IP_DrugFee = iP_DrugFee;
	}

	public String getIP_Rate() {
		return IP_Rate;
	}

	public void setIP_Rate(String iP_Rate) {
		IP_Rate = iP_Rate;
	}

	public List<ClassDrugDotDto> getClassIP() {
		return classIP;
	}

	public void setClassIP(List<ClassDrugDotDto> classIP) {
		this.classIP = classIP;
	}

	public List<ClassDrugDotDto> getClassIP_TDot() {
		return classIP_TDot;
	}

	public void setClassIP_TDot(List<ClassDrugDotDto> classIP_TDot) {
		this.classIP_TDot = classIP_TDot;
	}

	public List<ClassCaseCountDto> getClassIPCaseCount() {
		return classIPCaseCount;
	}

	public void setClassIPCaseCount(List<ClassCaseCountDto> classIPCaseCount) {
		this.classIPCaseCount = classIPCaseCount;
	}

	public List<ClassDrugFeeDto> getClassIPFeeRate() {
		return classIPFeeRate;
	}

	public void setClassIPFeeRate(List<ClassDrugFeeDto> classIPFeeRate) {
		this.classIPFeeRate = classIPFeeRate;
	}

	@Override
	public String toString() {
		return "HealthCareCost [season=" + season + ", AllDot=" + AllDot + ", AllCount=" + AllCount + ", AllDrugFee="
				+ AllDrugFee + ", AllRate=" + AllRate + ", classAll=" + classAll + ", classDoctorAll=" + classDoctorAll
				+ ", classDoctorAllWeekly=" + classDoctorAllWeekly + ", classAll_TDot=" + classAll_TDot
				+ ", classAllCaseCount=" + classAllCaseCount + ", classAllFeeRate=" + classAllFeeRate
				+ ", classAllFeeDiff=" + classAllFeeDiff + ", AllFeeDiff=" + AllFeeDiff + ", OP_Dot=" + OP_Dot
				+ ", OPCount=" + OPCount + ", OP_DrugFee=" + OP_DrugFee + ", OP_Rate=" + OP_Rate + ", classOP="
				+ classOP + ", classOP_TDot=" + classOP_TDot + ", classOPCaseCount=" + classOPCaseCount
				+ ", classOPFeeRate=" + classOPFeeRate + ", IP_Dot=" + IP_Dot + ", IPCount=" + IPCount + ", IP_DrugFee="
				+ IP_DrugFee + ", IP_Rate=" + IP_Rate + ", classIP=" + classIP + ", classIP_TDot=" + classIP_TDot
				+ ", classIPCaseCount=" + classIPCaseCount + ", classIPFeeRate=" + classIPFeeRate + "]";
	}

}
