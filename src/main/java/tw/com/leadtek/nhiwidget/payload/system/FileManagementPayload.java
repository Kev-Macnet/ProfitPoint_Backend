/**
 * Created on 2022/1/13.
 */
package tw.com.leadtek.nhiwidget.payload.system;

import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.model.rdb.PARAMETERS;
import tw.com.leadtek.nhiwidget.payload.BaseResponse;

@ApiModel("檔案管理功能設定")
public class FileManagementPayload extends BaseResponse {

  private static final long serialVersionUID = 1366874125284551532L;

  @ApiModelProperty(value = "是否每日自動從HIS讀取更新", required = true)
  protected Boolean dailyInput;
  
  @ApiModelProperty(value = "每日自動從HIS讀取更新時間，格式：HH:mm，如04:00", required = false)
  protected String inputTime;
  
  @ApiModelProperty(value = "透過檔案匯入/匯出功能, 定義特定起迄日期, 從HIS讀取更新", required = true)
  protected Boolean inputByFile;
  
  @ApiModelProperty(value = "透過頁面功能按鍵, 單一就醫紀錄直接從HIS讀取更新", required = true)
  protected Boolean inputByButton;
  
  @ApiModelProperty(value = "是否每日自動從系統回寫HIS更新", required = true)
  protected Boolean dailyOutput;
  
  @ApiModelProperty(value = "每日自動從系統回寫HIS更新時間，格式：HH:mm，如04:00", required = false)
  protected String outputTime;
  
  @ApiModelProperty(value = "透過檔案匯入/匯出功能, 定義特定起迄日期, 從系統回寫HIS更新", required = true)
  protected Boolean outputByFile;
  
  @ApiModelProperty(value = "透過頁面功能按鍵, 單一就醫紀錄直接從系統回寫HIS更新", required = true)
  protected Boolean outputByButton;
  
  public FileManagementPayload() {
    
  }
  
  public FileManagementPayload(List<PARAMETERS> list) {
    if (list == null || list.size() == 0) {
      return;
    }
    for (PARAMETERS parameters : list) {
      if (parameters.getName().equals("IS_DAILY_INPUT")) {
        dailyInput = "1".equals(parameters.getValue());
      } else if (parameters.getName().equals("INPUT_TIME")) {
        inputTime = parameters.getValue();
      } else if (parameters.getName().equals("INPUT_BY_FILE")) {
        inputByFile = "1".equals(parameters.getValue());
      } else if (parameters.getName().equals("INPUT_BY_BUTTON")) {
        inputByButton = "1".equals(parameters.getValue());
      } else if (parameters.getName().equals("IS_DAILY_OUTPUT")) {
        dailyOutput = "1".equals(parameters.getValue());
      } else if (parameters.getName().equals("OUTPUT_TIME")) {
        outputTime = parameters.getValue();
      } else if (parameters.getName().equals("OUTPUT_BY_FILE")) {
        outputByFile = "1".equals(parameters.getValue());
      } else if (parameters.getName().equals("OUTPUT_BY_BUTTON")) {
        outputByButton = "1".equals(parameters.getValue());
      }
    }
  }

  public Boolean getDailyInput() {
    return dailyInput;
  }

  public void setDailyInput(Boolean dailyInput) {
    this.dailyInput = dailyInput;
  }

  public String getInputTime() {
    return inputTime;
  }

  public void setInputTime(String inputTime) {
    this.inputTime = inputTime;
  }

  public Boolean getInputByFile() {
    return inputByFile;
  }

  public void setInputByFile(Boolean inputByFile) {
    this.inputByFile = inputByFile;
  }

  public Boolean getInputByButton() {
    return inputByButton;
  }

  public void setInputByButton(Boolean inputByButton) {
    this.inputByButton = inputByButton;
  }

  public Boolean getDailyOutput() {
    return dailyOutput;
  }

  public void setDailyOutput(Boolean dailyOutput) {
    this.dailyOutput = dailyOutput;
  }

  public String getOutputTime() {
    return outputTime;
  }

  public void setOutputTime(String outputTime) {
    this.outputTime = outputTime;
  }

  public Boolean getOutputByFile() {
    return outputByFile;
  }

  public void setOutputByFile(Boolean outputByFile) {
    this.outputByFile = outputByFile;
  }

  public Boolean getOutputByButton() {
    return outputByButton;
  }

  public void setOutputByButton(Boolean outputByButton) {
    this.outputByButton = outputByButton;
  }
  
}
