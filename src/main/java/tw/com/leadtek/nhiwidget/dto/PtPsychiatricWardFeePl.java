package tw.com.leadtek.nhiwidget.dto;

import javax.validation.constraints.NotEmpty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "pt精神慢性病房費設定參數")
public class PtPsychiatricWardFeePl extends PaymentTermsPl {
    @ApiModelProperty(value="分類", example="精神慢性病房費", required=true)
    @NotEmpty()
    private String category;

    @ApiModelProperty(value="限經中央衛生主管機關精神醫療院所(科)評鑑合格且辦理日間住院業務者申報(開關)", example="1",required=false)
    private int need_pass_review_enable;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getNeed_pass_review_enable() {
        return need_pass_review_enable;
    }

    public void setNeed_pass_review_enable(int need_pass_review_enable) {
        this.need_pass_review_enable = need_pass_review_enable;
    }

}



