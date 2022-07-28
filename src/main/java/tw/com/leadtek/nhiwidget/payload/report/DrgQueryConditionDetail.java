package tw.com.leadtek.nhiwidget.payload.report;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("DRG案件數分佈佔率與定額、實際點數詳細")
public class DrgQueryConditionDetail implements Serializable {

	private static final long serialVersionUID = -6667781513240183132L;
	@ApiModelProperty(value = "統計月份", required = false)
	private String date;
	@ApiModelProperty(value = "DRG代碼", required = false)
	private String drgCode;
	@ApiModelProperty(value = "DRG案件總數", required = false)
	private Long drgQuantity;
	@ApiModelProperty(value = "案件申報總點數", required = false)
	private Long drgApplPoint;
	@ApiModelProperty(value = "病例總點數(不含自費)", required = false)
	private Long drgActual;
	@ApiModelProperty(value = "點數差額", required = false)
	private Long diff;
	@ApiModelProperty(value = "案件佔率", required = false)
	private Double percent;
	@ApiModelProperty(value = "顯示名稱", required = false)
	private String displayName;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getDrgCode() {
		return drgCode;
	}

	public void setDrgCode(String drgCode) {
		this.drgCode = drgCode;
	}

	public Long getDrgQuantity() {
		return drgQuantity;
	}

	public void setDrgQuantity(Long drgQuantity) {
		this.drgQuantity = drgQuantity;
	}

	public Long getDrgApplPoint() {
		return drgApplPoint;
	}

	public void setDrgApplPoint(Long drgApplPoint) {
		this.drgApplPoint = drgApplPoint;
	}

	public Long getDrgActual() {
		return drgActual;
	}

	public void setDrgActual(Long drgActual) {
		this.drgActual = drgActual;
	}

	public Long getDiff() {
		return diff;
	}

	public void setDiff(Long diff) {
		this.diff = diff;
	}

	public Double getPercent() {
		return percent;
	}

	public void setPercent(Double percent) {
		this.percent = percent;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

}
