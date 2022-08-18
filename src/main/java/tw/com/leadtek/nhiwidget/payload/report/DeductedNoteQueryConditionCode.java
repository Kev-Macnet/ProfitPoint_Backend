package tw.com.leadtek.nhiwidget.payload.report;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;

public class DeductedNoteQueryConditionCode implements Serializable {

	private static final long serialVersionUID = 2650243632155022592L;
	@ApiModelProperty(value = "就醫類別", required = false)
	String dataFormat;
	@ApiModelProperty(value = "核刪數量", required = false)
	Long deductedQuantity;
	@ApiModelProperty(value = "核刪點數", required = false)
	Long deductedAmount;
	@ApiModelProperty(value = "核刪代碼", required = false)
	String code;

	public String getDataFormat() {
		return dataFormat;
	}

	public void setDataFormat(String dataFormat) {
		this.dataFormat = dataFormat;
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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
