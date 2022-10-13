package tw.com.leadtek.nhiwidget.dto;

import java.math.BigInteger;
import java.sql.Timestamp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
@ApiModel(value = "登出入紀錄DTO", description = "登出入紀錄DTO")
public class LogSigninDto{
	
	@ApiModelProperty(value = "姓名", example = "張小美", required = true)
    private String displayName;
    
	@ApiModelProperty(value = "帳號", example = "FED_C", required = true)
    private String username;

	@ApiModelProperty(value = "登入日期", example = "2022-08-29", required = false)
    private String createDate;
    
	@ApiModelProperty(value = "登入時間", example = "10:17:29", required = false)
    private Timestamp loginTime;
    
	@ApiModelProperty(value = "登出時間", example = "10:17:29", required = false)
    private Timestamp logoutTime;
    
	@ApiModelProperty(value = "登入秒數", example = "111", required = false)
    private BigInteger secondsBetween;
	
	@ApiModelProperty(value = "經過時間", example = "18:10:24", required = false)
    private String elapsedTime;
    
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

	public BigInteger getSecondsBetween() {
		return secondsBetween;
	}

	public void setSecondsBetween(BigInteger secondsBetween) {
		this.secondsBetween = secondsBetween;
	}

	public String getElapsedTime() {
		return elapsedTime;
	}

	public void setElapsedTime(String elapsedTime) {
		this.elapsedTime = elapsedTime;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public Timestamp getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(Timestamp loginTime) {
		this.loginTime = loginTime;
	}

	public Timestamp getLogoutTime() {
		return logoutTime;
	}

	public void setLogoutTime(Timestamp logoutTime) {
		this.logoutTime = logoutTime;
	}

}


