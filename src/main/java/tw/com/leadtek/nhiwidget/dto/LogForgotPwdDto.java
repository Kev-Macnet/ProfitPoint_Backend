package tw.com.leadtek.nhiwidget.dto;

import java.math.BigInteger;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "忘記密碼DTO", description = "忘記密碼DTO")
public class LogForgotPwdDto{
	
	@ApiModelProperty(value = "姓名", example = "張小美", required = true)
    private String displayName;
	
	@ApiModelProperty(value = "帳號", example = "FED_C", required = true)
    private String username;

	@ApiModelProperty(value = "密碼申請日期", example = "2022-08-29", required = false)
    private String createDate;
    
	@ApiModelProperty(value = "密碼申請時間", example = "10:17:29", required = false)
    private String createTime;
    
	@ApiModelProperty(value = "建立帳號日期", example = "2021-01-29", required = false)
    private String createUserAt;
    
	@ApiModelProperty(value = "密碼申請累計次數", example = "3", required = false)
    private BigInteger cnt;
    
	@ApiModelProperty(value = "權限", example = "醫護人員", required = false)
    private String role;
    
	@ApiModelProperty(value = "帳號狀態", example = "有效", required = false)
    private String status;

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

	public String getCreateUserAt() {
		return createUserAt;
	}

	public void setCreateUserAt(String createUserAt) {
		this.createUserAt = createUserAt;
	}

	public BigInteger getCnt() {
		return cnt;
	}

	public void setCnt(BigInteger cnt) {
		this.cnt = cnt;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	@Override
	public String toString() {
		return "LogForgotPwdDto [displayName=" + displayName + ", username=" + username + ", createDate=" + createDate
				+ ", createTime=" + createTime + ", createUserAt=" + createUserAt + ", cnt=" + cnt + ", role=" + role
				+ ", status=" + status + "]";
	}

}


