package tw.com.leadtek.nhiwidget.payload.report;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
@ApiModel("自費項目清單詳細")
public class OwnExpenseQueryConditionDetail implements Serializable {

	private static final long serialVersionUID = -5816469250165326547L;
	@ApiModelProperty(value = "就醫類別", required = false)
	private String dataFormat;
	@ApiModelProperty(value = "科別", required = false)
	private String funcType;
	@ApiModelProperty(value = "科別中文", required = false)
	private String descChi;
	@ApiModelProperty(value = "自費案件數", required = false)
	private Long quantity;
	@ApiModelProperty(value = "自費金額", required = false)
	private Long expense;
	@ApiModelProperty(value = "醫護人員", required = false)
	private String prsnId;

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

	public String getPrsnId() {
		return prsnId;
	}

	public void setPrsnId(String prsnId) {
		this.prsnId = prsnId;
	}
	
	

}
