package tw.com.leadtek.nhiwidget.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;

public class ClassCaseCountDto {
    @ApiModelProperty(value="科別中文名", example="消化內科", required=true, position=1)
    @NotNull()
    private String desc_chi;
    
    @ApiModelProperty(value="科別代碼", example="AA", required=true, position=2)
    @NotNull()
    private String code;
    
    @ApiModelProperty(value="案件數", example="100", required=true, position=3)
    @Min(value = 0)
    private String caseCount;

	public ClassCaseCountDto(@NotNull String desc_chi, @NotNull String code, @Min(0) String caseCount) {
		super();
		this.desc_chi = desc_chi;
		this.code = code;
		this.caseCount = caseCount;
	}
	
	public ClassCaseCountDto() {
		super();
		this.desc_chi = "";
		this.code = "";
		this.caseCount = "";
	}

	public String getDesc_chi() {
		return desc_chi;
	}

	public void setDesc_chi(String desc_chi) {
		this.desc_chi = desc_chi;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCaseCount() {
		return caseCount;
	}

	public void setCaseCount(String caseCount) {
		this.caseCount = caseCount;
	}
 
    
    
}
