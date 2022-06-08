/**
 * Created on 2021/11/4.
 */
package tw.com.leadtek.nhiwidget.payload.report;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.model.rdb.POINT_MONTHLY;
import tw.com.leadtek.nhiwidget.payload.BaseResponse;

@ApiModel("單月健保點數總表")
public class PointMRPayload extends BaseResponse implements Serializable {

	private static final long serialVersionUID = -4800113569180059126L;

	@ApiModelProperty(value = "當月健保點數", required = true)
	private POINT_MONTHLY current;

	@ApiModelProperty(value = "上個月健保點數", required = true)
	private POINT_MONTHLY lastM;

	@ApiModelProperty(value = "去年同期健保點數", required = true)
	private POINT_MONTHLY lastY;

	@ApiModelProperty(value = "與上個月總額的差額", required = true)
	private Long diffAllLastM;

	@ApiModelProperty(value = "與去年同期總額的差額", required = true)
	private Long diffAllLastY;

	@ApiModelProperty(value = "與上個月門急診的差額", required = true)
	private Long diffOpAllLastM;

	@ApiModelProperty(value = "與去年同期門急診的差額", required = true)
	private Long diffOpAllLastY;

	@ApiModelProperty(value = "與上個月門診的差額", required = true)
	private Long diffOpLastM;

	@ApiModelProperty(value = "與去年同期門診的差額", required = true)
	private Long diffOpLastY;

	@ApiModelProperty(value = "與上個月急診的差額", required = true)
	private Long diffEmLastM;

	@ApiModelProperty(value = "與去年同期急診的差額", required = true)
	private Long diffEmLastY;

	@ApiModelProperty(value = "與上個月住院的差額", required = true)
	private Long diffIpLastM;

	@ApiModelProperty(value = "與去年同期住院的差額", required = true)
	private Long diffIpLastY;

	@ApiModelProperty(value = "門急診總人次", required = true)
	private int patient_op_count;

	@ApiModelProperty(value = "住院總人次", required = true)
	private int patient_ip_count;

	@ApiModelProperty(value = "門急診/住院總人次", required = true)
	private int patient_total_count;

	@ApiModelProperty(value = "門急診圓餅圖人次", required = true)
	private List<Map<String, Object>> opPieCountData;

	@ApiModelProperty(value = "出院圓餅圖人次", required = true)
	private List<Map<String, Object>> ipPieOutCountData;

	@ApiModelProperty(value = "住院圓餅圖人次", required = true)
	private List<Map<String, Object>> ipPieCountData;

	@ApiModelProperty(value = "門急診/住院圓餅圖人次", required = true)
	private List<Map<String, Object>> totalPieCountData;

	@ApiModelProperty(value = "門急診圓餅圖點數", required = true)
	private List<Map<String, Object>> opPieDotData;

	@ApiModelProperty(value = "住院圓餅圖點數", required = true)
	private List<Map<String, Object>> ipPieDotData;

	@ApiModelProperty(value = "門急診/住院圓餅圖點數", required = true)
	private List<Map<String, Object>> totalPieDotData;

	@ApiModelProperty(value = "門急診趨勢圖人次", required = true)
	private List<Map<String, Object>> opTredCountData;

	@ApiModelProperty(value = "出院趨勢圖人次", required = true)
	private List<Map<String, Object>> ipTredOutCountData;

	@ApiModelProperty(value = "住院趨勢圖人次", required = true)
	private List<Map<String, Object>> ipTredCountData;

	@ApiModelProperty(value = "門急診/住院趨勢圖人次", required = true)
	private List<Map<String, Object>> totalTredCountData;

	@ApiModelProperty(value = "門急診趨勢圖點數", required = true)
	private List<Map<String, Object>> opTredDotData;

	@ApiModelProperty(value = "住院趨勢圖點數", required = true)
	private List<Map<String, Object>> ipTredDotData;

	@ApiModelProperty(value = "門急診/住院趨勢圖點數", required = true)
	private List<Map<String, Object>> totalTredDotData;

	@ApiModelProperty(value = "各科別名稱", required = true)
	protected List<String> funcTypes;

	private VisitsVarietyPayload visitsVarietyPayload;

	public PointMRPayload() {

	}

	public POINT_MONTHLY getCurrent() {
		return current;
	}

	public void setCurrent(POINT_MONTHLY current) {
		this.current = current;
	}

	public POINT_MONTHLY getLastM() {
		return lastM;
	}

	public void setLastM(POINT_MONTHLY lastM) {
		this.lastM = lastM;
	}

	public POINT_MONTHLY getLastY() {
		return lastY;
	}

	public void setLastY(POINT_MONTHLY lastY) {
		this.lastY = lastY;
	}

	public Long getDiffAllLastM() {
		return diffAllLastM;
	}

	public void setDiffAllLastM(Long diffAllLastM) {
		this.diffAllLastM = diffAllLastM;
	}

	public Long getDiffAllLastY() {
		return diffAllLastY;
	}

	public void setDiffAllLastY(Long diffAllLastY) {
		this.diffAllLastY = diffAllLastY;
	}

	public Long getDiffOpAllLastM() {
		return diffOpAllLastM;
	}

	public void setDiffOpAllLastM(Long diffOpAllLastM) {
		this.diffOpAllLastM = diffOpAllLastM;
	}

	public Long getDiffOpAllLastY() {
		return diffOpAllLastY;
	}

	public void setDiffOpAllLastY(Long diffOpAllLastY) {
		this.diffOpAllLastY = diffOpAllLastY;
	}

	public Long getDiffOpLastM() {
		return diffOpLastM;
	}

	public void setDiffOpLastM(Long diffOpLastM) {
		this.diffOpLastM = diffOpLastM;
	}

	public Long getDiffOpLastY() {
		return diffOpLastY;
	}

	public void setDiffOpLastY(Long diffOpLastY) {
		this.diffOpLastY = diffOpLastY;
	}

	public Long getDiffEmLastM() {
		return diffEmLastM;
	}

	public void setDiffEmLastM(Long diffEmLastM) {
		this.diffEmLastM = diffEmLastM;
	}

	public Long getDiffEmLastY() {
		return diffEmLastY;
	}

	public void setDiffEmLastY(Long diffEmLastY) {
		this.diffEmLastY = diffEmLastY;
	}

	public Long getDiffIpLastM() {
		return diffIpLastM;
	}

	public void setDiffIpLastM(Long diffIpLastM) {
		this.diffIpLastM = diffIpLastM;
	}

	public Long getDiffIpLastY() {
		return diffIpLastY;
	}

	public void setDiffIpLastY(Long diffIpLastY) {
		this.diffIpLastY = diffIpLastY;
	}

	public int getPatient_op_count() {
		return patient_op_count;
	}

	public void setPatient_op_count(int patient_op_count) {
		this.patient_op_count = patient_op_count;
	}

	public int getPatient_ip_count() {
		return patient_ip_count;
	}

	public void setPatient_ip_count(int patient_ip_count) {
		this.patient_ip_count = patient_ip_count;
	}

	public int getPatient_total_count() {
		return patient_total_count;
	}

	public void setPatient_total_count(int patient_total_count) {
		this.patient_total_count = patient_total_count;
	}

	public List<Map<String, Object>> getOpPieCountData() {
		return opPieCountData;
	}

	public void setOpPieCountData(List<Map<String, Object>> opPieCountData) {
		this.opPieCountData = opPieCountData;
	}

	public List<Map<String, Object>> getIpPieOutCountData() {
		return ipPieOutCountData;
	}

	public void setIpPieOutCountData(List<Map<String, Object>> ipPieOutCountData) {
		this.ipPieOutCountData = ipPieOutCountData;
	}

	public List<Map<String, Object>> getIpPieCountData() {
		return ipPieCountData;
	}

	public void setIpPieCountData(List<Map<String, Object>> ipPieCountData) {
		this.ipPieCountData = ipPieCountData;
	}

	public List<Map<String, Object>> getTotalPieCountData() {
		return totalPieCountData;
	}

	public void setTotalPieCountData(List<Map<String, Object>> totalPieCountData) {
		this.totalPieCountData = totalPieCountData;
	}

	public List<Map<String, Object>> getOpPieDotData() {
		return opPieDotData;
	}

	public void setOpPieDotData(List<Map<String, Object>> opPieDotData) {
		this.opPieDotData = opPieDotData;
	}

	public List<Map<String, Object>> getIpPieDotData() {
		return ipPieDotData;
	}

	public void setIpPieDotData(List<Map<String, Object>> ipPieDotData) {
		this.ipPieDotData = ipPieDotData;
	}

	public List<Map<String, Object>> getTotalPieDotData() {
		return totalPieDotData;
	}

	public void setTotalPieDotData(List<Map<String, Object>> totalPieDotData) {
		this.totalPieDotData = totalPieDotData;
	}

	public List<Map<String, Object>> getOpTredCountData() {
		return opTredCountData;
	}

	public void setOpTredCountData(List<Map<String, Object>> opTredCountData) {
		this.opTredCountData = opTredCountData;
	}

	public List<Map<String, Object>> getIpTredOutCountData() {
		return ipTredOutCountData;
	}

	public void setIpTredOutCountData(List<Map<String, Object>> ipTredOutCountData) {
		this.ipTredOutCountData = ipTredOutCountData;
	}

	public List<Map<String, Object>> getIpTredCountData() {
		return ipTredCountData;
	}

	public void setIpTredCountData(List<Map<String, Object>> ipTredCountData) {
		this.ipTredCountData = ipTredCountData;
	}

	public List<Map<String, Object>> getTotalTredCountData() {
		return totalTredCountData;
	}

	public void setTotalTredCountData(List<Map<String, Object>> totalTredCountData) {
		this.totalTredCountData = totalTredCountData;
	}

	public List<Map<String, Object>> getOpTredDotData() {
		return opTredDotData;
	}

	public void setOpTredDotData(List<Map<String, Object>> opTredDotData) {
		this.opTredDotData = opTredDotData;
	}

	public List<Map<String, Object>> getIpTredDotData() {
		return ipTredDotData;
	}

	public void setIpTredDotData(List<Map<String, Object>> ipTredDotData) {
		this.ipTredDotData = ipTredDotData;
	}

	public List<Map<String, Object>> getTotalTredDotData() {
		return totalTredDotData;
	}

	public void setTotalTredDotData(List<Map<String, Object>> totalTredDotData) {
		this.totalTredDotData = totalTredDotData;
	}

	public VisitsVarietyPayload getVisitsVarietyPayload() {
		return visitsVarietyPayload;
	}

	public void setVisitsVarietyPayload(VisitsVarietyPayload visitsVarietyPayload) {
		this.visitsVarietyPayload = visitsVarietyPayload;
	}

	public List<String> getFuncTypes() {
		return funcTypes;
	}

	public void setFuncTypes(List<String> funcTypes) {
		this.funcTypes = funcTypes;
	}

	public void calculateDifference() {
		if (lastM != null) {
			diffAllLastM = current.getApplAll() - lastM.getApplAll();
			diffOpAllLastM = current.getApplOpAll() - lastM.getApplOpAll();
			diffOpLastM = current.getApplOp() - lastM.getApplOp();
			diffEmLastM = current.getApplEm() - lastM.getApplEm();
			diffIpLastM = current.getApplIp() - lastM.getApplIp();
		} else {
			diffAllLastM = current.getApplAll() - 0;
			diffOpAllLastM = current.getApplOpAll() - 0;
			diffOpLastM = current.getApplOp() - 0;
			diffEmLastM = current.getApplEm() - 0;
			diffIpLastM = current.getApplIp() - 0;
		}
		if (lastY != null) {
			diffAllLastY = current.getApplAll() - lastY.getApplAll();
			diffOpAllLastY = current.getApplOpAll() - lastY.getApplOpAll();
			diffOpLastY = current.getApplOp() - lastY.getApplOp();
			diffEmLastY = current.getApplEm() - lastY.getApplEm();
			diffIpLastY = current.getApplIp() - lastY.getApplIp();
		} else {
			diffAllLastY = current.getApplAll() - 0;
			diffOpAllLastY = current.getApplOpAll() - 0;
			diffOpLastY = current.getApplOp() - 0;
			diffEmLastY = current.getApplEm() - 0;
			diffIpLastY = current.getApplIp() - 0;
		}
	}
}
