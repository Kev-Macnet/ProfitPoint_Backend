package tw.com.leadtek.nhiwidget.payload.report;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("自費項目清單詳細醫令")
public class OwnExpenseQueryConditionIhnCodeInfo implements Serializable {

	private static final long serialVersionUID = 8376765485161164779L;

	@ApiModelProperty(value = "醫令", required = false)
	private String ihnCode;
	@ApiModelProperty(value = "就醫類別", required = false)
	private String dataFormat;
	@ApiModelProperty(value = "科別", required = false)
	private String funcType;
	@ApiModelProperty(value = "自費案件數", required = false)
	private Long quantity;
	@ApiModelProperty(value = "自費金額", required = false)
	private Long expense;

	public String getIhnCode() {
		return ihnCode;
	}

	public void setIhnCode(String ihnCode) {
		this.ihnCode = ihnCode;
	}

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

	public Long getQuantity() {
		return quantity;
	}

	public void setQuantity(Long quantity) {
		this.quantity = quantity;
	}

	public Long getExpense() {
		return expense;
	}

	public void setExpense(Long expense) {
		this.expense = expense;
	}

}
