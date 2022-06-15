/**
 * Created on 2022/3/16.
 */
package tw.com.leadtek.nhiwidget.payload.report;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("季度資料")
public class QuarterData implements Serializable {

	private static final long serialVersionUID = -2429446783747963593L;

	@ApiModelProperty(value = "季度名稱，如 2021/Q1", required = true)
	protected String name;

	@ApiModelProperty(value = "原始總點數/原始實績總點數，部分負擔+申請點數+自費", required = true)
	protected Long original;

	@ApiModelProperty(value = "實際總點數/申報實績總點數，部分負擔+申請點數", required = true)
	protected Long actual;

	@ApiModelProperty(value = "分配總點數/分配實績總點數", required = true)
	protected Long assigned;

	@ApiModelProperty(value = "超額總點數", required = true)
	protected Long over;

	@ApiModelProperty(value = "總額達成率，無%", required = true)
	protected Float percent;

	@ApiModelProperty(value = "案件數量")
	protected Long cases;

	@ApiModelProperty(value = "顯示名稱")
	protected String dispalyName;

	public QuarterData() {

	}

	public QuarterData(String name) {
		this.name = name;
		original = 0L;
		actual = 0L;
		assigned = 0L;
		over = 0L;
		percent = 0f;
		cases = 0L;
		this.dispalyName = "";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getOriginal() {
		return original;
	}

	public void setOriginal(Long original) {
		this.original = original;
	}

	public Long getActual() {
		return actual;
	}

	public void setActual(Long actual) {
		this.actual = actual;
	}

	public Long getAssigned() {
		return assigned;
	}

	public void setAssigned(Long assigned) {
		this.assigned = assigned;
	}

	public Long getOver() {
		return over;
	}

	public void setOver(Long over) {
		this.over = over;
	}

	public Float getPercent() {
		return percent;
	}

	public void setPercent(Float percent) {
		this.percent = percent;
	}

	public Long getCases() {
		return cases;
	}

	public void setCases(Long cases) {
		this.cases = cases;
	}

	public String getDispalyName() {
		return dispalyName;
	}

	public void setDispalyName(String dispalyName) {
		this.dispalyName = dispalyName;
	}

}
