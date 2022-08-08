package tw.com.leadtek.nhiwidget.dto;

import io.swagger.annotations.ApiModelProperty;


public class DbBackupProgressDto {
    @ApiModelProperty(value="備份(或還原)進度", example="85.71", position=1)
    private double progress;
    @ApiModelProperty(value="1.備份(或還原)被中斷, 0.未被中斷(持續備份(或還原)中...)", example="0", position=2)
    private int abort;
    @ApiModelProperty(value="狀態", example="0", position=3)
    private int status;
    @ApiModelProperty(value="觸發備份或還原的帳號", example="0", position=3)
    private String username;
    
    public double getProgress() {
        return progress;
    }
    public void setProgress(double progress) {
        this.progress = progress;
    }
    public int getAbort() {
        return abort;
    }
    public void setAbort(int abort) {
        this.abort = abort;
    }
    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
    public String getUsername() {
      return username;
    }
    public void setUsername(String username) {
      this.username = username;
    }
}







