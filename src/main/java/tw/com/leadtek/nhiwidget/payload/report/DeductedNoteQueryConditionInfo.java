package tw.com.leadtek.nhiwidget.payload.report;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;

public class DeductedNoteQueryConditionInfo implements Serializable {

	private static final long serialVersionUID = 9116765899539749798L;

	@ApiModelProperty(value = "總抽件數", required = false)
	Long extractCase;
	@ApiModelProperty(value = "門急診核刪件數", required = false)
	Long deductedQuantityOp;
	@ApiModelProperty(value = "門急診核刪點數", required = false)
	Long deductedAmountOp;
	@ApiModelProperty(value = "門急診放大回推金額(月)", required = false)
	Long rollbackMOp;
	@ApiModelProperty(value = "住院核刪件數", required = false)
	Long deductedQuantityIp;
	@ApiModelProperty(value = "住院核刪點數", required = false)
	Long deductedAmountIp;
	@ApiModelProperty(value = "住院放大回推金額(月)", required = false)
	Long rollbackMIp;
	@ApiModelProperty(value = "統計月份", required = false)
	String date;

	public Long getExtractCase() {
		return extractCase;
	}

	public void setExtractCase(Long extractCase) {
		this.extractCase = extractCase;
	}

	public Long getDeductedQuantityOp() {
		return deductedQuantityOp;
	}

	public void setDeductedQuantityOp(Long deductedQuantityOp) {
		this.deductedQuantityOp = deductedQuantityOp;
	}

	public Long getDeductedAmountOp() {
		return deductedAmountOp;
	}

	public void setDeductedAmountOp(Long deductedAmountOp) {
		this.deductedAmountOp = deductedAmountOp;
	}

	public Long getRollbackMOp() {
		return rollbackMOp;
	}

	public void setRollbackMOp(Long rollbackMOp) {
		this.rollbackMOp = rollbackMOp;
	}

	public Long getDeductedQuantityIp() {
		return deductedQuantityIp;
	}

	public void setDeductedQuantityIp(Long deductedQuantityIp) {
		this.deductedQuantityIp = deductedQuantityIp;
	}

	public Long getDeductedAmountIp() {
		return deductedAmountIp;
	}

	public void setDeductedAmountIp(Long deductedAmountIp) {
		this.deductedAmountIp = deductedAmountIp;
	}

	public Long getRollbackMIp() {
		return rollbackMIp;
	}

	public void setRollbackMIp(Long rollbackMIp) {
		this.rollbackMIp = rollbackMIp;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

}
