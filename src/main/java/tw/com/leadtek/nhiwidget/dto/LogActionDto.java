package tw.com.leadtek.nhiwidget.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "使用者操作紀錄DTO", description = "使用者操作紀錄DTO")
public class LogActionDto{
	
	@ApiModelProperty(value = "姓名", example = "張小美", required = true)
    private String displayName;
    
	@ApiModelProperty(value = "帳號", example = "FED_C", required = true)
    private String username;

	@ApiModelProperty(value = "功能名稱", example = "更改指定病歷狀態", required = true)
    private String functionName;
    
	@ApiModelProperty(value = "CRUD", example = "C", required = false)
    private String crud;
    
	@ApiModelProperty(value = "異動主檔ID", example = "99,100,101", required = false)
    private String pks;
    
	@ApiModelProperty(value = "異動日期", example = "2022-08-29", required = false)
    private String createDate;
	
//	@ApiModelProperty(value = "異動時間", example = "10:17:29", required = false)
//    private String createTime;

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFunctionName() {
		return functionName;
	}

	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}

	public String getCrud() {
		return crud;
	}

	public void setCrud(String crud) {
		this.crud = crud;
	}

	public String getPks() {
		return pks;
	}

	public void setPks(String pks) {
		this.pks = pks;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	@Override
	public String toString() {
		return "LogActionDto [displayName=" + displayName + ", username=" + username + ", functionName=" + functionName
				+ ", crud=" + crud + ", pks=" + pks + ", createDate=" + createDate + "]";
	}


}


