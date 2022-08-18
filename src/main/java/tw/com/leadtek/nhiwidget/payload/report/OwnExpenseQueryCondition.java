package tw.com.leadtek.nhiwidget.payload.report;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.payload.BaseResponse;

@ApiModel("自費項目清單")
public class OwnExpenseQueryCondition implements Serializable {

	private static final long serialVersionUID = -6963338333989451514L;

	@ApiModelProperty(value = "不分區案件數", required = false)
	private Long allQuantity;
	@ApiModelProperty(value = "不分區自費總金額", required = false)
	private Long allExpense;
	@ApiModelProperty(value = "門急診案件數", required = false)
	private Long opAllQuantity;
	@ApiModelProperty(value = "門急診自費總金額", required = false)
	private Long opAllExpense;
	@ApiModelProperty(value = "門診案件數", required = false)
	private Long opQuantity;
	@ApiModelProperty(value = "門診自費總金額", required = false)
	private Long opExpense;
	@ApiModelProperty(value = "急診案件數", required = false)
	private Long emQuantity;
	@ApiModelProperty(value = "急診自費總金額", required = false)
	private Long emExpense;
	@ApiModelProperty(value = "住院案件數", required = false)
	private Long ipQuantity;
	@ApiModelProperty(value = "住院自費總金額", required = false)
	private Long ipExpense;
	@ApiModelProperty(value = "顯示名稱", required = false)
	private String displayName;
	@ApiModelProperty(value = "統計日期", required = false)
	private String date;
	@ApiModelProperty(value = "不分科資料詳細列表", required = false)
	private List<OwnExpenseQueryConditionDetail> allList;
	@ApiModelProperty(value = "門急診資料詳細列表", required = false)
	private List<OwnExpenseQueryConditionDetail> opAllList;
	@ApiModelProperty(value = "門診資料詳細列表", required = false)
	private List<OwnExpenseQueryConditionDetail> opList;
	@ApiModelProperty(value = "急診資料詳細列表", required = false)
	private List<OwnExpenseQueryConditionDetail> emList;
	@ApiModelProperty(value = "住院資料詳細列表", required = false)
	private List<OwnExpenseQueryConditionDetail> ipList;
	@ApiModelProperty(value = "不分科醫護人員資料詳細列表", required = false)
	private List<OwnExpenseQueryConditionDetail> allPrsnList;
	@ApiModelProperty(value = "門急診醫護人員資料詳細列表", required = false)
	private List<OwnExpenseQueryConditionDetail> opAllPrsnList;
	@ApiModelProperty(value = "門診醫護人員資料詳細列表", required = false)
	private List<OwnExpenseQueryConditionDetail> opPrsnList;
	@ApiModelProperty(value = "急診醫護人員資料詳細列表", required = false)
	private List<OwnExpenseQueryConditionDetail> emPrsnList;
	@ApiModelProperty(value = "住院醫護人員資料詳細列表", required = false)
	private List<OwnExpenseQueryConditionDetail> ipPrsnList;

	public Long getAllQuantity() {
		return allQuantity;
	}

	public void setAllQuantity(Long allQuantity) {
		this.allQuantity = allQuantity;
	}

	public Long getAllExpense() {
		return allExpense;
	}

	public void setAllExpense(Long allExpense) {
		this.allExpense = allExpense;
	}

	public Long getOpAllQuantity() {
		return opAllQuantity;
	}

	public void setOpAllQuantity(Long opAllQuantity) {
		this.opAllQuantity = opAllQuantity;
	}

	public Long getOpAllExpense() {
		return opAllExpense;
	}

	public void setOpAllExpense(Long opAllExpense) {
		this.opAllExpense = opAllExpense;
	}

	public Long getOpQuantity() {
		return opQuantity;
	}

	public void setOpQuantity(Long opQuantity) {
		this.opQuantity = opQuantity;
	}

	public Long getOpExpense() {
		return opExpense;
	}

	public void setOpExpense(Long opExpense) {
		this.opExpense = opExpense;
	}

	public Long getEmQuantity() {
		return emQuantity;
	}

	public void setEmQuantity(Long emQuantity) {
		this.emQuantity = emQuantity;
	}

	public Long getEmExpense() {
		return emExpense;
	}

	public void setEmExpense(Long emExpense) {
		this.emExpense = emExpense;
	}

	public Long getIpQuantity() {
		return ipQuantity;
	}

	public void setIpQuantity(Long ipQuantity) {
		this.ipQuantity = ipQuantity;
	}

	public Long getIpExpense() {
		return ipExpense;
	}

	public void setIpExpense(Long ipExpense) {
		this.ipExpense = ipExpense;
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

	public List<OwnExpenseQueryConditionDetail> getAllList() {
		return allList;
	}

	public void setAllList(List<OwnExpenseQueryConditionDetail> allList) {
		this.allList = allList;
	}

	public List<OwnExpenseQueryConditionDetail> getOpAllList() {
		return opAllList;
	}

	public void setOpAllList(List<OwnExpenseQueryConditionDetail> opAllList) {
		this.opAllList = opAllList;
	}

	public List<OwnExpenseQueryConditionDetail> getOpList() {
		return opList;
	}

	public void setOpList(List<OwnExpenseQueryConditionDetail> opList) {
		this.opList = opList;
	}

	public List<OwnExpenseQueryConditionDetail> getEmList() {
		return emList;
	}

	public void setEmList(List<OwnExpenseQueryConditionDetail> emList) {
		this.emList = emList;
	}

	public List<OwnExpenseQueryConditionDetail> getIpList() {
		return ipList;
	}

	public void setIpList(List<OwnExpenseQueryConditionDetail> ipList) {
		this.ipList = ipList;
	}

	public List<OwnExpenseQueryConditionDetail> getAllPrsnList() {
		return allPrsnList;
	}

	public void setAllPrsnList(List<OwnExpenseQueryConditionDetail> allPrsnList) {
		this.allPrsnList = allPrsnList;
	}

	public List<OwnExpenseQueryConditionDetail> getOpAllPrsnList() {
		return opAllPrsnList;
	}

	public void setOpAllPrsnList(List<OwnExpenseQueryConditionDetail> opAllPrsnList) {
		this.opAllPrsnList = opAllPrsnList;
	}

	public List<OwnExpenseQueryConditionDetail> getOpPrsnList() {
		return opPrsnList;
	}

	public void setOpPrsnList(List<OwnExpenseQueryConditionDetail> opPrsnList) {
		this.opPrsnList = opPrsnList;
	}

	public List<OwnExpenseQueryConditionDetail> getEmPrsnList() {
		return emPrsnList;
	}

	public void setEmPrsnList(List<OwnExpenseQueryConditionDetail> emPrsnList) {
		this.emPrsnList = emPrsnList;
	}

	public List<OwnExpenseQueryConditionDetail> getIpPrsnList() {
		return ipPrsnList;
	}

	public void setIpPrsnList(List<OwnExpenseQueryConditionDetail> ipPrsnList) {
		this.ipPrsnList = ipPrsnList;
	}

}
