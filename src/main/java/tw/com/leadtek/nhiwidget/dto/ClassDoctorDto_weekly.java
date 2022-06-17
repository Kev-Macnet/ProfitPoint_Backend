package tw.com.leadtek.nhiwidget.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;

public class ClassDoctorDto_weekly {
    
    @ApiModelProperty(value="醫師姓名", example="王曉明", required=true, position=1)
    @NotNull()
    private String doctorName;
    
    @ApiModelProperty(value="依序每週案件數、總點數、總藥費Doct", example="[{週期:2021w1,案件數:100,總點數:100,總藥費:100},{週期:2021w2,案件數:100,總點數:100,總藥費:100}]", required=true, position=2)
    @NotNull()
    private List<CaseDotFeeDto> caseDotFeeWeekly;

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public List<CaseDotFeeDto> getCaseDotFeeWeekly() {
		return caseDotFeeWeekly;
	}

	public void setCaseDotFeeWeekly(List<CaseDotFeeDto> caseDotFeeWeekly) {
		this.caseDotFeeWeekly = caseDotFeeWeekly;
	}

	public ClassDoctorDto_weekly(@NotNull String doctorName, @NotNull List<CaseDotFeeDto>caseDotFeeWeekly) {
		super();
		this.doctorName = doctorName;
		this.caseDotFeeWeekly = caseDotFeeWeekly;
	}

	public ClassDoctorDto_weekly() {
		super();
		// TODO Auto-generated constructor stub
		this.doctorName="";
		this.caseDotFeeWeekly=new ArrayList<CaseDotFeeDto>();
	}

}