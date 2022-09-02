/**
 * Created on 2021/11/16.
 */
package tw.com.leadtek.nhiwidget.payload.log;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.dto.LogActionDto;
import tw.com.leadtek.nhiwidget.dto.LogForgotPwdDto;
import tw.com.leadtek.nhiwidget.dto.LogMrDto;
import tw.com.leadtek.nhiwidget.dto.LogSigninDto;
import tw.com.leadtek.nhiwidget.payload.BasePageResponse;
import tw.com.leadtek.nhiwidget.payload.BaseResponse;

@ApiModel("使用者行為-資料清單")
public class LogOperateResponse extends BaseResponse implements Serializable {

	private static final long serialVersionUID = -4480995805053285097L;

	@ApiModelProperty(value = "忘記密碼List", required = false)
	private List<LogForgotPwdDto> fgList;
	
	@ApiModelProperty(value = "登出入紀錄List", required = false)
	private List<LogSigninDto> sgList;
	
	@ApiModelProperty(value = "比對警示待確認案件List", required = false)
	private List<LogMrDto> cwList;
	
	@ApiModelProperty(value = "疑問標示案件List", required = false)
	private List<LogMrDto> dmList;
	
	@ApiModelProperty(value = "評估不調整案件List", required = false)
	private List<LogMrDto> ecList;
  
	@ApiModelProperty(value = "優化完成案件List", required = false)
	private List<LogMrDto> ofList;
	
	@ApiModelProperty(value = "未讀取次數List", required = false)
	private List<LogMrDto> urList;
	
	@ApiModelProperty(value = "被通知次數List", required = false)
	private List<LogMrDto> bnList;
	
	@ApiModelProperty(value = "使用者操作List", required = false)
	private List<LogActionDto> acList;

	public List<LogForgotPwdDto> getFgList() {
		return fgList;
	}

	public void setFgList(List<LogForgotPwdDto> fgList) {
		this.fgList = fgList;
	}

	public List<LogSigninDto> getSgList() {
		return sgList;
	}

	public void setSgList(List<LogSigninDto> sgList) {
		this.sgList = sgList;
	}

	public List<LogMrDto> getCwList() {
		return cwList;
	}

	public void setCwList(List<LogMrDto> cwList) {
		this.cwList = cwList;
	}

	public List<LogMrDto> getDmList() {
		return dmList;
	}

	public void setDmList(List<LogMrDto> dmList) {
		this.dmList = dmList;
	}

	public List<LogMrDto> getEcList() {
		return ecList;
	}

	public void setEcList(List<LogMrDto> ecList) {
		this.ecList = ecList;
	}

	public List<LogMrDto> getOfList() {
		return ofList;
	}

	public void setOfList(List<LogMrDto> ofList) {
		this.ofList = ofList;
	}

	public List<LogMrDto> getUrList() {
		return urList;
	}

	public void setUrList(List<LogMrDto> urList) {
		this.urList = urList;
	}

	public List<LogMrDto> getBnList() {
		return bnList;
	}

	public void setBnList(List<LogMrDto> bnList) {
		this.bnList = bnList;
	}

	public List<LogActionDto> getAcList() {
		return acList;
	}

	public void setAcList(List<LogActionDto> acList) {
		this.acList = acList;
	}

}
