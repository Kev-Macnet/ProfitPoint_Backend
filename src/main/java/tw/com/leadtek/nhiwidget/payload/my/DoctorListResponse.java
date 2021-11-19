/**
 * Created on 2021/11/18.
 */
package tw.com.leadtek.nhiwidget.payload.my;

import java.io.Serializable;
import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.payload.BasePageResponse;

@ApiModel("我的清單-醫師查看清單")
public class DoctorListResponse extends BasePageResponse implements Serializable {

  private static final long serialVersionUID = 5667626839257792203L;
  
  @ApiModelProperty(value = "醫師查看清單陣列", required = false)
  private List<DoctorList> data;

  @ApiModelProperty(value = "疑問標示病歷數", example = "3", required = false)
  private Integer questionMark;
  
  @ApiModelProperty(value = "疑問標示已讀取數", example = "3", required = false)
  private Integer readed;
  
  @ApiModelProperty(value = "疑問標示未讀取數", example = "0", required = false)
  private Integer unread;
  
  public DoctorListResponse() {
    
  }
  
  public Integer getReaded() {
    return readed;
  }

  public void setReaded(Integer readed) {
    this.readed = readed;
  }

  public Integer getUnread() {
    return unread;
  }

  public void setUnread(Integer unread) {
    this.unread = unread;
  }

  public Integer getQuestionMark() {
    return questionMark;
  }

  public void setQuestionMark(Integer questionMark) {
    this.questionMark = questionMark;
  }

  public List<DoctorList> getData() {
    return data;
  }

  public void setData(List<DoctorList> data) {
    this.data = data;
  }
  
}
