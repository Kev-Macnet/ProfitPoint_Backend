package tw.com.leadtek.nhiwidget.dto;

import javax.validation.constraints.NotNull;
import io.swagger.annotations.ApiModelProperty;

public class ClassDrugFeeDto {
    @ApiModelProperty(value="科別中文名", example="消化內科", required=true, position=1)
    @NotNull()
    private String desc_chi;
    
    @ApiModelProperty(value="科別代碼", example="AA", required=true, position=2)
    @NotNull()
    private String code;
    
    @ApiModelProperty(value="藥費", example="100", required=true, position=3)
    private String fee;

	public ClassDrugFeeDto(@NotNull String desc_chi, @NotNull String code, String fee) {
		super();
		this.desc_chi = desc_chi;
		this.code = code;
		this.fee = fee;
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

	public String getFee() {
		return fee;
	}

	public void setFee(String fee) {
		this.fee = fee;
	}
    
    
}

