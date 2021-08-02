package tw.com.leadtek.nhiwidget.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "pt精神慢性病房費設定參數")
public class PtPsychiatricWardFeePl extends PaymentTermsPl {
    @ApiModelProperty(value="分類", example="精神慢性病房費", required=true)
    private String category;

    @ApiModelProperty(value="限經中央衛生主管機關精神醫療院所(科)評鑑合格且辦理日間住院業務者申報", example="1",required=false)
    private int lim_pass_review;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getLim_pass_review() {
        return lim_pass_review;
    }

    public void setLim_pass_review(int lim_pass_review) {
        this.lim_pass_review = lim_pass_review;
    }

}



