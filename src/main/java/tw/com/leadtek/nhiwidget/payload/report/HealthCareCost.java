package tw.com.leadtek.nhiwidget.payload.report;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
	
	@ApiModelProperty(value="門急診-住院病歷總藥費", required = true)
	private String AllDrugFee;
	
	@ApiModelProperty(value="門急診-住院藥費佔率", required = true)
	private String AllRate;
	
	@ApiModelProperty(value="門急診-住院各科別藥品總點數", required = true)
	private List<ClassDrugDotDto> classAll;
	
	@ApiModelProperty(value="門急診-住院各科別總藥費差額", required = true)
	private List<ClassDrugFeeDto> classAllFeeDiff;
	
	@ApiModelProperty(value="門急診-住院總藥費差額", required = true)
	private String AllFeeDiff;
	
	@ApiModelProperty(value="門急診病歷總點數", required = true)
	private String OP_Dot;
	
	@ApiModelProperty(value="門急診病歷總藥費", required = true)
	private String OP_DrugFee;
	
	@ApiModelProperty(value="門急診藥費佔率", required = true)
	private String OP_Rate;
	
	@ApiModelProperty(value="門急診各科別藥品總點數", required = true)
	private List<ClassDrugDotDto> classOP;
	
	@ApiModelProperty(value="住院病歷總點數", required = true)
	private String IP_Dot;
	
	@ApiModelProperty(value="住院病歷總藥費", required = true)
	private String IP_DrugFee;
	
	@ApiModelProperty(value="住院藥費佔率", required = true)
	private String IP_Rate;
	
	@ApiModelProperty(value="住院各科別藥品總點數", required = true)
	private List<ClassDrugDotDto> classIP;

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

	public String getIP_Dot() {
		return IP_Dot;
	}

	public void setIP_Dot(String iP_Dot) {
		IP_Dot = iP_Dot;
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

	@Override
	public String toString() {
		return "HealthCareCost [season=" + season + ", AllDot=" + AllDot + ", AllDrugFee=" + AllDrugFee + ", AllRate="
				+ AllRate + ", classAll=" + classAll + ", classAllFeeDiff=" + classAllFeeDiff + ", AllFeeDiff="
				+ AllFeeDiff + ", OP_Dot=" + OP_Dot + ", OP_DrugFee=" + OP_DrugFee + ", OP_Rate=" + OP_Rate
				+ ", classOP=" + classOP + ", IP_Dot=" + IP_Dot + ", IP_DrugFee=" + IP_DrugFee + ", IP_Rate=" + IP_Rate
				+ ", classIP=" + classIP + "]";
	}

}
