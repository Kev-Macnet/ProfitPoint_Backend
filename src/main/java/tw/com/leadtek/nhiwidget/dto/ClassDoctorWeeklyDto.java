package tw.com.leadtek.nhiwidget.dto;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;

public class ClassDoctorWeeklyDto {
    @ApiModelProperty(value="週", example="110w2", required=true, position=1)
    @NotNull()
    private String weekOfYear;
    
    @ApiModelProperty(value="科別與醫師列表", example="[外科:{王小明:{案件數:100,總點數:100,總藥費:100},李小明:{案件數:100,總點數:100,總藥費:100}},"
    		+ "內科:{王小明:{案件數:100,總點數:100,總藥費:100},李小明:{案件數:100,總點數:100,總藥費:100}}]", required=true, position=2)
    @NotNull()
    private List<ClassDoctorDto>classDoctors;
    
    
    
	public ClassDoctorWeeklyDto(@NotNull String weekOfYear, @NotNull List<ClassDoctorDto> classDoctors) {
		super();
		this.weekOfYear = weekOfYear;
		this.classDoctors = classDoctors;
	}

	public ClassDoctorWeeklyDto() {
		super();
		// TODO Auto-generated constructor stub
		this.weekOfYear="";
		this.classDoctors=new ArrayList<ClassDoctorDto>();
	}

	public String getWeekOfYear() {
		return weekOfYear;
	}

	public void setWeekOfYear(String weekOfYear) {
		this.weekOfYear = weekOfYear;
	}

	public List<ClassDoctorDto> getClassDoctors() {
		return classDoctors;
	}

	public void setClassDoctors(List<ClassDoctorDto> classDoctors) {
		this.classDoctors = classDoctors;
	}
    
    
}
