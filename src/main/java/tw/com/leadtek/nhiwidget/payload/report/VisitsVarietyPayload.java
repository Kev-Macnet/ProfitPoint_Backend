/**
 * Created on 2022/3/18.
 */
package tw.com.leadtek.nhiwidget.payload.report;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.payload.BaseResponse;

@ApiModel("門急診-住院-出院人次變化")
public class VisitsVarietyPayload extends BaseResponse implements Serializable {

	private static final long serialVersionUID = 6336744080955548591L;

	@ApiModelProperty("門急診-住院-出院人次變化-時間區間點數-實際總點數")
	private PointPeriod actual;

	@ApiModelProperty("門急診-住院-出院人次變化-時間區間點數-累計申報總點數")
	private PointPeriod appl;

	@ApiModelProperty("門急診-住院-出院人次變化-時間區間人次")
	private VisitsPeriod visitsPeriod;

	@ApiModelProperty(value = "各科別名稱", required = true)
	protected List<String> funcTypes;

	@ApiModelProperty(value = "各科門急診人次", required = true)
	protected Map<String, NameValueList> opemMap;

	@ApiModelProperty(value = "各科住院人次", required = true)
	protected Map<String, NameValueList> ipMap;

	@ApiModelProperty(value = "各科出院人次", required = true)
	protected Map<String, NameValueList> leaveMap;

	@ApiModelProperty(value = "各科門急診/住院院人次")
	protected Map<String, NameValueList> allMap;

	@ApiModelProperty(value = "各科門急診案件數/點數")
	protected Map<String, NameValueList3> opemMap3;

	@ApiModelProperty(value = "各科住院案件數/點數")
	protected Map<String, NameValueList3> ipMap3;

	@ApiModelProperty(value = "各科出院案件數/點數")
	protected Map<String, NameValueList3> leaveMap3;

	@ApiModelProperty(value = "各科出院案件數/點數")
	protected Map<String, NameValueList3> allMap3;

	public VisitsVarietyPayload() {
		opemMap = new HashMap<String, NameValueList>();
		ipMap = new HashMap<String, NameValueList>();
		leaveMap = new HashMap<String, NameValueList>();
		opemMap3 = new HashMap<String, NameValueList3>();
		ipMap3 = new HashMap<String, NameValueList3>();
		leaveMap3 = new HashMap<String, NameValueList3>();
		allMap3 = new HashMap<String, NameValueList3>();
	}

	public PointPeriod getActual() {
		return actual;
	}

	public void setActual(PointPeriod actual) {
		this.actual = actual;
	}

	public PointPeriod getAppl() {
		return appl;
	}

	public void setAppl(PointPeriod appl) {
		this.appl = appl;
	}

	public VisitsPeriod getVisitsPeriod() {
		return visitsPeriod;
	}

	public void setVisitsPeriod(VisitsPeriod visitsPeriod) {
		this.visitsPeriod = visitsPeriod;
	}

	public List<String> getFuncTypes() {
		return funcTypes;
	}

	public void setFuncTypes(List<String> funcTypes) {
		this.funcTypes = funcTypes;
	}

	public Map<String, NameValueList> getOpemMap() {
		return opemMap;
	}

	public void setOpemMap(Map<String, NameValueList> opemMap) {
		this.opemMap = opemMap;
	}

	public Map<String, NameValueList> getIpMap() {
		return ipMap;
	}

	public void setIpMap(Map<String, NameValueList> ipMap) {
		this.ipMap = ipMap;
	}

	public Map<String, NameValueList> getLeaveMap() {
		return leaveMap;
	}

	public void setLeaveMap(Map<String, NameValueList> leaveMap) {
		this.leaveMap = leaveMap;
	}

	public Map<String, NameValueList> getAllMap() {
		return allMap;
	}

	public void setAllMap(Map<String, NameValueList> allMap) {
		this.allMap = allMap;
	}

	public Map<String, NameValueList3> getOpemMap3() {
		return opemMap3;
	}

	public void setOpemMap3(Map<String, NameValueList3> opemMap3) {
		this.opemMap3 = opemMap3;
	}

	public Map<String, NameValueList3> getIpMap3() {
		return ipMap3;
	}

	public void setIpMap3(Map<String, NameValueList3> ipMap3) {
		this.ipMap3 = ipMap3;
	}

	public Map<String, NameValueList3> getLeaveMap3() {
		return leaveMap3;
	}

	public void setLeaveMap3(Map<String, NameValueList3> leaveMap3) {
		this.leaveMap3 = leaveMap3;
	}

	public Map<String, NameValueList3> getAllMap3() {
		return allMap3;
	}

	public void setAllMap3(Map<String, NameValueList3> allMap3) {
		this.allMap3 = allMap3;
	}

}
