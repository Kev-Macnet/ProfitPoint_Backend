package tw.com.leadtek.nhiwidget.payload.report;

import java.io.Serializable;

public class OwnExpenseQueryConditionDetail implements Serializable {

	private static final long serialVersionUID = -5816469250165326547L;
	
	private String dataFormat;
	private String funcType;
	private String descChi;
	private Long quantity;
	private Long expense;

	public String getFuncType() {
		return funcType;
	}

	public void setFuncType(String funcType) {
		this.funcType = funcType;
	}

	public String getDescChi() {
		return descChi;
	}

	public void setDescChi(String descChi) {
		this.descChi = descChi;
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

	public String getDataFormat() {
		return dataFormat;
	}

	public void setDataFormat(String dataFormat) {
		this.dataFormat = dataFormat;
	}

}
