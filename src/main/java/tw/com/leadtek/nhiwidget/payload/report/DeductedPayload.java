package tw.com.leadtek.nhiwidget.payload.report;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
@ApiModel("核刪資料")
public class DeductedPayload implements Serializable {

	private static final long serialVersionUID = -8778977573619307052L;

	@ApiModelProperty(value = "非專案(隨機)點數-門急診", required = false)
	Long noprojectAmountOp;
	@ApiModelProperty(value = "專案(隨機)點數-門急診", required = false)
	Long projectAmountOp;
	@ApiModelProperty(value = "藥費(隨機)點數-門急診", required = false)
	Long medAmountOp;
	@ApiModelProperty(value = "非專案(隨機)點數-住院", required = false)
	Long noprojectAmountIp;
	@ApiModelProperty(value = "專案(隨機)點數-住院", required = false)
	Long projectAmountIp;
	@ApiModelProperty(value = "藥費(隨機)點數-住院", required = false)
	Long medAmountIp;
	@ApiModelProperty(value = "非專案(隨機)點數-門急診/住院", required = false)
	Long noprojectAmountAll;
	@ApiModelProperty(value = "非專案(隨機)點數-門急診/住院", required = false)
	Long projectAmountAll;
	@ApiModelProperty(value = "非專案(隨機)點數-門急診/住院", required = false)
	Long medAmountAll;
	@ApiModelProperty(value = "非專案(隨機)件數-門急診", required = false)
	Long noprojectQuantityOp;
	@ApiModelProperty(value = "專案(隨機)件數-門急診", required = false)
	Long projectQuantityOp;
	@ApiModelProperty(value = "藥費(隨機)件數-門急診", required = false)
	Long medQuantityOp;
	@ApiModelProperty(value = "非專案(隨機)件數-住院", required = false)
	Long noprojectQuantityIp;
	@ApiModelProperty(value = "專案(隨機)件數-住院", required = false)
	Long projectQuantityIp;
	@ApiModelProperty(value = "藥費(隨機)件數-住院", required = false)
	Long medQuantityIp;
	@ApiModelProperty(value = "非專案(隨機)件數-門急診/住院", required = false)
	Long noprojectQuantityAll;
	@ApiModelProperty(value = "非專案(隨機)件數-門急診/住院", required = false)
	Long projectQuantityAll;
	@ApiModelProperty(value = "非專案(隨機)件數-門急診/住院", required = false)
	Long medQuantityAll;
	@ApiModelProperty(value = "總案件數", required = false)
	Long quatity;
	@ApiModelProperty(value = "總抽件數", required = false)
	Long extractCase;
	@ApiModelProperty(value = "核刪資料表", required = false)
	List<Map<String, Object>> deductedList;
	@ApiModelProperty(value = "回放大資料表", required = false)
	List<Map<String, Object>> rollbackList;
	@ApiModelProperty(value = "爭議資料表", required = false)
	List<Map<String, Object>> disputeList;
	@ApiModelProperty(value = "顯示名稱", required = false)
	String displayName;

	public Long getNoprojectAmountOp() {
		return noprojectAmountOp;
	}

	public void setNoprojectAmountOp(Long noprojectAmountOp) {
		this.noprojectAmountOp = noprojectAmountOp;
	}

	public Long getProjectAmountOp() {
		return projectAmountOp;
	}

	public void setProjectAmountOp(Long projectAmountOp) {
		this.projectAmountOp = projectAmountOp;
	}

	public Long getMedAmountOp() {
		return medAmountOp;
	}

	public void setMedAmountOp(Long medAmountOp) {
		this.medAmountOp = medAmountOp;
	}

	public Long getNoprojectAmountIp() {
		return noprojectAmountIp;
	}

	public void setNoprojectAmountIp(Long noprojectAmountIp) {
		this.noprojectAmountIp = noprojectAmountIp;
	}

	public Long getProjectAmountIp() {
		return projectAmountIp;
	}

	public void setProjectAmountIp(Long projectAmountIp) {
		this.projectAmountIp = projectAmountIp;
	}

	public Long getMedAmountIp() {
		return medAmountIp;
	}

	public void setMedAmountIp(Long medAmountIp) {
		this.medAmountIp = medAmountIp;
	}

	public Long getNoprojectAmountAll() {
		return noprojectAmountAll;
	}

	public void setNoprojectAmountAll(Long noprojectAmountAll) {
		this.noprojectAmountAll = noprojectAmountAll;
	}

	public Long getProjectAmountAll() {
		return projectAmountAll;
	}

	public void setProjectAmountAll(Long projectAmountAll) {
		this.projectAmountAll = projectAmountAll;
	}

	public Long getMedAmountAll() {
		return medAmountAll;
	}

	public void setMedAmountAll(Long medAmountAll) {
		this.medAmountAll = medAmountAll;
	}

	public Long getNoprojectQuantityOp() {
		return noprojectQuantityOp;
	}

	public void setNoprojectQuantityOp(Long noprojectQuantityOp) {
		this.noprojectQuantityOp = noprojectQuantityOp;
	}

	public Long getProjectQuantityOp() {
		return projectQuantityOp;
	}

	public void setProjectQuantityOp(Long projectQuantityOp) {
		this.projectQuantityOp = projectQuantityOp;
	}

	public Long getMedQuantityOp() {
		return medQuantityOp;
	}

	public void setMedQuantityOp(Long medQuantityOp) {
		this.medQuantityOp = medQuantityOp;
	}

	public Long getNoprojectQuantityIp() {
		return noprojectQuantityIp;
	}

	public void setNoprojectQuantityIp(Long noprojectQuantityIp) {
		this.noprojectQuantityIp = noprojectQuantityIp;
	}

	public Long getProjectQuantityIp() {
		return projectQuantityIp;
	}

	public void setProjectQuantityIp(Long projectQuantityIp) {
		this.projectQuantityIp = projectQuantityIp;
	}

	public Long getMedQuantityIp() {
		return medQuantityIp;
	}

	public void setMedQuantityIp(Long medQuantityIp) {
		this.medQuantityIp = medQuantityIp;
	}

	public Long getNoprojectQuantityAll() {
		return noprojectQuantityAll;
	}

	public void setNoprojectQuantityAll(Long noprojectQuantityAll) {
		this.noprojectQuantityAll = noprojectQuantityAll;
	}

	public Long getProjectQuantityAll() {
		return projectQuantityAll;
	}

	public void setProjectQuantityAll(Long projectQuantityAll) {
		this.projectQuantityAll = projectQuantityAll;
	}

	public Long getMedQuantityAll() {
		return medQuantityAll;
	}

	public void setMedQuantityAll(Long medQuantityAll) {
		this.medQuantityAll = medQuantityAll;
	}

	public Long getQuatity() {
		return quatity;
	}

	public void setQuatity(Long quatity) {
		this.quatity = quatity;
	}

	public Long getExtractCase() {
		return extractCase;
	}

	public void setExtractCase(Long extractCase) {
		this.extractCase = extractCase;
	}

	public List<Map<String, Object>> getDeductedList() {
		return deductedList;
	}

	public void setDeductedList(List<Map<String, Object>> deductedList) {
		this.deductedList = deductedList;
	}

	public List<Map<String, Object>> getRollbackList() {
		return rollbackList;
	}

	public void setRollbackList(List<Map<String, Object>> rollbackList) {
		this.rollbackList = rollbackList;
	}

	public List<Map<String, Object>> getDisputeList() {
		return disputeList;
	}

	public void setDisputeList(List<Map<String, Object>> disputeList) {
		this.disputeList = disputeList;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

}
