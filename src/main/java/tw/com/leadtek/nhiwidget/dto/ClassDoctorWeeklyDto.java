package tw.com.leadtek.nhiwidget.dto;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;

public class ClassDoctorWeeklyDto {
    @ApiModelProperty(value="科別名稱代碼", example="AA", required=true, position=1)
    @NotNull()
    private String code;
    
    @ApiModelProperty(value="各醫師每週案件數、總點數、總藥費", example="[{王曉明,[{週期:2021w1,100,100,100},{週期:2021w2,100,100,100}]}"
    		+ ",{李曉明,[{週期:2021w1,100,100,100},{週期:2021w3,100,100,100}]}]", required=true, position=2)
    @NotNull()
    private List<ClassDoctorDto_weekly>classDoctors;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public List<ClassDoctorDto_weekly> getClassDoctors() {
		return classDoctors;
	}

	public void setClassDoctors(List<ClassDoctorDto_weekly> classDoctors) {
		this.classDoctors = classDoctors;
	}

	public ClassDoctorWeeklyDto(@NotNull String code, @NotNull List<ClassDoctorDto_weekly> classDoctors) {
		super();
		this.code = code;
		this.classDoctors = classDoctors;
	}

	public ClassDoctorWeeklyDto() {
		super();
		// TODO Auto-generated constructor stub
		this.code="";
		this.classDoctors=new ArrayList<ClassDoctorDto_weekly>();
		
	}


    
    
}