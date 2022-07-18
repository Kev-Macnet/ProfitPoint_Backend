package tw.com.leadtek.nhiwidget.payload.report;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;

public class DeductedNoteQueryConditionList implements Serializable {

	private static final long serialVersionUID = -6287225792554703370L;

	@ApiModelProperty(value = "就醫類別", required = false)
	String dataFormat;
	@ApiModelProperty(value = "科別", required = false)
	String funcType;
	@ApiModelProperty(value = "科別中文", required = false)
	String funcTypeName;
	@ApiModelProperty(value = "申復不補付理由代碼", required = false)
	String afrNoPayCode;
	@ApiModelProperty(value = "核刪代碼", required = false)
	String code;
	@ApiModelProperty(value = "核刪支付代碼", required = false)
	String deductedOrder;
	@ApiModelProperty(value = "醫護人員ID", required = false)
	String prsnId;
	@ApiModelProperty(value = "就醫紀錄編號", required = false)
	String inhClinicId;
	@ApiModelProperty(value = "核刪數量", required = false)
	Long deductedQuantity;
	@ApiModelProperty(value = "核刪點數", required = false)
	Long deductedAmount;
	@ApiModelProperty(value = "放大回推金額(月)", required = false)
	Long rollbackM;
	@ApiModelProperty(value = "申復補付數量", required = false)
	Long afrPayQuantity;
	@ApiModelProperty(value = "申復補付金額", required = false)
	Long afrPayAmount;

	public String getDataFormat() {
		return dataFormat;
	}

	public void setDataFormat(String dataFormat) {
		this.dataFormat = dataFormat;
	}

	public String getFuncType() {
		return funcType;
	}

	public void setFuncType(String funcType) {
		this.funcType = funcType;
	}

	public String getFuncTypeName() {
		return funcTypeName;
	}

	public void setFuncTypeName(String funcTypeName) {
		this.funcTypeName = funcTypeName;
	}

	public String getAfrNoPayCode() {
		return afrNoPayCode;
	}

	public void setAfrNoPayCode(String afrNoPayCode) {
		this.afrNoPayCode = afrNoPayCode;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDeductedOrder() {
		return deductedOrder;
	}

	public void setDeductedOrder(String deductedOrder) {
		this.deductedOrder = deductedOrder;
	}

	public String getPrsnId() {
		return prsnId;
	}

	public void setPrsnId(String prsnId) {
		this.prsnId = prsnId;
	}

	public String getInhClinicId() {
		return inhClinicId;
	}

	public void setInhClinicId(String inhClinicId) {
		this.inhClinicId = inhClinicId;
	}

	public Long getDeductedQuantity() {
		return deductedQuantity;
	}

	public void setDeductedQuantity(Long deductedQuantity) {
		this.deductedQuantity = deductedQuantity;
	}

	public Long getDeductedAmount() {
		return deductedAmount;
	}

	public void setDeductedAmount(Long deductedAmount) {
		this.deductedAmount = deductedAmount;
	}

	public Long getRollbackM() {
		return rollbackM;
	}

	public void setRollbackM(Long rollbackM) {
		this.rollbackM = rollbackM;
	}

	public Long getAfrPayQuantity() {
		return afrPayQuantity;
	}

	public void setAfrPayQuantity(Long afrPayQuantity) {
		this.afrPayQuantity = afrPayQuantity;
	}

	public Long getAfrPayAmount() {
		return afrPayAmount;
	}

	public void setAfrPayAmount(Long afrPayAmount) {
		this.afrPayAmount = afrPayAmount;
	}
}
