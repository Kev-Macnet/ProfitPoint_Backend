package tw.com.leadtek.nhiwidget.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;

public class ClassDrugDotDto {
    @ApiModelProperty(value="科別中文名", example="消化內科", required=true, position=1)
    @NotNull()
    private String desc_chi;
    
    @ApiModelProperty(value="科別代碼", example="AA", required=true, position=2)
    @NotNull()
    private String code;
    
    @ApiModelProperty(value="藥品點數", example="100", required=true, position=3)
    @Min(value = 0)
    private String dot;

	public ClassDrugDotDto(@NotNull String desc_chi, @NotNull String code, @Min(0) String dot) {
		super();
		this.desc_chi = desc_chi;
		this.code = code;
		this.dot = dot;
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

	public String getDot() {
		return dot;
	}

	public void setDot(String dot) {
		this.dot = dot;
	}
    
    
}
