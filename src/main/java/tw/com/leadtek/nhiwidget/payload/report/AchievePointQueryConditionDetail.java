package tw.com.leadtek.nhiwidget.payload.report;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;

public class AchievePointQueryConditionDetail implements Serializable{

	private static final long serialVersionUID = 8214131519601492833L;
	@ApiModelProperty(value = "不分區類型費用總點數", required = false)
	private Long allPayCodeTypeDot;
	@ApiModelProperty(value = "門急診類型費用總點數", required = false)
	private Long opAllPayCodeTypeDot;
	@ApiModelProperty(value = "門診類型費用總點數", required = false)
	private Long opPayCodeTypeDot;
	@ApiModelProperty(value = "急診類型費用總點數", required = false)
	private Long emPayCodeTypeDot;
	@ApiModelProperty(value = "住院類型費用總點數", required = false)
	private Long ipPayCodeTypeDot;
	@ApiModelProperty(value = "不分區類型費用百分比", required = false)
	private Double allPayCodeTypeDotPercent;
	@ApiModelProperty(value = "門急診類型費用百分比", required = false)
	private Double opAllPayCodeTypeDotPercent;
	@ApiModelProperty(value = "門診類型費用百分比", required = false)
	private Double opPayCodeTypeDotPercent;
	@ApiModelProperty(value = "急診類型費用百分比", required = false)
	private Double emPayCodeTypeDotPercent;
	@ApiModelProperty(value = "住院類型費用百分比", required = false)
	private Double ipPayCodeTypeDotPercent;
	@ApiModelProperty(value = "類型費用代碼", required = false)
	private String code;
	@ApiModelProperty(value = "類型費用名稱", required = false)
	private String name;
	
	public Long getAllPayCodeTypeDot() {
		return allPayCodeTypeDot;
	}
	public void setAllPayCodeTypeDot(Long allPayCodeTypeDot) {
		this.allPayCodeTypeDot = allPayCodeTypeDot;
	}
	public Long getOpAllPayCodeTypeDot() {
		return opAllPayCodeTypeDot;
	}
	public void setOpAllPayCodeTypeDot(Long opAllPayCodeTypeDot) {
		this.opAllPayCodeTypeDot = opAllPayCodeTypeDot;
	}
	public Long getOpPayCodeTypeDot() {
		return opPayCodeTypeDot;
	}
	public void setOpPayCodeTypeDot(Long opPayCodeTypeDot) {
		this.opPayCodeTypeDot = opPayCodeTypeDot;
	}
	public Long getEmPayCodeTypeDot() {
		return emPayCodeTypeDot;
	}
	public void setEmPayCodeTypeDot(Long emPayCodeTypeDot) {
		this.emPayCodeTypeDot = emPayCodeTypeDot;
	}
	public Long getIpPayCodeTypeDot() {
		return ipPayCodeTypeDot;
	}
	public void setIpPayCodeTypeDot(Long ipPayCodeTypeDot) {
		this.ipPayCodeTypeDot = ipPayCodeTypeDot;
	}
	public Double getAllPayCodeTypeDotPercent() {
		return allPayCodeTypeDotPercent;
	}
	public void setAllPayCodeTypeDotPercent(Double allPayCodeTypeDotPercent) {
		this.allPayCodeTypeDotPercent = allPayCodeTypeDotPercent;
	}
	public Double getOpAllPayCodeTypeDotPercent() {
		return opAllPayCodeTypeDotPercent;
	}
	public void setOpAllPayCodeTypeDotPercent(Double opAllPayCodeTypeDotPercent) {
		this.opAllPayCodeTypeDotPercent = opAllPayCodeTypeDotPercent;
	}
	public Double getOpPayCodeTypeDotPercent() {
		return opPayCodeTypeDotPercent;
	}
	public void setOpPayCodeTypeDotPercent(Double opPayCodeTypeDotPercent) {
		this.opPayCodeTypeDotPercent = opPayCodeTypeDotPercent;
	}
	public Double getEmPayCodeTypeDotPercent() {
		return emPayCodeTypeDotPercent;
	}
	public void setEmPayCodeTypeDotPercent(Double emPayCodeTypeDotPercent) {
		this.emPayCodeTypeDotPercent = emPayCodeTypeDotPercent;
	}
	public Double getIpPayCodeTypeDotPercent() {
		return ipPayCodeTypeDotPercent;
	}
	public void setIpPayCodeTypeDotPercent(Double ipPayCodeTypeDotPercent) {
		this.ipPayCodeTypeDotPercent = ipPayCodeTypeDotPercent;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
	
	

}
