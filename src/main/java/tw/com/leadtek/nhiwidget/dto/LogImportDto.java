package tw.com.leadtek.nhiwidget.dto;

import java.math.BigInteger;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "LOG_IMPORT_DTO", description = "LOG_IMPORT_DTO")
public class LogImportDto{
	
	@ApiModelProperty(value = "姓名", example = "張小美", required = true)
    private String displayName;
	
	@ApiModelProperty(value = "帳號", example = "FED_C", required = true)
    private String username;

	@ApiModelProperty(value = "日期", example = "2022-08-29", required = false)
    private String createDate;
	
	@ApiModelProperty(value = "異動時間", example = "10:17:29", required = false)
    private String createTime;

	@ApiModelProperty(value = "匯入次數", example = "3", required = true)
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

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public BigInteger getCnt() {
		return cnt;
	}

	public void setCnt(BigInteger cnt) {
		this.cnt = cnt;
	}

	@Override
	public String toString() {
		return "LogImportDto [displayName=" + displayName + ", username=" + username + ", createDate=" + createDate
				+ ", createTime=" + createTime + ", cnt=" + cnt + "]";
	}
	
}


