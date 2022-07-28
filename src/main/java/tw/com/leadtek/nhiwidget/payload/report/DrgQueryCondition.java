package tw.com.leadtek.nhiwidget.payload.report;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("DRG案件數分佈佔率與定額、實際點數")
public class DrgQueryCondition implements Serializable {

	private static final long serialVersionUID = 2384814765849901485L;
	@ApiModelProperty(value = "統計日期", required = false)
	private String date;
	@ApiModelProperty(value = "顯示名稱", required = false)
	private String displayName;
	@ApiModelProperty(value = "總數據列", required = false)
	private List<DrgQueryConditionDetail> total;
	@ApiModelProperty(value = "A區數據列", required = false)
	private List<DrgQueryConditionDetail> sectionA;
	@ApiModelProperty(value = "B1區數據列", required = false)
	private List<DrgQueryConditionDetail> sectionB1;
	@ApiModelProperty(value = "B2區數據列", required = false)
	private List<DrgQueryConditionDetail> sectionB2;
	@ApiModelProperty(value = "C區數據列", required = false)
	private List<DrgQueryConditionDetail> sectionC;

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

	public List<DrgQueryConditionDetail> getTotal() {
		return total;
	}

	public void setTotal(List<DrgQueryConditionDetail> total) {
		this.total = total;
	}

	public List<DrgQueryConditionDetail> getSectionA() {
		return sectionA;
	}

	public void setSectionA(List<DrgQueryConditionDetail> sectionA) {
		this.sectionA = sectionA;
	}

	public List<DrgQueryConditionDetail> getSectionB1() {
		return sectionB1;
	}

	public void setSectionB1(List<DrgQueryConditionDetail> sectionB1) {
		this.sectionB1 = sectionB1;
	}

	public List<DrgQueryConditionDetail> getSectionB2() {
		return sectionB2;
	}

	public void setSectionB2(List<DrgQueryConditionDetail> sectionB2) {
		this.sectionB2 = sectionB2;
	}

	public List<DrgQueryConditionDetail> getSectionC() {
		return sectionC;
	}

	public void setSectionC(List<DrgQueryConditionDetail> sectionC) {
		this.sectionC = sectionC;
	}

}
