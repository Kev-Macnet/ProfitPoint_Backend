package tw.com.leadtek.nhiwidget.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "系統資料備份參數")
public class BackupSettingDto {
    @ApiModelProperty(value="備份方式: 0-none, 1-每日, 2-每周, 3-每月", example="2", required=true, position=1)
    private int every;
    @ApiModelProperty(value="每星期幾備份: 0-週日, 1-週一, ..., 6-週六", example="1", required=true, position=2)
    private int week;
    @ApiModelProperty(value="每月幾號備份: , 1-1號, ..., 30-30號", example="1", required=true, position=3)
    private int month;
    @ApiModelProperty(value="備份時間(24小時制)", example="14:23", required=true, position=4)
    private String time;
    @ApiModelProperty(value="備份模式: 0-全部, 1-系統, 2-資料", example="2", required=true, position=5)
    private int mode;
    @ApiModelProperty(value="備份資料, 0-全部data, 1-增量與變更", example="1", required=true, position=6)
    private int add;
    
    public BackupSettingDto() {
        //
    }
    
    public BackupSettingDto(int every, int week, int month, String time, int mode, int add) {
        this.every = every;
        this.week = week;
        this.month = month;
        this.time = time;
        this.mode = mode;
        this.add = add;
    }

    public int getEvery() {
        return every;
    }
    public void setEvery(int every) {
        this.every = every;
    }
    public int getWeek() {
        return week;
    }
    public void setWeek(int week) {
        this.week = week;
    }
    public int getMonth() {
        return month;
    }
    public void setMonth(int month) {
        this.month = month;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public int getMode() {
        return mode;
    }
    public void setMode(int mode) {
        this.mode = mode;
    }
    public int getAdd() {
        return add;
    }
    public void setAdd(int add) {
        this.add = add;
    }

    @Override
    public String toString() { 
        java.util.Map<String,Object> map = new java.util.LinkedHashMap<String,Object>();
        map.put("every", every);
        map.put("week", week);
        map.put("month", month);
        map.put("time", time);
        map.put("mode", mode);
        map.put("add", add);
        
        com.google.gson.Gson gson = new com.google.gson.Gson();
        return gson.toJson(map);
    } 
    
}


