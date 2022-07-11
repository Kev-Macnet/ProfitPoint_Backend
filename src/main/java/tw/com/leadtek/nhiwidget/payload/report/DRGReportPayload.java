/**
 * Created on 2021/11/12.
 */
package tw.com.leadtek.nhiwidget.payload.report;

import java.io.Serializable;
import java.util.List;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.payload.BaseResponse;

public class DRGReportPayload extends BaseResponse implements Serializable {

	private static final long serialVersionUID = -4445294264051601103L;

	@ApiModelProperty(value = "住院總件數", required = true)
	protected Long quantityIp;

	@ApiModelProperty(value = "DRG總件數", required = true)
	protected Long quantityDrg;

	@ApiModelProperty(value = "DRG件數佔率", required = true)
	protected Double rateDrg;

	@ApiModelProperty(value = "住院案件申報總點數", required = true)
	protected Long applPointIp;

	@ApiModelProperty(value = "DRG案件申報總點數", required = true)
	protected Long applPointDrg;

	@ApiModelProperty(value = "DRG費用佔率", required = true)
	protected Double ratePointDrg;

	@ApiModelProperty(value = "住院實際總點數", required = true)
	protected Long pointIp;

	@ApiModelProperty(value = "DRG案件實際總點數", required = true)
	protected Long pointDrg;

	@ApiModelProperty(value = "DRG案件支付差額點數", required = true)
	protected Long diffDrg;

	@ApiModelProperty(value = "住院病例總點數(含自費)", required = true)
	protected Long medPointIp;
	
	@ApiModelProperty(value = "住院病例總點數(不含自費)", required = true)
	protected Long medNoOwnPointIp;

	@ApiModelProperty(value = "各科別名稱", required = true)
	protected List<String> funcTypes;

	public DRGReportPayload() {

	}

	public Long getQuantityIp() {
		return quantityIp;
	}

	public void setQuantityIp(Long quantityIp) {
		this.quantityIp = quantityIp;
	}

	public Long getQuantityDrg() {
		return quantityDrg;
	}

	public void setQuantityDrg(Long quantityDrg) {
		this.quantityDrg = quantityDrg;
	}

	public Double getRateDrg() {
		return rateDrg;
	}

	public void setRateDrg(Double rateDrg) {
		this.rateDrg = rateDrg;
	}

	public Long getApplPointIp() {
		return applPointIp;
	}

	public void setApplPointIp(Long applPointIp) {
		this.applPointIp = applPointIp;
	}

	public Long getApplPointDrg() {
		return applPointDrg;
	}

	public void setApplPointDrg(Long applPointDrg) {
		this.applPointDrg = applPointDrg;
	}

	public Double getRatePointDrg() {
		return ratePointDrg;
	}

	public void setRatePointDrg(Double ratePointDrg) {
		this.ratePointDrg = ratePointDrg;
	}

	public Long getPointIp() {
		return pointIp;
	}

	public void setPointIp(Long pointIp) {
		this.pointIp = pointIp;
	}

	public Long getPointDrg() {
		return pointDrg;
	}

	public void setPointDrg(Long pointDrg) {
		this.pointDrg = pointDrg;
	}

	public Long getDiffDrg() {
		return diffDrg;
	}

	public void setDiffDrg(Long diffDrg) {
		this.diffDrg = diffDrg;
	}

	public List<String> getFuncTypes() {
		return funcTypes;
	}

	public void setFuncTypes(List<String> funcTypes) {
		this.funcTypes = funcTypes;
	}

	public Long getMedPointIp() {
		return medPointIp;
	}

	public void setMedPointIp(Long medPointIp) {
		this.medPointIp = medPointIp;
	}

	public Long getMedNoOwnPointIp() {
		return medNoOwnPointIp;
	}

	public void setMedNoOwnPointIp(Long medNoOwnPointIp) {
		this.medNoOwnPointIp = medNoOwnPointIp;
	}
	
	

}
