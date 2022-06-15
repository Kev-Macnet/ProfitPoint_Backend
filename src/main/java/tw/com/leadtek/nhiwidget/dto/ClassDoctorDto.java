package tw.com.leadtek.nhiwidget.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;

public class ClassDoctorDto {
    @ApiModelProperty(value="科別中文名", example="消化內科", required=true, position=1)
    @NotNull()
    private String desc_chi;
    
    @ApiModelProperty(value="科別代碼", example="AA", required=true, position=2)
    @NotNull()
    private String code;
    
    @ApiModelProperty(value="醫師列表", example="[王小明:{案件數:100,總點數:100,總藥費:100},李小明:{案件數:100,總點數:100,總藥費:100}]", required=true, position=3)
    @NotNull()
    private Map<String,CaseDotFeeDto> doctors;

	public ClassDoctorDto(@NotNull String desc_chi, @NotNull String code, @NotNull Map<String,CaseDotFeeDto> doctors) {
		super();
		this.desc_chi = desc_chi;
		this.code = code;
		this.doctors = doctors;
	}
	

	public ClassDoctorDto() {
		super();
		// TODO Auto-generated constructor stub
		this.desc_chi = "";
		this.code = "";
		this.doctors = new HashMap<String, CaseDotFeeDto>();
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

	public Map<String,CaseDotFeeDto> getDoctors() {
		return doctors;
	}

	public void setDoctors(Map<String,CaseDotFeeDto> doctors) {
		this.doctors = doctors;
	}

	@Override
	public String toString() {
		return "ClassDoctorDto [desc_chi=" + desc_chi + ", code=" + code + ", doctors=" + doctors + "]";
	}
    
    
}
