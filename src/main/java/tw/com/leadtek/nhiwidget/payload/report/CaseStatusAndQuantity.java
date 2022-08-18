package tw.com.leadtek.nhiwidget.payload.report;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.payload.BaseResponse;

@ApiModel("案件狀態與各別數量(可複選)")
public class CaseStatusAndQuantity extends BaseResponse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7966386608678521384L;
	
	@ApiModelProperty(value="開始時間", required = true)
	private String startDate;
	
	@ApiModelProperty(value="結束時間", required = true)
	private String endDate;

	@ApiModelProperty(value="案件狀態與各別數量", required = true)
	private Map<String, Integer> statusMap;

	@ApiModelProperty(value="就醫編號清單", required = true)
	private Map<String, List<String>> physicalMap;

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public Map<String, Integer> getStatusMap() {
		return statusMap;
	}

	public void setStatusMap(Map<String, Integer> statusMap) {
		this.statusMap = statusMap;
	}

	public Map<String, List<String>> getPhysicalMap() {
		return physicalMap;
	}

	public void setPhysicalMap(Map<String, List<String>> physicalMap) {
		this.physicalMap = physicalMap;
	}
	
	
}
