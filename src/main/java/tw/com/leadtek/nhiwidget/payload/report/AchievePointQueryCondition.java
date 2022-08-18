package tw.com.leadtek.nhiwidget.payload.report;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.payload.BaseResponse;

@ApiModel("申報分配佔率與點數、金額")
public class AchievePointQueryCondition extends BaseResponse implements Serializable {

	private static final long serialVersionUID = -3590982994009349956L;
	@ApiModelProperty(value = "不分區總點數", required = false)
	private Long allDot;
	@ApiModelProperty(value = "門急診總點數", required = false)
	private Long opAllDot;
	@ApiModelProperty(value = "門診總點數", required = false)
	private Long opDot;
	@ApiModelProperty(value = "急診總點數", required = false)
	private Long emDot;
	@ApiModelProperty(value = "住院總點數", required = false)
	private Long ipDot;
	@ApiModelProperty(value = "支付代碼不分區總點數", required = false)
	private Long allPayCodeDot;
	@ApiModelProperty(value = "支付代碼門急診總點數", required = false)
	private Long opAllPayCodeDot;
	@ApiModelProperty(value = "支付代碼門診總點數", required = false)
	private Long opPayCodeDot;
	@ApiModelProperty(value = "支付代碼急診總點數", required = false)
	private Long emPayCodeDot;
	@ApiModelProperty(value = "支付代碼住院總點數", required = false)
	private Long ipPayCodeDot;
	@ApiModelProperty(value = "內院碼不分區總點數", required = false)
	private Long allInhCodeDot;
	@ApiModelProperty(value = "內院碼門急診總點數", required = false)
	private Long opAllInhCodeDot;
	@ApiModelProperty(value = "內院碼門診總點數", required = false)
	private Long opInhCodeDot;
	@ApiModelProperty(value = "內院碼急診總點數", required = false)
	private Long emInhCodeDot;
	@ApiModelProperty(value = "內院碼住院總點數", required = false)
	private Long ipInhCodeDot;
	@ApiModelProperty(value = "支付代碼不分區百分比", required = false)
	private Double allPayCodeDotPercent;
	@ApiModelProperty(value = "支付代碼門急診百分比", required = false)
	private Double opAllPayCodeDotPercent;
	@ApiModelProperty(value = "支付代碼門診百分比", required = false)
	private Double opPayCodeDotPercent;
	@ApiModelProperty(value = "支付代碼急診百分比", required = false)
	private Double emPayCodeDotPercent;
	@ApiModelProperty(value = "支付代碼住院百分比", required = false)
	private Double ipPayCodeDotPercent;
	@ApiModelProperty(value = "內院碼不分區百分比", required = false)
	private Double allInhCodeDotPercent;
	@ApiModelProperty(value = "內院碼門急診百分比", required = false)
	private Double opAllInhCodeDotPercent;
	@ApiModelProperty(value = "內院碼門診百分比", required = false)
	private Double opInhCodeDotPercent;
	@ApiModelProperty(value = "內院碼急診百分比", required = false)
	private Double emInhCodeDotPercent;
	@ApiModelProperty(value = "內院碼百分比", required = false)
	private Double ipInhCodeDotPercent;
	@ApiModelProperty(value = "門急診點數占率", required = false)
	private Double opAllDotPercent;
	@ApiModelProperty(value = "門診點數占率", required = false)
	private Double opDotPercent;
	@ApiModelProperty(value = "急診點數占率", required = false)
	private Double emDotPercent;
	@ApiModelProperty(value = "住院點數占率", required = false)
	private Double ipDotPercent;
	@ApiModelProperty(value = "費用列表", required = false)
	private List<AchievePointQueryConditionDetail> payCodeTypeList;
	@ApiModelProperty(value = "輸入日期", required = false)
	private String date;
	@ApiModelProperty(value = "顯示名稱", required = false)
	private String displayName;

	public Long getAllDot() {
		return allDot;
	}

	public void setAllDot(Long allDot) {
		this.allDot = allDot;
	}

	public Long getOpAllDot() {
		return opAllDot;
	}

	public void setOpAllDot(Long opAllDot) {
		this.opAllDot = opAllDot;
	}

	public Long getOpDot() {
		return opDot;
	}

	public void setOpDot(Long opDot) {
		this.opDot = opDot;
	}

	public Long getEmDot() {
		return emDot;
	}

	public void setEmDot(Long emDot) {
		this.emDot = emDot;
	}

	public Long getIpDot() {
		return ipDot;
	}

	public void setIpDot(Long ipDot) {
		this.ipDot = ipDot;
	}

	public Long getAllPayCodeDot() {
		return allPayCodeDot;
	}

	public void setAllPayCodeDot(Long allPayCodeDot) {
		this.allPayCodeDot = allPayCodeDot;
	}

	public Long getOpAllPayCodeDot() {
		return opAllPayCodeDot;
	}

	public void setOpAllPayCodeDot(Long opAllPayCodeDot) {
		this.opAllPayCodeDot = opAllPayCodeDot;
	}

	public Long getOpPayCodeDot() {
		return opPayCodeDot;
	}

	public void setOpPayCodeDot(Long opPayCodeDot) {
		this.opPayCodeDot = opPayCodeDot;
	}

	public Long getEmPayCodeDot() {
		return emPayCodeDot;
	}

	public void setEmPayCodeDot(Long emPayCodeDot) {
		this.emPayCodeDot = emPayCodeDot;
	}

	public Long getIpPayCodeDot() {
		return ipPayCodeDot;
	}

	public void setIpPayCodeDot(Long ipPayCodeDot) {
		this.ipPayCodeDot = ipPayCodeDot;
	}

	public Long getAllInhCodeDot() {
		return allInhCodeDot;
	}

	public void setAllInhCodeDot(Long allInhCodeDot) {
		this.allInhCodeDot = allInhCodeDot;
	}

	public Long getOpAllInhCodeDot() {
		return opAllInhCodeDot;
	}

	public void setOpAllInhCodeDot(Long opAllInhCodeDot) {
		this.opAllInhCodeDot = opAllInhCodeDot;
	}

	public Long getOpInhCodeDot() {
		return opInhCodeDot;
	}

	public void setOpInhCodeDot(Long opInhCodeDot) {
		this.opInhCodeDot = opInhCodeDot;
	}

	public Long getEmInhCodeDot() {
		return emInhCodeDot;
	}

	public void setEmInhCodeDot(Long emInhCodeDot) {
		this.emInhCodeDot = emInhCodeDot;
	}

	public Long getIpInhCodeDot() {
		return ipInhCodeDot;
	}

	public void setIpInhCodeDot(Long ipInhCodeDot) {
		this.ipInhCodeDot = ipInhCodeDot;
	}

	public Double getAllPayCodeDotPercent() {
		return allPayCodeDotPercent;
	}

	public void setAllPayCodeDotPercent(Double allPayCodeDotPercent) {
		this.allPayCodeDotPercent = allPayCodeDotPercent;
	}

	public Double getOpAllPayCodeDotPercent() {
		return opAllPayCodeDotPercent;
	}

	public void setOpAllPayCodeDotPercent(Double opAllPayCodeDotPercent) {
		this.opAllPayCodeDotPercent = opAllPayCodeDotPercent;
	}

	public Double getOpPayCodeDotPercent() {
		return opPayCodeDotPercent;
	}

	public void setOpPayCodeDotPercent(Double opPayCodeDotPercent) {
		this.opPayCodeDotPercent = opPayCodeDotPercent;
	}

	public Double getEmPayCodeDotPercent() {
		return emPayCodeDotPercent;
	}

	public void setEmPayCodeDotPercent(Double emPayCodeDotPercent) {
		this.emPayCodeDotPercent = emPayCodeDotPercent;
	}

	public Double getIpPayCodeDotPercent() {
		return ipPayCodeDotPercent;
	}

	public void setIpPayCodeDotPercent(Double ipPayCodeDotPercent) {
		this.ipPayCodeDotPercent = ipPayCodeDotPercent;
	}

	public Double getAllInhCodeDotPercent() {
		return allInhCodeDotPercent;
	}

	public void setAllInhCodeDotPercent(Double allInhCodeDotPercent) {
		this.allInhCodeDotPercent = allInhCodeDotPercent;
	}

	public Double getOpAllInhCodeDotPercent() {
		return opAllInhCodeDotPercent;
	}

	public void setOpAllInhCodeDotPercent(Double opAllInhCodeDotPercent) {
		this.opAllInhCodeDotPercent = opAllInhCodeDotPercent;
	}

	public Double getOpInhCodeDotPercent() {
		return opInhCodeDotPercent;
	}

	public void setOpInhCodeDotPercent(Double opInhCodeDotPercent) {
		this.opInhCodeDotPercent = opInhCodeDotPercent;
	}

	public Double getEmInhCodeDotPercent() {
		return emInhCodeDotPercent;
	}

	public void setEmInhCodeDotPercent(Double emInhCodeDotPercent) {
		this.emInhCodeDotPercent = emInhCodeDotPercent;
	}

	public Double getIpInhCodeDotPercent() {
		return ipInhCodeDotPercent;
	}

	public void setIpInhCodeDotPercent(Double ipInhCodeDotPercent) {
		this.ipInhCodeDotPercent = ipInhCodeDotPercent;
	}

	

	public List<AchievePointQueryConditionDetail> getPayCodeTypeList() {
		return payCodeTypeList;
	}

	public void setPayCodeTypeList(List<AchievePointQueryConditionDetail> payCodeTypeList) {
		this.payCodeTypeList = payCodeTypeList;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public Double getOpAllDotPercent() {
		return opAllDotPercent;
	}

	public void setOpAllDotPercent(Double opAllDotPercent) {
		this.opAllDotPercent = opAllDotPercent;
	}

	public Double getOpDotPercent() {
		return opDotPercent;
	}

	public void setOpDotPercent(Double opDotPercent) {
		this.opDotPercent = opDotPercent;
	}

	public Double getEmDotPercent() {
		return emDotPercent;
	}

	public void setEmDotPercent(Double emDotPercent) {
		this.emDotPercent = emDotPercent;
	}

	public Double getIpDotPercent() {
		return ipDotPercent;
	}

	public void setIpDotPercent(Double ipDotPercent) {
		this.ipDotPercent = ipDotPercent;
	}
	
	
	
    

}
