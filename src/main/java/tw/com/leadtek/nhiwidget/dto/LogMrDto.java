package tw.com.leadtek.nhiwidget.dto;

import java.math.BigInteger;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "LOG_MR_DTO", description = "LOG_MR_DTO")
public class LogMrDto{
	
	@ApiModelProperty(value = "姓名", example = "張小美", required = true)
    private String displayName;
	
	@ApiModelProperty(value = "帳號", example = "FED_C", required = true)
    private String username;

	@ApiModelProperty(value = "日期", example = "2022-08-29", required = false)
    private String createDate;
    
	@ApiModelProperty(value = "就醫紀錄編號", example = "1,2,3", required = false)
    private String inhClinicIds;

	@ApiModelProperty(value = "案件數", example = "3", required = false)
    private BigInteger cnt;
	
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

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getInhClinicIds() {
		return inhClinicIds;
	}

	public void setInhClinicIds(String inhClinicIds) {
		this.inhClinicIds = inhClinicIds;
	}

	public BigInteger getCnt() {
		return cnt;
	}

	public void setCnt(BigInteger cnt) {
		this.cnt = cnt;
	}

	@Override
	public String toString() {
		return "LogMrDto [displayName=" + displayName + ", username=" + username + ", createDate=" + createDate
				+ ", inhClinicIds=" + inhClinicIds + ", cnt=" + cnt + "]";
	}

}


