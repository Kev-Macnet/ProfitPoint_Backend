/**
 * Created on 2021/1/20.
 */
package tw.com.leadtek.nhiwidget.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.model.redis.CodeBaseLongId;

@ApiModel("搜尋建議")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonSuggestion {

  @ApiModelProperty(value = "搜尋類別:搜尋項目", example = "A2", required = false)
  private String id;
  
  @ApiModelProperty(value = "英文說明", required = false)
  private String label;
  
  @ApiModelProperty(value = "中文說明", required = true)
  private String value;
  
  public JsonSuggestion() {
    
  }
  
  public JsonSuggestion(CodeBaseLongId cb) {
    if (cb.getCategory() != null) {
      this.id = cb.getCategory() + ":" + cb.getCode();
    } else {
      this.id = cb.getCode();
    }
    this.label = cb.getDescEn();
    this.value = cb.getDesc();
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
  
}
