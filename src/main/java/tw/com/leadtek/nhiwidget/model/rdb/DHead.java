/**
 * Created on 2020/12/24.
 */
package tw.com.leadtek.nhiwidget.model.rdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"d1", "d2"})
public class DHead {

  /**
   * 案件分類
   */
  private String CASE_TYPE;

  /**
   * 流水編號
   */
  private Integer SEQ_NO;

  public String getCASE_TYPE() {
    return CASE_TYPE;
  }

  @JsonProperty("d1")
  public void setCASE_TYPE(String cASE_TYPE) {
    CASE_TYPE = cASE_TYPE;
  }

  public Integer getSEQ_NO() {
    return SEQ_NO;
  }

  @JsonProperty("d2")
  public void setSEQ_NO(Integer sEQ_NO) {
    SEQ_NO = sEQ_NO;
  }

}
