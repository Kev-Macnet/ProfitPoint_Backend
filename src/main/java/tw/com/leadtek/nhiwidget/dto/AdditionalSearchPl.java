package tw.com.leadtek.nhiwidget.dto;

import io.swagger.annotations.ApiModelProperty;


public class AdditionalSearchPl {
    @ApiModelProperty(value="生效日", position=1, example="2021/07/11 (1625932800000)", required=true)
    private String start_date;
    
    @ApiModelProperty(value="失效日", position=2, example="2021/07/29 (1627488000000)", required=true)
    private String end_date;

    public String getStart_date() {
        return start_date.replaceAll("\'", "\'\'");
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date.replaceAll("\'", "\'\'");
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }
    

}







