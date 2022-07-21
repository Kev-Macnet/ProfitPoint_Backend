package tw.com.leadtek.nhiwidget.payload.report;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;

public class DeductedNoteQueryCondition implements Serializable {

	private static final long serialVersionUID = -3737369377543268476L;
	@ApiModelProperty(value = "核刪件數資訊", required = false)
	List<DeductedNoteQueryConditionInfo> deductedNoteInfo;
	@ApiModelProperty(value = "核刪項目清單", required = false)
	List<DeductedNoteQueryConditionList> deductedList;
	@ApiModelProperty(value = "核刪代碼數量", required = false)
	List<DeductedNoteQueryConditionCode> deductedCode;
	@ApiModelProperty(value = "核刪代碼數量總計算", required = false)
	List<DeductedNoteQueryConditionCode> finalDeductedCode;
	@ApiModelProperty(value = "顯示名稱", required = false)
	String displayName;
	@ApiModelProperty(value = "統計月份", required = false)
	String date;

	public List<DeductedNoteQueryConditionInfo> getDeductedNoteInfo() {
		return deductedNoteInfo;
	}

	public void setDeductedNoteInfo(List<DeductedNoteQueryConditionInfo> deductedNoteInfo) {
		this.deductedNoteInfo = deductedNoteInfo;
	}

	public List<DeductedNoteQueryConditionList> getDeductedList() {
		return deductedList;
	}

	public void setDeductedList(List<DeductedNoteQueryConditionList> deductedList) {
		this.deductedList = deductedList;
	}

	public List<DeductedNoteQueryConditionCode> getDeductedCode() {
		return deductedCode;
	}

	public void setDeductedCode(List<DeductedNoteQueryConditionCode> deductedCode) {
		this.deductedCode = deductedCode;
	}

	public List<DeductedNoteQueryConditionCode> getFinalDeductedCode() {
		return finalDeductedCode;
	}

	public void setFinalDeductedCode(List<DeductedNoteQueryConditionCode> finalDeductedCode) {
		this.finalDeductedCode = finalDeductedCode;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

}
