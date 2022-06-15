package tw.com.leadtek.nhiwidget.dto;

import javax.validation.constraints.Min;

import io.swagger.annotations.ApiModelProperty;

public class CaseDotFeeDto {

    @ApiModelProperty(value="案件數", example="100", required=true, position=1)
    @Min(value = 0)
    private Integer caseCount;
    
    @ApiModelProperty(value="總點數", example="100", required=true, position=2)
    @Min(value = 0)
    private Integer dot;
    
    @ApiModelProperty(value="總藥費", example="100", required=true, position=3)
    @Min(value = 0)
    private Integer drugFee;

	public Integer getCaseCount() {
		return caseCount;
	}

	public void setCaseCount(Integer caseCount) {
		this.caseCount = caseCount;
	}

	public Integer getDot() {
		return dot;
	}

	public void setDot(Integer dot) {
		this.dot = dot;
	}

	public Integer getDrugFee() {
		return drugFee;
	}

	public void setDrugFee(Integer drugFee) {
		this.drugFee = drugFee;
	}
	
	

	public CaseDotFeeDto(@Min(0) Integer caseCount, @Min(0) Integer dot, @Min(0) Integer drugFee) {
		super();
		this.caseCount = caseCount;
		this.dot = dot;
		this.drugFee = drugFee;
	}
	

	public CaseDotFeeDto() {
		super();
		// TODO Auto-generated constructor stub
		this.caseCount = 0;
		this.dot = 0;
		this.drugFee = 0;
	}

	@Override
	public String toString() {
		return "CaseDotFeeDto [caseCount=" + caseCount + ", dot=" + dot + ", drugFee=" + drugFee + "]";
	}
    
    
}
