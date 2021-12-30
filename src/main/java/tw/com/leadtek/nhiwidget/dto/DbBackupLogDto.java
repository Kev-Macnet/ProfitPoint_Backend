package tw.com.leadtek.nhiwidget.dto;

import java.util.Map;

import io.swagger.annotations.ApiModelProperty;


public class DbBackupLogDto {
    @ApiModelProperty(value="備份Id", example="38", position=1)
    private long id;
    @ApiModelProperty(value="備份人帳號", example="leadtek", position=2)
    private String username;
    @ApiModelProperty(value="備份檔案(空白時表無備份到資料,無須還原)", example="leadtek", position=3)
    private String filename;
    @ApiModelProperty(value="備份模式: 0-全部, 1-系統, 2-資料", example="2", position=4)
    private int mode;
//    @ApiModelProperty(value="各Table備份筆數", example="[{MR: 0}, {IP_P: 0}]", position=5)
    @ApiModelProperty(value="各Table備份筆數", example="", position=5)
    private java.util.List<Map<String, Object>> description;
    @ApiModelProperty(value="備份時間(timestamp)", example="1625932800000", position=6)
    private long update_tm;
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public int getMode() {
        return mode;
    }
    public void setMode(int mode) {
        this.mode = mode;
    }
    
    public java.util.List<Map<String, Object>> getDescription() {
        return description;
    }
    public void setDescription(java.util.List<Map<String, Object>> description) {
        this.description = description;
    }
    public long getUpdate_tm() {
        return update_tm;
    }
    public void setUpdate_tm(long update_tm) {
        this.update_tm = update_tm;
    }
    public String getFilename() {
        return filename;
    }
    public void setFilename(String filename) {
        this.filename = filename;
    }

}







